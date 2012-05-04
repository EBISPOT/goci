var enableDebugging = true;

var currentScale = 0;
var scalingFactor = 1.0;
var dragOffsetX = 0;
var dragOffsetY = 0;

var renderingComplete = false;

function init() {
    $(document).ready(function() {
        if (enableDebugging) {
            $("#logitem").show();
            $("#logtab").show();
        }
        else {
            $("#logitem").hide();
            $("#logtab").hide();
        }

        log("Initializing pussycat UI...");
        // create tabs
        $("#browsertabs").tabs({
                                   show:tabShow,
                                   selected:1
                               });

        // resize the page so it fills window
        resizePage();
        // register resize handler so that the page resizes when the window size changes
        $(window).resize(function() {
            resizePage();
        });

        $("#retrybutton").button();
        $("#retrybutton").click(renderDiagram);

        // bind mousewheel event handler
        $('#diagramareacontent').mousewheel(function(event, delta) {
            if (delta > 0) {
                currentScale++;
                zoomIn();
            }
            else if (delta < 0) {
                currentScale--;
                zoomOut();
            }
        });

        // bind drag handler
        $('#diagramareacontent')
                .drag(function(ev, dd) {
                          $("#xOffset").html(dd.deltaX);
                          $("#yOffset").html(dd.deltaY);
                          pan(dd.deltaX, dd.deltaY);
                      }, {relative:true})
                .drag("end", function(ev, dd) {
                          updateOffset();
                      }
        );
    });
    log("Pussycat UI initialized OK");
}

function resizePage() {
    // get sizes of borders around body and page_wrapper element
    var bodyMargin = parseInt($('body').css('margin-top')) + parseInt($('body').css('margin-bottom'));
    var pageMargin = parseInt($("#page_wrapper").css('margin-top')) + parseInt($("#page_wrapper").css('margin-bottom'));
    var pageBorder = parseInt($("#page_wrapper").css('border-top-width')) +
            parseInt($("#page_wrapper").css('border-bottom-width'));
    var offset = bodyMargin + pageMargin + pageBorder;

    // update the size of the page_wrapper to fill document
    $("#page_wrapper").height($(window).height() - offset);

    // update the size of divtable to fill the available space
    $("#divtable").height($("#page_wrapper").height() - $("#divtable").offset().top);

    // update the size of tabs to fill the available space
    $("#tabs").height($("#page_wrapper").height() - $("#tabs").offset().top);

    // update the size of tabcontent to match the size of max tabs child
    var maxPadding = 0;
    $('.tabcontent').each(function(i) {
        var padding = parseFloat($(this).css("padding-top")) + parseFloat($(this).css("padding-bottom"));
        if (padding > maxPadding) {
            maxPadding = padding;
        }
    });
    $(".tabcontent").height($("#tabs").height() - maxPadding);

    // update the diagramarea to fill the available space
    $("#diagramarea").height($("#diagramtab").height() -
                                     ($("#diagramarea").position().top - $("#diagramtab").position().top));
    log("Window resized: new size = " + $("#page_wrapper").height() + ", " + $("#page_wrapper").width());
}

function tabShow(event, ui) {
    if (ui.panel.id == "diagramtab") {
        renderDiagram();
    }
}

function renderDiagram() {
    if (!renderingComplete) {
        // call to api/views/gwasdiagram to get required svg
        log("Rendering GWAS diagram - calling api/views/gwasdiagram...");
        $.ajax({
                   url:'api/views/gwasdiagram',
                   dataType:'html',
                   success:insertSVG,
                   error:serverCommunicationFail
               });
    }
    else {
        log("Rendering already complete, update request not sent");
    }
}

function insertSVG(svg) {
    // update diagramarea div with returned SVG
    log("Obtained SVG from server OK, rendering...");
    $("#diagramareaerror").css({"display":"none"});
    $("#diagramareacontent").html(svg);
    renderingComplete = true;
    log("Diagram area div updated with SVG content OK");
}

function serverCommunicationFail(jqXHR, textStatus, errorThrown) {
    // show diagram area error div
    $("#diagramareaerror").css({"display":"block"});
    $("#diagramareaerrortext").html(jqXHR.responseText);
    log("Failed to acquire SVG from server - " + jqXHR.responseText);
}

function zoomIn() {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    // get width and height
    var width = elements[2];
    var height = elements[3];
    // update scaling factor
    scalingFactor = scalingFactor * 0.95;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 0.95
    var newWidth = width * 0.95;
    var newHeight = height * 0.95;
    var newViewBox = elements[0] + " " + elements[1] + " " + newWidth + " " + newHeight;
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
    log("Zoom in over SVG event.  New zoom level: " + scalingFactor + ", viewBox now set to " + newViewBox);
}

function zoomOut() {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    // get width and height
    var width = elements[2];
    var height = elements[3];
    // update scaling factor
    scalingFactor = scalingFactor * 1.05;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 1.05
    var newWidth = width * 1.05;
    var newHeight = height * 1.05;
    var newViewBox = elements[0] + " " + elements[1] + " " + newWidth + " " + newHeight;
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
    log("Zoom out over SVG event.  New zoom level: " + scalingFactor + ", viewBox now set to " + newViewBox);
}

function pan(deltaX, deltaY) {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    var newX = (parseFloat(-deltaX) * scalingFactor) + parseFloat(dragOffsetX);
    var newY = (parseFloat(-deltaY) * scalingFactor) + parseFloat(dragOffsetY);
    $("#xOffsetSVG").html(newX);
    $("#yOffsetSVG").html(newY);
    // get width and height
    var newViewBox = newX + " " + newY + " " + elements[2] + " " + elements[3];
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
    log("Pan over SVG event.  ViewBox now set to " + newViewBox);
}

function updateOffset() {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    dragOffsetX = elements[0];
    dragOffsetY = elements[1];
}

function log(msg) {
    $('#log').append('<div class=\"logmessage\">' + msg + '</div>');
}
