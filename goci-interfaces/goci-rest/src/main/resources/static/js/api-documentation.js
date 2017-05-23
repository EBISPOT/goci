/**
 * Created by dwelter on 24/11/16.
 */


$(document).ready(function() {


    var content = $("#docs-content");

    // read the window location to set the breadcrumb
    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);
    var url_header = "../documents/".concat(pagename).concat(".html #header");
    var url_footer = "../documents/".concat(pagename).concat(".html #footer");
    var url = "../documents/".concat(pagename).concat(".html #content");
    console.log("Documentation should be loaded from " + url + "...");

    $("#docs-header").load (url_header);
    $("#docs-content").load (url);
    $("#docs-footer").load (url_footer);

    var displayName = pagename.replace(/(^| )(\w)/g, function(x) {
        return x.toUpperCase();
    });

    displayName = displayName.replace("-", " ");
    
    $("#current-page").text(displayName);
    console.log("Updated breadcrumb (" + displayName + ")");
    
    console.log("Done!");

});




