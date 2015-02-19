$(document).ready(function() {
    $('#search-box').change(function() {
        doSearch();
    });

    $('#search-button').click(function() {
        doSearch();
    });

    $('.toplevel-view').hover(function() {
        $(this).addClass("background-color-complementary-accent", 300, "easeOutExpo");
    }, function() {
        $(this).removeClass("background-color-complementary-accent", 300, "easeOutExpo");
    });

    $.getJSON('/api/search/stats')
        .done(function (data) {
             setStats(data);
        });

});

function setStats(data){
    try{
        $('#releasedate-stat').text("Last data release on " + data.date);
        $('#studies-stat').text(data.studies + " studies");
        $('#associations-stat').text(data.associations + " SNP-trait associations");
        $('#genomebuild').text("Genome assembly " + data.genebuild);
        $('#catalog-stats').show();
    }
    catch (ex){
        console.log("Failure to process stats " + ex);
    }
}

function doSearch(){
    var searchTerm = $("#search-box").val();
    // redirect to search page
    window.location = "search?query=" + searchTerm;
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
}

