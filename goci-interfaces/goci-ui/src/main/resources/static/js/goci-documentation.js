$(document).ready(function() {
    var content = $("#docs-content");

    // read the window location to set the breadcrumb
    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);
    var url = "content/".concat(pagename).concat("-content.html");
    console.log("Documentation should be loaded from " + url + "...");

    // load the page content
    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
});

var loadDocumentation = function(pagename, content) {
    console.log("Attempting to load documentation...");
    return function(data, textStatus, jqXHR) {
        // set breadcrumb
        var displayName = pagename.replace(/(^| )(\w)/g, function(x) {
            return x.toUpperCase();
        });
        $("#current-page").text(displayName);
        console.log("Updated breadcrumb (" + displayName + ")");
        // load the data content
        console.log("Updating " + content + "...");
        console.log(data);
        content.html(data);
        console.log("Done!");

    }
};