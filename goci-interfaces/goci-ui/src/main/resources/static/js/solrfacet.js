/**
 * Created by dwelter on 28/01/15.
 */

var resources = ['study', 'association', 'diseasetrait'];

function applyFacet() {
    var searchTerm = $("#search-box").val();
    $('a.list-group-item').removeClass('selected');
    buildBreadcrumbs();
    if (window.location.hash) {
        var facet = window.location.hash.substr(1);
        console.log("Searching: " + searchTerm + " with facet: " + facet + "...");
        $('#facet').text(facet);
        $('#' + facet + "-facet").addClass('selected');
        //solrFacet(searchTerm, facet);
        processFacet(facet)
    }
    else {
        clearFacetting();
    }
}

//function solrFacet(queryTerm, facet){
//    console.log("Solr research request received for " + queryTerm + " and facet " + facet);
//    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
//    var url = 'api/search/' + facet;
//    $.getJSON(url, {'q': searchTerm, 'max' : 100000})
//            .done(function(data) {
//                          console.log(data);
//                          processFacet(data);
//                      });
//}

function clearFacetting() {
    $('#facet').text();
    // collapse all expanded sections

    loadResults();

    //$(".results-container:not(.no-results)").show();
    //$(".results-container.more-results .table-toggle").show();
    //$(".hidden-resource.in").collapse('hide');
    //
    //$(".ttbutton:not(.glyphicon-chevron-down)").toggleClass('glyphicon-chevron-down glyphicon-chevron-up');

}

function processFacet(resource) {
    //console.log("Received data and ready to process");
    //var resource = data.responseHeader.params.fq.substring(13).toLowerCase();
    console.log("Facet is " + resource);
    for (var f = 0; f < resources.length; f++) {
        var summaries = $('#' + resources[f] + '-summaries');
        if (resources[f] != resource) {
            //if (summaries.hasClass("more-results")) {
            //    summaries.find('.table-toggle').show();
            //}
            //summaries.find('.hidden-resource').collapse('hide');
            summaries.hide();
        }
        else {
            summaries.show();

            loadAdditionalResults(resource, false);
            summaries.find('.table-toggle').hide();
        }
    }

}

