/**
 * Created by dwelter on 24/11/16.
 */


$(document).ready(function() {

    // read the window location to set the breadcrumb
    var path = window.location.pathname;
    var pagename = path.substr(path.lastIndexOf('/') + 1);
    if (pagename == 'docs') {
        pagename = "/index";
    }
    //var url_header = "../documents/".concat(pagename).concat(".html #header");
    //var url_footer = "../documents/".concat(pagename).concat(".html #footer");
    var url = "../documents/".concat(pagename).concat(".html #content");
    //console.log("Documentation should be loaded from " + url + "...");

    //$("#docs-header").load (url_header);
    $("#docs-content").load (url);
    //$("#docs-footer").load (url_footer);

    //if (pagename == 'about') {
    //    $('#local-nav-about').addClass('active');
    //} else {
    //    $('#local-nav-docs').addClass('active');
    //
    //}

    // load the page content
//    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
});


//$(document).ready(function() {
//    var content = $("#docs-content");
//
//    // read the window location to set the breadcrumb
//    var path = window.location.pathname;
//    var pagename = path.substr(path.lastIndexOf('/') + 1);
//    var url = "content/".concat(pagename).concat("-content.html");
//    console.log("Documentation should be loaded from " + url + "...");
//
//    // load the page content
//    $.get(url, loadDocumentation(pagename, content)).fail(console.log("Failed to get content from " + url));
//});