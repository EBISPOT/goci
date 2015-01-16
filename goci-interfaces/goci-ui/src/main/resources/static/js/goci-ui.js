
$(document).ready(function(){
  //  $('#search-button').click(search());

    //$('#search-box').submit(search());

});

function search(){
    alert("Your term is " + $('#search-box').val());
    $('#lower_container').show();
    $('#search-term').text($('#search-box').val());
}

function toggleSidebar(ts){
   
        if ($(ts).hasClass('panel-collapsed')) {
            // expand the panel
            $(ts).parents('.panel').find('.panel-body').slideDown();
            $(ts).removeClass('panel-collapsed');
            $(ts).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
            $(ts).parents('#filter-bar').removeClass('col-md-1').addClass('col-md-3');
            $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-11').addClass('col-md-9');

        }
        else {
            // collapse the panel
            $(ts).parents('.panel').find('.panel-body').slideUp();
            $(ts).addClass('panel-collapsed');
            $(ts).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
            $(ts).parents('#filter-bar').removeClass('col-md-3').addClass('col-md-1');
            $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-9').addClass('col-md-11');
        }
} ;


//$(document).ready(function(){
//    $('#search-button').click(function(){
//        alert($('#search-box').val());
//    })
//
//    //
//    //$('#lower_container').show();
//    $('#search-term').text($('#search-box').val())
//
//});
