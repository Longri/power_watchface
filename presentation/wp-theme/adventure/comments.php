<?php if (post_password_required()) { ?>
    <li><h4 class="nocomments"><?php _e('This post is password protected. Enter the password to view any comments.', 'responsive'); ?></h4></li>
    
	<?php return; } ?>
   

<?php if (have_comments()) : ?>
<li>
	<ul class="commentbox">
        <h4 class="title">Comments</h4>
    
        <ol class="commentlist">
            <?php wp_list_comments('avatar_size=100&type=comment'); ?>
        </ol>
	</ul>	
    
    <?php if ( get_comment_pages_count() > 1 && get_option( 'page_comments' ) ) : ?>
        <ul class="starsbar"><h6>
            <span class="left"><?php previous_comments_link(); ?></span>
            &#9733; &#9733; &#9733; &#9733; &#9733;
            <span class="right"><?php next_comments_link(); ?></span>
        </h6></ul> 
    <?php endif; ?>
    


	<?php if (!empty($comments_by_type['pings'])) : ?>
        <ul class="commentbox">
            <h4 class="title">Pings &amp; Trackbacks</h4>
        
            <ol class="commentlist">
                <?php wp_list_comments('type=pings&max_depth=<em>'); ?>
            </ol>
        </ul>
    <?php endif; ?>

</li>
<?php endif; ?>

<?php if (comments_open()) : ?>
<li> 
    <?php $fields = array(
			'author' => '<p class="comment-form-author">' . '<label for="author">' . __('Name','responsive') . '</label> ' . ( $req ? '<span class="required">*</span>' : '' ) .
			'<input id="author" name="author" type="text" value="' . esc_attr($commenter['comment_author']) . '" size="30" /></p>',
			'email' => '<p class="comment-form-email"><label for="email">' . __('E-mail','responsive') . '</label> ' . ( $req ? '<span class="required">*</span>' : '' ) .
			'<input id="email" name="email" type="text" value="' . esc_attr($commenter['comment_author_email']) . '" size="30" /></p>',
			'url' => '<p class="comment-form-url"><label for="url">' . __('Website','responsive') . '</label>' .
			'<input id="url" name="url" type="text" value="' . esc_attr($commenter['comment_author_url']) . '" size="30" /></p>', );

    $defaults = array('fields' => apply_filters('comment_form_default_fields', $fields));

    comment_form($defaults); ?>

</li>
<?php endif; ?>
