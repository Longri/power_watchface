<?php get_header();

while ( have_posts() ) : the_post(); ?>

<div id="post-<?php the_ID(); ?>" <?php post_class('contents'); ?>>

	<?php if ( get_theme_mod('display_post_title_setting') == 'off' ) : else : ?>

		<h4><?php if ( get_the_title() ) { the_title(); } else { _e('(No Title)', 'localize_adventure'); } ?></h4>

	<?php endif; ?>
            
	<?php if (wp_attachment_is_image($post->id)) {
        $att_image = wp_get_attachment_image_src( $post->id, "full"); ?>
        <p class="attachment">
            <img src="<?php echo $att_image[0];?>" alt="<?php $post->post_excerpt; ?>" title="<?php if ( get_the_title() ) { the_title();} else { _e('(No Title)', 'localize_adventure');} ?>" />
        </p>
    <?php }
    
    if (get_the_title() != get_the_title($post->post_parent) ) : ?>

        <span class="tag">
    
            <?php _e('The image is attached to', 'localize_adventure'); ?> <a href="<?php echo get_permalink($post->post_parent); ?>"><?php echo get_the_title($post->post_parent); ?></a>
    
            <?php if (get_theme_mod('commentsclosed_setting') != 'off') : 
    
                if ((get_theme_mod('comments_setting') != 'none') || (get_theme_mod('comments_setting') != 'page')) : 
       
                    _e('with ', 'localize_adventure');  comments_popup_link( __('no comments yet', 'localize_adventure'), __('1 comment', 'localize_adventure'), __('% comments', 'localize_adventure'), 'comments-link', __('comments disabled', 'localize_adventure'));
    
                endif; ?>.
    
            <?php endif; ?>
    
        </span>
    
    <?php endif; ?>
    
</div>

<div class="contents">
	<h3>
		<span class="left"><?php previous_image_link( false, '&#8249; ' . __(' Previous Image', 'localize_adventure')); ?></span>
		<span class="right"><?php next_image_link( false, __('Next Image', 'localize_adventure') . ' &#8250;'); ?></span>
	</h3>
</div>

<?php if (!is_home() && (get_theme_mod('comments_setting') != 'none')) :

    comments_template();

endif;

endwhile;

get_footer(); ?>