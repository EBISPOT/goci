/**
 * Created by Laurent on 22/08/2016.
 */

var SearchState = {
    LOADING: {value: 0},
    NO_RESULTS: {value: 1},
    RESULTS: {value: 2}
};

var EPMC = "http://www.europepmc.org/abstract/MED/";

$(document).ready(function() {
    var searchTerm = $('#query').text();
    console.log("Loading search module!");
    console.log("rsID: "+searchTerm);
    if (searchTerm != '') {
        console.log("Start search for SNP "+searchTerm);
        getSnpData(searchTerm);
    }
    getLDPopulations();
});

function getSnpData(rsId) {
    console.log("Solr research request received for " + rsId);

    setState(SearchState.LOADING);
    $.getJSON('/gwas/api/search/association',
              {
                  'q': "rsId:"+rsId,
                  'max': 1000
              })
              .done(function(data) {
                  console.log(data.response);
                  processSnpData(data.response,rsId);
              });
    console.log("Solr research done for " + rsId);
}

function processSnpData(data,rsId) {
    // Snp summary panel
    getSnpInfo(data);
    // External links panel
    getLinkButtons(rsId);
    // Associations table
    getSnpAssociations(data.docs);
    // Traits table
    getSnpTraits(data.docs);
}

function getSnpInfo(data,rsId) {
    var chr = data.docs[0].chromosomeName[0];
    var pos = data.docs[0].chromosomePosition[0];
    var region = data.docs[0].region[0];
    var func = data.docs[0].context[0];

    var genes_reported = [];
    var genes_reported_url = [];
    var genes_mapped = [];
    var genes_mapped_url = [];
    $.each(data.docs, function (index, doc) {
        // Reported genes
        var reportedGenes = doc.reportedGene;
        $.each(reportedGenes, function (index, gene) {
            if (jQuery.inArray(gene, genes_reported) == -1) {
                genes_reported.push(gene);
                genes_reported_url.push(setQueryUrl(gene));
            }
        });
        // Mapped genes
        var mappedGenes = doc.entrezMappedGenes;
        $.each(mappedGenes, function (index, gene) {
            if (jQuery.inArray(gene, genes_mapped) == -1) {
                genes_mapped.push(gene);
                genes_mapped_url.push(setQueryUrl(gene));
            }
        });
    });
    genes_reported_url.sort();
    genes_mapped_url.sort();

    $("#snpLocation").html("chr"+chr+":"+pos);
    $("#snpRegion").html(region);
    $("#snpClass").html(func);
    $("#snpReportedGenes").html(genes_reported_url.join(', '));
    $("#snpMappedGenes").html(genes_mapped_url.join(', '));
}

function getLinkButtons (rsId) {
    $("#ensembl_button").attr('onclick', "window.location.href='http://www.ensembl.org/Homo_sapiens/Variation/Explore?v="+rsId+"'");
    $("#dbsnp_button").attr('onclick', "window.location.href='http://www.ncbi.nlm.nih.gov/SNP/snp_ref.cgi?rs="+rsId+"'");
}

function getSnpAssociations(data) {
    var asso_count = data.length;

    $("#association_count").html(asso_count);

    if (asso_count == 1) {
        $("#association_label").html("Association");
    }

    $.each(data, function(index,asso) {

        var row = $('<tr/>');

        // Risk allele
        var riskAllele = asso.strongestAllele[0];
        riskAllele = setQueryUrl(riskAllele);
        row.append(newCell(riskAllele));

        // Risk allele frequency
        var riskAlleleFreq = asso.riskFrequency;
        row.append(newCell(riskAlleleFreq));

        // p-value
        var pValue = asso.pValueMantissa;
        if (pValue) {
            var pValueExp = " x 10<sup>" + asso.pValueExponent + "</sup>";
            pValue += pValueExp;
            if (asso.qualifier) {
                if (asso.qualifier[0].match(/\w/)) {
                    pValue += " " + asso.qualifier.join(',');
                }
            }
            row.append(newCell(pValue));
        } else {
            row.append(newCell('-'));
        }

        // OR
        var orValue = asso.orPerCopyNum;
        if (orValue) {
            if (asso.orDescription) {
                orValue += " " + asso.orDescription;
            }
            row.append(newCell(orValue));
        } else {
            row.append(newCell('-'));
        }

        // Beta
        var beta = asso.betaNum;
        if (beta) {
            if (asso.betaUnit) {
                beta += " " + asso.betaUnit;
            }
            if (asso.betaDirection) {
                beta += " " + asso.betaDirection;
            }
            row.append(newCell(beta));
        } else {
            row.append(newCell('-'));
        }

        // CI
        var ci = asso.range;
        if (ci) {
            row.append(newCell(ci));
        } else {
            row.append(newCell('-'));
        }

        // Reported genes
        var genes = [];
        var reportedGenes = asso.reportedGene;
        if (reportedGenes) {
            $.each(reportedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            row.append(newCell(genes.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Reported traits
        var traits = [];
        var reportedTraits = asso.traitName;
        if (reportedTraits) {
            $.each(reportedTraits, function(index, trait) {
                traits.push(setQueryUrl(trait));
            });
            row.append(newCell(traits.join(', ')));
        } else {
           row.append(newCell('-'));
        }

        // Mapped traits
        var mappedTraits = asso.mappedLabel;
        if (mappedTraits) {
            row.append(newCell(mappedTraits.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Study
        var author = asso.author_s;
        var publicationDate = asso.publicationDate;
        var pubDate = publicationDate.split("-");
        var pubmedId = asso.pubmedId;
        var study = setQueryUrl(author, author + " - " + pubDate[0]);
        study += ' <small><a class="external_link" href="'+EPMC+pubmedId+'">PMID:'+pubmedId+' <img class="link-icon-smaller" src="../icons/external1.png"/></a></small>';
        row.append(newCell(study));

        var studyId = asso.studyId;

        row.append(newCell(showHideStudy(studyId)));

        // Populate the table
        $("#association-table-body").append(row);


        // Add hidden study description/details
        var studyRow = $('<tr/>');
        studyRow.addClass("accordion-body hidden-study-row collapse");
        studyRow.attr('id',"study-"+studyId);

        var sudyContent = $("<td></td>");
        sudyContent.attr('colspan',11);
        sudyContent.attr('style',"border-top: none");

        var initialSampleDescription = asso.initialSampleDescription;
        var replicateSampleDescription = asso.replicateSampleDescription;
        var ancestralGroups = asso.ancestralGroups;

        var studyTable = $("<table>").addClass('sample-info sample-info-border');

        // Header //
        studyTable.append($("<thead>").append($("<tr>").append($("<th>").attr("colspan","2").attr("style", "background-color:#E7F7F9").html("Study information"))));

        // Content //
        var studyTableContent = $("<tbody>");

        // Initial sample description
        var initialSampleDescriptionRow = $("<tr>");
        initialSampleDescriptionRow.append($("<td>").attr("style", "max-width:30%;text-align:right;font-weight:bold").html("Initial sample description"));
        initialSampleDescriptionRow.append($("<td>").attr("style", "text-align:left").html(initialSampleDescription));
        studyTableContent.append(initialSampleDescriptionRow);

        // Replication sample description
        var replicateSampleDescriptionRow = $("<tr>");
        replicateSampleDescriptionRow.append($("<td>").attr("style", "max-width:30%;text-align:right;font-weight:bold").html("Replication sample description"));
        replicateSampleDescriptionRow.append($("<td>").attr("style", "text-align:left").html(replicateSampleDescription));
        studyTableContent.append(replicateSampleDescriptionRow);

        // Ancestral groups
        var ancestralGroupsRow = $("<tr>");
        ancestralGroupsRow.append($("<td>").attr("style", "max-width:30%;text-align:right;font-weight:bold").html("Ancestral groups"));
        ancestralGroupsRow.append($("<td>").attr("style", "text-align:left").html(ancestralGroups.join(', ')));
        studyTableContent.append(ancestralGroupsRow);

        studyTable.append(studyTableContent);

        /*var content = '<table class="sample-info sample-info-border"><thead><tr><th colspan="2" style="background-color:#E7F7F9">Study information</th></tr></thead>' +
        '  <tbody>' +
        '    <tr>' +
        '      <td style="max-width:30%;text-align:right;font-weight:bold">Initial sample description</td>' +
        '      <td style="text-align:left">'+initialSampleDescription+'</td>' +
        '    </tr>' +
        '    <tr>' +
        '      <td style="max-width:30%;text-align:right;font-weight:bold">Replication sample description</td>' +
        '      <td style="text-align:left">'+replicateSampleDescription+'</td>' +
        '    </tr>' +
        '    <tr>' +
        '      <td style="max-width:30%;text-align:right;font-weight:bold">Ancestral groups</td>' +
        '      <td style="text-align:left">'+ancestralGroups.join(', ')+'</td>' +
        '    </tr>' +
        '  </tbody>' +
        '</table>';

        sudyContent.html(content);*/
        sudyContent.html(studyTable);
        studyRow.append(sudyContent);

        $("#association-table-body").append(studyRow);
    });
}


function getSnpTraits(data) {

    // Fetch data //
    var traits = [];
    var mappedtraits = {};
    var mappedtraitsUri = {};
    var synonymtraits = {};
    var studytraits = {};
    $.each(data, function(index,asso) {
        // Trait
        var trait = asso.traitName_s;
        if (jQuery.inArray(trait, traits) == -1) {
            traits.push(trait);
        }

        // Mapped trait(s)
        mappedtraits[trait] = asso.mappedLabel;
        mappedtraitsUri[trait] = asso.mappedUri;

        // Synonym trait(s)
        synonymtraits[trait] = asso.synonym;

        // Study trait(s)
        var author = asso.author_s;
        var publicationDate = asso.publicationDate;
        var pubDate = publicationDate.split("-");
        var pubmedId = asso.pubmedId;
        var study = setQueryUrl(author, author + " - " + pubDate[0]);
        study += setExternalLink(EPMC+pubmedId);
        if (jQuery.inArray(study, studytraits[trait]) == -1) {
            if (!studytraits[trait]) {
                studytraits[trait] = [];
            }
            studytraits[trait].push(study);
        }
    });


    // Display/print data //
    $("#diseasetrait_count").html(traits.length);

    if (traits.length == 1) {
        $("#diseasetrait_label").html("Trait");
    }

    traits.sort();

    $.each(traits, function(index,trait) {
        var row = $('<tr/>');

        // Trait
        row.append(newCell(setQueryUrl(trait)));

        // Mapped trait(s)
        if (mappedtraits[trait]) {
            var mappedtrait = [];
            $.each(mappedtraits[trait], function(index, mtrait) {
                var mapped_html = setQueryUrl(mtrait);
                mapped_html += setExternalLink(mappedtraitsUri[trait][index]);
                mappedtrait.push(mapped_html);
            });
            row.append(newCell(mappedtrait.join(',<br />')));
        } else {
            row.append(newCell('-'));
        }

        // Synonym trait(s)
        if (synonymtraits[trait]) {
            var synonymtrait = [];
            $.each(synonymtraits[trait], function(index, syntrait) {
                synonymtrait.push(setQueryUrl(syntrait));
            });
            row.append(newCell(synonymtrait.join(', ')));
        } else {
            row.append(newCell('-'));
        }

        // Study trait(s)
        if (studytraits[trait]) {
            row.append(newCell(studytraits[trait].join(',<br />')));
        } else {
            row.append(newCell('-'));
        }
        $("#diseasetrait-table-body").append(row);
    });
}

function setQueryUrl(query, label) {
    if (!label) {
        label = query;
    }
    return '<a href="/gwas/search?query='+query+'">'+label+'</a>';
}

function setExternalLink(url) {
    return '<span style="padding-left:5px"><a href="'+url+'" target="_blank"><img alt="externalLink" class="link-icon" src="../icons/external1.png"/></a></span>';
}

function newCell(content) {
    return $("<td></td>").html(content);
}

function showHideStudy(studyId) {
    //return '<button title="Click to show/hide more study information" class="row-toggle btn btn-default btn-xs accordion-toggle" data-toggle="collapse" data-target=".study-'+studyId+'.hidden-study-row" aria-expanded="false" aria-controls="study:'+studyId+'">' +
    var study_button = $("<button></button>");
    study_button.attr('title', 'Click to show/hide more study information');
    study_button.attr('id', 'button-study-'+studyId);
    study_button.attr('onclick', 'toggleDiv("study-'+studyId+'")');
    study_button.addClass("btn btn-default btn-xs btn-study");
    study_button.html('<span class="glyphicon glyphicon-plus tgb"></span>');

    return study_button;
}

function getLDPopulations() {
    $.getJSON('http://rest.ensembl.org/info/variation/populations/homo_sapiens?content-type=application/json;filter=LD')
            .done(function(data) {
                console.log(data);
                processLDPopulationData(data);
            });
    console.log("Ensembl REST query done to retrieve LD populations");
}

function processLDPopulationData(data) {
    $.each(data, function(index, pop) {
        var popName = pop.name;
        var popLabel = popName.split(':')[2] + " - " + pop.description;
        $('#ld-population-selection').append($("<option>").attr("value", popName).html(popLabel));
    });
}

function setState(state) {
    var loading = $('#loading');
    var noresults = $('#noResults');
    var results = $('#results');
    console.log("Search state update...");
    console.log(state);
    switch (state.value) {
        case 0:
            loading.show();
            noresults.hide();
            results.hide();
            break;
        case 1:
            loading.hide();
            noresults.show();
            results.hide();
            break;
        case 2:
            loading.hide();
            noresults.hide();
            results.show();
            break;
        default:
            console.log("Unknown search state; redirecting to search page");
            window.location = "snp";
    }
}