<?php if (have_comments()) :
	
	$comment_count = 0;

	$pings_and_tracks = 0;

	foreach ( get_approved_comments( $id ) as $comment ) {
		
		if ( $comment->comment_type === '' ) : $comment_count++;
		else : $pings_and_tracks++;
		endif; }

	if ($comment_count > 0) : ?>

		<div id="comments" class="contents">
			<h4 class="title"><?php _e('Comments', 'localize_adventure'); ?></h4>
			<ul class="commentlist"><?php wp_list_comments(array('avatar_size' => 100, 'style' => 'li', 'type' => 'comment')); ?></ul>
		</div>

	<?php endif;

	if ($pings_and_tracks > 0) : ?>

		<div class="contents">
			<h4 class="title"><?php _e('Pingbacks &amp; Trackbacks', 'localize_adventure'); ?></h4>
			<ul class="commentlist"><?php wp_list_comments(array('avatar_size' => 100, 'style' => 'li', 'type' => 'pings')); ?></ul>
		</div>

	<?php endif;

    if ( get_comment_pages_count() > 1 && get_option( 'page_comments' ) ) : ?>

    <div class="contents">   
        <h3>
            <span class="left"><?php previous_comments_link( __( '&larr; Older Comments', 'adventure_localizer' ) ); ?></span>
            <span class="right"><?php next_comments_link( __( 'Newer Comments &rarr;', 'adventure_localizer') . ' &#8250;'); ?></span>
        </h3>
    </div>

    <?php endif;
    
endif;
        
if (comments_open()) : ?>
    <div class="contents">
        <?php comment_form(); ?> 
    </div>
<?php endif; ?>