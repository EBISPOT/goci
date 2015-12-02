/**
 * Created by dwelter on 23/06/15.
 */

var renderingComplete = false;
var maxScale = 100;
var currentScale = maxScale / 2;
var scalingFactor = 1.0;
var dragOffsetX = 0;
var dragOffsetY = 0;

var enableSVG = true;


$(document).ready(function() {


    renderDiagram();

    resizeDisplay();

    $(window).resize(function() {
        resizeDisplay();
    });

    // fetch server info
    $.getJSON('pussycat/status', function(data) {
        $("#version").html(data.version);
        $("#build-number").html(data.buildNumber);
        $("#release-date").html(data.releaseDate);
        var date = new Date(data.startupTime);
        $("#uptime").html(date.toLocaleTimeString() + " on " + date.toLocaleDateString());
        console.log("Added server status: " + JSON.stringify(data));
    });

    // bind mousemove handler
    $(document).mousemove(function(ev) {
        $('#tooltip').css({"left": ev.pageX + 20, "top": ev.pageY});

    });

    $("#filter-button").click(function() {
        doFilter();
    });
    $("#clear-filter-button").click(function() {
        showAllTraits();
    });


    $(".legend-item").click(function() {
        if (!$(this).hasClass("disabled")) {
            filterTraits($(this).attr("id"));
        }
    });

    if (enableSVG) {
        // bind mousewheel event handler
        $('#diagram-area-content').mousewheel(function(ev, delta, deltaX, deltaY) {
            if (delta > 0) {
                currentScale++;
                $(".zoom-range").val(currentScale).trigger('change');
                zoomIn();
            }
            else if (delta < 0) {
                currentScale--;
                $(".zoom-range").val(currentScale).trigger('change');
                zoomOut();
            }

        });

        // bind drag handler
        $('#diagram-area-content')
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

    // initialize slider

    $(".zoom-range").attr("value", currentScale);
    $(".zoom-range").attr("max", maxScale);
    $(".zoom-range").attr("min", 0);
    $(".zoom-range").attr("step", 1);

    //$('.zoom-range').change();

    $('.zoom-range').change(function() {
        console.log("Somebody clicked the slider. New value is " + $(this).val());
        slideZoom($(this).val());
    });

    $(".zoom-in").click(function() {
        currentScale++;
        $(".zoom-range").val(currentScale).trigger('change');
        zoomIn();
    });
    $(".zoom-out").click(function() {
        currentScale--;
        $(".zoom-range").val(currentScale).trigger('change');
        zoomOut();
    });

});

function resizeDisplay() {
// update the svg size to fill the space available in the diagram area

    var topstuff = parseInt($('.navbar').height()) + parseInt($('.container-fluid').css('padding-top'));
    var breadcrumb = parseInt($('.breadcrumb').css('padding-top')) + parseInt($('.breadcrumb').css('padding-bottom'))
            + parseInt($('.breadcrumb').height()) + parseInt($('.breadcrumb').css('margin-bottom'));
    var offset = topstuff + breadcrumb;

    var footer = parseInt($('.footer').height());

    var mainHeight = $(window).height() - offset - footer;

    $('#diagram-area').height(mainHeight);

    $("#diagram-space").height(mainHeight);
    $("#diagram-area-content").height(mainHeight);

    if (!$('#legend-toggle').hasClass('panel-collapsed')) {
        console.log("The legend isn't collapsed and can be resized");
        $('#legend-bar').height(mainHeight);
        $('#legend-panel').height(mainHeight);

        var lengedbarheader = parseInt($('#legend-bar-header').height()) +
                parseInt($('#legend-bar-header').css('padding-top')) +
                parseInt($('#legend-bar-header').css('padding-bottom'));
        $('#legend-bar-body').css('max-height', mainHeight - lengedbarheader - 10);
        //$('#legend').height($('#legend-bar-body').height() - $('#filters').height() - parseInt($('#legend').css('margin-top')));
        //$('#legend-items').height($('#legend').height() - $('#legend > b').height());
    }
    else {
        console.log("The legend is collapsed and must not be resized");

    }

    var width = $("#diagram-area-content").width();
    var height = $("#diagram-area-content").height();
    $("#goci-svg").attr("width", width);
    $("#goci-svg").attr("height", height);
    //var viewBox =  "-50 -25 " + width + " " + height;
    //document.getElementById('goci-svg').setAttribute("viewBox", viewBox);

}


function toggleLegend(ts) {
    if ($(ts).hasClass('panel-collapsed')) {
        // expand the panel
        $(ts).parents('.panel').find('.panel-body').slideDown();
        $(ts).parents('.panel').find('.panel-title').text("Filter the diagram");
        $(ts).removeClass('panel-collapsed');
        $(ts).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        $(ts).parents('#legend-bar').removeClass('col-8').addClass('col-16');
        $(ts).parents('#legend-bar').siblings('#diagram-area').removeClass('col-8-rest').addClass('col-16-rest');
        resizeDisplay();

    }
    else {
        // collapse the panel
        $(ts).parents('.panel').find('.panel-body').slideUp();
        $('#legend-panel').removeAttr('style');
        $(ts).parents('.panel').find('.panel-title').text("Filter...");
        $(ts).addClass('panel-collapsed');
        $(ts).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        $(ts).parents('#legend-bar').removeClass('col-16').addClass('col-8');
        $(ts).parents('#legend-bar').siblings('#diagram-area').removeClass('col-16-rest').addClass('col-8-rest');
        resizeDisplay();
    }
}


function renderDiagram() {
    //if (enableSVG) {
    if (!renderingComplete) {
        //log("Hiding zoombar...");
        //$(".zoombar").hide();
        // call to api/gwasdiagram to get required svg
        console.log("Rendering GWAS diagram - calling api/gwasdiagram...");
        $.ajax({
                   //url: 'pussycat/gwasdiagram/associations/5/-8',
                   url: 'pussycat/gwasdiagram/associations?pvaluemax=5e-8',
                   dataType: 'html',
                   beforeSend: showSVGLoadWhirly,
                   success: insertSVG,
                   error: serverCommunicationFail
               });
    }
    else {
        console.log("Rendering already complete, update request not sent");
    }
    //}
    //else {
    //    log("Browser doesn't support SVG rendering, showing static image only");
    //    insertPNG();
    //}
}

function showSVGLoadWhirly() {
    $('#loadingResults').show();

    //$("#diagram-area-content").html(
    //        "<div id=\"svgload\" style=\"text-align: center;\">" +
    //        "<p>Getting GWAS Diagram from server...</p>" +
    //        "<img src=\"../static/images/svg-load.gif\" alt=\"Please wait - diagram loading\"/><br/>" +
    //        "<p><a href=\"../static/images/gwas-latest.png\">" +
    //        "A static version of the diagram in PNG format can be found here" +
    //        "</a>" +
    //        "</p>" +
    //        "</div>");
}

function insertSVG(svg) {
    $('#loadingResults').hide();
    // update diagramarea div with returned SVG
    console.log("Obtained SVG from server OK, rendering...");
    //$("#diagramareaerror").css({"display": "none"});
    $("#diagram-area-content").html(svg);
    //log("Displaying zoombar...");
    $(".zoombar").show();

    var width = $("#diagram-area-content").width();
    var height = $("#diagram-area-content").height();
    $("#goci-svg").attr("width", width);
    $("#goci-svg").attr("height", height);
    var viewBox = "-50 -25 " + width + " " + height;
    document.getElementById('goci-svg').setAttribute("viewBox", viewBox);


    renderingComplete = true;
    console.log("Diagram area div updated with SVG content OK");

    /*TO DO: add selector to ensure that only circles that are traits get mouse-overs, not any potential future circles*/

    updateLegendBadges();

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
                $(this).attr('title', '');
            }
    );

    $("circle").click(function() {
                          var vis = $(this).attr("fading");
                          if (vis == "false") {
                              var associations = $(this).attr("gwasassociation");
                              var name = $(this).attr("gwasname");
                              if (associations != null) {
                                  showSummary(associations, name);
                              }
                          }
                      }
    );

}

function serverCommunicationFail(jqXHR, textStatus, errorThrown) {
    // show diagram area error div
    $("#diagram-area-content").html("");
    //$("#diagramareaerror").css({"display": "block"});
    //$("#diagramareaerrortext").html(jqXHR.responseText);
    console.log("Failed to acquire SVG from server - " + jqXHR.responseText);
}

function showTooltip(tooltipText) {
    $("#tooltip-text").html(tooltipText);
    $("#tooltip").show();
}

function hideTooltip() {
    $("#tooltip").hide();
}

function updateLegendBadges() {
    //declare a bunch of variables for each colour
    var digestive = 0;
    var cardio = 0;
    var metabolic = 0;
    var immune = 0;
    var neuro = 0;
    var liver = 0;
    var lipid = 0;
    var inflam = 0;
    var haemo = 0;
    var body = 0;
    var cardioMeas = 0;
    var measure = 0;
    var drug = 0;
    var process = 0;
    var cancer = 0;
    var disease = 0;
    var other = 0;


    $("circle").each(function() {
        switch ($(this).attr("fill")) {
            case '#B33232':
                cardio++;
                break;
            case '#8DD3C7':
                haemo++;
                break;
            case '#FFFFB3':
                neuro++;
                break;
            case '#BEBADA':
                process++;
                break;
            case '#80B1D3':
                cardioMeas++;
                break;
            case '#FB8072':
                other++;
                break;
            case '#FDB462':
                metabolic++;
                break;
            case '#FCCDE5':
                drug++;
                break;
            case '#B3DE69':
                lipid++;
                break;
            case '#66CCFF':
                body++;
                break;
            case '#BC80BD':
                cancer++;
                break;
            case '#CCEBC5':
                inflam++;
                break;
            case '#FFED6F':
                immune++;
                break;
            case '#006699':
                measure++;
                break;
            case '#669900':
                liver++;
                break;
            case '#FF3399':
                disease++;
                break;
            case '#B7704C':
                digestive++;
                break;
        }
    })

    $(".icon-trait-digestive").next().empty().append(digestive);
    $(".icon-trait-cardio").next().empty().append(cardio);
    $(".icon-trait-metabolic").next().empty().append(metabolic);
    $(".icon-trait-immune").next().empty().append(immune);
    $(".icon-trait-neuro").next().empty().append(neuro);
    $(".icon-trait-liver-measure").next().empty().append(liver);
    $(".icon-trait-lipid-measure").next().empty().append(lipid);
    $(".icon-trait-inflam-measure").next().empty().append(inflam);
    $(".icon-trait-haemo-measure").next().empty().append(haemo);
    $(".icon-trait-body-measure").next().empty().append(body);
    $(".icon-trait-cardio-measure").next().empty().append(cardioMeas);
    $(".icon-trait-measure").next().empty().append(measure);
    $(".icon-trait-drug").next().empty().append(drug);
    $(".icon-trait-process").next().empty().append(process);
    $(".icon-trait-neoplasm").next().empty().append(cancer);
    $(".icon-trait-disease").next().empty().append(disease);
    $(".icon-trait-other").next().empty().append(other);

}


function slideZoom(newScale) {
    if (currentScale < 0) {
        // try and recover from excessive zooming out!
        console.log("Attempting to recover from zooming waaaay out - current scale is " + currentScale);
        currentScale++;
        zoomIn();
        slideZoom(newScale);
    }
    else if (currentScale > maxScale) {
        // try and recover from excessive zooming in!
        console.log("Attempting to recover from zooming waaaay in - current scale is " + currentScale);
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
            console.log("Zooming in - zoom operations required = " + times);
            currentScale = newScale;
            for (i = 0; i < times; i++) {
                zoomIn();
            }
        }
        else if (newScale < currentScale) {
            times = currentScale - newScale;
            console.log("Zooming out - zoom operations required = " + times);
            currentScale = newScale;
            for (i = 0; i < times; i++) {
                zoomOut();
            }
        }
        else {
            console.log("Detected sidebar zoom event, current scale matches required scale");
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

    var centX = (width / 2) + origX;
    var centY = (height / 2) + origY;

    // update scaling factor
    scalingFactor = scalingFactor * 0.95;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 0.95
    var newWidth = width * 0.95;
    var newHeight = height * 0.95;

    var newX = centX - (newWidth / 2);
    var newY = centY - (newHeight / 2);

    var newViewBox = newX + " " + newY + " " + newWidth + " " + newHeight;
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
    console.log("Zoom in over SVG event.  New zoom level: " + scalingFactor + ", viewBox now set to " + newViewBox);
    // make sure zoom bar matches currentScale
    if (currentScale > 0 && currentScale < maxScale) {
        //$(".zoom-range").attr("value", currentScale);
        console.log("Set zoom bar value to " + currentScale);
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

    var centX = (width / 2) + origX;
    var centY = (height / 2) + origY;

    // update scaling factor
    scalingFactor = scalingFactor * 1.05;
    $("#scale").html(scalingFactor);
    // calculate new sizes
    // transform width and height by multiplying by 1.05
    var newWidth = width * 1.05;
    var newHeight = height * 1.05;

    var newX = centX - (newWidth / 2);
    var newY = centY - (newHeight / 2);

    var newViewBox = newX + " " + newY + " " + newWidth + " " + newHeight;
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
    console.log("Zoom out over SVG event.  New zoom level: " + scalingFactor + ", viewBox now set to " + newViewBox);
    // make sure zoom bar matches currentScale (between min and max)
    if (currentScale > 0 && currentScale < maxScale) {
        //$(".zoom-range").attr("value", currentScale);
        console.log("Set zoom bar value to " + currentScale);
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
    console.log("Pan over SVG event.  ViewBox now set to " + newViewBox);
}

function updateOffset() {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    dragOffsetX = elements[0];
    dragOffsetY = elements[1];
}



