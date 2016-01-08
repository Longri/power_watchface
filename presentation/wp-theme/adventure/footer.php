    </div>

    <?php if (adventure_is_sidebar_active('adventure_widget')) : if (!dynamic_sidebar('sidebar')) : endif; endif; ?>

    <div class="finishing">

        <?php if (adventure_is_sidebar_active('sidebar-1')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        <?php if (adventure_is_sidebar_active('sidebar-2')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        <?php if (adventure_is_sidebar_active('sidebar-3')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        
    </div>
    
</main>    

<footer>
    <?php if ( ( get_theme_mod('footer_text_setting') != 'Replace the text in the footer' ) && ( get_theme_mod('footer_text_setting') != '' ) ) : ?>
    
    <p><?php echo get_theme_mod('footer_text_setting') ;?>
    
    <?php else : ?>
    
    <p><?php _e('Good Old Fashioned Hand Written Code by', 'localize_adventure'); ?> <a href="http://schwarttzy.com/about-2/">Eric J. Schwarz</a>
        
    <?php endif; ?><!-- <?php echo get_num_queries(); ?> queries in <?php timer_stop(1); ?> seconds --></p></footer>

<!-- Start of WordPress Footer  -->
<?php wp_footer(); ?>
<!-- End of WordPress Footer -->
    
</body>
</html>