$(document).ready(function() {
   
	/* Header */
  $('#header').addClass('full');
  switchHeader();
  $(window).scroll(function(){
    $('#header').removeClass('full');
    switchHeader();
  });
   
});

function switchHeader(){
  if (document.documentElement.clientWidth <= 480){
    if ($(window).scrollTop() <= 35){
      $('#header').addClass('full');
    }
  } else {
    if ($(window).scrollTop() <= 180){
      $('#header').addClass('full');
    }
  }

}