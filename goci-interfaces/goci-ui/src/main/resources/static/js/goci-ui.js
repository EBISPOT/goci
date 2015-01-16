
//function init() {

    $(document).ready(function () {

         ////if(window.location == )
         // alert(window.location.pathname);

        $('#search-box').change(function(){
            doSearch()
        });

          $('#search-button').click(function(){
                  doSearch()

          });
    });

//};

function doSearch(){

    if(window.location.pathname != "/search"){
        window.location.replace("/search");
        $('#lower_container').show();
        $('#search-term').text($('#search-box').val());

    }
    else {
        //alert("Your term is " + $('#search-box').val());
        $('#lower_container').show();
        $('#search-term').text($('#search-box').val());

    }
//trigger the actual search of the solar index here
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

