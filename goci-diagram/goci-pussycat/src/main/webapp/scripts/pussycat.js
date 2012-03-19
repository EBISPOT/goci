function init() {
    $(document).ready(function() {
        // create tabs
        $("#browsertabs").tabs({
                                   show:tabShow
                               });
        $("#retrybutton").button();
        $("#retrybutton").click(renderDiagram);
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