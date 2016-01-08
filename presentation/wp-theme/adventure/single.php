<?php get_header();

while ( have_posts() ) : the_post(); ?>

<div id="post-<?php the_ID(); ?>" <?php post_class('contents'); ?>>

    <?php if ( get_theme_mod('display_post_title_setting') == 'off' ) : else : ?>

                    <h4><?php if (get_theme_mod('display_date_setting') != 'off' ) : ?>
                        <time datetime="<?php the_time('Y-m-d H:i') ?>">
                            <?php if ( get_theme_mod('dateformat_setting') != '' ) : 
                                echo the_time(get_theme_mod('dateformat_setting'));
                            else : the_time('M jS'); endif; ?></time><?php endif; ?>
                        <a href="<?php the_permalink() ?>" rel="bookmark" title="<?php the_title_attribute(); ?>">
                            <?php if ( get_the_title() ) { the_title();} else { _e('(No Title)', 'localize_adventure'); } ?></a></h4>
    <?php endif;

    the_content(); ?>

    <span class="tag">

        <?php wp_link_pages(); ?><br>

        Posted in <?php the_category(', '); the_tags(' and tagged ', ', ', '');
        
        if (get_theme_mod('author_setting') != 'off') : ?> by <?php the_author_posts_link(); echo ' '; endif;
      
        if (get_theme_mod('commentsclosed_setting') != 'off') :
             
            if ((get_theme_mod('comments_setting') != 'none') || (get_theme_mod('comments_setting') != 'page')) : 
           
                _e('with ', 'localize_adventure');  comments_popup_link( __('no comments yet', 'localize_adventure'), __('1 comment', 'localize_adventure'), __('% comments', 'localize_adventure'), 'comments-link', __('comments disabled', 'localize_adventure'));
                                
            endif;

            _e('.', 'localize_adventure');

        endif; ?>

    </span>

</div>

<?php if (!is_home() && (get_theme_mod('comments_setting') != 'none') && (get_theme_mod('comments_setting') != 'page')) :

    comments_template();

endif;

endwhile;

if ((get_theme_mod('previousnext_setting') != 'page') && (get_theme_mod('previousnext_setting') != 'neither')) : ?>

    <div class="contents">
        <h3>
            <span class="left"><?php previous_post_link('%link', '&#8249; ' . __(' Older Post', 'localize_adventure')); ?></span>
            <span class="right"><?php next_post_link('%link', __('Newer Post', 'localize_adventure') . ' &#8250;'); ?></span>
        </h3>
    </div>

<?php endif;

get_footer(); ?>