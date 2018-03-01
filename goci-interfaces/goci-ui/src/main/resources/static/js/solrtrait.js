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
            'max': 1000
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
            
            for (var j = 0; j < documents.length; j++) {
                try {
                    var doc = documents[j];
                    processTraitNoEFODocs(doc, traitTable);
                }
                catch (ex) {
                    console.log("Failure to process document " + ex);
                }
            }
    }
    $('#efotrait-summaries').show();
}


function processTraitNoEFODocs(doc, table) {
    var row = $("<tr>");
    if (doc.doclist.docs.length > 0 ) {
        var traitsearch = "<span><a href='search?query=".concat(doc.groupValue).concat("'>").concat(doc.groupValue).concat(
            "</a></span>");
        row.append($("<td>").html(traitsearch));
    }
    
    var studies = '';
    for (var j = 0; j < doc.doclist.docs.length; j++) {
        var document = doc.doclist.docs[j];
        if (document.resourcename == 'study') {
                    var author = document.author;
                    var authorLabel = author.concat("-");
                    var pubmedid = document.pubmedid;
            
                    var europepmc = "http://www.europepmc.org/abstract/MED/".concat(pubmedid);
                    var searchlink = "<span><a href='search?query=".concat(author).concat("'>").concat(authorLabel).concat(
                        "</a></span>");
                    var epmclink = "<span><a href='".concat(europepmc).concat("' target='_blank'>").concat(
                        "<img alt='externalLink' class='link-icon' src='icons/external1.png' th:src='@{icons/external1.png}'/></a></span>");
            
                    if (studies == '') {
                        studies = studies.concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);
                    }
                    else {
                        studies = studies.concat(",<br>").concat(searchlink).concat('&nbsp;&nbsp;').concat(epmclink);
                    }
        }
        
    }
    
    row.append($("<td>").html(studies));
    
    table.append(row);
}

