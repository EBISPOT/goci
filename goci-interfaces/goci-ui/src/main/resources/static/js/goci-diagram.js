/**
 * Created by dwelter on 23/06/15.
 */

var renderingComplete = false;


$(document).ready(function() {
    renderDiagram()

});



function toggleLegend(ts){
    if ($(ts).hasClass('panel-collapsed')) {
        // expand the panel
        $(ts).parents('.panel').find('.panel-body').slideDown();
        $(ts).parents('.panel').find('.panel-title').text("Filter the diagram");
        $(ts).removeClass('panel-collapsed');
        $(ts).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        $(ts).parents('#legend-bar').removeClass('col-md-1').addClass('col-md-2');
        $(ts).parents('#legend-bar').siblings('#diagram-area').removeClass('col-md-11').addClass('col-md-10');

    }
    else {
        // collapse the panel
        $(ts).parents('.panel').find('.panel-body').slideUp();
        $(ts).parents('.panel').find('.panel-title').text("Filter...");
        $(ts).addClass('panel-collapsed');
        $(ts).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        $(ts).parents('#legend-bar').removeClass('col-md-2').addClass('col-md-1');
        $(ts).parents('#legend-bar').siblings('#diagram-area').removeClass('col-md-10').addClass('col-md-11');
    }
}



function renderDiagram() {
    //if (enableSVG) {
        if (!renderingComplete) {
            //log("Hiding navbar...");
            //$(".navbar").hide();
            // call to api/gwasdiagram to get required svg
            console.log("Rendering GWAS diagram - calling api/gwasdiagram...");
            $.ajax({
                       url: 'http://www.ebi.ac.uk/fgpt/gwas/api/gwasdiagram',
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
    $("#diagramareacontent").html(
            "<div id=\"svgload\" style=\"text-align: center;\">" +
            "<p>Getting GWAS Diagram from server...</p>" +
            "<img src=\"../static/images/svg-load.gif\" alt=\"Please wait - diagram loading\"/><br/>" +
            "<p><a href=\"../static/images/gwas-latest.png\">" +
            "A static version of the diagram in PNG format can be found here" +
            "</a>" +
            "</p>" +
            "</div>");
}

function insertSVG(svg) {
    // update diagramarea div with returned SVG
    console.log("Obtained SVG from server OK, rendering...");
    //$("#diagramareaerror").css({"display": "none"});
    $("#diagram-area-content").html(svg);
    //log("Displaying navbar...");
    //$(".navbar").show();
    //resizeDisplay();
    renderingComplete = true;
    console.log("Diagram area div updated with SVG content OK");

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

    //$("circle").click(function() {
    //                      var vis = $(this).attr("fading");
    //                      if (vis == "false") {
    //                          var associations = $(this).attr("gwasassociation");
    //                          var name = $(this).attr("gwasname");
    //                          showSummary(associations, name);
    //                      }
    //                  }
    //);

}

function serverCommunicationFail(jqXHR, textStatus, errorThrown) {
    // show diagram area error div
    $("#diagram-area-content").html("");
    //$("#diagramareaerror").css({"display": "block"});
    //$("#diagramareaerrortext").html(jqXHR.responseText);
    console.log("Failed to acquire SVG from server - " + jqXHR.responseText);
}
