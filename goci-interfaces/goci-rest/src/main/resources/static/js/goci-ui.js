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

    var shiftWindow = function() {
        scrollBy(0, -100)
    };
    window.addEventListener("hashchange", shiftWindow);
    function load() {
        if (window.location.hash) {
            shiftWindow();
        }
    }
});


