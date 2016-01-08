    </div>

    <?php if (adventure_is_sidebar_active('adventure_widget')) : if (!dynamic_sidebar('sidebar')) : endif; endif; ?>

    <div class="finishing">

        <?php if (adventure_is_sidebar_active('sidebar-1')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        <?php if (adventure_is_sidebar_active('sidebar-2')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        <?php if (adventure_is_sidebar_active('sidebar-3')) : if (!dynamic_sidebar('Footer Widget 1 of 3')) : ?><?php endif; endif; ?>
        
    </div>
    
</main>    

<!-- Start of WordPress Footer  -->
<?php wp_footer(); ?>
<!-- End of WordPress Footer -->
    
</body>
</html>