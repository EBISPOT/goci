/**
 * Created by dwelter on 20/01/15.
 */

//var solrBaseURL = 'http://orange.ebi.ac.uk:8983/solr/gwas/select'

function solrSearch(queryTerm){

    console.log("Solr research request received for " + queryTerm);
    var searchTerm = 'text:"'.concat(queryTerm).concat('"');
    //
    //$.ajax({
    //    url: solrBaseURL,
    //    data: {'q': searchTerm,
    //            'indent': 'true',
    //            'rows' : 1000000,
    //            'facet':'true',
    //            'facet.field':'resourcename',
    //            'group' : 'true',
    //            'group.field' : 'pubmedId',
    //            'group.limit' : '-1',
    //            'wt':'json'},
    //    dataType: 'jsonp',
    //    jsonp: 'json.wrf',
    //    //success: function(response){
    //    //    console.log(response);
    //    //},
    //    success: processData,
    //    error: fail
    //});

    $.getJSON('api/search', {'q': searchTerm,
                    'max' : 100000
                    //'facet':'true',
                    //'facet.field':'resourcename',
                    //'group' : 'true',
                    //'group.by' : 'pubmedId'
    })
        .done(function(data) {
            console.log(data);
            processData(data);
        });


};

function processData(data){
   console.log("Solr search returned data");

    setCountBadges(data.facet_counts.facet_fields.resourcename);


    //var groups = data.grouped.pubmedId.groups;
 //   var queryTerm = data.responseHeader.params.q.substring(5);

    var documents = data.response.docs;

    //console.log("Due to process " + groups.length + " groups");

    console.log("Due to process " + documents.length + " documents");


    //if(groups.length != 0){
    if(documents.length != 0){
        var studyTable = $('#studySummaries').find('tbody').empty();
        var associationTable = $('#associationSummaries').find('tbody').empty();
        var traitTable = $('#diseasetraitSummaries').find('tbody').empty();
        var snpTable = $('#singlenucleotidepolymorphismSummaries').find('tbody').empty();

        //var si = 0;
        //var ai = 0;
        //var ti = 0;
        //var sni = 0;

        //for(var i=0; i<groups.length; i++){
        //    var documents = groups[i].doclist.docs;

            for(var j=0; j< documents.length; j++) {
               var doc = documents[j];

                if (doc.resourcename == "study") {
                    if(studyTable.find('tr').length == 10) {
                        $('#studyToggle').show();

                    }
                    processStudy(doc, studyTable);
                }
                else if (doc.resourcename == "association") {
                    if(associationTable.find('tr').length == 5){
                        $('#associationToggle').show();

                    }
                    processAssociation(doc, associationTable);
                }
                else if (doc.resourcename == "diseaseTrait") {
                    if(traitTable.find('tr').length == 5){
                        $('#diseasetraitToggle').show();
                    }
                    processTrait(doc, traitTable);
                }
                else if (doc.resourcename == "singleNucleotidePolymorphism") {
                    if(snpTable.find('tr').length == 5){
                        $('#singlenucleotidepolymorphismToggle').show();

                    }
                    processSnp(doc, snpTable);
                }
            }
        //}

        $('#studySummaries').append(studyTable);
        //$('#studySummaries').attr('data-toggle', 'table');
        //$('#studySummaries').dataTable();

        $('#associationSummaries').append(associationTable);
        //$('#associationSummaries').dataTable();

        $('#diseasetraitSummaries').append(traitTable);
        //$('#diseasetraitSummaries').dataTable();

        $('#singlenucleotidepolymorphismSummaries').append(snpTable);
        //$('#singlenucleotidepolymorphismSummaries').dataTable();

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
    //row.addClass('clickable');
    //row.attr('data-toggle', 'collapse');
    ////row.attr('id', study.id);
    //row.attr('data-target', '.'.concat(study.id));

    if(table.find('tr').length >= 10){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenStudy');
    }
    var ukpmc = "http://www.ukpmc.ac.uk/abstract/MED/".concat(study.pubmedId);
    var link = "<a href='".concat(ukpmc).concat("' target='_blank'>").concat(study.author).concat("</a>");

    row.append($("<td>").html(link));
    row.append($("<td>").html(study.publicationDate.substring(0,10)));
    row.append($("<td>").html(study.publication));
    row.append($("<td>").html(study.title));
    row.append($("<td>").html(study.trait));
    row.append($("<td>").html(study.associationCount));
    var plusicon = "<button class='btn btn-default btn-xs accordion-toggle' data-toggle='collapse' data-target='.".concat(study.id).concat("' aria-expanded='false' aria-controls='").concat(study.id).concat("'><span class='glyphicon glyphicon-plus'></span></button>");

    row.append($("<td>").html(plusicon));
    table.append(row);


    var hiddenrow = $("<tr>");
    hiddenrow.addClass(study.id);
    hiddenrow.addClass('collapse');
    hiddenrow.addClass('accordion-body');
    hiddenrow.addClass('hiddenRow');
    //hiddenrow.append($("<td>"));


    //var c1 = $("<td>").addClass('hiddenRow').attr('colspan', 3);
    //c1.append($("<div>").addClass('collapse').addClass(study.id).html(study.initialSampleDescription));
    //hiddenrow.append(c1);
    //var c2 = $("<td>").addClass('hiddenRow').attr('colspan', 2);
    //c2.append($("<div>").addClass('collapse').addClass(study.id).html(study.replicateSampleDescription));
    //hiddenrow.append(c2);
    //var c3 = $("<td>").addClass('hiddenRow').attr('colspan', 3);
    //c3.append($("<div>").addClass('collapse').addClass(study.id).html(study.platform));
    //hiddenrow.append(c3);
    //
    //if(study.cnv){
    //    var c4 = $("<td>").addClass('hiddenRow');
    //    c4.append($("<div>").addClass('collapse').addClass(study.id).html("yes"));
    //    hiddenrow.append(c4);
    //}
    //else{
    //    var c4 = $("<td>").addClass('hiddenRow');
    //    c4.append($("<div>").addClass('collapse').addClass(study.id).html("no"));
    //    hiddenrow.append(c4);
    //}
    table.append(hiddenrow);
}

function processAssociation(association, table){
    var row = $("<tr>");
    if(table.find('tr').length >= 5){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenAssociation');
    }

    if(association.rsId != null){
        if(association.rsId.length == 1 && (association.rsId[0].indexOf('x') == -1)){
            var dbsnp = "<a href='http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=".concat(association.rsId[0].substring(2)).concat("'>").concat(association.strongestAllele).concat("</a>");
            row.append($("<td>").html(dbsnp));
        }
        else {
            //for (var k = 0; k < association.rsId.length; k++) {
            //
            //}
            row.append($("<td>").html(association.strongestAllele));

        }
    }

    //}
    //else{
    //    var location = "chr".concat(association.chromosomeName).concat(":").concat(association.chromosomePosition);
    //    row.append($("<td>").html(location));
    //}

    var pval = association.pValue;

    if(association.qualifier != null && association.qualifier != ''){
        pval = pval.toString().concat(" ").concat(association.qualifier[0]);
    }
    row.append($("<td>").html(pval));

    if(association.orType == true){
        row.append($("<td>").html(association.orPerCopyNum));
        row.append($("<td>").html(''));
    }
    else {
        row.append($("<td>").html(''));
        if (association.orPerCopyUnitDescr != null) {
            var beta = (association.orPerCopyNum).toString().concat(" ").concat(association.orPerCopyUnitDescr);
            row.append($("<td>").html(beta));
        }
        else{
            row.append($("<td>").html(association.orPerCopyNum));
        }
    }
    //if(association.orPerCopyRange != null){
        row.append($("<td>").html(association.orPerCopyRange));
    //}
    //else{
    //    row.append($("<td>").html(''));
    //}
    row.append($("<td>").html(association.chromosomePosition));

    var repgene = '';
    if(association.reportedGene != null){
        for(var j=0; j < association.reportedGene.length; j++){
            if(repgene == ''){
                repgene = association.reportedGene[j];
            }

            else{
                repgene = repgene.concat(", ").concat(association.reportedGene[j]);
            }
        }
    }
    row.append($("<td>").html(repgene));

    var mapgene = '';
    if(association.mappedGene != null){
        for(var j=0; j < association.mappedGene.length; j++){
            if(mapgene == ''){
                mapgene = association.mappedGene[j];
            }

            else{
                mapgene = mapgene.concat(", ").concat(association.mappedGene[j]);
            }
        }
    }
    row.append($("<td>").html(mapgene));
//    TO DO: make the author field into a link to the study page using the pmid
    var ukpmc = "http://www.ukpmc.ac.uk/abstract/MED/".concat(association.pubmedId);
    var study = "<a href='".concat(ukpmc).concat("' target='_blank'>").concat(association.author).concat("</a>");
    row.append($("<td>").html(study));


    table.append(row);
}

function processTrait(diseasetrait, table){

    var row = $("<tr>");
    if(table.find('tr').length >= 5){
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
    if(table.find('tr').length >= 5){
        row.addClass('accordion-body');
        row.addClass('collapse');
        row.addClass('hiddenSNP');
    }
    row.append($("<td>").html(snp.rsId));

    var location = "chr".concat(snp.chromosomeName).concat(":").concat(snp.chromosomePosition);
    row.append($("<td>").html(location));
    row.append($("<td>").html(snp.region));
    row.append($("<td>").html(snp.context));

    var gene = '';
    if(snp.mappedGene != null){
        for(var j=0; j < snp.mappedGene.length; j++){
            if(gene == ''){
                gene = snp.mappedGene[j];
            }

            else{
                gene = gene.concat(", ").concat(snp.mappedGene[j]);
            }
        }
    }
    row.append($("<td>").html(gene));

    var efo = '';
    if(snp.efoLink != null){
        for(var j=0; j < snp.efoLink.length; j++){
            var data = snp.efoLink[j].split("|");
            var link = "<a href='".concat(data[2]).concat("' target='_blank'>").concat(data[0]).concat("</a>");

            if(efo == ''){
                efo = link;
            }
            else{
                efo = efo.concat(", <br>").concat(link);
            }
        }
    }
    else{
        efo = "N/A";
    }
    row.append($("<td>").html(efo));
    table.append(row);
}