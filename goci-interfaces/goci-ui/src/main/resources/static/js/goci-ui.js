
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
    if ($('#query').text() != ''){
        solrSearch($('#query').text());
        $('#lower_container').show();
        //$('#search-term').text($('#query').text());

        if($('.breadcrumb').children().length == 2){
            var gwas = $('ol.breadcrumb li:last-child');
            gwas.removeClass('active');
            gwas.empty();
            gwas.append("<a href='search'>Search</a>");

            $('.breadcrumb').append($("<li></li>").attr("class", "active").text($('#query').text())) ;
        }





    }
};

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
};

