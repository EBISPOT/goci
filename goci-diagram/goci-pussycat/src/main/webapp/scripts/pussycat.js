var currentScale = 0;
var scalingFactor = 1.0;
var dragOffsetX = 0;
var dragOffsetY = 0;

function init() {
    $(document).ready(function() {
        // create tabs
        $("#browsertabs").tabs({
                                   show:tabShow
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
}

function tabShow(event, ui) {
    if (ui.panel.id == "diagramtab") {
        renderDiagram();
    }
}

function renderDiagram() {
    // call to api/views/gwasdiagram to get required svg
    $.ajax({
               url:'api/views/gwasdiagram',
               dataType:'html',
               success:insertSVG,
               error:serverCommunicationFail
           });
}

function insertSVG(svg) {
    // update diagramarea div with returned SVG
    $("#diagramareaerror").css({"display":"none"});
    $("#diagramareacontent").html(svg);
}

function serverCommunicationFail(jqXHR, textStatus, errorThrown) {
    // show diagram area error div
    $("#diagramareaerror").css({"display":"block"});
    $("#diagramareaerrortext").html(jqXHR.responseText);
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
}

function pan(deltaX, deltaY) {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    var newX = (parseFloat(-deltaX)*scalingFactor) + parseFloat(dragOffsetX);
    var newY = (parseFloat(-deltaY)*scalingFactor) + parseFloat(dragOffsetY);
    $("#xOffsetSVG").html(newX);
    $("#yOffsetSVG").html(newY);
    // get width and height
    var newViewBox = newX + " " + newY + " " + elements[2] + " " + elements[3];
    document.getElementById('goci-svg').setAttribute("viewBox", newViewBox);
}

function updateOffset() {
    var viewBox = document.getElementById('goci-svg').getAttribute("viewBox");
    var elements = viewBox.split(' ');
    dragOffsetX = elements[0];
    dragOffsetY = elements[1];
}
