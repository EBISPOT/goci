$(document).ready(function() {

    $('#search-box').change(function() {
        doSearch();
    });

    $('#search-button').click(function() {
        doSearch();
    });


    $('#filter-form').submit(function(event) {
        event.preventDefault();
        doFiltering();
    });

    $('#clear-filter').click(function() {
        clearFilters();
    });

    $('.toplevel-view').hover(function() {
        $(this).addClass("background-color-complementary-accent", 300, "easeOutExpo");
    }, function() {
        $(this).removeClass("background-color-complementary-accent", 300, "easeOutExpo");
    });

    $('#studyToggle').click(function() {
        $(this).find('span').toggleClass('glyphicon-chevron-right glyphicon-chevron-down');
    });

    $('#associationToggle').click(function() {
        $(this).find('span').toggleClass('glyphicon-chevron-right glyphicon-chevron-down');
    });
    $('#diseasetraitToggle').click(function() {
        $(this).find('span').toggleClass('glyphicon-chevron-right glyphicon-chevron-down');
    });
    $('#singlenucleotidepolymorphismToggle').click(function() {
        $(this).find('span').toggleClass('glyphicon-chevron-right glyphicon-chevron-down');
    });

    $('.collapse').on('show.bs.collapse', function() {
        $('.collapse.in').collapse('hide');
    });

    if (window.history && window.history.pushState) {
        //window.history.pushState('forward', null, './#forward');
        $(window).on('popstate', function() {
            applyFacet();
        });

    }
});


function doSearch(){
    var searchTerm = $("#search-box").val();
    window.location = "search?query=" + searchTerm;
}

function applyFacet(){
    var searchTerm = $("#search-box").val();
    $('a.list-group-item').removeClass('selected');
    if (window.location.hash) {
        var facet = window.location.hash.substr(1);
        console.log("Searching: " + searchTerm + " with facet: " + facet + "...");

        $('#facet').text(facet);
        $('#' + facet + "-facet").addClass('selected');

        if($('.breadcrumb').children().length == 3){
            var gwas = $('ol.breadcrumb li:last-child');
            gwas.removeClass('active');
            gwas.empty();
            var url = "<a href='search?query=" + searchTerm + "'>" + searchTerm + "</a>";
            gwas.append(url);

            var last = $("<li></li>").attr("class", "active");
            if(facet == "study"){
                last.text("Studies");
            }
            else if(facet == "association"){
                last.text("Associations");
            }
            else if(facet == "diseasetrait"){
                last.text("Catalog traits");
            }
            else if(facet == "singlenucleotidepolymorphism"){
                last.text("SNPs");
            }

            $('.breadcrumb').append(last) ;
        }
        else{
            var last = $('ol.breadcrumb li:last-child');
            last.empty();

            if(facet == "study"){
                last.text("Studies");
            }
            else if(facet == "association"){
                last.text("Associations");
            }
            else if(facet == "diseasetrait"){
                last.text("Catalog traits");
            }
            else if(facet == "singlenucleotidepolymorphism"){
                last.text("SNPs");
            }
        }
        solrFacet(searchTerm, facet);
    }
    else {
        loadResults();
    }
}

function loadResults(){
    if ($('#query').text() != ''){
        // build breadcrumb trail
        $(".breadcrumb").empty();
        var breadcrumbs = $("ol.breadcrumb");
        breadcrumbs.append("<li><a href=\"home\">GWAS</a></li>");
        breadcrumbs.append("<li><a href=\"search\">Search</a></li>");
        breadcrumbs.append("<li class=\"active\">" + $('#query').text() + "</li>");

        solrSearch($('#query').text());
        if (window.location.hash) {
            applyFacet();
        }
        else {
            // no facets to apply, so make sure we are showing all results tables
            clearFacetting();
        }
        $('#lower_container').show();
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

function clearFilters(){
   $('#filter-form').find('input').val('');

    if($('#facet').text() == ''){
        doSearch();
    }
    else{
        applyFacet();
    }
}

