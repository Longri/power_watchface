$(document).ready(function() {

  $(window).scroll(function(){
    $('#navi').addClass('small');
    switchHeader();
  });
   
});

function switchHeader(){
  if (document.documentElement.clientWidth <= 400){
    if ($(window).scrollTop() <= 65){
      $('#navi').removeClass('small');
    }
  }
  else if (document.documentElement.clientWidth <= 600){
    if ($(window).scrollTop() <= 90){
      $('#navi').removeClass('small');
    }
  }
  else if (document.documentElement.clientWidth <= 900){
    if ($(window).scrollTop() <= 118){
      $('#navi').removeClass('small');
    }
  }
  else {
    if ($(window).scrollTop() <= 140){
      $('#navi').removeClass('small');
    }
  }

}