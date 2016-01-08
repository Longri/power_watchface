<?php get_header();

while ( have_posts() ) : the_post(); ?>

<div id="post-<?php the_ID(); ?>" <?php post_class('contents'); ?>>

	<?php if ( get_theme_mod('display_post_title_setting') != 'off' ) : ?>

		<h4><?php if ( get_the_title() ) { the_title(); } else { _e('(No Title)', 'localize_adventure'); } ?></h4>

	<?php endif;

	the_content(); ?>
                
	<span class="tag">
                    
		<?php wp_link_pages(); ?><br>
                    
		<?php if (get_theme_mod('author_setting') != 'off') : _e(' by ', 'localize_adventure'); the_author_posts_link(); echo ' '; endif;
                    
			if (get_theme_mod('commentsclosed_setting') != 'off') :
			
				if ((get_theme_mod('comments_setting') != 'none') || (get_theme_mod('comments_setting') != 'single')) : 
				
					if (comments_open()) : 

					_e('with ', 'localize_adventure');  comments_popup_link( __('no comments yet', 'localize_adventure'), __('1 comment', 'localize_adventure'), __('% comments', 'localize_adventure'), 'comments-link', __('comments disabled', 'localize_adventure'));

					_e('.', 'localize_adventure');

				endif;

			endif;

		endif; ?>

	</span>

</div>

<?php if (!is_home() && (get_theme_mod('comments_setting') != 'none') && (get_theme_mod('comments_setting') != 'single')) :

	comments_template();

endif;

endwhile;

get_footer(); ?>