/**
 * Created by cmalangone 28.02.2018
 * This new module is retrieving the trait related to a generic search.
 */

function addTraits() {
    
    var searchTerm = $("#search-box").val();
    console.log("Solr research request received for");
    var filter = getParameterByName('filter');
    
    // If the query is from "selected traits from list"
    if (filter != null) {
        searchTerm=filter;
    }
    
    //var traitToSearch = encodeURIComponent(searchTerm).replace(/[!'()*]/g, escape);
    
    var id = $('#efotrait-summaries').find('span.sorted').parent('th').attr('id');
    var expand = true;
    $("#efotrait_spinner").show();
    $("#efotrait-table").hide();
    $.getJSON('api/search/parentSearch',
        {
            'q': searchTerm,
            'max': 100  //fake max
        })
        .done(function (data) {
            addResultsToTrait(data, id);
            $("#efotrait-table").show();
            $("#efotrait_spinner").hide();
        })
        .always(function () {
            $("#efotrait-table").show();
            $("#efotrait_spinner").hide();
        });
}


// This is not the best solution but a short term solution for the issue: GOCI_2187.
// Reduce the size of DiseaseTrait documents.
function addResultsToTrait(data, id) {
    var traitsTermArray = processTraitDropdown();
    var traitsTerm;
    var indexTrait = 0;
    var filterTraitsOn = false;
    if (traitsTermArray.length > 0) {
        filterTraitsOn = true;
        traitsTerm = traitsTermArray.map((item)=>item.replace(/\+/g, ' '));
    }
    
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
    
        if (filterTraitsOn == false) {
            var countArray = ["efotrait", documents.length];
            updateCountBadges(countArray);
        }
        else {
            var countArray = ["efotrait", traitsTerm.length];
            updateCountBadges(countArray);
    
        }
        
        for (var j = 0; j < documents.length; j++) {
            
                try {
                    var doc = documents[j];
                    if (filterTraitsOn == false) {
                        processTraitNoEFODocs(doc, traitTable, j);
                    }
                    else {
                        if ($.inArray( doc.groupValue.trim(), traitsTerm ) > -1) {
                            processTraitNoEFODocs(doc, traitTable, indexTrait);
                            indexTrait = indexTrait +1;
                        }
                        
                    }
                }
                catch (ex) {
                    console.log("Failure to process document " + ex);
                }
            
        }
        $('#efotrait-summaries .table-toggle').show();
        $('#efotrait-summaries').addClass("more-results");
        $('.efotrait-toggle').empty().text("Show more results");
    }
}

function processTraitNoEFODocs(doc, table, indexDoc) {
    
    var row;
    if (indexDoc>4) {
        row = $("<tr style='display:none;'>");
    }
    else { row = $("<tr>"); }
    
    if (doc.doclist.docs.length > 0 ) {
        
        var elem = doc.doclist.docs[0];
        var efo = '';
        // same piece of code. SolrSeach.js
        if (elem.efoLink != null) {
            for (var j = 0; j < elem.efoLink.length; j++) {
                var data = elem.efoLink[j].split("|");
                var termToSearch= encodeURIComponent(data[0]).replace(/[!'()*]/g, escape);
    
                var efosearch = "<span><a href='search?query=".concat(termToSearch).concat("'>").concat(data[0]).concat(
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
        var traitToSearchURI= encodeURIComponent(doc.groupValue).replace(/[!'()*]/g, escape);
        var traitsearch = "<span><a href='search?query=".concat(traitToSearchURI).concat("'>").concat(doc.groupValue).concat(
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
    
        var searchStudiesByTrait = "<div id='trait_"+indexDoc+"'><a href='#' onclick='getStudiesRelatedByTrait(\""+doc.groupValue+"\","+indexDoc+");return false;'>Show publications</a></div>";
        row.append($("<td>").html(searchStudiesByTrait));
        
    }
    table.append(row);
    
    
}

function loadAdditionalResultsTraitNoEFODocs(showmore) {
    console.log("Processing more/less Trait");
    if (showmore) {
        $('#efotrait-summaries').removeClass('more-results');
        $('#efotrait-table-body tr').removeAttr("style");
    }
    else {
        $('#efotrait-summaries').addClass('more-results');
        $('#efotrait-table-body tr').attr("style","display:none;");
        var item = 0;
        $("#efotrait-table-body tr").each(function() {
            if(++item > 5) {
                return false;
            }
            $this = $(this);
            $this.css("display", "");
           
        });
    }
}


// Retrieve the studies related with a specific trait
function getStudiesRelatedByTrait(trait, divId) {
    
    $.getJSON('api/search/studyByTraitSearch',
        {
            'q': trait,
            'max': 200
        })
        .done(function (data) {
            $("#trait_"+divId).html("");
            for (var index = 0; index < data.grouped.pubmedId.groups.length; index++) {
                try {
                    var doc = data.grouped.pubmedId.groups[index].doclist.docs[0];
                    var yearOfPublication = doc.publicationLink[0].split("|")[1];
                    var infoStudy = doc.author_s.concat(' (PMID: ').concat(doc.pubmedId).concat("), ").concat(yearOfPublication);
                    var searchlink = "<span><a href='search?query=".concat(doc.pubmedId).concat("'>").concat(infoStudy).concat(
                        "</a></span>");
                    var viewPapers = '<div class=\"btn-group\"> <button type=\"button\" data-toggle=\"dropdown\" class=\"btn btn-xs btn-default dropdown-toggle\"><span><img alt=\"externalLink\" class=\"link-icon\" src=\"icons/external1.png\" th:src=\"@{icons/external1.png}\"/></span></button><ul class=\"dropdown-menu\"> <li><a target=\"_blank\" href=\"http://europepmc.org/abstract/MED/'+doc.pubmedId+'\">View in Europe PMC</a></li> <li><a target=\"_blank\" href=\"http://www.ncbi.nlm.nih.gov/pubmed/?term='+doc.pubmedId+'\">View in PubMed</a></li></ul></div>';
                    var globalInfo = searchlink.concat("&nbsp;&nbsp;").concat(viewPapers).concat("<br>");
                    $("#trait_"+divId).append(globalInfo);
                }
                catch (ex) {
                        console.log("Failure to process document " + ex);
                }
            }
        });
}