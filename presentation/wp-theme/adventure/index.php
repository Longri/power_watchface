<!doctype html>
<html <?php language_attributes(); ?>>
<head>
<meta charset="<?php bloginfo( 'charset' ); ?>" />
<title><?php wp_title('&#124;', true, 'right'); ?><?php bloginfo('name'); ?></title>
<link rel="profile" href="http://gmpg.org/xfn/11" />
<link rel="stylesheet" type="text/css" media="all" href="<?php echo get_stylesheet_uri(); ?>" />
<link rel="pingback" href="<?php bloginfo( 'pingback_url' ); ?>" />

<!-- Begin WordPress Header -->
<?php wp_head(); ?>
<!-- End WordPress Header -->

</head>

<body <?php body_class(); ?>>

<!-- Setting up The Layout of the Webpage -->
<ul id="spacing"></ul>
<ul id="content" <?php if (adventure_is_sidebar_active('widget')) : ?><?php else : ?>class="floatnone" <?php endif; ?>>

<!-- Searching Code -->

<!-- End Search -->
	
<?php if (adventure_is_sidebar_active('widget')) : ?>
<!-- The Sidebar -->
<li id="sidebar"><?php if (!dynamic_sidebar('sidebar')) : ?><?php endif; ?></li>
<!-- End Sidebar -->
<?php else : ?>
<!-- No Sidebar -->
<?php endif; ?>

<?php $semperfi_404 = false;/* A 404 Error check */?>
<?php if (is_search()) : ?>
<!-- Searching Code -->
<?php if (have_posts()) : ?>
<li>
<h4>Your Search "<?php echo get_search_query(); ?>" Returned <?php echo $wp_query->found_posts; ?> Results</h4>
<p>Your search for "<?php echo get_search_query(); ?>" has returned exactly <?php echo $wp_query->found_posts; ?> results, no more and no less. Hopefully what you're looking for will be found just below on this page, but if you're unable find what you are looking for you may need to use the links "Older Search Results" or "Newer Search Results" to navigate through more pages of results for "<?php echo get_search_query(); ?>." Please keep in mind that if not enough results return, the links for "Older Search Results" and "Newer Search Results" may not appear becuase there is nothing more to show.</p>
<ul>
<?php while (have_posts()) : the_post(); ?>
    <li id="post-<?php the_ID(); ?>" <?php post_class(); ?>><a href="<?php the_permalink() ?>" rel="bookmark" title="<?php the_title_attribute(); ?>">
    <span><?php the_time('M') ?><br/><?php the_time('jS') ?></span><?php if ( get_the_title() ) { the_title();} else { echo '(No Title)';} ?>
    </a></li>
<?php endwhile; ?>
</ul>
</li>

<?php else : ?>
<li>
<h2>Your search resulted in nothing being found.</h2>
</li>
<?php endif; ?>
<!-- End Search -->

<?php if (have_posts() != true) : ?>
<!-- 404 Error -->
<li>
	<h4>404 Error</h4>
</li>
<?php $semperfi_404 = true;?>
<!-- End 404 Error -->
<?php endif; ?>

<?php elseif (have_posts()) : ?>
<!-- The Posts or Page -->   
<?php while (have_posts()) : the_post(); ?>
<li id="post-<?php the_ID(); ?>" <?php post_class('content'); ?>>
	<h4><?php if ( is_page()) : else : ?><span><?php the_time('M') ?> <?php the_time('jS') ?></span><?php endif; ?><?php if (is_home() || is_404() || is_category() || is_day() || is_month() || is_year() || is_paged() || is_tag()) : ?><a href="<?php the_permalink() ?>" rel="bookmark" title="<?php the_title_attribute(); ?>"><?php endif; ?><?php if ( get_the_title() ) { the_title();} else { echo '(No Title)';} ?><?php if (is_home() || is_404() || is_category() || is_day() || is_month() || is_year() || is_paged() || is_tag()) : ?></a><?php endif; ?></h4>
	<?php the_content(); ?>
	
    <span class="tag"><?php wp_link_pages(); ?></br><?php if (is_single()) : ?>This entry was posted in <?php the_category(', '); the_tags(' and tagged ', ', ', ''); endif;?> by <?php the_author_posts_link(); ?><?php if ( ( is_page() && !comments_open() ) || is_single() ) : ?> and comments are closed.<?php endif; ?></span>
</li>

<!-- The Comments -->
<?php if (is_search()) : ?>
<?php elseif($semperfi_404) : ?>
<?php elseif ( comments_open() ) : ?>
<?php comments_template( '', true ); ?>
<?php elseif ( ( is_page() && comments_open() ) || is_single() ) : ?>
<?php comments_template( '', true ); ?>
<?php endif; ?>
<!-- End Comments -->

<?php endwhile; ?>

<!-- The Next Link -->
<?php if (is_attachment()) : ?>
<li>
	<h3>
	<span class="left"><?php previous_image_link( false, '&#8249; Previous Image'); ?></span>
	<span class="right"><?php next_image_link( false, 'Next Image &#8250;'); ?></span>
    </h3>
</li>
<?php elseif ( is_home() || is_404() || is_category() || is_day() || is_month() || is_year() || is_paged() || is_tag() ) : ?>
<li>
	<h3>
	<span class="left"><?php next_posts_link('&#8249; Older Posts'); ?></span>
	<span class="right"><?php previous_posts_link('Newer Posts &#8250;'); ?></span>
    </h3>
</li>
<?php elseif ( ( is_page() && get_theme_mod('previousnext_setting') == 'pages' ) || ( is_single() && get_theme_mod('previousnext_setting') == 'posts' ) || ( get_theme_mod('previousnext_setting') == 'both' ) ) : ?>
<li>
	<h3>
    <span class="left"><?php previous_post_link('%link', '&#8249; Older Post'); ?></span>
	<span class="right"><?php next_post_link('%link', 'Newer Post &#8250;'); ?></span>
	</h3>
</li>
<?php endif; ?><!-- End Next Link -->

<?php endif; ?><!-- End Posts or Page -->

<ul id="finishing"></ul>
</ul>

<!-- Longri changed: disable footer
	<div id="footer">
	<p>Good Old Fashioned Hand Written Code by <a href="http://schwarttzy.com/about-2/">Eric J. Schwarz</a> </p>
	</div> 
-->
<!-- Closing the Layout of the Page with a Finishing Touch. -->


<!-- The Navigation Menu -->
<ul id="navi">
	<h1 id="fittext3"><a href="<?php echo home_url(); ?>/"><?php bloginfo('name'); ?></a><i><?php bloginfo('description');?></i></h1>
	<?php if ( has_nav_menu( 'bar' ) ) :  wp_nav_menu( array( 'theme_location' => 'bar', 'depth' => 2 ) ); else : ?>
	<?php wp_list_pages( 'title_li=&depth=2' ); ?>
	<?php endif; ?>
</ul>
<!-- End Navigation Menu -->

<!-- Start of WordPress Footer  -->
<?php wp_footer(); ?>
<!-- End of WordPress Footer -->

</body>
</html>