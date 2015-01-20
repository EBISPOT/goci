
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
    var searchTerm = $("#search-box").val();
    window.location = "search?query=" + searchTerm;
}

function loadResults(){
    $('#lower_container').show();
    $('#search-term').text(searchTerm);

    $('#search-box').text(searchTerm);
}

function solrSearch(){
    if ($('#query').text() != ''){

    }
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

