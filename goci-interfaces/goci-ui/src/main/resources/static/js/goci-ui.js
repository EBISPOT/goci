$(document).ready(function() {

    //if(window.location.pathname.indexOf("beta") != -1){
    //    $('#beta-icon').show();
    //}

    
    $("a").tooltip({
        'selector': '',
        'placement': 'top',
        'container':'body'
    });
    
    $('.auto-tooltip').tooltip();
    
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

    var shiftWindow = function() {
        scrollBy(0, -100)
    };
    window.addEventListener("hashchange", shiftWindow);
    function load() {
        if (window.location.hash) {
            shiftWindow();
        }
    }

    if($("#homepageStats")){
        $.getJSON(contextPath+'api/search/stats')
                .done(function(stats) {
                          setStats(stats);
                      });
    }

    $('body').tooltip({
                          selector: '[data-toggle="tooltip"]'
                      });
    $('[data-toggle="tooltip"]').tooltip({
                                             container: 'body'
                                         });

});

function useAutoCompleteInput(){
    if(window.location.pathname.indexOf("/diagram")){
        doFilter();
    }
    else{
        doSearch();
    }
}

function doSearch() {
    var searchTerm = $("#search-box").val();

    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);

    // redirect to search page

    if ((path.indexOf("docs") != -1 && pagename != "docs") || path.indexOf("variant") != -1 || (path.indexOf("downloads") != -1 && pagename != "downloads") || path.indexOf("traits") != -1) {
        window.location = "../search?query=" + searchTerm;
    }
    else {
        window.location = "search?query=" + searchTerm;
    }
}

function toggleSidebar(ts) {
    toggleSidebarFlex(ts,3,1);
    // if ($(ts).hasClass('panel-collapsed')) {
    //     // expand the panel
    //     $(ts).parents('.panel').find('.panel-body').slideDown();
    //     $(ts).removeClass('panel-collapsed');
    //     $(ts).find('span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
    //     $(ts).parents('#filter-bar').removeClass('col-md-1').addClass('col-md-3');
    //     $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-11').addClass('col-md-9');
    //
    // }
    // else {
    //     // collapse the panel
    //     $(ts).parents('.panel').find('.panel-body').slideUp();
    //     $(ts).addClass('panel-collapsed');
    //     $(ts).find('span').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
    //     $(ts).parents('#filter-bar').removeClass('col-md-3').addClass('col-md-1');
    //     $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-9').addClass('col-md-11');
    // }
}

function toggleSidebarFlex(ts,sideColExpended,sideColCollapsed) {
    var resultColEpended=12 - sideColExpended;
    var resultColClp= 12 - sideColCollapsed;

    if ($(ts).hasClass('panel-collapsed')) {
        // expand the panel
        $(ts).parents('.panel').find('.panel-body').slideDown();
        $(ts).removeClass('panel-collapsed');
        $(ts).find('span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        $(ts).parents('#filter-bar').removeClass('col-md-'+sideColCollapsed).addClass('col-md-'+sideColExpended);
        $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-'+resultColClp).addClass('col-md-'+resultColEpended);

    }
    else {
        // collapse the panel
        $(ts).parents('.panel').find('.panel-body').slideUp();
        $(ts).addClass('panel-collapsed');
        $(ts).find('span').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        $(ts).parents('#filter-bar').removeClass('col-md-'+sideColExpended).addClass('col-md-'+sideColCollapsed);
        $(ts).parents('#filter-bar').siblings('#results-area').removeClass('col-md-'+resultColEpended).addClass('col-md-'+resultColClp);
    }
}

function toggleDiv(ts) {
    var divId = '#'+ts;
    var buttonId = '#button-'+ts;
    if ($(divId).hasClass('collapse')) {
        $(divId).removeClass('collapse').addClass('expanded');
        $(divId).show();
        $(buttonId).find('span').removeClass('glyphicon-plus').addClass('glyphicon-minus');
    }
    else {
        // collapse the panel
        $(divId).removeClass('expanded').addClass('collapse');
        $(divId).hide();
        $(buttonId).find('span').removeClass('glyphicon-minus').addClass('glyphicon-plus');
    }
}

function setStats(data) {
    try {
        $('#lastUpdateDate').text(data.date);
        $('#studyCount').text(data.studies);
        $('#associationCount').text(data.associations);
        $('#genomeAssembly').text(data.genebuild);
        $('#dbSNP').text(data.dbsnpbuild);
    }
    catch (ex) {
        console.log("Failure to process build variables " + ex);
    }
}

