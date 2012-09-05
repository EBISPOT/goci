// FINAL VARIABLES - tweak these to adjust standard configuration
var enableDebugging = false;
var gwasLatest = "images/timeseries/gwas-2012-06.png";

var enableFiltering = true;
var enableSVG = true;

var maxScale = 100;
var currentScale = maxScale / 2;
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

        // detect browser version, disable features if required
        log("Browser detection says: " + JSON.stringify($.browser));
        if ($.browser.mozilla) {
            enableFiltering = false;
            var mozVersion = parseInt($.browser.version);
            if (mozVersion < 3) {
                enableSVG = false;
            }
        }

        if ($.browser.msie) {
            enableFiltering = false;
            var ieVersion = parseInt($.browser.version);
            if (ieVersion < 9) {
                enableSVG = false;
            }
        }

        log("Initializing pussycat UI...");
        // create tabs
        $("#browsertabs").tabs({
                                   show:tabShow
                               });

        $("#browsertabs ul").localScroll({
                                             target:"#browsertabs",
                                             duration:0,
                                             hash:true
                                         });

        // create slides for timeseries
        $('#timeseriestab').slides({
                                       preload:true,
                                       play:2000,
                                       pause:2500,
                                       hoverPause:true,
                                       effect:'fade',
                                       crossfade:true
                                   });

        // create slides for filtered views
        $('#filteredtab').slides({
                                       preload:true,
                                       play:2000,
                                       pause:2500,
                                       hoverPause:true,
                                       effect:'fade',
                                       crossfade:true
                                   });

        $("#clearbutton").button();
        $("#clearbutton").click(showAllTraits);

        $("#legendbutton").button({icons:{primary:'ui-icon-newwin'}});
        $("#legendbutton").click(toggleLegend);

        $("#retrybutton").button();
        $("#retrybutton").click(renderDiagram);

        // bind mousemove handler
        $(document).mousemove(function(ev) {
            $('#tooltip').css({"left":ev.pageX + 20, "top":ev.pageY});
        });

        if (enableSVG) {
            // bind mousewheel event handler
            $('#diagramareacontent').mousewheel(function(ev, delta) {
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
        }

        // switch on all containers
        $(".container").show();
        log("Pussycat display enabled");

        // initialize slider
        var navbarPos = $("#diagramarea").offset().top + 25;
        $(".navbar").css({"top":navbarPos});
        log("Setting navigation bar offset to " + navbarPos);
        $("#zoom").slider({
                              orientation:"vertical",
                              max:maxScale,
                              value:currentScale,
                              slide:function(event, ui) {
                                  slideZoom(ui.value);
                              }
                          });
        $("#zoomInButton").button();
        $("#zoomInButton").click(function() {
            currentScale++;
            zoomIn();
        });
        $("#zoomOutButton").button();
        $("#zoomOutButton").click(function() {
            currentScale--;
            zoomOut();
        });
        log("Initialized zoom sidebar OK");
    });
    log("Pussycat UI features initialized OK");

    // resize the page so it fills window
    resizeDisplay();
    // register resize handler so that the page resizes when the window size changes
    $(window).resize(function() {
        resizeDisplay();
    });
    log("Pussycat display resized to fit window");

    if (!enableFiltering) {
        if (!enableSVG) {
            disableInteractiveFeatures();
        }
        else {
            disableFilteringFeatures();
        }
    }
}

function disableFilteringFeatures() {
    $("#filtering").empty("");
    $("#filtering").html(
            "<div id=\"notification\" style=\"float: left\">" +
                    "<span class=\"ui-icon ui-icon-alert\"  style=\"float: left; margin-right: .3em;\" />" +
                    "<span id=\"message\">" +
                    "The browser you are using does not support some " +
                    "<a href='#' onclick='$(\"#browsertabs\").tabs(\"select\", 3)'>features</a>." +
                    "You will still be able to explore traits with mouse-over and zoom in and out on the diagram, " +
                    "but filtering is disabled.  " +
                    "This website works best when viewed in <a href='http://www.google.com/chrome'>Chrome</a> " +
                    "or <a href='http://www.apple.com/safari/'>Safari</a>." +
                    "</span>" +
                    "</div>");
    highlightMessage();
}

function disableInteractiveFeatures() {
    $("#filtering").empty("");
    $("#filtering").html(
            "<div id=\"notification\" style=\"float: left\">" +
                    "<span class=\"ui-icon ui-icon-alert\"  style=\"float: left; margin-right: .3em;\" />" +
                    "<span id=\"message\">" +
                    "The browser you are using does not support SVG, which is used to display the diagram. " +
                    "Interactive diagram <a href='#' onclick='$(\"#browsertabs\").tabs(\"select\", 3)'>features</a>, " +
                    "like mouse-over, zooming and filtering, " +
                    "will not be available." +
                    "This website works best when viewed in <a href='http://www.google.com/chrome'>Chrome</a> " +
                    "or <a href='http://www.apple.com/safari/'>Safari</a>." +
                    "</span>" +
                    "</div>");
    highlightMessage();
}

function highlightMessage() {
    $("#notification")
            .addClass("ui-state-error ui-corner-all");

    $("#message")
            .stop()
            .css("background-color", "#CD0A0A")
            .animate({backgroundColor:"#FEF1EC"}, 750);
}

function resizeDisplay() {
    if (enableSVG) {
        // get sizes of borders around body and page_wrapper element
        var bodyMargin = parseInt($('body').css('margin-top')) + parseInt($('body').css('margin-bottom'));
        var pageMargin = parseInt($("#page_wrapper").css('margin-top')) +
                parseInt($("#page_wrapper").css('margin-bottom'));
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
        var availableHeight = $("#diagramtab").height() -
                ($("#diagramarea").position().top - $("#diagramtab").position().top);
        $("#diagramarea").height(availableHeight);
        log("Window resized: new size = " + $("#page_wrapper").height() + ", " + $("#page_wrapper").width());

        // resize the navbar to match
        $(".navbar").height(availableHeight - 50);
        log("navbar resized: new size = " + $(".navbar").height());

        // update the svg size to fill the space available in the diagram area
        if (enableSVG) {
            try {
                var width = $("#diagramarea").width();
                var height = $("#diagramarea").height();
                $("#goci-svg").attr("width", width);
                $("#goci-svg").attr("height", height);
//                // update the svg viewBox to match width and height adjusting for scaling
//                var newWidth = width * scalingFactor;
//                var newHeight = height * scalingFactor;
//                var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
//                var elements = viewBox.split(' ');
//                var newViewBox = elements[0] + " " + elements[1] + " " + newWidth + " " + newHeight;
//                document.getElementById('goci-svg').setAttribute("viewBox", newViewBox); // this uses server SVG size.  If commented out, default view is zoomed
//                log("Adjusted SVG dimensions - SVG now width = " + width + ", height = " + height + ", viewBox = " +
//                            newViewBox);
                log("Adjusted SVG dimensions - SVG now width = " + width + ", height = " + height);
            }
            catch (ex) {
                log("Failed to adjust SVG dimensions: " + ex);
            }
        }
    }
    else {
        // if no SVG enabled, there is no need for any dynamic resize - all content is static
        // so don't even bother attempting a resize
    }
}

function tabShow(event, ui) {
    if (ui.panel.id == "diagramtab") {
        $(".navbar").show();
        renderDiagram();
    }
    else {
        $(".navbar").hide();
    }
}

function renderDiagram() {
    if (enableSVG) {
        if (!renderingComplete) {
            log("Hiding navbar...");
            $(".navbar").hide();
            // call to api/views/gwasdiagram to get required svg
            log("Rendering GWAS diagram - calling api/views/gwasdiagram...");
            $.ajax({
                       url:'api/views/gwasdiagram',
                       dataType:'html',
                       beforeSend:showSVGLoadWhirly,
                       success:insertSVG,
                       error:serverCommunicationFail
                   });
        }
        else {
            log("Rendering already complete, update request not sent");
        }
    }
    else {
        log("Browser doesn't support SVG rendering, showing static image only");
        insertPNG();
    }
}

function showSVGLoadWhirly() {
    $("#diagramareacontent").html(
            "<div id=\"svgload\" style=\"text-align: center;\">" +
                    "<p>Getting GWAS Diagram from server...</p>" +
                    "<img src=\"images/svg-load.gif\" alt=\"Please wait - diagram loading\"/><br/>" +
                    "<p><a href=\"images/GWASdiagram.png\">" +
                    "A static version of the diagram in PNG format can be found here" +
                    "</a>" +
                    "</p>" +
                    "</div>");
}

function insertSVG(svg) {
    // update diagramarea div with returned SVG
    log("Obtained SVG from server OK, rendering...");
    $("#diagramareaerror").css({"display":"none"});
    $("#diagramareacontent").html(svg);
    log("Displaying navbar...");
    $(".navbar").show();
    resizeDisplay();
    renderingComplete = true;
    log("Diagram area div updated with SVG content OK");
}

function insertPNG() {
    // update diagramarea div with stock PNG
    $("#diagramareaerror").css({"display":"none"});
    $("#diagramareacontent").html("<img src=\"" + gwasLatest + "\" alt=\"GWAS Diagram, static image\"/>");
    log("Hiding navbar...");
    $(".navbar").hide();
    resizeDisplay();
    log("Enabling scrolling...");
    $("#diagramarea").css("overflow", "auto");
    renderingComplete = true;
    log("Diagram area div updated with PNG image OK");
}

function serverCommunicationFail(jqXHR, textStatus, errorThrown) {
    // show diagram area error div
    $("#diagramareacontent").html("");
    $("#diagramareaerror").css({"display":"block"});
    $("#diagramareaerrortext").html(jqXHR.responseText);
    log("Failed to acquire SVG from server - " + jqXHR.responseText);
}

function showTooltip(tooltipText) {
    $("#tooltip-text").html(tooltipText);
    $("#tooltip").show();
}

function hideTooltip() {
    $("#tooltip").hide();
}

function filterTraits(traitName) {
    hideAllTraits();

    // expand query to get all filtered sets
    $.getJSON('api/views/filter/' + traitName, function(data) {
        $.each(data, function(index, val) {
            try {
                var trait = val.replace(":", "\\:");
                log("Showing trait '" + trait + "' element");
                $("." + trait).attr("mask", "");
            }
            catch (ex) {
                log("Failed to show element '" + val + "'");
            }
        });
        log("All filtered traits should now be shown");
    });
}

function showAllTraits() {
    log("Showing all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "");
}

function hideAllTraits() {
    log("Hiding all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "url(#traitMask)");
}

function doFilterOnEnter(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        log("Detected filter keypress - " + $("#trait-filter").val());
        filterTraits($("#trait-filter").val());
    }
}

function toggleLegend() {
    if ($(".legend").css("display") == "none") {
        // show legend
        $(".legend").show('fold');
        // update text
        $("#legendbutton .ui-button-text").html("Hide Legend");
    }
    else {
        // hide legend
        $(".legend").hide('fold');
        // update text
        $("#legendbutton .ui-button-text").html("Show Legend");
    }
}

function slideZoom(newScale) {
    if (currentScale < 0) {
        // try and recover from excessive zooming out!
        log("Attempting to recover from zooming waaaay out - current scale is " + currentScale);
        currentScale++;
        zoomIn();
        slideZoom(newScale);
    }
    else if (currentScale > maxScale) {
        // try and recover from excessive zooming in!
        log("Attempting to recover from zooming waaaay in - current scale is " + currentScale);
        currentScale--;
        zoomOut();
        slideZoom(newScale);
    }
    else {
        // zooming inside normal ranges, so adjust
        var times;
        var i;
        if (newScale > currentScale) {
            times = newScale - currentScale;
            log("Zooming in - zoom operations required = " + times);
            currentScale = newScale;
            for (i = 0; i < times; i++) {
                zoomIn();
            }
        }
        else if (newScale < currentScale) {
            times = currentScale - newScale;
            log("Zooming out - zoom operations required = " + times);
            currentScale = newScale;
            for (i = 0; i < times; i++) {
                zoomOut();
            }
        }
        else {
            log("Detected sidebar zoom event, current scale matches required scale");
        }
    }
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
    // make sure zoom bar matches currentScale
    if (currentScale > 0 && currentScale < maxScale) {
        $("#zoom").slider("option", "value", currentScale);
        log("Set zoom bar value to " + currentScale);
    }
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
    // make sure zoom bar matches currentScale (between min and max)
    if (currentScale > 0 && currentScale < maxScale) {
        $("#zoom").slider("option", "value", currentScale);
        log("Set zoom bar value to " + currentScale);
    }
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
