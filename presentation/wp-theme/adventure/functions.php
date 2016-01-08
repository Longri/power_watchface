<?php

// Exit if accessed directly
if ( !defined('ABSPATH')) exit;

?>
<?php

// Global Content Width, Kind of a Joke with this theme, lol
	if (!isset($content_width))
		$content_width = 648;
			
// Ladies, Gentalmen, boys and girls let's start our engines
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
			
add_action('after_setup_theme', 'adventure_setup');

// Get our wp_nav_menu() fallback, wp_page_menu(), to show a home link
function adventure_page_menu_args( $args ) {
	$args['show_home'] = true;
	return $args; }

add_filter( 'wp_page_menu_args', 'adventure_page_menu_args' );

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

add_filter('get_comments_number', 'adventure_comment_count', 0);

/**
 * wp_list_comments() Pings Callback
 * 
 * wp_list_comments() Callback function for 
 * Pings (Trackbacks/Pingbacks)
 */
function adventure_comment_list_pings( $comment ) {
	$GLOBALS['comment'] = $comment; ?>
	<li <?php comment_class(); ?> id="li-comment-<?php comment_ID(); ?>"><?php echo comment_author_link(); ?></li>
<?php }

// Sets the post excerpt length to 250 characters
function adventure_excerpt_length($length) {
    return 250; }

add_filter('excerpt_length', 'adventure_excerpt_length');

// This function adds in code specifically for IE6 to IE9
function adventure_ie_css() {
	echo "\n" . '<!-- IE 6 to 9 CSS Hacking -->' . "\n";
	echo '<!--[if lte IE 8]><style type="text/css">#content li{background: url(' . get_stylesheet_directory_uri() . '/images/75.png);}li#sidebar{background: url(' . get_stylesheet_directory_uri() . '/images/blacktrans.png);}#content li li{background:none;}</style><![endif]-->' . "\n";
	echo '<!--[if lte IE 7]><style type="text/css">#navi li{float:left;width:150px;}</style><![endif]-->' . "\n";
	echo "\n"; }

add_action('wp_head', 'adventure_ie_css');

// This function removes inline styles set by WordPress gallery
function adventure_remove_gallery_css($css) {
    return preg_replace("#<style type='text/css'>(.*?)</style>#s", '', $css); }

add_filter('gallery_style', 'adventure_remove_gallery_css');

// This function removes default styles set by WordPress recent comments widget
function adventure_remove_recent_comments_style() {
	global $wp_widget_factory;
	remove_action( 'wp_head', array( $wp_widget_factory->widgets['WP_Widget_Recent_Comments'], 'recent_comments_style' ) ); }

add_action( 'widgets_init', 'adventure_remove_recent_comments_style' );

// A comment reply
function adventure_enqueue_comment_reply() {
    if ( is_singular() && comments_open() && get_option('thread_comments')) 
            wp_enqueue_script('comment-reply'); }

add_action( 'wp_enqueue_scripts', 'adventure_enqueue_comment_reply' );	

// Wrap Video with a DIV for a CSS Resposive Video
function wrap_embed_with_div($html, $url, $attr) { 
	// YouTube isn't in here because it provides sufficient mark-ups to just use their html elements
	if (preg_match("/vimeo/", $html)) { return '<div class="video-container">' . $html . '</div>'; }
	if (preg_match("/wordpress.tv/", $html)) { return '<div class="video-container">' . $html . '</div>'; } }
	// Don't see your video host in here? Just add it in, make sure you have the forward slash marks

add_filter('embed_oembed_html', 'wrap_embed_with_div', 10, 3);

// Redirect to the theme options Page after theme is activated
if ( is_admin() && isset($_GET['activated'] ) && $pagenow == "themes.php" )
	wp_redirect( 'themes.php?page=theme_options' ); 

// WordPress Widgets start right here.
function adventure_widgets_init() {

	register_sidebars(1, array(
		'name'=>'sidebar',
		'id' => 'widget',
		'description' => 'Widgets in this area will be shown below the the content of every page.',
		'before_widget' => '',
		'after_widget' => '',
		'before_title' => '<h2>',
		'after_title' => '</h2>', )); }
	
add_action('widgets_init', 'adventure_widgets_init');

// Checks if the Widgets are active
function adventure_is_sidebar_active($index) {
	global $wp_registered_sidebars;
	$widgetcolums = wp_get_sidebars_widgets();
	if ($widgetcolums[$index]) {
		return true; }
		return false; }
		
// Load up links in admin bar so theme is edit
function adventure_theme_options_add_page() {
	add_theme_page('Theme Customizer', 'Theme Customizer', 'edit_theme_options', 'customize.php' );
    add_theme_page('Theme Info', 'Theme Info', 'edit_theme_options', 'theme_options', 'adventure_theme_options_do_page');}
	
// Add link to theme options in Admin bar
function adventure_admin_link() {
	global $wp_admin_bar;
	$wp_admin_bar->add_menu( array( 'id' => 'Adventure_Customizer', 'title' => 'Theme Customizer', 'href' => admin_url( 'customize.php' ) ));
	$wp_admin_bar->add_menu( array( 'id' => 'Adventure_Information', 'title' => 'Theme Information', 'href' => admin_url( 'themes.php?page=theme_options' ) )); }

add_action( 'admin_bar_menu', 'adventure_admin_link', 113 );

// Sets up the Customize.php for Adventure (More to come)
function adventure_customize($wp_customize) {

	// Before we begin let's create a textarea input
	class adventure_Customize_Textarea_Control extends WP_Customize_Control {
    
		public $type = 'textarea';
	 
		public function render_content() { ?>
			<label>
			<span class="customize-control-title"><?php echo esc_html( $this->label ); ?></span>
			<textarea rows="1" style="width:100%;" <?php $this->link(); ?>><?php echo esc_textarea( $this->value() ); ?></textarea>
			</label> <?php } }

	// The Standard Sections for Theme Custimizer
	$wp_customize->add_section( 'header_section', array(
        'title'				=> 'Header',
		'priority'			=> 26, ));

	$wp_customize->add_section( 'nav', array(
        'title'				=> 'Menu',
		'priority'			=> 27, ));

	$wp_customize->add_section( 'background_image', array(
        'title'				=> 'Background',
		'priority'			=> 28, ));

	$wp_customize->add_section( 'content_section', array(
        'title'				=> 'Content',
        'priority'			=> 29, ));

	$wp_customize->add_section( 'sidebar_section', array(
        'title'				=> 'Sidebar',
        'priority'			=> 30, ));

	$wp_customize->add_section( 'links_section', array(
        'title'				=> 'Links',
        'priority'			=> 32, ));

	// Remove the Section Colors for the Sake of making Sense
	$wp_customize->remove_section( 'colors');

	// Background needed to be moved to to the Background Section
	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'background_color', array(
		'label'				=> 'Background Color',
		'section'			=> 'background_image', )));

	// Change Site Title Color
	$wp_customize->add_setting( 'titlecolor_setting', array(
		'default'			=> '#eee2d6', ));

	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'titlecolor_control', array(
		'label'				=> 'Site Title Color - #eee2d6',
		'section'			=> 'title_tagline',
		'settings'			=> 'titlecolor_setting', )));

	// Change Tagline Color
	$wp_customize->add_setting( 'taglinecolor_setting', array(
		'default'			=> '#066ba0', ));

	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'taglinecolor_control', array(
		'label'				=> 'Site Title Color - #066ba0',
		'section'			=> 'title_tagline',
		'settings'			=> 'taglinecolor_setting', )));

	// Choose the Different Images for the Banner
	$wp_customize->add_setting('bannerimage_setting', array(
		'default'			=> 'purple.png',
		'capability'		=> 'edit_theme_options',
		'type'				=> 'option', ));

	$wp_customize->add_control('themename_color_scheme', array(
		'label'				=> 'Banner Background',
		'priority'			=> 1,
		'section'			=> 'header_section',
		'settings'			=> 'bannerimage_setting',
		'type'				=> 'radio',
		'choices'			=> array(
			'purple.png'	=> 'Purple (Default)',
			'blue.png'		=> 'Blue',
			'marble.png'	=> 'Marble',
			'green.png'		=> 'Green', ), ));

	// Upload and Customization for the Banner and Header Options
	$wp_customize->add_setting('menu_setting', array(
		'default'			=> 'standard',
		'capability'		=> 'edit_theme_options',
		'type'				=> 'option', ));

	$wp_customize->add_control('menu_control', array(
		'label'				=> 'Menu Display Options',
		'priority'			=> 6,
		'section'			=> 'header_section',
		'settings'			=> 'menu_setting',
		'type'				=> 'radio',
		'choices'			=> array(
			'standard'		=> 'Standard (Default)',
			'notitle'		=> 'No Title',
			'bottom'		=> 'Moves Menu To Bottom', ), ));

	// Adjust the Space Between the Top of the Page and Content
	$wp_customize->add_setting( 'headerspacing_setting', array(
		'default'           => '35%',
		'control'           => 'select',));

	$wp_customize->add_control( 'headerspacing_control', array(
		'label'				=> 'Adjust the Spacing Between Top and Content',
		'priority'			=> 90,
		'section'			=> 'header_section',
		'settings'			=> 'headerspacing_setting',
		'type'				=> 'radio',
		'choices'			=> array(
			'65'			=> '65%',
			'60'			=> '60%',
			'55'			=> '55%',
			'50'			=> '50%',
			'45'			=> '45%',
			'40'			=> '40%',
			'35'			=> '35% Default',
			'30'			=> '30%',
			'25'			=> '25%',
			'20'			=> '20%',
			'15'			=> '15%',
			'10'			=> '10%',
			'5'				=> '5%',), ));

	// Add the option to use the CSS3 property Background-size
	$wp_customize->add_setting( 'backgroundsize_setting', array(
		'default'           => 'auto',
		'control'           => 'select',));

	$wp_customize->add_control( 'backgroundsize_control', array(
		'label'				=> 'Background Size',
		'section'			=> 'background_image',
		'settings'			=> 'backgroundsize_setting',
		'priority'			=> 10,
		'type'				=> 'radio',
		'choices'			=> array(
			'auto'			=> 'Auto (Default)',
			'contain'		=> 'Contain',
			'cover'			=> 'Cover',), ));

	// Change the color of the Content Background
	$wp_customize->add_setting( 'backgroundcolor_setting', array(
		'default'           => '#b4b09d',
		'control'           => 'select',));

	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'backgroundcolor_control', array(
		'label'				=> 'Color of the Content Background',
		'section'			=> 'content_section',
		'settings'			=> 'backgroundcolor_setting', )));

	// Change the opacity of the Content Background
	$wp_customize->add_setting( 'contentbackground_setting', array(
		'default'           => '.80',
		'control'           => 'select',));

	$wp_customize->add_control( 'contentbackground_control', array(
		'label'				=> 'Transparency of Content Background',
		'section'			=> 'content_section',
		'settings'			=> 'contentbackground_setting',
		'type'				=> 'radio',
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

	// Settings for the Previous & Next Post Link
	$wp_customize->add_setting( 'previousnext_setting', array(
		'default'           => 'both',
		'control'           => 'select',));

	$wp_customize->add_control( 'previousnext_control', array(
		'label'				=> 'Previous & Next Links After Content',
		'section'			=> 'content_section',
		'settings'			=> 'previousnext_setting',
		'type'				=> 'radio',
		'choices'			=> array(
			'both'			=> 'Both Pages & Posts',
			'posts'			=> 'Only Posts',
			'pages'			=> 'Only Pages',
			'neither'		=> 'Neither', ), ));

	// Change the color of the Sidebar Background
	$wp_customize->add_setting( 'sidebarcolor_setting', array(
		'default'           => '#000000',
		'control'           => 'select',));

	$wp_customize->add_control( new WP_Customize_Color_Control( $wp_customize, 'sidebarcolor_control', array(
		'label'				=> 'Color of the Sidebar Background',
		'section'			=> 'sidebar_section',
		'settings'			=> 'sidebarcolor_setting', )));

	// Change the opacity of the Sidebar Background
	$wp_customize->add_setting( 'sidebarbackground_setting', array(
		'default'           => '.50',
		'control'           => 'select',));

	$wp_customize->add_control( 'sidebarbackground_control', array(
		'label'				=> 'Transparency of Sidebar Background',
		'section'			=> 'sidebar_section',
		'settings'			=> 'sidebarbackground_setting',
		'type'				=> 'radio',
		'choices'			=> array(
			'1'				=> '100',
			'.95'			=> '95',
			'.90'			=> '90',
			'.85'			=> '85',
			'.75'			=> '75',
			'.70'			=> '70',
			'.65'			=> '65',
			'.60'			=> '60',
			'.55'			=> '55',
			'.50'			=> '50 (Default)',
			'.45'			=> '45',
			'.40'			=> '40',
			'.35'			=> '35',
			'.30'			=> '30',
			'.25'			=> '25',
			'.20'			=> '20',
			'.15'			=> '15',
			'.10'			=> '10',
			'.05'			=> '5',
			'.00'			=> '0',), )); }

add_action('customize_register', 'adventure_customize');

// Preview CSS3 Property Background-size in Customizer
function adventure_customizer_preview() {
	wp_enqueue_script('adventure-customizer', get_template_directory_uri() . '/js/customizer.js', array('jquery'), '1.3', true);}

add_action( 'customize_controls_print_footer_scripts', 'adventure_customizer_preview', 10 );
	
// Inject the Customizer Choices into the Theme
function adventure_inline_css() {
		
		// Convert Content from Hex to RGB
		$hex = str_replace("#", "", get_theme_mod('backgroundcolor_setting'));

		if(strlen($hex) == 3) {
			$r = hexdec(substr($hex,0,1).substr($hex,0,1));
			$g = hexdec(substr($hex,1,1).substr($hex,1,1));
			$b = hexdec(substr($hex,2,1).substr($hex,2,1)); }
		else {
			$r = hexdec(substr($hex,0,2));
			$g = hexdec(substr($hex,2,2));
			$b = hexdec(substr($hex,4,2)); }

		// Convert Sidebar from Hex to RGB
		$hexs = str_replace("#", "", get_theme_mod('sidebarcolor_setting'));

		if(strlen($hexs) == 3) {
			$rs = hexdec(substr($hexs,0,1).substr($hexs,0,1));
			$gs = hexdec(substr($hexs,1,1).substr($hexs,1,1));
			$bs = hexdec(substr($hexs,2,1).substr($hexs,2,1)); }
		else {
			$rs = hexdec(substr($hexs,0,2));
			$gs = hexdec(substr($hexs,2,2));
			$bs = hexdec(substr($hexs,4,2)); }

		echo '<!-- Custom CSS Styles -->' . "\n";
        echo '<style type="text/css" media="screen">' . "\n";
		if ( get_theme_mod('backgroundsize_setting') != 'auto' ) echo '	body {background-size:' . get_theme_mod('backgroundsize_setting') . ';}' . "\n";
		if ( get_theme_mod('fontcolor_setting') != '#000' ) echo '	body {color:'  . get_theme_mod('fontcolor_setting') . ';}' . "\n";
		echo '	#content>li {background: rgba(' . $r . ',' . $g . ', ' . $b . ', ' .  get_theme_mod('contentbackground_setting') .  ');}' . "\n";
		echo '	li#sidebar {background: rgba(' . $rs . ',' . $gs . ', ' . $bs . ', ' .  get_theme_mod('sidebarbackground_setting') .  ');}' . "\n";
		if ( get_theme_mod('titlecolor_setting') != '#eee2d6' ) echo '	#navi h1 a {color:' . get_theme_mod('titlecolor_setting') . ';}' . "\n";
		if ( get_theme_mod('taglinecolor_setting') != '#066ba0' ) echo '	#navi h1 i {color:' . get_theme_mod('taglinecolor_setting') . ';}' . "\n";
		if ( get_option('bannerimage_setting') != 'purple.png' ) echo '	#navi {background: bottom url(' . get_template_directory_uri() . '/images/' . get_option('bannerimage_setting') .  ');}'. "\n";
		if ( get_theme_mod('backgroundsize_setting') != '35' ) echo '	#spacing {height:' . get_theme_mod('headerspacing_setting') . '%;}'. "\n";
		if ( get_option('menu_setting') == 'notitle' ) { echo '	#navi {position: fixed;margin-top:0px;}' . "\n" . '	.admin-bar #navi {margin-top:28px;}' . "\n" . '#navi h1:first-child, #navi h1:first-child i,  #navi img:first-child {display: none;}' . "\n"; }
		if ( get_option('menu_setting') == 'bottom' ) { echo '	#navi {position: fixed; bottom:0; top:auto;}' . "\n" . '	#navi h1:first-child, #navi h1:first-child i,  #navi img:first-child {display: none;}' . "\n" . '#navi li ul {bottom:2.78em; top:auto;}' . "\n";}
		echo '</style>' . "\n";
		echo '<!-- End Custom CSS -->' . "\n";
		echo "\n";

	if (get_option('favicon_setting') != '') {
		echo '<!-- Favicon Code -->' . "\n";
		echo '<link rel="icon" href="' . get_option('favicon_setting') . '" />' . "\n\n";} 
		
	$options = get_theme_mod('typekit_setting');
    if (get_theme_mod( 'typekit_setting' ) != 'For example mine is "jgu6yjc"') {
		echo '<!-- Typekit Kit ID -->' . "\n";
		echo '<script type="text/javascript" src="//use.typekit.net/' . get_theme_mod( 'typekit_setting' ) . '.js"></script>' . "\n";
		echo '<script type="text/javascript">try{Typekit.load();}catch(e){}</script>' . "\n\n";} }

add_action('wp_head', 'adventure_inline_css');

//	A safe way of adding javascripts to a WordPress generated page
if (!function_exists('adventure_js')) {
	function adventure_js() {
			// JS at the bottom for fast page loading
			wp_enqueue_script('adventure-jquery-easing', get_template_directory_uri() . '/js/jquery.easing.js', array('jquery'), '1.3', true);
            wp_enqueue_script('adventure-menu-scrolling', get_template_directory_uri() . '/js/jquery.menu.scrolling.js', array('jquery'), '1', true);
			wp_enqueue_script('adventure-scripts', get_template_directory_uri() . '/js/jquery.fittext.js', array('jquery'), '1.0', true);
			wp_enqueue_script('adventure-fittext', get_template_directory_uri() . '/js/jquery.fittext.sizing.js', array('jquery'), '1', true);  } }

if (!is_admin()) add_action('wp_enqueue_scripts', 'adventure_js');

// Add some CSS so I can Style the Theme Options Page
function adventure_admin_enqueue_scripts( $hook_suffix ) {
	wp_enqueue_style('adventure-theme-options', get_template_directory_uri() . '/theme-options.css', false, '1.0');}

add_action('admin_print_styles-appearance_page_theme_options', 'adventure_admin_enqueue_scripts');
	
// Create the Theme Information page (Theme Options)
function adventure_theme_options_do_page() { ?>
    
    <div class="cover">
    
    <ul id="spacing"></ul>
	<ul id="content">
    
    <li>
    <h4><span>Please Read</br>This Page!</span>Thanks for using Adventure Lite!</h4>
	<p>Thank you for downloading and installing the WordPress Theme "Adventure." I hope that you enjoy it and that I can continue to create these beautiful themes for years to come. But, if you could take a moment and become acutely aware that I have created this Theme and other themes free of charge, and while I'm not looking to get rich, I really like creating these themes for you guys. Which is why I offer additional customization of "Adventure" when you support me and install the "Adventure+" on your WordPress. If you're interested in supporting my work, or need some of the addition features in "Adventure+" head on over to <a href="http://schwarttzy.com/shop/adventureplus/">this page</a>.</p>
    <p>Incase you happen to have any issues, questions, comments, or a requests for features with "Adventure Lite," you can contact me through E-Mail with the form on <a href="http://schwarttzy.com/contact-me/">this page</a>.</p>
    <p>Thank you again,</br><a href="http://schwarttzy.com/about-2/">Eric J. Schwarz</a></p>
    </li>
    
    <li>
    <h4>Customizing Adventure</h4>
    <p><span class='embed-youtube' style='text-align:center; display: block;'><iframe class='youtube-player' type='text/html' width='671' height='396' src='http://www.youtube.com/embed/IU__-ipUxxc?version=3&#038;rel=1&#038;fs=1&#038;showsearch=0&#038;showinfo=1&#038;iv_load_policy=1&#038;wmode=transparent' frameborder='0'></iframe></span>
    This video is out of date only because it doesn't show all the features</p>
    <h3 class="title">Features</h3>
    <table>
        <tbody>
        <tr>
        <th class="justify">Adventure Theme Features</th>
        <th>Adventure</th>
        <th>Adventure+</th>
        </tr>
        <tr>
        <td class="justify">100% Responsive WordPress Theme</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Clean and Beautiful Stylized HTML, CSS, JavaScript</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Change the site Title and Slogan Colors</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Upload Your Own Background Image</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Adjust the opacity of the Content from 0 to 100% in 5% intervails</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Adjust the opacity of the Sidebar from 0 to 100% in 5% intervails</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <td class="justify">Adjust Color of the Background for Content</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <td class="justify">Adjust Color of the Background for Sidebar</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Multiple Menu Banner Images to Choose</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Control wether or not the "Previous" & "Next" shows</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <td class="justify">Adjust the spacing between the top of the page and content</td>
        <td>&#9733;</td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Upload your own image for the Background</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Upload Your Own Custom Banner Image</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Upload Your Own Logo in either the Header or above Content</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Change the Font Color in the Content</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
		<tr>
        <td class="justify">Change the Link Colors in the Menu</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Choose you own Hyper Link Color</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Favicon on Your Website</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">The Ability to use Custom Fonts from Typekit</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        <tr>
        <td class="justify">Remove my Mark from the Footer</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
		<tr>
        <td class="justify">Personal Support on Technical Issues You May Run Into</td>
        <td></td>
        <td>&#9733;</td>
        </tr>
        </tbody>
	</table>
    <p>Don't see a feature that you want, maybe theres plugin that doesn't work right, <a href="http://schwarttzy.com/contact-me/">send me an Email about it</a>.</p>
	</li>
    
    <li>
    <h4>Adventure - Version Information</h4>
    <table>
        <tbody>
        <tr>
        <th>Version</th>
        <th class="justify"></th>
        </tr>
        <tr>
        <th>2.2</th>
        <td class="justify">The update this time around was mainly for Adventure+ but in the process I added in a few more features. I included the option to have the menu lock to the top of the screen or the bottom similar to how the theme use to look. A lot of people asked for the ability to remove the “previous” & “next” links that come after content and I you guys one better. You now have the choice to remove the “previous” & “next” from just posts, just page, or both and you still can have it display the same. The slider and the content portion can now change to any color and adjust the opacity from 0% to 100% in 5% intervals. I also spent some time cleaning and organizing the customizer page, which means it is laid out a bit differently now but it works just the same. You now have the option to adjust the the amount of space fromt he top of the page to the where the content begins. I might have missed a thing or two but future updates should come much sooner with this hurdle cleared.</td>
        </tr>
        <tr>
        <th>2.1</th>
        <td class="justify">This is main an update to fix issues that I and others (like you) have found and fixed for the theme. The content no longer shifts to the right after the sidebar and embed video from YouTube and Vimeo are now responsive when embedded, plus some other minor stuff. I have also introduced the ablity change the color of the content of the background of content. In the next update I will include the ablity to change the sidebar.</td>
        </tr>
        <tr>
        <th>1.8</th>
        <td class="justify">The entire code for the WordPress theme "Adventure" has been completely rewritten in Version 1.8 and is a complete re-release of the theme. Not a single shred of code survived, and for good reason. The code was written over 3 years ago, before the HTML5 / CSS3 revolution, and had to be compatible with IE6 back then. Now that its three years later, I'm much better at coding and coupled with the progress made with HTML standards, the theme is back. While "Adventure" looks for the most part the same, there is a lot more happening in the code.</td>
        </tr>
        </tbody>
	</table>
    </li>
    
    <li>
    <h4>Adventure+ Version Information</h4>
    <table>
        <tbody>
        <tr>
        <th>Version</th>
        <th class="justify"></th>
        </tr>
        <tr>
        <th>4</th>
        <td class="justify">Major backend update which is mainly for Adventure+ to implement a bunch of features, in particular the Logo that I have received so many emails for. If you have any trouble using the logo for your website send me an email about it and I’ll take a look, this isn’t exact science  and I might need to adjust the code some. I included the option to have the menu lock to the top of the screen or the bottom similar to how the theme use to look. I add in the ability to adjust the spacing from the top of the page to where the content and sidebar begins. You can also change the color of the font in the content, but it will receive more work in a future update. A lot of people asked for the ability to remove the “previous” & “next” links that come after content and I you guys one better. You now have the choice to remove the “previous” & “next” from just posts, just page, or both and you still can have it display the same. The slider and the content portion can now change to any color and adjust the opacity from 0% to 100% in 5% intervals. I also spent some time cleaning and organizing the customizer page, which means it is laid out a bit differently now but it works just the same. You now have the option to adjust the the amount of space fromt he top of the page to the where the content begins. I might have missed a thing or two but future updates should come much sooner with this hurdle cleared.</td>
        </tr>
        <tr>
        <th>3</th>
        <td class="justify">Nothing extra, just the same great code that Adventure 1.9 recieved in the latest update to the themes.</td>
        </tr>
        <tr>
        <th>2</th>
        <td class="justify">Since completely rewriting all the code for Adventure, and because Adventure+ is dependant and Adventure, I have designated that version 2 of Adventure+ is considered the initial re-release.</td>
        </tr>
        </tbody>
	</table>
	</li>
    
    <li>
    <h4>About the Theme Adventure</h4>
	<p>Inspired by a certain GPS Manufacture, I designed this theme to feel like you're looking through a window out into the wilderness and hopefully inspire you to explore. I'm constantly adding new features to this theme to allow you to personalize it to your own needs. If you want a new feature or just want to be able to change something just ask me and I would be happy to add it for you. I would like to thank you for your support, visit the Theme URI for the update history, and Enjoy!</p>
    </li>
    
    <ul id="finishing"></ul>
    </ul>
    
    </div>
<?php }
add_action('admin_menu', 'adventure_theme_options_add_page'); ?>