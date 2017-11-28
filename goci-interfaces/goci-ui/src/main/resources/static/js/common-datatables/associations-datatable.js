/** First refactoring action: common js - DRY. From Xin original code
 *  Datatable action to rendering association panel.
 * */

/**
 * display association table
 * @param {Object} data - association solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displayDatatableAssociations(data, cleanBeforeInsert) {
    //by default, we clean the table before inserting data
    if (cleanBeforeInsert === undefined) {
        cleanBeforeInsert = true;
    }
    
    var asso_count = data.length;
    
    $(".association_count").html(asso_count);
    
    if (asso_count == 1) {
        $(".association_label").html("Association");
    }
    
    if(cleanBeforeInsert){
        $('#association-table').bootstrapTable('removeAll');
    }
    
    var data_json = []
    $.each(data, function(index,asso) {
        
        //if association does not have rsid, skip
        if(asso.strongestAllele == undefined){return true};
        
        var tmp = {};
        // Risk allele
        var riskAllele = asso.strongestAllele[0];
        var riskAlleleLabel = riskAllele;
        var riskAllele_rsid = riskAllele;
        if (riskAlleleLabel.match(/\w+-.+/)) {
            riskAlleleLabel = riskAllele.split('-').join('-<b>')+'</b>';
            riskAllele_rsid = riskAllele.split('-')[0];
        }
        // This is now linking to the variant page instead of the search page
        // riskAllele = setQueryUrl(riskAllele,riskAlleleLabel);
        riskAllele = setExternalLinkText( window.location.pathname.split('/study/')[0] + '/variants/' + riskAllele_rsid,riskAlleleLabel);
        
        tmp['riskAllele'] = riskAllele;
        
        // Risk allele frequency
        tmp['riskAlleleFreq'] = asso.riskFrequency;
        
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
            tmp['pValue'] = pValue;
        } else {
            tmp['pValue'] = '-';
        }
        
        // OR
        var orValue = asso.orPerCopyNum;
        if (orValue) {
            if (asso.orDescription) {
                orValue += " " + asso.orDescription;
            }
            tmp['orValue'] = orValue;
        } else {
            tmp['orValue'] = '-';
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
            tmp['beta'] = beta;
        } else {
            tmp['beta'] = '-';
        }
        
        // CI
        var ci = (asso.range) ? asso.range : '-';
        tmp['beta'] = ci;
        
        
        // Reported genes
        var genes = [];
        var reportedGenes = asso.reportedGene;
        if (reportedGenes) {
            $.each(reportedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            tmp['reportedGenes'] = genes.join(', ');
        } else {
            tmp['reportedGenes'] = '-';
            
        }
        
        // Mapped genes
        var genes = [];
        var mappedGenes = asso.entrezMappedGenes;
        if (mappedGenes) {
            $.each(mappedGenes, function(index, gene) {
                genes.push(setQueryUrl(gene));
            });
            tmp['mappedGenes'] = genes.join(', ');
        } else {
            tmp['mappedGenes'] = '-';
            
        }
        
        // Reported traits
        var traits = [];
        var reportedTraits = asso.traitName;
        if (reportedTraits) {
            $.each(reportedTraits, function(index, trait) {
                traits.push(setQueryUrl(trait));
            });
            tmp['reportedTraits'] = traits.join(', ');
        } else {
            tmp['reportedTraits'] = '-';
        }
        
        // Mapped traits
        var mappedTraits = asso.mappedLabel;
        if (mappedTraits) {
            $.each(mappedTraits, function(index, trait) {
                var link = window.location.pathname.split('/study/')[0]+'/efotraits/' + asso.mappedUri[index].split('/').slice(-1)[0]
                mappedTraits[index] = setExternalLinkText(link,trait)
            });
            tmp['mappedTraits'] = mappedTraits.join(', ');
        } else {
            tmp['mappedTraits'] = '-';
        }
        
        // Study
        var author = asso.author_s;
        var publicationDate = asso.publicationDate;
        var pubDate = publicationDate.split("-");
        var pubmedId = asso.pubmedId;
        var study = setQueryUrl(author, author + " - " + pubDate[0]);
        study += '<div><small>'+setExternalLink(EPMC_URL+pubmedId,'PMID:'+pubmedId)+'</small></div>';
        tmp['study'] = study;
        
        var studyId = asso.studyId;
        
        // Populate the table
        data_json.push(tmp)
        
        
    });
    
    $('#association-table').bootstrapTable({
        exportDataType: 'all',
        columns: [{
            field: 'riskAllele',
            title: 'Risk allele',
            sortable: true
        }, {
            field: 'riskAlleleFreq',
            title: 'RAF',
            sortable: true
        }, {
            field: 'pValue',
            title: 'p-value',
            sortable: true
        },{
            field: 'orValue',
            title: 'OR',
            sortable: true
        },{
            field: 'beta',
            title: 'Beta',
            sortable: true
        },{
            field: 'price',
            title: 'CI',
            sortable: true
        },{
            field: 'mappedGenes',
            title: 'Mapped gene(s)',
            sortable: true
        },{
            field: 'reportedTraits',
            title: 'Reported trait',
            sortable: true
        },{
            field: 'mappedTraits',
            title: 'Mapped trait',
            sortable: true
        },{
            field: 'study',
            title: 'Study',
            sortable: true
        }],
        data: data_json,
        
    });
    
    $('#association-table').bootstrapTable('load',data_json)
    if(data_json.length>5){
        $('#association-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: pageRowLimit,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#association-table-loading')
}

