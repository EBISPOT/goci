
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

        $('#study-facet').click(function(){
            applyFacet("study")
        });

        $('#association-facet').click(function(){
            applyFacet("association")
        });

        $('#diseasetrait-facet').click(function(){
            applyFacet("diseasetrait")
        });

        $('#singlenucleotidepolymorphism-facet').click(function(){
            applyFacet("singenucleotidepolymorphism")
        });

        $('.toplevel-view').hover(function() {
            $(this).addClass("background-color-complementary-accent", 300, "easeOutExpo");
        }, function() {
            $(this).removeClass("background-color-complementary-accent", 300, "easeOutExpo");
        });
    });

//};

function doSearch(){
    var searchTerm = $("#search-box").val();
    window.location = "search?query=" + searchTerm;
}

function applyFacet(facet){
    var searchTerm = $("#search-box").val();
    //solrFacet($('#query').text(), $('#facet').text());
    solrFacet(searchTerm, facet);

    //window.location = "search/"+ facet + "?query=" + searchTerm;
}

function loadResults(){
    if ($('#query').text() != ''){
        if($('#facet').text() != ''){
            //solrFacet($('#query').text(), $('#facet').text());
        }
        else
        {
            solrSearch($('#query').text());
        }
        $('#lower_container').show();

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

