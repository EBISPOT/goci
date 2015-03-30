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

    var shiftWindow = function() { scrollBy(0, -100) };
    window.addEventListener("hashchange", shiftWindow);
    function load() { if (window.location.hash) shiftWindow(); }
});


function doSearch(){
    var searchTerm = $("#search-box").val();

    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);

    // redirect to search page

    if(path.indexOf("docs") != -1 && pagename != "docs"){
        window.location = "../search?query=" + searchTerm;
    }
    else{
        window.location = "search?query=" + searchTerm;
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
}

