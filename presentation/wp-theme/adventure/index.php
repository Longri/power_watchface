<?php get_header();

    if (have_posts()) :

        while (have_posts()) : the_post(); ?>

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
				
				if ( get_theme_mod('display_excerpt_setting') != 'on' ) : the_content(); else : the_excerpt(); endif ?>
                
                <span class="tag">
                    
                    <?php wp_link_pages(); ?><br>
                
                    <?php _e('Posted in', 'localize_adventure'); ?> <?php the_category(', '); the_tags( __(' and tagged ', 'localize_adventure'), ', ', '');
                    
                    if (get_theme_mod('author_setting') != 'off') : _e(' by ', 'localize_adventure'); the_author_posts_link(); echo ' '; endif;
                    
                    if (get_theme_mod('comments_setting') != 'none') :
                            
                        if (get_theme_mod('commentsclosed_setting') != 'off') : 
                            
                            _e('with ', 'localize_adventure');  comments_popup_link( __('no comments yet', 'localize_adventure'), __('1 comment', 'localize_adventure'), __('% comments', 'localize_adventure'), 'comments-link', __('comments disabled', 'localize_adventure'));
                                  
                        endif;

                        _e('.', 'localize_adventure');

                    endif; ?>

                </span>

            </div>

            <?php if (!is_home() && (get_theme_mod('comments_setting') != 'none')) :

                comments_template();

            endif;

        endwhile;

    endif; ?>

<?php if ( (get_next_posts_link() != '') || (get_previous_posts_link() != '') ) : ?>

    <div class="contents">   
        <h3>
            <span class="left"><?php next_posts_link( '&#8249; ' . __('Older Posts', 'adventure_localizer')); ?></span>
            <span class="right"><?php previous_posts_link( __('Newer Posts', 'adventure_localizer') . ' &#8250;'); ?></span>
        </h3>
    </div>

<?php endif;

get_footer(); ?>