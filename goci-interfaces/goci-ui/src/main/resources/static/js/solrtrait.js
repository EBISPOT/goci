/**
 * Created by cmalangone 28.02.2018
 */

function addTraits() {

    var searchTerm = $("#search-box").val();
    console.log("Solr research request received for");
    var id = $('#efotrait-summaries').find('span.sorted').parent('th').attr('id');
    var expand = true;
    $.getJSON('api/search/parentSearch',
        {
            'q': searchTerm,
            'max': 5
        })
        .done(function (data) {
            addResultsToTrait(data, id);
        });
}


function addResultsToTrait(data, id) {
    if (data.error != null) {
        var sorter = $('#' + id).find('span.sorted');
        sorter.removeClass('asc desc glyphicon-arrow-up glyphicon-arrow-down').addClass("glyphicon-sort unsorted");
    }
    else {
        var documents = data.grouped.traitName_s.groups;
        console.log("Got a bunch of docs" + documents.length);
        
           console.log("Processing efotrait");
            var traitTable = $('#efotrait-table-body').empty();
            $('#efotrait-summaries').removeClass('more-results');
    
            var countArray = ["efotrait", documents.length];
            updateCountBadges(countArray);
            
            for (var j = 0; j < documents.length; j++) {
                if (j<5) {
                    try {
                        var doc = documents[j];
                        processTraitNoEFODocs(doc, traitTable);
                    }
                    catch (ex) {
                        console.log("Failure to process document " + ex);
                    }
                }
            }
            $('#efotrait-summaries .table-toggle').show();
            $('#efotrait-summaries').addClass("more-results");
            $('.efotrait-toggle').empty().text("Show more results");
    }
}


function processTraitNoEFODocs(doc, table) {
    var row = $("<tr>");
    
    if (doc.doclist.docs.length > 0 ) {
    
        var elem = doc.doclist.docs[0]
        var efo = '';
        // same piece of code. SolrSeach.js
        if (elem.efoLink != null) {
            for (var j = 0; j < elem.efoLink.length; j++) {
                var data = elem.efoLink[j].split("|");
                var efosearch = "<span><a href='search?query=".concat(data[0]).concat("'>").concat(data[0]).concat(
                    "</a></span>");
                var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(
                    "<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
            
                if (efo == '') {
                    efo = efosearch.concat('&nbsp;&nbsp;').concat(link);
                }
                else {
                    efo = efo.concat(", <br>").concat(efosearch.concat('&nbsp;&nbsp;').concat(link));
                }
            }
        }
        else {
            efo = "N/A";
        }
        row.append($("<td>").html(efo));
    
        var traitsearch = "<span><a href='search?query=".concat(doc.groupValue).concat("'>").concat(doc.groupValue).concat(
            "</a></span>");
        row.append($("<td>").html(traitsearch));
    
        var syns = '';
        if (elem.synonym != null) {
            for (var j = 0; j < elem.synonym.length; j++) {
                var synonymsearch = "<span><a href='search?query=".concat(elem.synonym[j]).concat("'>").concat(
                    elem.synonym[j]).concat("</a></span>");
                if (syns == '') {
                    syns = synonymsearch;
                }
                else if (j > 4) {
                    syns = syns.concat(", [...]");
                    break;
                }
                else {
                    syns = syns.concat(", ").concat(synonymsearch);
                }
            }
        }
    
        row.append($("<td>").html(syns));
    
    
    }
    table.append(row);
    
    
}

