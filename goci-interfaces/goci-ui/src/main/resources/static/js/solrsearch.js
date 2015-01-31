/**
 * Created by dwelter on 20/01/15.
 */

var solrBaseURL = 'http://orange.ebi.ac.uk:8983/solr/gwas/select'

function solrSearch(queryTerm){

    console.log("Solr research request received for " + queryTerm);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');

    $.ajax({
        url: solrBaseURL,
        data: {'q': searchTerm,
                'indent': 'true',
                'rows' : 1000000,
                'facet':'true',
                'facet.field':'resourcename',
                'group' : 'true',
                'group.field' : 'pubmedId',
                'group.limit' : '-1',
                'wt':'json'},
        dataType: 'jsonp',
        jsonp: 'json.wrf',
        //success: function(response){
        //    console.log(response);
        //},
        success: processData,
        error: fail
    });

}

function processData(data){
   console.log("Solr search returned data");

    setCountBadges(data.facet_counts.facet_fields.resourcename);

    var groups = data.grouped.pubmedId.groups;
 //   var queryTerm = data.responseHeader.params.q.substring(5);


    console.log("Due to process " + groups.length + " groups");

    if(groups.length != 0){
        var studyTable = $("<tbody>");
        var associationTable = $("<tbody>");
        var traitTable = $("<tbody>");
        var snpTable = $("<tbody>");

        for(var i=0; i<groups.length; i++){
            var documents = groups[i].doclist.docs;

            for(var j=0; j< documents.length; j++) {
               var doc = documents[j];

                if (doc.resourcename == "study") {
                    if(studyTable.find('tr').length == 5) {
                        var toggle = $('<tr data-toggle="collapse" data-target=".hiddenStudy" class="accordion-toggle"> <td colspan="6"><button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-eye-open"></span></button></td></tr> ');
                        studyTable.append(toggle);
                    }
                    processStudy(doc, studyTable);

                }
                else if (doc.resourcename == "association") {
                    if(associationTable.find('tr').length == 5){
                        var toggle = $('<tr data-toggle="collapse" data-target=".hiddenAssociation" class="accordion-toggle"> <td colspan="6"><button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-eye-open"></span></button></td></tr> ');
                        associationTable.append(toggle);
                    }
                    processAssociation(doc, associationTable);
                }
                else if (doc.resourcename == "diseaseTrait") {
                    if(traitTable.find('tr').length == 5){
                        var toggle = $('<tr data-toggle="collapse" data-target=".hiddenTrait" class="accordion-toggle"> <td colspan="6"><button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-eye-open"></span></button></td></tr> ');
                        traitTable.append(toggle);
                    }
                    processTrait(doc, traitTable);
                }
                else if (doc.resourcename == "singleNucleotidePolymorphism") {
                    if(snpTable.find('tr').length == 5){
                        var toggle = $('<tr data-toggle="collapse" data-target=".hiddenSNP" class="accordion-toggle"> <td colspan="6"><button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-eye-open"></span></button></td></tr> ');
                        snpTable.append(toggle);
                    }
                    processSnp(doc, snpTable);
                }
            }
        }

        $('#studySummaries').append(studyTable);
        $('#associationSummaries').append(associationTable);
        $('#diseasetraitSummaries').append(traitTable);
        $('#singlenucleotidepolymorphismSummaries').append(snpTable);
    }
    else{
        $('#noResults').show();
        //$('#search-term-noResult').text(queryTerm);
        $('#results').hide();
    }

    console.log("Data display complete");
}

function fail(){
    console.log("Oh dear, something went wrong there");
}



function setCountBadges(countArray){
    for(var i=0; i<countArray.length; i=i+2){
        var resource = countArray[i];
        var count = countArray[i+1];

        if(count > 0){
            var facet = $('#' +resource + '-facet span');
            facet.empty();
            facet.append(count);
        }
        else{
            $('#' +resource+ '-facet').addClass("disabled");
            $('#' +resource+ 'Table').hide();
        }
    }
}


function processStudy(study, table){
    var row = $("<tr>");
    if(table.find('tr').length > 6){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenStudy');
    }
    row.append($("<td>").html(study.author));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    row.append($("<td>").html(study.trait));
    row.append($("<td>").html(study.publicationDate.substring(0,10)));
    row.append($("<td>").html(study.pubmedId));
    table.append(row);
}

function processAssociation(association, table){
    var row = $("<tr>");
    if(table.find('tr').length > 6){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenAssociation');
    }
    row.append($("<td>").html(association.strongestAllele));
    row.append($("<td>").html(association.pValue));

    if(association.orType == '1'){
        row.append($("<td>").html(association.orPerCopyNum));
        row.append($("<td>").html(''));
    }
    else {
        row.append($("<td>").html(''));
        if (association.orPerCopyUnitDescr != null) {
            var beta = (association.orPerCopyNum).concat(' ').concat(association.orPerCopyUnitDescr);
            row.append($("<td>").html(beta));
        }
        else{
            row.append($("<td>").html(association.orPerCopyNum));
        }
    }
    row.append($("<td>").html(association.chromosomePosition));

    var gene = '';
    if(snp.gene != null){
        for(var j=0; j < association.gene.length; j++){
            if(gene == ''){
                gene = association.gene[j];
            }

            else{
                gene = gene.concat(", ").concat(association.gene[j]);
            }
        }
    }
    row.append($("<td>").html(gene));
//    TO DO: make the author field into a link to the study page using the pmid
    var study = association.author.concat(" et al.");
    row.append($("<td>").html(study));


    table.append(row);
}

function processTrait(diseasetrait, table){

    var row = $("<tr>");
    if(table.find('tr').length > 6){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenTrait');
    }
    row.append($("<td>").html(diseasetrait.trait));

    var efo = '';
    if(diseasetrait.efoLink != null){
        for(var j=0; j < diseasetrait.efoLink.length; j++){
            var data = diseasetrait.efoLink[j].split("|");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

            if(efo == ''){
                efo = link;
            }
            //else if(j > 4){
            //    efo = efo.concat(", [...]");
            //    break;
            //}
            else{
                efo = efo.concat(", <br>").concat(link);
            }
        }
    }
    else{
        efo = "N/A";
    }
    row.append($("<td>").html(efo));



    var syns = '';
    if(diseasetrait.synonym != null){
        for(var j=0; j < diseasetrait.synonym.length; j++){
            if(syns == ''){
                syns = diseasetrait.synonym[j];
            }
            else if(j > 4){
                syns = syns.concat(", [...]");
                break;
            }
            else{
                syns = syns.concat(", ").concat(diseasetrait.synonym[j]);
            }
        }
    }

    row.append($("<td>").html(syns));

    //subtract one from child count as each term is a child of itself in the Solr index
    //row.append($("<td>").html(diseasetrait.child.length-1));
    table.append(row);
}

function processSnp(snp, table){
    var row = $("<tr>");
    if(table.find('tr').length > 6){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenSNP');
    }
    row.append($("<td>").html(snp.rsId));
    row.append($("<td>").html(snp.chromosomePosition));
    row.append($("<td>").html(snp.region));

    var gene = '';
    if(snp.gene != null){
        for(var j=0; j < snp.gene.length; j++){
            if(gene == ''){
                gene = snp.gene[j];
            }

            else{
                gene = gene.concat(", ").concat(snp.gene[j]);
            }
        }
    }
    row.append($("<td>").html(gene));
    table.append(row);
}