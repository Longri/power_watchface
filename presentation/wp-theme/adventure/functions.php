<?php

// Exit if accessed directly
if ( !defined('ABSPATH')) exit;

?><?php

// Global Content Width, Kind of a Joke with this theme, lol
	if (!isset($content_width))
		$content_width = 648;


// Ladies, Gentalmen, boys and girls let's start our engines
add_action('after_setup_theme', 'adventure_setup');

if (!function_exists('adventure_setup')):

function adventure_setup() {

    global $content_width; 
			
    // Add Callback for Custom TinyMCE editor stylesheets. (editor-style.css)
    add_editor_style();

    // This feature enables post and comment RSS feed links to head
    add_theme_support('automatic-feed-links');

    // This feature enables custom-menus support for a theme
    register_nav_menus(array(
        'bar' => __('The Menu Bar', 'adventure' ) ) );

    // WordPress 3.4+
    if ( function_exists('get_custom_header')) {
        add_theme_support('custom-background'); } } endif;


// Get our wp_nav_menu() fallback, wp_page_menu(), to show a home link
add_filter( 'wp_page_menu_args', 'adventure_page_menu_args' );
function adventure_page_menu_args( $args ) {
	$args['show_home'] = true;
	return $args; }


// Filters the title so that it says something useful on the tabs
add_filter( 'wp_title', 'adventure_filter_wp_title' );
function adventure_filter_wp_title( $title ) {
	global $page, $paged;

	if ( is_feed() )
		return $title;

	$site_description = get_bloginfo( 'description' );

	$filtered_title = $title . get_bloginfo( 'name' );
	$filtered_title .= ( ! empty( $site_description ) && ( is_home() || is_front_page() ) ) ? ' | ' . $site_description: '';
	$filtered_title .= ( 2 <= $paged || 2 <= $page ) ? ' | ' . sprintf( __( 'Page %s' ), max( $paged, $page ) ) : '';

	return $filtered_title;}


/**
 * Filter 'get_comments_number'
 * 
 * Filter 'get_comments_number' to display correct 
 * number of comments (count only comments, not 
 * trackbacks/pingbacks)
 *
 * Courtesy of Chip Bennett
 */
function adventure_comment_count( $count ) {  
	if ( ! is_admin() ) {
		global $id;
		$comments_by_type = &separate_comments(get_comments('status=approve&post_id=' . $id));
		return count($comments_by_type['comment']); }
	else {
		return $count; } }


/**
 * wp_list_comments() Pings Callback
 * 
 * wp_list_comments() Callback function for 
 * Pings (Trackbacks/Pingbacks)
 */
add_filter('get_comments_number', 'adventure_comment_count', 0);
function adventure_comment_list_pings( $comment ) {
	$GLOBALS['comment'] = $comment; ?>
	<li <?php comment_class(); ?> id="li-comment-<?php comment_ID(); ?>"><?php echo comment_author_link(); ?></li>
<?php }


// Sets the post excerpt length to 250 characters
add_filter('excerpt_length', 'adventure_excerpt_length');
function adventure_excerpt_length($length) {
    return 250; }

// Continue Reading link when excerpt is used
function new_excerpt_more( $more ) {
	return ' <a class="read-more" href="'. get_permalink( get_the_ID() ) . '">Continue Reading &#8594;</a>';
}
add_filter( 'excerpt_more', 'new_excerpt_more' );


/* This function adds in code specifically for IE6 to IE9 (haven't gotten around to this since the redesign)
add_action('wp_head', 'adventure_ie_css');
function adventure_ie_css() {
	echo "\n" . '<!-- IE 6 to 9 CSS Hacking -->' . "\n";
	echo '<!--[if lte IE 8]><style type="text/css">#content li{background: url(' . get_stylesheet_directory_uri() . '/images/75.png);} aside{background: url(' . get_stylesheet_directory_uri() . '/images/blacktrans.png);}#content li li{background:none;}</style><![endif]-->' . "\n";
	echo '<!--[if lte IE 7]><style type="text/css">.header li{float:left;width:150px;}</style><![endif]-->' . "\n";
	echo "\n"; } */


// This function removes inline styles set by WordPress gallery
add_filter('gallery_style', 'adventure_remove_gallery_css');
function adventure_remove_gallery_css($css) {
    return preg_replace("#<style type='text/css'>(.*?)</style>#s", '', $css); }


// This function removes default styles set by WordPress recent comments widget
add_action( 'widgets_init', 'adventure_remove_recent_comments_style' );
function adventure_remove_recent_comments_style() {
	global $wp_widget_factory;
	remove_action( 'wp_head', array( $wp_widget_factory->widgets['WP_Widget_Recent_Comments'], 'recent_comments_style' ) ); }


// A comment reply
add_action( 'wp_enqueue_scripts', 'adventure_enqueue_comment_reply' );
function adventure_enqueue_comment_reply() {
    if ( is_singular() && comments_open() && get_option('thread_comments')) 
            wp_enqueue_script('comment-reply'); }


// Wrap Video in a DIV so that videos width and height become reponsive using CSS
add_filter('embed_oembed_html', 'wrap_embed_with_div', 10, 3);
function wrap_embed_with_div($html, $url, $attr) {
	if (preg_match("/youtu.be/", $html) || preg_match("/youtube.com/", $html) || preg_match("/vimeo/", $html) || preg_match("/wordpress.tv/", $html) || preg_match("/v.wordpress.com/", $html)) { 
        // Don't see your video host in here? Just add it in, make sure you have the forward slash marks
            $html = '<div class="video-container">' . $html . "</div>"; }
            return $html;}


// WordPress Widgets start right here.
add_action('widgets_init', 'adventure_widgets_init');
function adventure_widgets_init() {
	register_sidebar( array(
		'name'          => __('sidebar', 'localize_adventure'),
		'id'            => 'adventure_widget',
		'description'   => __('Widgets in this area will appear to the right of the content, normally.', 'localize_adventure'),
		'before_widget' => '<aside>',
		'after_widget'  => '</aside>',
		'before_title'  => '<h2>',
		'after_title'   => '</h2>',));

	register_sidebar( array(
		'name'          => __( 'Footer Widget Area', 'localize_adventure' ),
		'id'            => 'sidebar-1',
		'description'   => __( 'Appears in the footer section of the site.', 'localize_adventure' ),
		'before_widget' => '<aside>',
		'after_widget'  => '</aside>',
		'before_title'  => '<h2>',
		'after_title'   => '</h2>',));}


// Checks if the Widgets are active
function adventure_is_sidebar_active($index) {
	global $wp_registered_sidebars;
	$widgetcolums = wp_get_sidebars_widgets();
	if ($widgetcolums[$index]) {
		return true; }
		return false; }


// Add CSS to the body depending on the sidebar side option
add_filter('body_class','adventure_sidebar_side');
function adventure_sidebar_side($classes) {
    if (adventure_is_sidebar_active('adventure_widget')) :    
        if (get_theme_mod('sidebar_position_setting') == 'right') :
            $classes[] = 'right_sidebar';
            return $classes;
        elseif (get_theme_mod('sidebar_position_setting') == 'left') :
            $classes[] = 'left_sidebar';
            return $classes;
        else :
            $classes[] = 'right_sidebar';
            return $classes;
        endif;
    else :
        $classes[] = 'no_sidebar';
        return $classes;
    endif; }

		
// Load up links in admin bar so theme is edit
function adventure_theme_options_add_page() {
    add_theme_page(__('Theme Information', 'localize_adventure'), __('Theme Information', 'localize_adventure'), 'edit_theme_options', 'theme_options', 'adventure_theme_options_do_page');}

// Load up the Localizer so that the theme can be translated
add_action('after_setup_theme', 'my_theme_setup');
function my_theme_setup(){
    load_theme_textdomain('adventure_localizer', get_template_directory() . '/languages');}

// Adds a meta box to the post editing screen
add_action( 'add_meta_boxes', 'prfx_custom_meta' );
function prfx_custom_meta() {
    add_meta_box( 'prfx_meta', __( 'Featured Background', 'localize_adventure' ), 'prfx_meta_callback', 'post', 'side' );
    add_meta_box( 'prfx_meta', __( 'Featured Background', 'localize_adventure' ), 'prfx_meta_callback', 'page', 'side' ); }


// Outputs the content of the meta box
function prfx_meta_callback( $post ) {
    wp_nonce_field( basename( __FILE__ ), 'prfx_nonce' );
    $prfx_stored_meta = get_post_meta( $post->ID );
	if (!empty($prfx_stored_meta['featured-background'][0]) ) $featured_background = $prfx_stored_meta['featured-background'][0];
    ?>

	<p>
	<label for="featured-background" class="prfx-row-title" style="text-align:justify;"><?php _e( 'The ideal image size is smaller than 400kb and a resolution around 1920 by 1080 pixels.', 'localize_adventure' )?><br><br></label>
	<img id="theimage" src='<?php if (empty($featured_background)) { echo get_template_directory_uri() . '/images/nothing_found.jpg';} else {echo $featured_background;} ?>' style="box-shadow:0 0 .05em rgba(19,19,19,.5); height:auto; width:100%;"/>
		<input type="text" name="featured-background" id="featured-background" value="<?php if ( isset ( $featured_background ) ) echo $featured_background; ?>" style="margin:0 0 .5em; width:100%;" />
		<input type="button" id="featured-background-button" class="button" value="<?php _e( 'Choose or Upload an Image', 'localize_adventure' )?>" style="margin:0 0 .25em; width:100%;" />
	</p> <?php }


// Loads the image management javascript
add_action( 'admin_enqueue_scripts', 'enqueue_featured_background' );

function enqueue_featured_background() {
	global $typenow;
    if(($typenow == 'post' ) || ($typenow == 'page' )) {

        // This function loads in the required files for the media manager.
        wp_enqueue_media();

        // Register, localize and enqueue our custom JS.
        wp_register_script( 'featured-background', get_template_directory_uri() . '/js/featured-background.js', array( 'jquery' ), '1', true );
        wp_localize_script( 'featured-background', 'featured_background',
            array(
                'title'     => 'Upload or choose an image for the Featured Background',
                'button'    => 'Use as Featured Background') );
        wp_enqueue_script( 'featured-background' ); } }

// Saves the custom meta input
add_action( 'save_post', 'prfx_meta_save' );
function prfx_meta_save( $post_id ) {
 
    // Checks save status
    $is_autosave = wp_is_post_autosave( $post_id );
    $is_revision = wp_is_post_revision( $post_id );
    $is_valid_nonce = ( isset( $_POST[ 'prfx_nonce' ] ) && wp_verify_nonce( $_POST[ 'prfx_nonce' ], basename( __FILE__ ) ) ) ? 'true' : 'false';
 
    // Exits script depending on save status
    if ( $is_autosave || $is_revision || !$is_valid_nonce ) {
        return; }
	
	// Checks for input and saves if needed
	if( isset( $_POST[ 'featured-background' ] ) ) {
    	update_post_meta( $post_id, 'featured-background', $_POST[ 'featured-background' ] ); } }

// Sets up the Customize.php for Adventure
function adventure_customize($wp_customize) {

	// Before we begin let's create a textarea input
	class adventure_Customize_Textarea_Control extends WP_Customize_Control {
    
		public $type = 'textarea';
	 
		public function render_content() { ?>
			<label>
			<span class="customize-control-title"><?php echo esc_html( $this->label ); ?></span>
			<textarea rows="1" style="width:100%;" <?php $this->link(); ?>><?php echo esc_textarea( $this->value() ); ?></textarea>
			</label> <?php } }

	// Create an Array with a ton of google fonts	
	$google_font_array = array(
        'Default'				=> 'Default',
        'Abel'					=> 'Abel',
        'Abril+Fatface'			=> 'Abril+Fatface',
        'Aclonica'				=> 'Aclonica',
        'Actor'					=> 'Actor',
        'Adamina'				=> 'Adamina',
        'Aldrich'				=> 'Aldrich',
        'Alice'					=> 'Alice',
        'Alike'					=> 'Alike',
        'Alike+Angular'			=> 'Alike+Angular',
        'Allan:700'				=> 'Allan:700',
        'Allerta'				=> 'Allerta',
        'Allerta+Stencil'		=> 'Allerta+Stencil',
        'Amaranth'				=> 'Amaranth',
        'Amatic+SC'				=> 'Amatic+SC',
        'Andada'				=> 'Andada',
        'Andika'				=> 'Andika',
        'Annie+Use+Your+Telescope' => 'Annie+Use+Your+Telescope',
        'Anonymous+Pro'			=> 'Anonymous+Pro',
        'Antic'					=> 'Antic',
        'Anton'					=> 'Anton',
        'Arapey'				=> 'Arapey',
        'Architects+Daughter'	=> 'Architects+Daughter',
        'Arimo'					=> 'Arimo',
        'Artifika'				=> 'Artifika',
        'Arvo'					=> 'Arvo',
        'Asset'					=> 'Asset',
        'Astloch'				=> 'Astloch',
        'Atomic+Age'			=> 'Atomic+Age',
        'Aubrey'				=> 'Aubrey',
        'Bangers'				=> 'Bangers',
        'Bentham'				=> 'Bentham',
        'Bevan'					=> 'Bevan',
        'Bigshot+One'			=> 'Bigshot+One',
        'Bitter'				=> 'Bitter',
        'Black+Ops+One'			=> 'Black+Ops+One',
        'Bowlby+One'			=> 'Bowlby+One',
        'Bowlby+One+SC'			=> 'Bowlby+One+SC',
        'Brawler'				=> 'Brawler',
        'Buda:300'				=> 'Buda:300',
        'Butcherman+Caps'		=> 'Butcherman+Caps',
        'Cabin'					=> 'Cabin',
        'Cabin+Sketch'			=> 'Cabin+Sketch',
        'Calligraffitti'		=> 'Calligraffitti',
        'Candal'				=> 'Candal',
        'Cantarell'				=> 'Cantarell',
        'Cardo'					=> 'Cardo',
        'Carme'					=> 'Carme',
        'Carter+One'			=> 'Carter+One',
        'Caudex'				=> 'Caudex',
        'Cedarville+Cursive'	=> 'Cedarville+Cursive',
        'Changa+One'			=> 'Changa+One',
        'Cherry+Cream+Soda'		=> 'Cherry+Cream+Soda',
        'Chewy'					=> 'Chewy',
        'Chivo'					=> 'Chivo',
        'Coda'					=> 'Coda',
        'Coda+Caption:800'		=> 'Coda+Caption:800',
        'Comfortaa'				=> 'Comfortaa',
        'Coming+Soon'			=> 'Coming+Soon',
        'Contrail+One'			=> 'Contrail+One',
        'Convergence'			=> 'Convergence',
        'Cookie'				=> 'Cookie',
        'Copse'					=> 'Copse',
        'Corben'				=> 'Corben',
        'Cousine'				=> 'Cousine',
        'Coustard'				=> 'Coustard',
        'Covered+By+Your+Grace' => 'Covered+By+Your+Grace',
        'Creepster+Caps'		=> 'Creepster+Caps',
        'Crimson+Text'			=> 'Crimson+Text',
        'Crushed'				=> 'Crushed',
        'Crafty+Girls'			=> 'Crafty+Girls',
        'Cuprum'				=> 'Cuprum',
        'Damion'				=> 'Damion',
        'Dancing+Script'		=> 'Dancing+Script',
        'Dawning+of+a+New+Day'	=> 'Dawning+of+a+New+Day',
        'Days+One'				=> 'Days+One',
        'Delius'				=> 'Delius',
        'Delius+Swash+Caps'		=> 'Delius+Swash+Caps',
        'Delius+Unicase'		=> 'Delius+Unicase',
        'Didact+Gothic'			=> 'Didact+Gothic',
        'Dorsa'					=> 'Dorsa',
        'Droid+Sans'			=> 'Droid+Sans',
        'Droid+Sans+Mono'		=> 'Droid+Sans+Mono',
        'Droid+Serif'			=> 'Droid+Serif',
        'Eater+Caps'			=> 'Eater+Caps',
        'EB+Garamond'			=> 'EB+Garamond',
        'Expletus+Sans'			=> 'Expletus+Sans',
        'Fanwood+Text'			=> 'Fanwood+Text',
        'Federant'				=> 'Federant',
        'Federo'				=> 'Federo',
        'Fjord+One'				=> 'Fjord+One',
        'Fontdiner+Swanky'		=> 'Fontdiner+Swanky',
        'Forum'					=> 'Forum',
        'Francois+One'			=> 'Francois+One',
        'Gentium+Basic'			=> 'Gentium+Basic',
        'Gentium+Book+Basic'	=> 'Gentium+Book+Basic',
        'Geo'					=> 'Geo',
        'Geostar'				=> 'Geostar',
        'Geostar+Fill'			=> 'Geostar+Fill',
        'Give+You+Glory'		=> 'Give+You+Glory',
        'Gloria+Hallelujah'		=> 'Gloria+Hallelujah',
        'Goblin+One'			=> 'Goblin+One',
        'Gochi+Hand'			=> 'Gochi+Hand',
        'Goudy+Bookletter+1911' => 'Goudy+Bookletter+1911',
        'Gravitas+One'			=> 'Gravitas+One',
        'Gruppo'				=> 'Gruppo',
        'Hammersmith+One'		=> 'Hammersmith+One',
        'Holtwood+One+SC'		=> 'Holtwood+One+SC',
        'Homemade+Apple'		=> 'Homemade+Apple',
        'IM+Fell+Double+Pica'	=> 'IM+Fell+Double+Pica',
        'IM+Fell+Double+Pica+SC' => 'IM+Fell+Double+Pica+SC',
        'IM+Fell+DW+Pica'		=> 'IM+Fell+DW+Pica',
        'IM+Fell+DW+Pica+SC'	=> 'IM+Fell+DW+Pica+SC',
        'IM+Fell+English'		=> 'IM+Fell+English',
        'IM+Fell+English+SC'	=> 'IM+Fell+English+SC',
        'IM+Fell+French+Canon'	=> 'IM+Fell+French+Canon',
        'IM+Fell+French+Canon+SC' => 'IM+Fell+French+Canon+SC',
        'IM+Fell+Great+Primer'	=> 'IM+Fell+Great+Primer',
        'IM+Fell+Great+Primer+SC' => 'IM+Fell+Great+Primer+SC',
        'Inconsolata'			=> 'Inconsolata',
        'Indie+Flower'			=> 'Indie+Flower',
        'Irish+Grover'			=> 'Irish+Grover',
        'Istok+Web'				=> 'Istok+Web',
        'Jockey+One'			=> 'Jockey+One',
        'Josefin+Sans'			=> 'Josefin+Sans',
        'Josefin+Slab'			=> 'Josefin+Slab',
        'Judson'				=> 'Judson',
        'Julee'					=> 'Julee',
        'Jura'					=> 'Jura',
        'Just+Another+Hand'		=> 'Just+Another+Hand',
        'Just+Me+Again+Down+Here' => 'Just+Me+Again+Down+Here',
        'Kameron'				=> 'Kameron',
        'Kelly+Slab'			=> 'Kelly+Slab',
        'Kenia'					=> 'Kenia',
        'Kranky'				=> 'Kranky',
        'Kreon'					=> 'Kreon',
        'Kristi'				=> 'Kristi',
        'La+Belle+Aurore'		=> 'La+Belle+Aurore',
        'Lancelot'				=> 'Lancelot',
        'Lato'					=> 'Lato',
        'League+Script'			=> 'League+Script',
        'Leckerli+One'			=> 'Leckerli+One',
        'Lekton'				=> 'Lekton',
        'Limelight'				=> 'Limelight',
        'Linden+Hill'			=> 'Linden+Hill',
        'Lobster'				=> 'Lobster',
        'Lobster+Two'			=> 'Lobster+Two',
        'Lora'					=> 'Lora',
        'Love+Ya+Like+A+Sister' => 'Love+Ya+Like+A+Sister',
        'Loved+by+the+King'		=> 'Loved+by+the+King',
        'Luckiest+Guy'			=> 'Luckiest+Guy',
        'Maiden+Orange'			=> 'Maiden+Orange',
        'Mako'					=> 'Mako',
        'Marck+Script'			=> 'Marck+Script',
        'Marvel'				=> 'Marvel',
        'Mate'					=> 'Mate',
        'Mate+SC'				=> 'Mate+SC',
        'Maven+Pro'				=> 'Maven+Pro',
        'Meddon'				=> 'Meddon',
        'MedievalSharp'			=> 'MedievalSharp',
        'Megrim'				=> 'Megrim',
        'Merienda+One'			=> 'Merienda+One',
        'Merriweather'			=> 'Merriweather',
        'Metrophobic'			=> 'Metrophobic',
        'Michroma'				=> 'Michroma',
        'Miltonian'				=> 'Miltonian',
        'Miltonian+Tattoo'		=> 'Miltonian+Tattoo',
        'Molengo'				=> 'Molengo',
        'Monofett'				=> 'Monofett',
        'Monoton'				=> 'Monoton',
        'Montez'				=> 'Montez',
        'Modern+Antiqua'		=> 'Modern+Antiqua',
        'Mountains+of+Christmas' => 'Mountains+of+Christmas',
        'Muli'					=> 'Muli',
        'Neucha'				=> 'Neucha',
        'Neuton'				=> 'Neuton',
        'News+Cycle'			=> 'News+Cycle',
        'Nixie+One'				=> 'Nixie+One',
        'Nobile'				=> 'Nobile',
        'Nosifer+Caps'			=> 'Nosifer+Caps',
        'Nothing+You+Could+Do'	=> 'Nothing+You+Could+Do',
        'Nova+Cut'				=> 'Nova+Cut',
        'Nova+Flat'				=> 'Nova+Flat',
        'Nova+Mono'				=> 'Nova+Mono',
        'Nova+Oval'				=> 'Nova+Oval',
        'Nova+Script'			=> 'Nova+Script',
        'Nova+Slim'				=> 'Nova+Slim',
        'Nova+Round'			=> 'Nova+Round',
        'Nova+Square'			=> 'Nova+Square',
        'Numans'				=> 'Numans',
        'Nunito'				=> 'Nunito',
        'Old+Standard+TT'		=> 'Old+Standard+TT',
        'Open+Sans'				=> 'Open+Sans',
        'Open+Sans+Condensed:300' => 'Open+Sans+Condensed:300',
        'Orbitron'				=> 'Orbitron',
        'Oswald'				=> 'Oswald',
        'Over+the+Rainbow'		=> 'Over+the+Rainbow',
        'Ovo'					=> 'Ovo',
        'Pacifico'				=> 'Pacifico',
        'Play'					=> 'Play',
        'Passero+One'			=> 'Passero+One',
        'Patrick+Hand'			=> 'Patrick+Hand',
        'Paytone+One'			=> 'Paytone+One',
        'Permanent+Marker'		=> 'Permanent+Marker',
        'Petrona'				=> 'Petrona',
        'Philosopher'			=> 'Philosopher',
        'Pinyon+Script'			=> 'Pinyon+Script',
        'Playfair+Display'		=> 'Playfair+Display',
        'Podkova'				=> 'Podkova',
        'Poller+One'			=> 'Poller+One',
        'Poly'					=> 'Poly',
        'Pompiere'				=> 'Pompiere',
        'Prata'					=> 'Prata',
        'Prociono'				=> 'Prociono',
        'PT+Sans'				=> 'PT+Sans',
        'PT+Sans+Caption'		=> 'PT+Sans+Caption',
        'PT+Sans+Narrow'		=> 'PT+Sans+Narrow',
        'PT+Serif'				=> 'PT+Serif',
        'PT+Serif+Caption'		=> 'PT+Serif+Caption',
        'Puritan'				=> 'Puritan',
        'Quattrocento'			=> 'Quattrocento',
        'Quattrocento+Sans'		=> 'Quattrocento+Sans',
        'Questrial'				=> 'Questrial',
        'Quicksand'				=> 'Quicksand',
        'Radley'				=> 'Radley',
        'Raleway:100'			=> 'Raleway:100',
        'Rammetto+One'			=> 'Rammetto+One',
        'Rancho'				=> 'Rancho',
        'Rationale'				=> 'Rationale',
        'Redressed'				=> 'Redressed',
        'Reenie+Beanie'			=> 'Reenie+Beanie',
        'Rock+Salt'				=> 'Rock+Salt',
        'Rochester'				=> 'Rochester',
        'Rokkitt'				=> 'Rokkitt',
        'Rosario'				=> 'Rosario',
        'Ruslan+Display'		=> 'Ruslan+Display',
        'Salsa'					=> 'Salsa',
        'Sancreek'				=> 'Sancreek',
        'Sansita+One'			=> 'Sansita+One',
        'Satisfy'				=> 'Satisfy',
        'Schoolbell'			=> 'Schoolbell',
        'Shadows+Into+Light'	=> 'Shadows+Into+Light',
        'Shanti'				=> 'Shanti',
        'Short+Stack'			=> 'Short+Stack',
        'Sigmar+One'			=> 'Sigmar+One',
        'Six+Caps'				=> 'Six+Caps',
        'Slackey'				=> 'Slackey',
        'Smokum'				=> 'Smokum',
        'Smythe'				=> 'Smythe',
        'Sniglet:800'			=> 'Sniglet:800',
        'Snippet'				=> 'Snippet',
        'Sorts+Mill+Goudy'		=> 'Sorts+Mill+Goudy',
        'Special+Elite'			=> 'Special+Elite',
        'Spinnaker'				=> 'Spinnaker',
        'Stardos+Stencil'		=> 'Stardos+Stencil',
        'Sue+Ellen+Francisco'	=> 'Sue+Ellen+Francisco',
        'Supermercado+One'		=> 'Supermercado+One',
        'Sunshiney'				=> 'Sunshiney',
        'Swanky+and+Moo+Moo'	=> 'Swanky+and+Moo+Moo',
        'Syncopate'				=> 'Syncopate',
        'Tangerine'				=> 'Tangerine',
        'Tenor+Sans'			=> 'Tenor+Sans',
        'Terminal+Dosis'		=> 'Terminal+Dosis',
        'The+Girl+Next+Door'	=> 'The+Girl+Next+Door',
        'Tienne'				=> 'Tienne',
        'Tinos'					=> 'Tinos',
        'Tulpen+One'			=> 'Tulpen+One',
        'Ubuntu'				=> 'Ubuntu',
        'Ubuntu+Condensed'		=> 'Ubuntu+Condensed',
        'Ubuntu+Mono'			=> 'Ubuntu+Mono',
        'Ultra'					=> 'Ultra',
        'UnifrakturCook:700'	=> 'UnifrakturCook:700',
        'UnifrakturMaguntia'	=> 'UnifrakturMaguntia',
        'Unkempt'				=> 'Unkempt',
        'Unna'					=> 'Unna',
        'Varela'				=> 'Varela',
        'Varela+Round'			=> 'Varela+Round',
        'Vast+Shadow'			=> 'Vast+Shadow',
        'Vidaloka'				=> 'Vidaloka',
        'Vibur'					=> 'Inconsolata',
        'Volkhov'				=> 'Volkhov',
        'Vollkorn'				=> 'Vollkorn',
        'Voltaire'				=> 'Voltaire',
        'VT323'					=> 'VT323',
        'Waiting+for+the+Sunrise' => 'Waiting+for+the+Sunrise',
        'Wallpoet'				=> 'Wallpoet',
        'Walter+Turncoat'		=> 'Walter+Turncoat',
        'Wire+One'				=> 'Wire+One',
        'Yanone+Kaffeesatz'		=> 'Yanone+Kaffeesatz',
        'Yellowtail'			=> 'Yellowtail',
        'Yeseva+One'			=> 'Yeseva+One',
        'Zeyada'				=> 'Zeyada');
    
    // Add in all the settings with an array
    $set_adventures_theme_option_defaults = array(
        'author_setting'			    => 'on',
        'backgroundcolor_setting'       => '#b4b09d',
        'bodyfontstyle_setting'	        => 'Default',
        'backgroundsize_setting'        => 'Default',
        'bannerimage_setting'           => 'purple.png',
        'border_setting'			    => '3px',
        'bordercolor_setting'			=> '#4a4646',
        'commentsclosed_setting'        => 'on',
        'comments_setting'			    => 'both',
        'contentbackground_setting'     => '.80',
        'dateformat_setting'            => '',
        'display_date_setting'          => 'on',
        'display_excerpt_setting'       => 'off',
        'display_post_title_setting'    => 'on',
        'dropcolor_setting'			    => '#BBBBBB',
        'dropcolorhover_setting'        => '#0b6492',
        'facebook_setting'              => __('The url link goes in here.', 'localize_adventure'),
        'fontcolor_setting'			    => '#000000',
        'fontsizeadjust_setting'        => '1',
        'google_webmaster_tool_setting' => 'For example mine is "gN9drVvyyDUFQzMSBL8Y8-EttW1pUDtnUypP-331Kqh"',
        'google_analytics_setting'      => 'For example mine is "UA-9335180-X"',
        'google_plus_setting'           => __('The url link goes in here.', 'localize_adventure'),
        'headerfontstyle_setting'       => 'Default',
        'headerspacing_setting'	        => '18',
        'header_image_width_setting'    => '20',
        'instagram_setting'             => __('The url link goes in here.', 'localize_adventure'),
        'linkcolor_setting'	            => '#0b6492',
        'linkcolorhover_setting'        => '#FFFFFF',
        'menu_setting'                  => 'standard',
        'navcolor_setting'              => '#CCCCCC',
        'navcolorhover_setting'         => '#0b6492',
        'navi_search_setting'           => 'off',
        'previousnext_setting'		    => 'both',
        'removefooter_setting'          => 'visible',
        'sidebarbackground_setting' 	=> '.50',
        'sidebarcolor_setting'		    => '#000000',
        'soundcloud_setting'            => __('The url link goes in here.', 'localize_adventure'),
        'taglinecolor_setting'		    => '#066ba0',
        'taglinefontstyle_setting'      => 'Default',
        'tagline_rotation_setting'      => '-1.00',
        'title_size_setting'            => '4.0',
        'titlecolor_setting'            => '#eee2d6',
        'titlefontstyle_setting'        => 'Default',
        'twitter_setting'               => __('The url link goes in here.', 'localize_adventure'),
        'vimeo_setting'                 => __('The url link goes in here.', 'localize_adventure'),
        'youtube_setting'               => __('The url link goes in here.', 'localize_adventure'));
    
    // Create the Setting
    foreach($set_adventures_theme_option_defaults as $setting => $value) {
            $wp_customize->add_setting( $setting , array('default' => $value )); }
    
    // Set the setting if it is some how blank
    foreach($set_adventures_theme_option_defaults as $setting => $value) {
        if ( get_theme_mod($setting) == '' ) set_theme_mod($setting , $value); }

	// The Standard Sections for Theme Custimizer
	$wp_customize->add_section( 'meta_section', array(
        'title'					=> __('Meta', 'localize_adventure'),
        'priority'				=> 1, ));

	$wp_customize->add_section( 'header_section', array(
        'title'				=> __('Header', 'localize_adventure'),
        'description'       => 'does this work? <a href="http://youtube.com/">YouTube</a>',
		'priority'			=> 26, ));

	$wp_customize->add_section( 'nav', array(
        'title'				=> __('Menu', 'localize_adventure'),
		'priority'			=> 27, ));

	$wp_customize->add_section( 'background_image', array(
        'title'				=> __('Background', 'localize_adventure'),
		'priority'			=> 28, ));

	$wp_customize->add_section( 'content_section', array(
        'title'				=> __('Content', 'localize_adventure'),
        'priority'			=> 29, ));

	$wp_customize->add_section( 'sidebar_section', array(
        'title'				=> __('Sidebar', 'localize_adventure'),
        'priority'			=> 30, ));

	$wp_customize->add_section( 'links_section', array(
        'title'				=> __('Links', 'localize_adventure'),
        'priority'			=> 32, ));

	// Remove the Section Colors for the Sake of making Sense
	$wp_customize->remove_section( 'colors');

	// Background needed to be moved to to the Background Section
	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'background_color', array(
		'label'				=> __('Background Color', 'localize_adventure'),
		'section'			=> 'background_image', )));

	// Change Site Title Color
	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'titlecolor_control', array(
		'label'				=> __('Site Title Color', 'localize_adventure'),
		'section'			=> 'title_tagline',
		'settings'			=> 'titlecolor_setting', )));
    
    // Control the Size of the site Title and Slogan size
    $wp_customize->add_control('title_size_control', array(
		'label'				=> __('Title Font Size', 'localize_adventure'),
		'priority'			=> 1,
		'section'			=> 'header_section',
		'settings'			=> 'title_size_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'6.0'			=> '6.0em',
			'5.8'			=> '5.8em',
			'5.6'			=> '5.6em',
			'5.4'			=> '5.4em',
			'5.2'			=> '5.2em',
			'5.0'			=> '5.0em',
			'4.8'			=> '4.8em',
			'4.6'			=> '4.6em',
			'4.4'			=> '4.4em',
			'4.2'			=> '4.2em',
			'4.0'			=> '4.0em',
			'3.8'			=> '3.8em',
			'3.6'			=> '3.6em',
			'3.4'			=> '3.4em',
			'3.2'			=> '3.2em',
			'3.0'			=> '3.0em',
			'2.8'			=> '2.8em', ), ));

	// Change Tagline Color
	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'taglinecolor_control', array(
		'label'				=> __('Tagline Color', 'localize_adventure'),
		'section'			=> 'title_tagline',
		'settings'			=> 'taglinecolor_setting', )));
    
    // Rotation of The Tagline
    $wp_customize->add_control('tagline_rotation_control', array(
		'label'				=> __('Tagline Rotation', 'localize_adventure'),
		'priority'			=> 2,
		'section'			=> 'header_section',
		'settings'			=> 'tagline_rotation_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'-2.00'			=> '-2.00&deg;',
			'-1.75'			=> '-1.75&deg;',
			'-1.50'			=> '-1.50&deg;',
			'-1.25'			=> '-1.25&deg;',
			'-1.00'			=> '-1.00&deg;',
			'-0.75'			=> '-0.75&deg;',
			'-0.50'			=> '-0.50&deg;',
			'-0.25'			=> '-0.25&deg;',
			'0.00'			=> '0.00&deg;',
			'0.25'			=> '0.25&deg;',
			'0.50'			=> '0.50&deg;',
			'0.75'			=> '0.75&deg;',
			'1.00'			=> '1.00&deg;', ), ));

	// Choose the Different Images for the Banner
	$wp_customize->add_control('themename_color_scheme', array(
		'label'				=> __('Banner Background', 'localize_adventure'),
		'priority'			=> 1,
		'section'			=> 'header_section',
		'settings'			=> 'bannerimage_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'purple.png'	=> __('Purple (Default)', 'localize_adventure'),
			'blue.png'		=> __('Blue', 'localize_adventure'),
			'marble.png'	=> __('Marble', 'localize_adventure'),
			'green.png'		=> __('Green', 'localize_adventure'), ), ));

	// Upload and Customization for the Banner and Header Options
	$wp_customize->add_control('menu_control', array(
		'label'				=> __('Menu Display Options', 'localize_adventure'),
		'priority'			=> 6,
		'section'			=> 'header_section',
		'settings'			=> 'menu_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'standard'		=> __('Standard (Default)', 'localize_adventure'),
			'notitle'		=> __('No Title', 'localize_adventure'),
			'bottom'		=> __('Moves Menu To Bottom', 'localize_adventure'), ), ));
			
	// Turn the Search bar in the navigation on or off
	$wp_customize->add_control( 'navi_search_control', array(
		'label'					=> __('Search bar in navigaton', 'localize_adventure'),
		'section'				=> 'nav',
		'settings'				=> 'navi_search_setting',
		'type'					=> 'select',
		'choices'				=> array(
			'off'				=> __('Do not display search', 'localize_adventure'),
			'on'				=> __('Display the search', 'localize_adventure'),), ));
			
	// Turn the title of posts off
	$wp_customize->add_control( 'display_post_title_control', array(
		'section'				=> 'content_section',
		'label'					=> __('Display the Page Title', 'localize_adventure'),
		'settings'				=> 'display_post_title_setting',
		'type'					=> 'select',
		'choices'				=> array(
			'on'				=> __('Display the title', 'localize_adventure'),
			'off'				=> __('Do not display title', 'localize_adventure'),), ));
			
	// Turn the date / time on post on or off
	$wp_customize->add_control( 'display_date_control', array(
		'section'				=> 'content_section',
		'label'					=> __('Display the date', 'localize_adventure'),
		'settings'				=> 'display_date_setting',
		'type'					=> 'select',
		'choices'				=> array(
			'on'				=> __('Display the dates', 'localize_adventure'),
			'off'				=> __('Do not display dates', 'localize_adventure'),), ));
			
	// Display an excerpt on the landing page
	$wp_customize->add_control( 'display_excerpt_control', array(
		'section'				=> 'content_section',
		'label'					=> __('Display excerpt on paged content', 'localize_adventure'),
		'settings'				=> 'display_excerpt_setting',
		'type'					=> 'select',
		'choices'				=> array(
			'off'				=> __('Display the content', 'localize_adventure'),
			'on'				=> __('Display the excerpt', 'localize_adventure'),), ));
			
	// Add Facebook Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'facebook_control', array(
		'label'				=> __('Facebook icon in the Menu', 'localize_adventure'),
		'priority'			=> 50,
		'section'			=> 'nav',
		'settings'			=> 'facebook_setting', )));
			
	// Add Twitter Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'twitter_control', array(
		'label'				=> __('Twitter icon in the Menu', 'localize_adventure'),
		'priority'			=> 51,
		'section'			=> 'nav',
		'settings'			=> 'twitter_setting', )));
			
	// Add Instagram Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'instagram_plus_control', array(
		'label'				=> __('Instagram icon in the Menu', 'localize_adventure'),
		'priority'			=> 52,
		'section'			=> 'nav',
		'settings'			=> 'instagram_setting', )));
			
	// Add Google+ Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'google_plus_control', array(
		'label'				=> __('Google Plus icon in the Menu', 'localize_adventure'),
		'priority'			=> 52,
		'section'			=> 'nav',
		'settings'			=> 'google_plus_setting', )));
			
	// Add YouTube Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'youtube_control', array(
		'label'				=> 'Youtube icon in the Menu',
		'priority'			=> 54,
		'section'			=> 'nav',
		'settings'			=> 'youtube_setting', )));
			
	// Add Vimeo Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'vimeo_control', array(
		'label'				=> __('Vimeo icon in the Menu', 'localize_adventure'),
		'priority'			=> 55,
		'section'			=> 'nav',
		'settings'			=> 'vimeo_setting', )));
			
	// Add Soundcloud Icon to the navigation
	$wp_customize->add_control( new adventure_Customize_Textarea_Control( $wp_customize, 'soundcloud_control', array(
		'label'				=> __('Soundcloud icon in the Menu', 'localize_adventure'),
		'priority'			=> 56,
		'section'			=> 'nav',
		'settings'			=> 'soundcloud_setting', )));			
			
	// Change the font for the website title
	$wp_customize->add_setting( 'titlefontstyle_setting', array(
		'Default'           => 'Default',
		'control'           => 'select',));

	$wp_customize->add_control( 'titlefontstyle_control', array(
		'label'					=> __('Google Webfonts Site Title', 'localize_adventure'),
		'priority'				=> 10,
		'section'				=> 'title_tagline',
		'settings'				=> 'titlefontstyle_setting',
		'type'					=> 'select',
		'choices'				=> $google_font_array, ));
			
	// Change the font for the tag line
	$wp_customize->add_setting( 'taglinefontstyle_setting', array(
		'Default'           => 'Default',
		'control'           => 'select',));

	$wp_customize->add_control( 'taglinefontstyle_control', array(
		'label'					=> __('Google Webfonts Tagline', 'localize_adventure'),
		'priority'				=> 11,
		'section'				=> 'title_tagline',
		'settings'				=> 'taglinefontstyle_setting',
		'type'					=> 'select',
		'choices'				=> $google_font_array, ));

	// Adjust the Space Between the Top of the Page and Content
	$wp_customize->add_setting( 'headerspacing_setting', array(
		'default'           => '18',
		'control'           => 'select',));

	$wp_customize->add_control( 'headerspacing_control', array(
		'label'				=> __('Spacing Between Top and Content', 'localize_adventure'),
		'priority'			=> 90,
		'section'			=> 'header_section',
		'settings'			=> 'headerspacing_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'26'			=> '26em',
			'24'			=> '24em',
			'22'			=> '22em',
			'20'			=> '20em',
			'18'			=> '18em Default',
			'16'			=> '16em',
			'14'			=> '14em',
			'12'			=> '12em',
			'10'			=> '10em',
			'9'				=> '9em',
			'8'				=> '8em',
			'7'				=> '7em',
			'6'				=> '6em',
			'5'				=> '5em',
			'4'				=> '4em',
			'3'				=> '3em',
			'2'				=> '2em',
			'1'				=> '1em',
			'0'				=> '0em',), ));

	// Add the option to use the CSS3 property Background-size
	$wp_customize->add_setting( 'backgroundsize_setting', array(
		'default'           => 'auto',
		'control'           => 'select',));

	$wp_customize->add_control( 'backgroundsize_control', array(
		'label'				=> __('Background Size', 'localize_adventure'),
		'section'			=> 'background_image',
		'settings'			=> 'backgroundsize_setting',
		'priority'			=> 10,
		'type'				=> 'select',
		'choices'			=> array(
			'auto'			=> __('Auto (Default)', 'localize_adventure'),
			'contain'		=> __('Contain', 'localize_adventure'),
			'cover'			=> __('Cover', 'localize_adventure'),), ));

	// Change the color of the Content Background
	$wp_customize->add_setting( 'backgroundcolor_setting', array(
		'default'           => '#b4b09d',
		'control'           => 'select',));

	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'backgroundcolor_control', array(
		'label'				=> __('Color of the Content Background', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'backgroundcolor_setting', )));

	// Change the opacity of the Content Background
	$wp_customize->add_control( 'contentbackground_control', array(
		'label'				=> __('Transparency of Content Background', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'contentbackground_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'1'				=> '100',
			'.95'			=> '95',
			'.90'			=> '90',
			'.85'			=> '85',
			'.80'			=> '80 (Default)',
			'.75'			=> '75',
			'.70'			=> '70',
			'.65'			=> '65',
			'.60'			=> '60',
			'.55'			=> '55',
			'.50'			=> '50',
			'.45'			=> '45',
			'.40'			=> '40',
			'.35'			=> '35',
			'.30'			=> '30',
			'.25'			=> '25',
			'.20'			=> '20',
			'.15'			=> '15',
			'.10'			=> '10',
			'.05'			=> '5',
			'.00'			=> '0',), ));

	// Settings for the Date
	$wp_customize->add_control( 'dateformat_control', array(
		'label'				=> __('Format for Date', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'dateformat_setting',
		'type'				=> 'select',
		'choices'			=> array(
			''			    => __('May 14th (Default)', 'localize_adventure'),
			'M j'    		=> __('May 14', 'localize_adventure'),
			'M jS, Y'    	=> __('May 14th, 2014', 'localize_adventure'),
			'M j, Y'	    => __('May 14, 2014', 'localize_adventure'),
			'jS M, Y'     	=> __('14th May, 2014', 'localize_adventure'),
			'Y, M js'       => __('2014, May 14th', 'localize_adventure'),
			'Y/m/d'     	=> __('2014/5/14', 'localize_adventure'),
			'd/m/Y'         => __('14/5/2014', 'localize_adventure'), ), ));

	// Settings for the Previous & Next Post Link
	$wp_customize->add_setting( 'previousnext_setting', array(
		'default'           => 'both',
		'control'           => 'select',));

	$wp_customize->add_control( 'previousnext_control', array(
		'label'				=> __('Previous & Next Links After Content', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'previousnext_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'both'			=> __('Both Pages & Posts', 'localize_adventure'),
			'single'	    => __('Only Posts', 'localize_adventure'),
			'page'			=> __('Only Pages', 'localize_adventure'),
			'neither'		=> __('Neither', 'localize_adventure'), ), ));

	// Settings for the text about the Author
	$wp_customize->add_setting( 'author_setting', array(
		'default'           => 'on',
		'control'           => 'select',));

	$wp_customize->add_control( 'author_control', array(
		'label'				=> __('Author Information', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'author_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'on'             => __('On', 'localize_adventure'),
			'off'            => __('Off', 'localize_adventure'), ), ));

	// Turn the information for the comments On or Off
	$wp_customize->add_setting( 'commentsclosed_setting', array(
		'default'           => 'on',
		'control'           => 'select',));

	$wp_customize->add_control( 'commentsclosed_control', array(
		'label'				=> __('Comment Information', 'localize_adventure'),
		'section'			=> 'content_section',
		'settings'			=> 'commentsclosed_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'on'             => __('On', 'localize_adventure'),
			'off'            => __('Off', 'localize_adventure'), ), ));

	// Adjust the position of the sidebar to be on the left or the right
	$wp_customize->add_setting( 'sidebar_position_setting', array(
		'default'           => 'left',
		'control'           => 'select',));

	$wp_customize->add_control( 'sidebar_position_control', array(
		'label'				=> __('Sidebar Position', 'localize_adventure'),
		'section'			=> 'sidebar_section',
		'settings'			=> 'sidebar_position_setting',
		'type'				=> 'select',
		'choices'			=> array(
			'right'			=> __('Right', 'localize_adventure'),
			'left'			=> __('Left', 'localize_adventure'),
			'no-sidebar'    => __('No Sidebar (remove the widgets too)', 'localize_adventure'), ), ));
			
	// Comments Choice
	$wp_customize->add_setting( 'comments_setting', array(
		'default'           	=> 'both',
		'control'           	=> 'select',));

	$wp_customize->add_control( 'comments_control', array(
		'section'				=> 'content_section',
		'label'					=> 'Options for Displaying Comments',
		'settings'				=> 'comments_setting',
		'type'					=> 'select',
		'choices'				=> array(
			'both'	            => __('Comments on both Pages & Posts', 'localize_adventure'),
			'single'	        => __('Comments only on Posts', 'localize_adventure'),
			'page'				=> __('Comments only on Pages', 'localize_adventure'),
			'none'				=> __('Comments completely Off', 'localize_adventure'),), )); }

add_action('customize_register', 'adventure_customize');


// Inject the Customizer Choices into the Theme
function adventure_inline_css() {
    
    //Favicon
    if ( get_theme_mod('favicon_setting') != '' ) {
        echo '<!-- Favicon Image -->' . "\n";
        echo '<link rel="shortcut icon" href="' . get_theme_mod('favicon_setting') . '" />' . "\n\n";}
		
		// Convert Content from Hex to RGB
		if ( get_theme_mod('backgroundcolor_setting') != '#b4b09d' ) {
			$hex = str_replace("#", "", get_theme_mod('backgroundcolor_setting'));
			if(strlen($hex) == 3) {
				$r = hexdec(substr($hex,0,1).substr($hex,0,1));
				$g = hexdec(substr($hex,1,1).substr($hex,1,1));
				$b = hexdec(substr($hex,2,1).substr($hex,2,1)); }
			else {
				$r = hexdec(substr($hex,0,2));
				$g = hexdec(substr($hex,2,2));
				$b = hexdec(substr($hex,4,2)); } }

		// Convert Sidebar from Hex to RGB
		if ( ( get_theme_mod('sidebarcolor_setting') != '#000000' ) ) {
		$hexs = str_replace("#", "", get_theme_mod('sidebarcolor_setting'));

		if(strlen($hexs) == 3) {
			$rs = hexdec(substr($hexs,0,1).substr($hexs,0,1));
			$gs = hexdec(substr($hexs,1,1).substr($hexs,1,1));
			$bs = hexdec(substr($hexs,2,1).substr($hexs,2,1)); }
		else {
			$rs = hexdec(substr($hexs,0,2));
			$gs = hexdec(substr($hexs,2,2));
			$bs = hexdec(substr($hexs,4,2)); } }
		
        if ( ( get_theme_mod('titlefontstyle_setting') != 'Default') || (get_theme_mod('taglinefontstyle_setting') != 'Default') || (get_theme_mod('bodyfontstyle_setting') != 'Default') || (get_theme_mod('headerfontstyle_setting') != 'Default')) {
            echo '<!-- Custom Font Styles -->' . "\n";
            if (get_theme_mod('titlefontstyle_setting') != 'Default') {echo "<link href='http://fonts.googleapis.com/css?family=" . get_theme_mod('titlefontstyle_setting') . "' rel='stylesheet' type='text/css'>"  . "\n"; }
            if (get_theme_mod('taglinefontstyle_setting') != 'Default') {	echo "<link href='http://fonts.googleapis.com/css?family=" . get_theme_mod('taglinefontstyle_setting') . "' rel='stylesheet' type='text/css'>"  . "\n"; }
            if (get_theme_mod('bodyfontstyle_setting') != 'Default') {	echo "<link href='http://fonts.googleapis.com/css?family=" . get_theme_mod('bodyfontstyle_setting') . "' rel='stylesheet' type='text/css'>"  . "\n"; }
            if (get_theme_mod('headerfontstyle_setting') != 'Default') {	echo "<link href='http://fonts.googleapis.com/css?family=" . get_theme_mod('headerfontstyle_setting') . "' rel='stylesheet' type='text/css'>"  . "\n"; }
            echo '<!-- End Custom Fonts -->' . "\n\n";}

		echo '<!-- Custom CSS Styles -->' . "\n";
        echo '<style type="text/css" media="screen">' . "\n";
        if (is_page() || is_single()) $featured_background = get_post_meta( get_queried_object_ID(), 'featured-background', true ); if (!empty($featured_background)) echo '   body, body.custom-background {background-image:url(' . $featured_background . '); background-size:cover;}' . "\n";
		if ( get_theme_mod('backgroundsize_setting') != 'auto' ) echo '	body, body.custom-background {background-size:' . get_theme_mod('backgroundsize_setting') . ';}' . "\n";
		if ( get_theme_mod('backgroundcolor_setting') != '#b4b09d' ) echo '	.contents {background: rgba(' . $r . ',' . $g . ', ' . $b . ', ' .  get_theme_mod('contentbackground_setting') .  ');}' . "\n";
        if ( get_theme_mod('backgroundcolor_setting') != '#b4b09d' ) echo ' @media only screen and (max-width:55em) { .contents {background: rgba(' . $r . ',' . $g . ', ' . $b . ', .95 );} }' . "\n";
		if ( ( get_theme_mod('sidebarcolor_setting') != '#000000'  ) || ( get_theme_mod('sidebarbackground_setting') != '.50' ) ) echo '	aside {background: rgba(' . $rs . ',' . $gs . ', ' . $bs . ', ' .  get_theme_mod('sidebarbackground_setting') .  ');}' . "\n";
		if ( get_theme_mod('titlecolor_setting') != '#eee2d6' ) echo '	.header h1 a {color:' . get_theme_mod('titlecolor_setting') . ';}' . "\n";
		if ( get_theme_mod('taglinecolor_setting') != '#066ba0' ) echo '	.header h1 i {color:' . get_theme_mod('taglinecolor_setting') . ';}' . "\n";
		if ( get_theme_mod('title_size_setting') != '4.0' ) echo '	.header h1 {font-size:' . get_theme_mod('title_size_setting') . 'em;}' . "\n";  
		if ( get_theme_mod('tagline_rotation_setting') != '-1.00' ) echo '	.header h1 i {-moz-transform:rotate(' . get_theme_mod('tagline_rotation_setting') . 'deg); transform:rotate(' . get_theme_mod('tagline_rotation_setting') . 'deg);}' . "\n";
		if ( (get_theme_mod('bannerimage_setting') != 'purple.png') && (get_theme_mod('bannerimage_setting') != '') ) echo '	.header {background: bottom url(' . get_template_directory_uri() . '/images/' . get_theme_mod('bannerimage_setting') .  ');}'. "\n";
		if ( get_theme_mod('headerspacing_setting') != '18' ) echo '	.spacing {height:' . get_theme_mod('headerspacing_setting') . 'em;}'. "\n";
		if ( get_theme_mod('menu_setting') == 'notitle' ) { echo '	.header {position: fixed;margin-top:0px;}' . "\n" . '	.admin-bar .header {margin-top:28px;}' . "\n" . '.header h1:first-child, .header h1:first-child i,  .header img:first-child {display: none;}' . "\n"; }
		if ( get_theme_mod('menu_setting') == 'bottom' ) { echo '	.header {position: fixed; bottom:0; top:auto;}' . "\n" . '	.header h1:first-child, .header h1:first-child i,  .header img:first-child {display: none;}' . "\n" . '.header li ul {bottom:2.78em; top:auto;}' . "\n";}
        if ( get_theme_mod('border_setting') == 'hidden' ) { echo '	.contents {border:none; box-shadow:0 0 3px #111;}' . "\n";}
        if ( (get_theme_mod('border_setting') != '3px') && (get_theme_mod('border_setting') != 'hidden') ) { echo '	.contents {border-width:' . get_theme_mod('border_setting') . ';}' . "\n";}
        if ( get_theme_mod('bordercolor_setting') != '#4a4646') { echo '	.contents {border-color:' . get_theme_mod('bordercolor_setting') . ';}' . "\n";}
        if ( get_theme_mod('content_bg_setting') != '') { echo '   .contents {background-image:url(' . get_theme_mod('content_bg_setting') . ');}' . "\n";}
    
		
		if ( get_theme_mod('titlefontstyle_setting') != 'Default' ) {
			$q = get_theme_mod('titlefontstyle_setting');
			$q = preg_replace('/[^a-zA-Z0-9]+/', ' ', $q);
		 	echo	"	.header h1 {font-family: '" . $q . "';}" . "\n"; }

		if ( get_theme_mod('taglinefontstyle_setting') != 'Default') {
			$x = get_theme_mod('taglinefontstyle_setting');
			$x = preg_replace('/[^a-zA-Z0-9]+/', ' ', $x);
			echo	"	.header h1 i {font-family: '" . $x . "';}" . "\n"; }


            if ( get_theme_mod('bodyfontstyle_setting') != 'Default' ) {
                $xs = get_theme_mod('bodyfontstyle_setting');
                $xs = preg_replace('/[^a-zA-Z0-9]+/', ' ', $xs);
                echo	"	body {font-family: '" . $xs . "';}" . "\n"; }
    
            if ( get_theme_mod('headerfontstyle_setting') != 'Default' ) {
                $xd = get_theme_mod('headerfontstyle_setting');
                $xd = preg_replace('/[^a-zA-Z0-9]+/', ' ', $xd);
                echo	"	.contents h1, .contents h2, .contents h3, .contents h4, .contents h5, .contents h6, aside h1, aside h2, aside h3, aside h4, aside h5, aside h6 {font-family: '" . $xd . "';}" . "\n"; }
    
            if ( get_theme_mod('linkcolor_setting') != '#0b6492' ) echo '	a {color:' . get_theme_mod('linkcolor_setting') . ';}' . "\n";
            if ( get_theme_mod('linkcolorhover_setting') != '#FFFFFF' ) echo '	a:hover {color:' . get_theme_mod('linkcolorhover_setting') . ';}' . "\n";
            if ( get_theme_mod('link_text_shadow_setting') != '' ) echo '	.contents a {text-shadow:.1em .1em 0 ' . get_theme_mod('link_text_shadow_setting') . ';}' . "\n";
            if ( get_theme_mod('fontcolor_setting') != '#000000' ) echo '	body {color:' . get_theme_mod('fontcolor_setting') . ';}' . "\n";
            if ( get_theme_mod('navcolor_setting') != '#CCCCCC' ) echo '	.header li a {color:' . get_theme_mod('navcolor_setting') . ';}' . "\n";
            if ( get_theme_mod('navcolorhover_setting') != '#0b6492' ) echo '	.header li a:hover {color:' . get_theme_mod('navcolorhover_setting') . ';}' . "\n";
            if ( get_theme_mod('dropcolor_setting') != '#BBBBBB' ) echo '	.header li ul li a {color:' . get_theme_mod('dropcolor_setting') . ';}' . "\n";
            if ( get_theme_mod('dropcolorhover_setting') != '#0b6492' ) echo '	.header li ul li a:hover {color:' . get_theme_mod('dropcolorhover_setting') . ';}' . "\n";
            if ( get_theme_mod('fontsizeadjust_setting') != '1' ) echo '	.contents, aside {font-size:' . get_theme_mod('fontsizeadjust_setting') . 'em;}' . "\n";
            if ( get_theme_mod('removefooter_setting') != 'visible' ) echo '	footer {visibility:' . get_theme_mod('removefooter_setting') . ';}' . "\n";;
            if ( get_theme_mod('header_image_width_setting') != '20' ) {
                $header_margin_percentage = (100 - get_theme_mod('header_image_width_setting')) / 2;
                echo '	.header li.website_logo {margin:0 ' . $header_margin_percentage . '%; width:' . get_theme_mod('header_image_width_setting') . '%;}' . "\n";}
            if ( get_theme_mod('custombanner_setting') != '') echo '	.header {background: bottom url(' . get_theme_mod('custombanner_setting') .  ');}' . "\n";

		echo '</style>' . "\n";
		echo '<!-- End Custom CSS -->' . "\n";
		echo "\n"; }

add_action('wp_head', 'adventure_inline_css', 50);

//	A safe way of adding javascripts to a WordPress generated page
if (!function_exists('adventure_js')) {
	function adventure_js() {
        // JS at the bottom for fast page loading
        wp_enqueue_script('adventure-menu-scrolling', get_template_directory_uri() . '/js/jquery.menu.scrolling.js', array('jquery'), '1.1', true);
        wp_enqueue_script('adventure-main', get_template_directory_uri() . '/js/main.js', array('jquery'), '1.0', true);
        wp_enqueue_script('adventure-doubletaptogo', get_template_directory_uri() . '/js/doubletaptogo.min.js', array('jquery'), '1.0', true); } }

if (!is_admin()) add_action('wp_enqueue_scripts', 'adventure_js');

// Add some CSS so I can Style the Theme Options Page
function adventure_admin_enqueue_scripts( $hook_suffix ) {
	wp_enqueue_style('adventure-theme-options', get_template_directory_uri() . '/theme-options.css', false, '1.0');}

add_action('admin_print_styles-appearance_page_theme_options', 'adventure_admin_enqueue_scripts');

// Create the Theme Information page (Theme Options)
function adventure_theme_options_do_page() { ?>
 
    <div class="cover">

    <ul id="spacing"></ul>

    <div class="contain">
            
        <div id="header">
		
			<div class="themetitle">
				<a href="http://schwarttzy.com/shop/adventureplus/" target="_blank" ><h1><?php $my_theme = wp_get_theme(); echo $my_theme->get( 'Name' ); ?></h1>
				<span>- v<?php $my_theme = wp_get_theme(); echo $my_theme->get( 'Version' ); ?></span></a>
			</div>
            
            
			<div class="upgrade">
                <a href="http://schwarttzy.com/shop/adventureplus/" target="_blank" ><h2><?php _e('Upgrade to Adventure+', 'localize_adventure'); ?></h2></a>
            </div>
		
    	</div>
            
        <ul class="info_bar">
			<li><a href="#description"><?php _e('Description', 'localize_adventure'); ?></a></li>
			<li><a href="#customizing"><?php _e('Customizing', 'localize_adventure'); ?></a></li>
			<li><a href="#features"><?php _e('Features', 'localize_adventure'); ?></a></li>
			<li><a href="#faq"><?php _e('FAQ', 'localize_adventure'); ?></a></li>
			<!-- <li><a href="#screenshots"><?php _e('Screen Shots', 'localize_adventure'); ?></a></li> -->
			<li><a href="#changelog"><?php _e('Changelog', 'localize_adventure'); ?></a></li>
			<li><a href="#support"><?php _e('Support', 'localize_adventure'); ?></a></li>
		</ul>
        
        <div id="main">
            
            <div id="customizing" class="information">
                <h3><?php _e('Customizing', 'localize_adventure'); ?></h3>
                <p><?php _e('Basically all I have right now is <a href="https://www.youtube.com/watch?v=IU__-ipUxxc" target="_blank">this video</a> on YouTube. I know the video is for a different theme, but this will change soon. Also, I would embed the video, but regrettably people wiser than me have said that it will introduce security issues. In the future I plan to add stuff here, but for now I just need to get the theme approved.', 'localize_adventure'); ?></p>
            </div>
            
            <div id="features" class="information">
                <h3><?php _e('Features', 'localize_adventure'); ?></h3>
                <ul>
                    <li><?php _e('100% Responsive WordPress Theme', 'localize_adventure'); ?></li>
                    <li><?php _e('Clean and Beautiful Stylized HTML, CSS, JavaScript', 'localize_adventure'); ?></li>
                    <li><?php _e('Change the site Title and Slogan Colors', 'localize_adventure'); ?></li>
                    <li><?php _e('Upload Your Own Background Image', 'localize_adventure'); ?></li>
                    <li><?php _e('Adjust the opacity of the Content from 0 to 100% in 5% intervails', 'localize_adventure'); ?></li>
                    <li><?php _e('Adjust the opacity of the Sidebar from 0 to 100% in 5% intervails', 'localize_adventure'); ?></li>
                    <li><?php _e('Adjust Color of the Background for Content', 'localize_adventure'); ?></li>
                    <li><?php _e('Adjust Color of the Background for Sidebar', 'localize_adventure'); ?></li>
                    <li><?php _e('Multiple Menu Banner Images to Choose From', 'localize_adventure'); ?></li>
                    <li><?php _e('Control wether or not the "Previous" & "Next" shows', 'localize_adventure'); ?></li>
                    <li><?php _e('Adjust the spacing between the top of the page and content', 'localize_adventure'); ?></li>
                    <li><?php _e('Comments on Pages only, Posts only, Both, or Nones', 'localize_adventure'); ?></li>
                    <li><?php _e('Featured Background Image unique to a post or page', 'localize_adventure'); ?></li>
                    <li><?php _e("Choose from 100's of Google fonts for the Title and Slogan", 'localize_adventure'); ?></li>
                </ul>
                <p><?php _e('Do not see a feature the theme needs? <a href="http://schwarttzy.com/contact-me/" target="_blank">Contact me</a> about it.', 'localize_adventure'); ?></p>
                <h3><?php _e('Adventure+ Features', 'localize_adventure'); ?></h3>
                <ul>
                    <li><?php _e('Remove the border or change thickness, color, and more', 'localize_adventure'); ?></li>
                    <li><?php _e('Put what ever text and links you want in the footer', 'localize_adventure'); ?></li>
                    <li><?php _e('Easily remove the footer with the link to my website', 'localize_adventure'); ?></li>
                    <li><?php _e('Favicon on Your Website', 'localize_adventure'); ?></li>
                    <li><?php _e('Change the Hyper Link Color', 'localize_adventure'); ?></li>
                    <li><?php _e('Change the Link Colors in the Menu', 'localize_adventure'); ?></li>
                    <li><?php _e('Change the Font Color in the Content', 'localize_adventure'); ?></li>
                    <li><?php _e('Upload Your Own Logo in either the Header or above Content', 'localize_adventure'); ?></li>
                    <li><?php _e('Upload Your Own Custom Banner Image', 'localize_adventure'); ?></li>
                    <li><?php _e('Upload your own image for the Background', 'localize_adventure'); ?></li>
                    <li><?php _e('Basic Google Meta for Analytics & Webmaster Verification', 'localize_adventure'); ?></li>
                    <li><?php _e('Add Text Shadow to links and non-linked text.', 'localize_adventure'); ?></li>
                    <li><?php _e('More to come!', 'localize_adventure'); ?></li>
                </ul>
            </div>
            
            <div id="faq" class="information">
                <h3><?php _e('FAQ', 'localize_adventure'); ?></h3>
                <p><b><?php _e('How do I remove the "Good Old Fashioned Hand Written code by Eric J. Schwarz"', 'localize_adventure'); ?></b></p>
                <p><?php _e('According to the WordPress.org I am allowed to include one credit link, which you can read about <a href="http://make.wordpress.org/themes/guidelines/guidelines-license-theme-name-credit-links-up-sell-themes/" target="_blank">here</a>. I use this link to spread the word about my coding skills in the hopes I will get some jobs. Anyway, you can dig through the code and remove it by hand but if you upgrade to the lastest version it will come right back. It is not really a big deal to do it by hand each time I release an update. However if you want to support my theme and get the Adventure+ upgrade, its just a simple "On or Off" option in the "Theme Customizer."', 'localize_adventure'); ?></p>
                <p><b><?php _e('More FAQs coming soon!', 'localize_adventure'); ?></b></p>
            </div>
            
            <!--- <div id="screenshots" class="information">
                <h3><?php _e('I will take some screen shots', 'localize_adventure'); ?></h3>
            </div> -->
            
            <div id="changelog" class="information">
                <h3><?php _e('The Changelog Adventure+', 'localize_adventure'); ?></h3>
                <table>
                    <tbody>
                        <tr>
                            <th><?php _e('Version', 'localize_adventure'); ?></th>
                            <th></th>
                        </tr>
                        <tr>
                            <th>26</th>
                            <td><?php _e('Another bug in the code fixed, sorry guys.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>25</th>
                            <td><?php _e('Minor error in the code fixed.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>24</th>
                            <td><?php _e('Fixed the issue with the title being worthless, unless you have an SEO plugin installed. Fixed an issue with the sidebar sliding up on mobile platforms. Also added in instagram as a social icon.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>23</th>
                            <td><?php _e('Added the ablity to change the date format.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>22</th>
                            <td><?php _e('Quick update to help out mobile devices with social icons in the menu. Pintrest and few other social icons coming soon too.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>21</th>
                            <td><?php _e('Quick update to help out mobile devices with the CSS3 class background-size not working on them.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>20</th>
                            <td><?php _e('Fixed issues with the Feature Background Image. Change the site Title and Slogan to use CSS. Options to control the size of the font for the Title and Slogan in the Theme Customizer. The website font scale depending on the size of the screen viewing the website.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>19</th>
                            <td><?php _e('Added an option to control the rotation of the tagline in the header. Also added coded to make drop down menus to work with touch screens devices. Single tag drops the menu, select and of the drop down option or an additional tap activates that link.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>16</th>
                            <td><?php _e('Fixed some activation errors that people were having.', 'localize_adventure'); ?></td>
                        <tr>
                            <th>14</th>
                            <td><?php _e('Changed up the code for Menu Image Header thing so that it would look better and be easier to use. Added text-shadow options so you can choose from more colors. Added options for the border for width, color, and to just remove it. You can upload your own image for the content background now. Added a Font Size adjustment that works in the content and sidebar.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>13</th>
                            <td><?php _e('Never existed, made a mistake and double jumped the version.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>12</th>
                            <td><?php _e('I dont rememeber... sorry.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>11</th>
                            <td><?php _e('Minor update to fix some issues I have found.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>10</th>
                            <td><?php _e('The theme was completely rewritten from the ground up so that the code would be easier to manage, better written, and enable faster updates.', 'localize_adventure'); ?></td>
                        </tr>
                    </tbody>
                </table>
                <h3><?php _e('The Changelog Adventure', 'localize_adventure'); ?></h3>
                <table>
                    <tbody>
                        <tr>
                            <th><?php _e('Version', 'localize_adventure'); ?></th>
                            <th></th>
                        </tr>
                        <tr>
                            <th>4.1</th>
                            <td><?php _e('Added another sidebar location to the theme, Thank tek428 for this option.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.9</th>
                            <td><?php _e('Another bug in the code fixed, unrelated to the Adenture+ bug.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.8</th>
                            <td><?php _e('Minor error in the code fixed.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.7</th>
                            <td><?php _e('Fixed the issue with the title being worthless, unless you have an SEO plugin installed. Fixed an issue with the sidebar sliding up on mobile platforms. Also added in instagram as a social icon.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.6</th>
                            <td><?php _e('Same as Adventure+ Version ', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.5</th>
                            <td><?php _e('Quick update to help out mobile devices with social icons in the menu. Pintrest and few other social icons coming soon too.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.4</th>
                            <td><?php _e('Quick update to help out mobile devices with the CSS3 class background-size not working on them.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.3</th>
                            <td><?php _e('Fixed issues with the Feature Background Image. Change the site Title and Slogan to use CSS. Options to control the size of the font for the Title and Slogan in the Theme Customizer. The website font scale depending on the size of the screen viewing the website.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.1</th>
                            <td><?php _e('Added an option to control the rotation of the tagline in the header. Also added coded to make drop down menus to work with touch screens devices. Single tag drops the menu, select and of the drop down option or an additional tap activates that link.', 'localize_adventure'); ?></td>
                        <tr>
                        <tr>
                            <th>3.0</th>
                            <td><?php _e('Updated Adventure to use the same new code that Adventure Plus has been running. The code is much cleaner and does away with a bunch of poor coding designs the theme had. The chances does away with complex loops that became to difficult do to all the customization options. Added code to fix the issue with the sidebar going to the top on mobile device.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.80</th>
                            <td><?php _e('Updated the theme information page to a new look. Dropped code I am not allowed to have in the theme like redirecting to the Theme Infomation page upon activating the theme, google analytics code. Also fix some odd error with using the sidebar and a run on title 52+ letters in a row.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.70</th>
                            <td><?php _e('Created one of the most demanded feature of all time, custom backgrounds. I call it "Featured Background," because you can now upload or select any image to be a background unique to any page or post. Also fixed an issue with comments displaying ordered numbers.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.60</th>
                            <td><?php _e('Added the new option to have the content on the right and the sidebar on the left.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.4</th>
                            <td><?php _e('Added the ablitity to put soical icon and/or a search bar into the menu.  Fixed the issue with the theme display "and comments are closed." Added Google Analytics and Web Master Tool option because everyone should have it and more control of over the comments display too. The option to choose either display excerpts or the entire content of a post or page. You can choose to display dates on posts.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.3</th>
                            <td><?php _e('Minor update to add few things to the theme along with fixes. The custom CSS generated from the theme customizer should only show if you have changed something in the features. Static pages now will not show the pagination or comments. Include the option to do anything you want with the comments. Added Google Fonts to the Header for the Title and Slogan.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.2</th>
                            <td><?php _e('The update this time around was mainly for Adventure+ but in the process I added in a few more features. I included the option to have the menu lock to the top of the screen or the bottom similar to how the theme use to look. A lot of people asked for the ability to remove the “previous” & “next” links that come after content and I you guys one better. You now have the choice to remove the “previous” & “next” from just posts, just page, or both and you still can have it display the same. The slider and the content portion can now change to any color and adjust the opacity from 0% to 100% in 5% intervals. I also spent some time cleaning and organizing the customizer page, which means it is laid out a bit differently now but it works just the same. You now have the option to adjust the the amount of space fromt he top of the page to the where the content begins. I might have missed a thing or two but future updates should come much sooner with this hurdle cleared.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>2.1</th>
                            <td><?php _e('This is main an update to fix issues that I and others (like you) have found and fixed for the theme. The content no longer shifts to the right after the sidebar and embed video from YouTube and Vimeo are now responsive when embedded, plus some other minor stuff. I have also introduced the ablity change the color of the content of the background of content. In the next update I will include the ablity to change the sidebar.', 'localize_adventure'); ?></td>
                        </tr>
                        <tr>
                            <th>1.8</th>
                            <td><?php _e('The entire code for the WordPress theme "Adventure" has been completely rewritten in Version 1.8 and is a complete re-release of the theme. Not a single shred of code survived, and for good reason. The code was written over 3 years ago, before the HTML5 / CSS3 revolution, and had to be compatible with IE6 back then. Now that its three years later, I am much better at coding and coupled with the progress made with HTML standards, the theme is back. While "Adventure" looks for the most part the same, there is a lot more happening in the code.', 'localize_adventure'); ?></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div id="support" class="information">
                <h3><?php _e('Support Information', 'localize_adventure'); ?></h3>
                <p><?php _e('If you happen to have issues with plugins, writing posts, customizing the theme, and basically anything just shoot me an email on <a href="http://schwarttzy.com/contact-me/" target="_blank">this page</a> I setup for contacting me.', 'localize_adventure'); ?></p>
                <p><?php _e('I have a <a href="https://twitter.com/Schwarttzy" target="_blank">Twitter</a> account, but all I really use it for is posting information on updates to my themes. So if you looking for a new feature, you may be in luck. I am not really sure what to do with Twitter, but I know a lot of people use it.', 'localize_adventure'); ?></p>
                <p><?php _e('Your always welcome to use the "<a href="http://wordpress.org/support/theme/adventure" target="_blank">Support</a>" forums on WordPress.org for any questions or problems, I just do not check it as often because I do not recieve email notifications on new posts or replies.', 'localize_adventure'); ?></p>
            </div>
        
            <div id="description" class="information">
                <h3><?php _e('Description', 'localize_adventure'); ?></h3>
                <p><?php _e('If you are having trouble with using the WordPress Theme', 'localize_adventure'); $my_theme = wp_get_theme(); echo $my_theme->get( 'Name' ); _e('and need some help, <a href="http://schwarttzy.com/contact-me/" target="_blank">contact me</a> about it. But I recommend taking a look at <a href="https://www.youtube.com/watch?v=IU__-ipUxxc" target="_blank">this video</a> before sending me an email. The video is for a different theme, but it will show everything there is to customizing the theme ', 'localize_adventure'); ?>"<?php $my_theme = wp_get_theme(); echo $my_theme->get( 'Name' )?>."</p>
                <p><?php _e('Now that I have covered contacting me and a how to video, I would like to thank you for downloading and installing this theme. I hope that you enjoy it. I also hope that I can continue to create more beautiful themes like this for years to come, but that requires your help. I have created this Theme, and others, free of charge. And while I am not looking to get rich, I really like creating these themes for you guys.', 'localize_adventure'); ?></p>
                <p><?php _e('So if you are interested in supporting my work, I can offer you an <a href="http://schwarttzy.com/shop/adventureplus/" target="_blank" >upgrade to Adventure</a>. I have already included a few more features, some of which I am not allowed include in the free version, and I also offer to write additional code to customize the theme for you. Even if the code will be unique to your website.', 'localize_adventure'); ?></p>
                <p><?php _e('Eric Schwarz<br><a href="http://schwarttzy.com/" targe="_blank">http://schwarttzy.com/</a>', 'localize_adventure'); ?></p>                
            </div>
        
        </div>
            
    </div>
        
  
        
        
    
    <ul id="finishing"></ul>

    
    </div>
<?php }
add_action('admin_menu', 'adventure_theme_options_add_page'); ?>