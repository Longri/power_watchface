( function( $ ){
    wp.customize( 'backgroundsize_setting', function( value ) {
        value.bind( function( to ) {
            $('#customize-preview > iframe').contents().find('body').css('background-size',to);
        } );
    } );
} )( jQuery );
( function( $ ){
    wp.customize( 'titlecolor_setting', function( value ) {
        value.bind( function( to ) {
            $('#customize-preview > iframe').contents().find('#header h1 a').css('color',to);
        } );
    } );
} )( jQuery );
( function( $ ){
    wp.customize( 'taglinecolor_setting', function( value ) {
        value.bind( function( to ) {
            $('#customize-preview > iframe').contents().find('#header h1 i').css('color',to);
        } );
    } );
} )( jQuery );