// FINAL VARIABLES - tweak these to adjust standard configuration
var enableDebugging = false;
var gwasLatest = "images/timeseries/gwas-2012-12.png";

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

//        $("#trait-filter").zooma({'zIndex': '100', 'onSelect': function(json) {
//            doFilter();
//        }});

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
                                   show: tabShow
                               });

        // enable buttons
        $("#submitbutton").button();
        $("#submitbutton").click(doFilter);

        $("#clearbutton").button();
        $("#clearbutton").click(showAllTraits);

        $("#legendbutton").button({icons: {primary: 'ui-icon-newwin'}});
        $("#legendbutton").click(toggleLegend);

        $("#retrybutton").button();
        $("#retrybutton").click(renderDiagram);

        $("#sendmailbutton").button();
        $("#sendmailbutton").click(sendMessage);


        $(".legenditem").click(function() {
            filterTraits($(this).attr("id"));
        });


//       $(".helpItem").css('background-image', 'ui-icon ui-icon-circle-plus');

//        $(".helpItem").click(function() {
//            toggleHelp();
//        })

        // show tabs, this is the point on load that display becomes visible
        $("#pussycattabs").show();
        resizeDisplay();

        $("#browsertabs ul").localScroll({
                                             target: "#browsertabs",
                                             duration: 0,
                                             hash: true
                                         });

        $(".iconlist li")
                .mouseenter(function() {
                                $(this).addClass('ui-state-hover');
                            })
                .mouseleave(function() {
                                $(this).removeClass("ui-state-hover");
                            });

        // create cycle for timeseries
        $('#ts-pause').click(function() {
            $('#timeseriescontent').cycle('pause');
            return false;
        });
        $('#ts-play').click(function() {
            $('#timeseriescontent').cycle('resume');
            return false;
        });
        $('#timeserieswrapper').hover(
                function() {
                    $('#timeseriescontrols').fadeIn();
                },
                function() {
                    $('#timeseriescontrols').fadeOut('fast');
                }
        );
        $('#timeseriescontent').cycle({fx: 'none', next: '#ts-next', prev: '#ts-prev'});

        // create cycle for filtered views
        $('#f-pause').click(function() {
            $('#filteredcontent').cycle('pause');
            return false;
        });
        $('#f-play').click(function() {
            $('#filteredcontent').cycle('resume');
            return false;
        });
        $('#filteredwrapper').hover(
                function() {
                    $('#filteredcontrols').fadeIn();
                },
                function() {
                    $('#filteredcontrols').fadeOut('fast');
                }
        );
        $('#filteredcontent').cycle({fx: 'none', next: '#f-next', prev: '#f-prev'});

        // fetch server info
        $.getJSON('api/status', function(data) {
            $("#version").html(data.version);
            $("#build-number").html(data.buildNumber);
            $("#release-date").html(data.releaseDate);
            var date = new Date(data.startupTime);
            $("#uptime").html(date.toLocaleTimeString() + " on " + date.toLocaleDateString());
            log("Added server status: " + JSON.stringify(data));
        });

        // bind mousemove handler
        $(document).mousemove(function(ev) {
            $('#tooltip').css({"left": ev.pageX + 20, "top": ev.pageY});

        });


        if (enableSVG) {
            // bind mousewheel event handler
            $('#diagramareacontent').mousewheel(function(ev, delta, deltaX, deltaY) {
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
                          }, {relative: true})
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
        $(".navbar").css({"top": navbarPos});
        log("Setting navigation bar offset to " + navbarPos);
        $("#zoom").slider({
                              orientation: "vertical",
                              max: maxScale,
                              value: currentScale,
                              slide: function(event, ui) {
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
    else {
        $("#trait-filter").change(function() {
            log("Detected change() event on Bioportal widget");
            doFilter();
        });
        $("#trait-filter").blur(function() {
            log("Focus lost on Bioportal widget");
            doFilter();
        })
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
            .animate({backgroundColor: "#FEF1EC"}, 750);
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

        if ($(".legend").css("display") == "block") {
            hideLegend();
        }
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
                       url: 'api/views/gwasdiagram',
                       dataType: 'html',
                       beforeSend: showSVGLoadWhirly,
                       success: insertSVG,
                       error: serverCommunicationFail
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
    $("#diagramareaerror").css({"display": "none"});
    $("#diagramareacontent").html(svg);
    log("Displaying navbar...");
    $(".navbar").show();
    resizeDisplay();
    renderingComplete = true;
    log("Diagram area div updated with SVG content OK");

    /*TO DO: add selector to ensure that only circles that are traits get mouse-overs, not any potential future circles*/

    $("circle").hover(
            function() {
                var vis = $(this).attr("fading");
                if (vis == "false") {
                    var trait = $(this).attr("gwasname");
                    $("#tooltip-text").html(trait);
                    $("#tooltip").show();
                }
            },
            function() {
                $("#tooltip").hide();
            }
    );

    $("circle").click(function() {
                          var vis = $(this).attr("fading");
                          if (vis == "false") {
                              var associations = $(this).attr("gwasassociation");
                              var name = $(this).attr("gwasname");
                              showSummary(associations, name);
                          }
                      }
    );

}

function insertPNG() {
    // update diagramarea div with stock PNG
    $("#diagramareaerror").css({"display": "none"});
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
    $("#diagramareaerror").css({"display": "block"});
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

function showSummary(associations, name) {
    $("#tooltip").hide();

    $("#traitpopup").html("").dialog("close");

    $.getJSON('api/summaries/associations/' + associations, function(data) {

        var trait = "All SNPs associated with trait '".concat(name).concat("' in band ").concat(data.chromBand);

        var summaryTable = $("<table>");
        summaryTable.html("<th>SNP</th><th>p-Value</th><th>EFO mapping</th><th>GWAS trait</th><th>Study</th><th>GWAS catalog</th>");
        summaryTable.html("<th>SNP</th><th>p-Value</th><th>EFO mapping</th><th>GWAS trait</th><th>Study</th><th>GWAS catalog</th>");

        try {
            var index = data.snpsummaries.length;
            for (var i = 0; i < index; i++) {
                var row = $("<tr>");
                var snpsummary = data.snpsummaries[i];
                var snp = "http://www.ensembl.org/Homo_sapiens/Variation/Summary?v=".concat(snpsummary.snp);
                var snpurl = "<a href='".concat(snp).concat("' target='_blank'>").concat(snpsummary.snp).concat("</a>");
                row.append($("<td>").html(snpurl));
                row.append($("<td class='center'>").html(snpsummary.pval));
                var efoterm = snpsummary.efoTrait;
                var efourl = "<a href='".concat(snpsummary.efoUri).concat("' target='_blank'>").concat(efoterm).concat("</a>");
                row.append($("<td class='center'>").html(efourl));
                row.append($("<td class='center'>").html(snpsummary.gwastrait));
                var study = snpsummary.author.concat(" et al., ").concat(snpsummary.date);
                var studyurl = "http://www.ukpmc.ac.uk/abstract/MED/".concat(snpsummary.study);
                var studyentry = "<a href='".concat(studyurl).concat("' target='_blank'>").concat(study).concat("</a>");
                row.append($("<td>").html(studyentry));
                var gwasurl = "http://www.genome.gov/gwastudies/index.cfm?snp=".concat(snpsummary.snp).concat("#result_table");
                var gwaslink = "<a href='".concat(gwasurl).concat("' target='_blank'>More information</a>");
                row.append($("<td>").html(gwaslink));
                summaryTable.append(row);

            }

            $("#traitpopup").append(summaryTable).dialog({"title": trait, /*"draggable":false,*/ "width": 800});

        }
        catch (ex) {
            alert(ex);
        }

    });
}

//function doFilterOnEnter(e) {
//    var key = e.keyCode || e.which;
//    if (key == 13) {
//        log("Detected filter keypress - " + $("#trait-filter").val());
//        filterTraits($("#trait-filter").val());
//    }
//}

//function doFilter() {
//    log("Detected bioportal widget filtering event - " + $("#trait-filter").val());
//    filterTraits($("#trait-filter").val());
//}

function doFilter() {
    log("Detected bioportal widget filtering event - " + $("#trait-filter").val());
    filterTraits($("#trait-filter").val());
}

function doJSONFilter(json) {
    var tags = json.semanticTags;
    if (tags.length > 0) {
        var tag = tags[0];
        log("Detected zooma widget filtering event - " + tag);
        filterTraits($("#trait-filter").val());
    }
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
                $("circle." + trait).attr("fading", "false");
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
    $("#trait-filter").val("");
    $("circle.gwas-trait").attr("fading", "false");
}

function hideAllTraits() {
    log("Hiding all 'gwas-trait' elements");
    $(".gwas-trait").attr("mask", "url(#traitMask)");
    $("circle.gwas-trait").attr("fading", "true");
}

function toggleLegend() {
    if ($(".legend").css("display") == "none") {
        showLegend();
    }
    else {
        hideLegend();
    }
}

function showLegend(){
    // show legend
    $(".legend").show('fold');
    // update text
    $("#legendbutton .ui-button-text").html("Hide Legend");
}

function hideLegend(){
    // hide legend
    $(".legend").hide('fold');
    // update text
    $("#legendbutton .ui-button-text").html("Show Legend");
}

//function toggleHelp() {
//    if ($(".helpText").css("display") == "none") {
//
//            $(this).toggleClass("display", "block");
//    }
//    else{
//        $(this).toggleClass("display", "none");
//    }
//
//}

function sendMessage(){
 //declare teh variables here
    var message = "foobar";

    $.ajax({
        type: 'POST',
        url: 'send_email.php',
        data: message,
        success: //define a function for success, probably an alert pop-up
      //  error: serverCommunicationFail
    });
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
    // get current top left corner X&Y values & width and height
    var origX = parseFloat(elements[0]);
    var origY = parseFloat(elements[1]);
    var width = parseFloat(elements[2]);
    var height = parseFloat(elements[3]);

    var centX = (width/2)+origX;
    var centY = (height/2)+origY;

     // update scaling factor
    scalingFactor = scalingFactor * 0.95;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 0.95
    var newWidth = width * 0.95;
    var newHeight = height * 0.95;

    var newX = centX-(newWidth/2);
    var newY = centY-(newHeight/2);

    var newViewBox = newX +  " " + newY + " " + newWidth + " " + newHeight;
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
    // get current top left corner X&Y values & width and height
    var origX = parseFloat(elements[0]);
    var origY = parseFloat(elements[1]);
    var width = parseFloat(elements[2]);
    var height = parseFloat(elements[3]);

    var centX = (width/2)+origX;
    var centY = (height/2)+origY;

    // update scaling factor
    scalingFactor = scalingFactor * 1.05;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 1.05
    var newWidth = width * 1.05;
    var newHeight = height * 1.05;

    var newX = centX-(newWidth/2);
    var newY = centY-(newHeight/2);

    var newViewBox = newX + " " + newY + " " + newWidth + " " + newHeight;
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
