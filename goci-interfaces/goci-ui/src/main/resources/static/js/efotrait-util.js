/**
 * Created by xin on 19/04/2017.
 */


var global_color_url = 'http://wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMapping/';
var global_ols_api = 'http://www.ebi.ac.uk/ols/api/';
var global_ols_seach_api =  global_ols_api + 'search';
var global_ols_api_all_descendant_limit = 99999;
var global_efo_info_tag_id = '#efo-info';
var global_efo_selected_tag_id = '#selected-efos';
var global_pmc_api = 'http://www.ebi.ac.uk/europepmc/webservices/rest/search';


new Clipboard('#sharable_link_btn', {
    text: function (trigger) {
        return $('#sharable_link').val();
    }
});


_peak = function() {
    console.log($(global_efo_info_tag_id).data());
    console.log($(global_efo_selected_tag_id).data());
}

_cleanDataTag = function(tagID){
    console.debug('before clean...')
    _peak();
    tagID=tagID || global_efo_info_tag_id;
    Object.keys($(tagID).data()).forEach(function(i){
        $(tagID).removeData(i)
    })
    console.debug('after clean...')
    _peak()
}

//get data asyn with promise
function promiseGet(url, params,debug) {
    if(debug == undefined){
        debug = false
    }

    if (!url.startsWith("http")) {
        url = window.location.origin + url
    }

    // Return a new promise.
    return new Promise(function(resolve, reject) {
        // Do the usual XHR stuff
        var req = new XMLHttpRequest();

        params = params || {}
        if (Object.keys(params).length > 0) {
            var params_str = '';
            Object.keys(params).forEach(function(key, index) {
                params_str = params_str + '&' + key + '=' + this[key];
            }, params);
            url = url + '?' + params_str;
        }

        req.open('GET', url);
        if(debug){
            console.log('promise get from :' + url);
        }

        req.onload = function() {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
                // Resolve the promise with the response text
                resolve(req.response);
            }
            else {
                // Otherwise reject with the status text
                // which will hopefully be a meaningful error
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function() {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send();
    });
}

//load ontology info
getOntologyInfo=function() {
    var dataPromise = getDataFromTag(global_efo_info_tag_id, 'ontologyInfo');
    if (dataPromise == undefined) {
        //lazy load
        console.log('Loading Ontology Info...')
        dataPromise = promiseGet('http://www.ebi.ac.uk/ols/api/ontologies',
                                 {'size': 99999}).then(JSON.parse).then(function(response) {
            var ontologyInfo = {};
            response._embedded.ontologies.forEach(function(d) {
                ontologyInfo[d.ontologyId] = d;
            })

            return ontologyInfo;
        }).catch(function(err) {
            console.error('Error when loading ontology info! ' + err);
        })

        //add to tag
        $(global_efo_info_tag_id).data('ontologyInfo',dataPromise);
    }else{
        console.debug('Loading Ontology Info from cache.')
    }
    return dataPromise;
}

getPrefix2OntologyId=function(){
    var dataPromise = getDataFromTag(global_efo_info_tag_id, 'prefix2ontId');
    if (dataPromise == undefined) {
        //lazy load
        console.log('Loading prefix2ontId...')
        dataPromise = getOntologyInfo().then(function(ontoInfo){
            var prefix2ontid = {}
            Object.keys(ontoInfo).forEach(function(ontId){
                prefix2ontid[ontoInfo[ontId].config.baseUris[0].split('/').slice(-1)[0].split('_')[0]] = ontId;
            })
            return prefix2ontid;
        }).catch(function(err){
            console.error('Error when loading prefix2ontId! ' + err);
        })
        //add to tag
        $(global_efo_info_tag_id).data('prefix2ontId',dataPromise);
    }else{
        console.debug('Loading prefix2ontId from cache.')
    }
    return dataPromise;
}

getOntologyIdByPrefix = function(prefix) {
    return getPrefix2OntologyId().then(function(p2o){
        return p2o[prefix]
    })
}

getIriByShortForm = function(shortForm){
    var prefix = shortForm.split('_')[0];
    var id = shortForm.split('_')[1];
    var ont = getOntologyByShortForm(shortForm);

    return ont.then(function(ontName){
        return getOntologyInfo().then(function(ontologies){
            return ontologies[ontName].config.baseUris[0] + id;
        })
    }).catch(function(err){
        console.log('Error finding iri for term ' + shortForm + '. ' + err);
    })
}

getOntologyByShortForm = function(shortForm){
    var prefix = shortForm.split('_')[0];
    var id = shortForm.split('_')[1];
    p = getOntologyIdByPrefix(prefix);

    return p.then(function(ontology){
        return ontology;
    }).catch(function(err){
        console.log('Error finding iri for term ' + shortForm + '. ' + err);
    })
}

getRelatedTerms = function(efoid){
    queryRelatedTerms = function(efoid){
        console.log('Loading related terms...')
        return searchOLS(efoid,{
            'ontology': 'efo',
            'fieldList': 'iri,ontology_name,ontology_prefix,short_form,description,id,label,is_defining_ontology,obo_id,type,logical_description'
        }).then(function(data){
            var terms = {};
            data.response.docs.forEach(function(d) {
                d.obo_id = d.obo_id.replace(":", "_")
                terms[d.obo_id] = d
            })
            var tmp={};
            tmp[efoid] = terms;
            return tmp;
        }).catch(function(err){
            console.error('Error when loading related terms! ' + err);
        })
    }

    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'relatedEFOs');

    return dataPromise.then(function(data){
        //query if data not exist
        if($.inArray(efoid,Object.keys(data)) == -1){
            dataPromise = queryRelatedTerms(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'relatedEFOs');
                return data[efoid];
            })
        }else{
            console.debug('Loading related terms from cache.')
            return data[efoid]
        }
    })
}

getColourForEFO = function(efoid) {
    queryColour = function(efoid){
        console.log('Loading Colour...')
        return promiseGet(global_color_url + efoid).then(JSON.parse).then(function(response) {
            var tmp = {};
            tmp[efoid] = response;
            return tmp;
        })
    }

    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efo2colour');

    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo colour is not currently loaded
            console.log('Loading Colour...')
            dataPromise = queryColour(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efo2colour');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading Colour from cache.')
            return data[efoid]
        }
    })
}

getEFOInfo = function(efoid){
    queryEFOInfo = function(efoid){
        return getOLSLink(efoid).then(function(url){
            return promiseGet(url).then(JSON.parse).then(function(response) {
                var tmp = {};
                tmp[efoid] = response;
                return tmp;
            }).catch(function(err){
                console.debug('error when loading efo info for ' + efoid + '. ' + err);
            })
        })
    }

    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efoInfo');
    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo info is not currently loaded
            console.log('Loading efoInfo for ' + efoid)
            dataPromise = queryEFOInfo(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efoInfo');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading efoInfo from cache for ' + efoid);
            return data[efoid]
        }
    })
}

getOLSLink = function(efoid){
    var ont = getOntologyByShortForm(efoid);
    var iri = getIriByShortForm(efoid);
    return Promise.all([ont,iri]).then(function(arrayPromise) {
        var url = global_ols_api + 'ontologies/' + arrayPromise[0] + '/terms/' +
                encodeURIComponent(encodeURIComponent(arrayPromise[1]));
        return url
    })
}

getHierarchicalDescendants = function(efoid){
    queryDescendant = function(efoid){
        var efoinfo = getEFOInfo(efoid)
        return efoinfo.then(function(response){
            if (response.has_children) {
                //xintodo size >=1000 is set to 1000 by ols, ask?
                return promiseGet(response._links.hierarchicalDescendants.href + '?size=' +
                                  global_ols_api_all_descendant_limit).then(JSON.parse).then(function(response) {
                    var terms = {};
                    response._embedded.terms.forEach(function(d, i) {
                        terms[d.short_form] = d;
                    })
                    var tmp = {}
                    tmp[efoid] = terms;
                    return tmp;
                })
            }
            else {
                console.log('no descendant found for ' + efoid);
                return new Promise(function(resolve, reject) {
                    var tmp = {};
                    tmp[efoid] = {};
                    resolve(tmp);
                })
            }
        })
    }



    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efoDecendants');

    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo descendant not currently loaded
            console.log('Loading descendant for ' + efoid)
            dataPromise = queryDescendant(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efoDecendants');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading descendant from cache for ' + efoid);
            return data[efoid]
        }
    })
}

//return a data promise containing all available efos. lazy load.
getAvailableEFOs=function(){
    var dataPromise = getDataFromTag(global_efo_info_tag_id,'availableEFOs');
    if(dataPromise == undefined){
        //lazy load
        console.log('Loading all available EFOs in Gwas Catalog...')
        dataPromise =  promiseGet('/gwas/api/search/efotrait', {
            'q': '*:*',
            'max': 9999,
            'fq': 'resourcename:efotrait',
            'group.limit': 9999,
            'fl' : 'shortForm'
        }).then(JSON.parse).then(function(data) {
            $.each(data.grouped.resourcename.groups, function(index, group) {
                switch (group.groupValue) {
                    case "efotrait":
                        available_efos_docs = group.doclist.docs;
                        break;
                    default:
                }
            });
            var tmp = {};
            available_efos_docs.forEach(function(doc) {
                if (doc.shortForm) {
                    tmp[doc.shortForm[0]] = doc;
                }
            });

            return tmp;
        }).catch(function(err){
            console.error('Error when loading all available EFOs! ' + err);
        })

        //add to tag
        $(global_efo_info_tag_id).data('availableEFOs',dataPromise);

    }else{
        console.debug('Loading all available EFOs from cache.')
    }
    return dataPromise;
}

addPromiseToTag = function(tagID, promise, key, overwriteWarning) {
    overwriteWarning = overwriteWarning || false;

    var oldPromise = $(tagID).data(key) || new Promise(function(resolve){
                resolve({})
            });


    var result;
    var p = Promise.all([oldPromise,promise]).then(function(arrayPromise){
        oldData = arrayPromise[0]
        newData = arrayPromise[1]

        if(overwriteWarning){
            var overlap = Object.keys(oldData).filter(function(n) {
                return Object.keys(newData).indexOf(n) > -1;
            });
            if (overlap.length > 0) {
                overlap.forEach(function(d) {
                    console.log(d + 'will be overwritten.')
                })
            }
        }
        result = $.extend({}, oldData, newData)
        return result;
    });


    p.then(function(){
        $(global_efo_info_tag_id).data(key,p);
        console.debug('adding promise to ' + key + ' tag ' + tagID);
    })
}

getPromiseFromTag = function(tagID,key){
    var dataPromise =  getDataFromTag(tagID,key)
    if (dataPromise == undefined) {
        dataPromise = new Promise(function(resolve){
            resolve({});
        })
    }
    return dataPromise;
}

addDataToTag = function(tagID, hash, key, overwriteWarning) {
    var old = $(tagID).data(key) || {};
    overwriteWarning = overwriteWarning || false;
    if (overwriteWarning) {
        var result = Object.keys(old).filter(function(n) {
            return Object.keys(hash).indexOf(n) > -1;
        });
        if (result.length > 0) {
            result.forEach(function(d) {
                console.log(d + 'will be overwritten.')
            })
        }
    }
    result = $.extend({}, old, hash)
    if (key) {
        $(tagID).data(key, result)
    }
    else {
        $(tagID).data(result)
    }

    return result;
}

getDataFromTag = function(tagID, key) {
    key = key || ''
    var data = $(tagID).data();
    if (key == '')
        return data
    if (Object.keys(data).valueOf(key) == -1){
        console.warn('The requested data with key ' + key + ' to ' + tagID + ' does not exist!');
        return undefined;
    }
    return data[key]
}

getMainEFO = function(){
    return getDataFromTag(global_efo_info_tag_id,'mainEFOInfo')
}


//When ploting with descendants, the number of efos increase drmatically.
//This is to remove the efos that have no annotation in GWAS catalog,
//Thus querying/ploting only those have at lease one annotation
filterAvailableEFOs = function(toBeFilter) {
    return getAvailableEFOs().then(function(availableEFOs){
        if(availableEFOs)
            return toBeFilter.filter(function(n) {
                return Object.keys(availableEFOs).indexOf(n) !== -1;
            });
    })
}

searchOLS = function(keyword,params){
    params = $.extend({}, params, {'q':keyword});
    return promiseGet(global_ols_seach_api,params).then(JSON.parse).then(function(data){
        console.log("data returned by ols search:");
        console.log(data);
        return data;
    })

}


//pmc
getFromPMC= function(pubmed_id){
    return promiseGet(global_pmc_api,
                      {
                          'query': 'ext_id:'+pubmed_id + '%20src:med',
                          'resulttype' :  'core',
                          'format' : 'json'
                      }).then(JSON.parse);
}


//
getMainEFO = function(){
    return $('#query').text();
}

getCurrentSelected = function(){
    return $(global_efo_info_tag_id).data('selectedEfos');
}


function updateSharableLink(){
    $("#sharable_link").attr('value', window.location.origin + window.location.pathname + '?included=' +
                             Object.keys(getCurrentSelected()).join(','));
    hideLoadingOverLay('#sharable_link_btn');
}


// find studies for a given efoid from the solr result, return empty hash if the solr result is undefined
// require solr result.
findStudiesForEFO = function(efoid) {
    var studies = {};
    if(data_highlighting==undefined)
        return studies;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^study/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1] == efoid) {
                    studies[key] = efoid;
                }
            });
        }
    })

    $.each(data_study.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(studies)) != -1) {
            studies[value.id] = value;
        }
    })
    return studies;
}



// find association for a given efoid from the solr result, return empty hash if the solr result is undefined
// require solr result.
findAssociationForEFO = function(efoid){
    var associations = {};

    if(data_highlighting==undefined)
        return associations;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^association/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1] == efoid) {
                    associations[key] = efoid;
                }
            });
        }
    })

    $.each(data_association.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(associations)) != -1) {
            associations[value.id] = value;
        }
    })
    return associations;
}



findStudiesForEFOs = function(efoids){
    var studies = {};
    if(data_highlighting==undefined)
        return studies;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^study/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (efoids.indexOf(highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1]) != -1) {
                    studies[key] = key;
                }
            });
        }
    })

    $.each(data_study.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(studies)) != -1) {
            studies[value.id] = value;
        }
    })
    return studies;
}

findAssociationForEFOs = function(efoids){
    var associations = {};

    if(data_highlighting==undefined)
        return associations;
    Object.keys(data_highlighting).forEach(function(key) {
        if (/^association/.test(key)) {
            data_highlighting[key].efoLink.forEach(function(highlightedefo) {
                if (efoids.indexOf(highlightedefo.match(/<b>(\w*_\d*)<\/b>/)[1]) != -1) {
                    associations[key] = key;
                }
            });
        }
    })

    $.each(data_association.docs, function(index, value) {
        if ($.inArray(value.id, Object.keys(associations)) != -1) {
            associations[value.id] = value;
        }
    })
    return associations;
}

//an association can be annotated to more than one efo. find them all.
//require solr result.
_findAllEFOsforAssociation = function(association_id,data) {
    var terms = data.highlighting[association_id].shortForm;
    terms.forEach(function(d, i) {
        //xintodo only efo?
        terms[i] = d.match(/<b>(\w*_\d*)<\/b>/)[1];
    })
    return terms;
}

//work out which efo is the highlighted study
//current find the first one come in the list
//require solr result.
_findhighlightEFOForAssociation = function(traits, association) {
    return traits[0];
}

//work out which study is the highlighted study
//currently find the most recent one
//require solr result.
findHighlightedStudiesForEFO = function(efoid) {
    var studies = findStudiesForEFO(efoid);
    var publishDate = {};

    $.each(studies, function(index, value) {
        publishDate[value.id] = value.publicationDate;
    })

    var keysSorted = Object.keys(publishDate).sort(function(a, b) {
        return Date.parse(publishDate[b]) - Date.parse(publishDate[a])
    })
    return studies[keysSorted[0]];
//                return studies;
}

//return html to display information for a data point in the locusplot when moveover the datapoint
buildLocusPlotPopoverHTML = function(association){
    _addNameValuePairHTML = function(name,value){
        if(value instanceof Array){
            value = value.join(',')
        }
        return "<div>" + name + ":<strong> " +  value + "</strong></div>";
    }
    var text = $('<div/>');
    text.append(_addNameValuePairHTML('CatalogPublishDate',new Date(association.catalogPublishDate).toLocaleDateString()));
    text.append(_addNameValuePairHTML('author_s',association.author_s));
    text.append(_addNameValuePairHTML('chromLocation',association.chromLocation));
    text.append(_addNameValuePairHTML('countriesOfRecruitment',association.countriesOfRecruitment));
    text.append(_addNameValuePairHTML('initialSampleDescription',association.initialSampleDescription));
    text.append(_addNameValuePairHTML('entrezMappedGenes',association.entrezMappedGenes));
    text.append(_addNameValuePairHTML('mappedLabel',association.mappedLabel));
    text.append(_addNameValuePairHTML('pValueExponent',association.pValueExponent));
    text.append(_addNameValuePairHTML('pubmedId',association.pubmedId));
    text.append(_addNameValuePairHTML('reportedGene',association.reportedGene));
    text.append(_addNameValuePairHTML('title',association.title));
    return text.prop('outerHTML');
}

//display highlighted study
displayHighlightedStudy = function(highlightedStudy) {
    $('#efotrait-highlighted-study-title').html(highlightedStudy.title);
    $('#efotrait-highlighted-study-author').html(highlightedStudy.author_s + ' (' +
                                                 setExternalLink(EPMC + highlightedStudy.pubmedId,
                                                                 'PMID:' + highlightedStudy.pubmedId) +
                                                 ')');
    $('#efotrait-highlighted-study-catalogPublishDate').html(new Date(highlightedStudy.catalogPublishDate).toLocaleDateString());
//                $('#efotrait-highlighted-st udy-initialSampleDescription').html(highlightedStudy.initialSampleDescription);
//                $('#efotrait-highlighted-study-replicateSampleDescription').html(highlightedStudy.replicateSampleDescription);
//                $("#efotrait-highlighted-study-all").html(longContent("efotrait-highlighted-study-all_div",
//                                                                      JSON.stringify(highlightedStudy),'all'));

    getFromPMC(highlightedStudy.pubmedId).then(function(data) {
        var paperDetail = data.resultList.result[0];
//                    $("#efotrait-highlighted-study-abstract").html(longContent("efotrait-highlighted-study-abstract_div",
//                                                                               paperDetail.abstractText,''));
//         $('#efotrait-highlighted-study-abstract').html(createPopover('detail',
//                                                                      'abstract',
//                                                                      paperDetail.abstractText));
        $('#efotrait-highlighted-study-abstract').html(paperDetail.abstractText);

        return paperDetail;
    }).catch(function(err) {
        console.warning('Error when loading data from PMC! ' + err);
    }).then(function(){
        hideLoadingOverLay('#highlight-study-panel-loading');
    });
}

//get efo info and display in efo summary
displayEfoTraitInfo = function(efotraitId) {
    getEFOInfo(efotraitId).then(function(response) {
        var efotrait_link = response.iri;
        var efotrait_id = efotraitId;
        var synonym = response.synonyms;
        var efotrait_label = response.label;
        addDataToTag(global_efo_info_tag_id, response, 'mainEFOInfo');
        $("#efotrait-description").html(displayArrayAsList(response.description));
        $("#efotrait-id").html(setExternalLink(efotrait_link, efotrait_id));
//            $("#efotrait-label").html(efotrait_label);
        $("#efotrait-label").html(createPopover(efotrait_label,
                                                'description',
                                                displayArrayAsList(response.description)));
        if (synonym) {
            if (synonym.length > list_min) {
                $("#efotrait-synonym").html(longContentList("gwas_efotrait_synonym_div",
                                                            synonym,
                                                            'synonyms'));
            }
            else {
                $("#efotrait-synonym").html(synonym.join(", "));
            }
        }
    }).catch(function(err) {
        console.warning('Error loading efo info from OLS. ' + err);
    }).then(function() {
        hideLoadingOverLay('#summary-panel-loading')
    })
}



//display association table
function displayEfotraitAssociations(data, cleanBeforeInsert) {
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
        $("#association-table-body tr").remove();
    }

    $.each(data, function(index,asso) {

        var row = $('<tr/>');

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
        riskAllele = setExternalLinkText('/gwas/beta/variants/' + riskAllele_rsid,riskAlleleLabel);

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
        var ci = (asso.range) ? asso.range : '-';
        row.append(newCell(ci));
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
        study += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
        row.append(newCell(study));

        var studyId = asso.studyId;

        // Populate the table
        $("#association-table-body").append(row);
        hideLoadingOverLay('#association-table-loading')
    });
}

//display study table
function displayEfotraitStudies(data, cleanBeforeInsert) {
    //by default, we clean the table before inserting data
    if (cleanBeforeInsert === undefined) {
        cleanBeforeInsert = true;
    }


    var study_ids = [];
    if(cleanBeforeInsert){
        $("#study-table-body tr").remove();
    }
    $.each(data, function(index, asso) {
        var study_id = asso.id;
        if (jQuery.inArray(study_id, study_ids) == -1) {

            var row = $('<tr/>');

            study_ids.push(study_id);

            // Author
            var author = asso.author_s;
            var publicationDate = asso.publicationDate;
            var pubDate = publicationDate.split("-");
            var pubmedId = asso.pubmedId;
            var study_author = setQueryUrl(author, author);
            study_author += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
            row.append(newCell(study_author));

            // Publication date
            var p_date = asso.publicationDate;
            var publi = p_date.split('T')[0];
            row.append(newCell(publi));

            // Journal
            row.append(newCell(asso.publication));

            // Title
            row.append(newCell(asso.title));

            // Initial sample desc
            var initial_sample_text = '-';
            if (asso.initialSampleDescription) {
                initial_sample_text = displayArrayAsList(asso.initialSampleDescription.split(', '));
            }
            row.append(newCell(initial_sample_text));

            // Replicate sample desc
            var replicate_sample_text = '-';
            if (asso.replicateSampleDescription) {
                replicate_sample_text = displayArrayAsList(asso.replicateSampleDescription.split(', '));
            }
            row.append(newCell(replicate_sample_text));

            // ancestralGroups
            var ancestral_groups_text = '-';
            if (asso.ancestralGroups) {
                ancestral_groups_text = displayArrayAsList(asso.ancestralGroups);
            }
            row.append(newCell(ancestral_groups_text));

            // Populate the table
            $("#study-table-body").append(row);
        }
    });
    // Study count //
    $(".study_count").html(study_ids.length);

    if (study_ids.length == 1) {
        $(".study_label").html("Study");
    }

    hideLoadingOverLay('#study-table-loading')
}

//Load overlay
//https://gasparesganga.com/labs/jquery-loading-overlay/
showLoadingOverLay = function(tagID){
    var options = {
        color: "rgba(255, 255, 255, 0.8)",   // String
        custom: "",                // String/DOM Element/jQuery Object
        fade: [100, 3000],                      // Boolean/Integer/String/Array
        fontawesome: "",                          // String
//            image: "data:image/gif;base32,...",  // String
        imagePosition: "center center",             // String
        maxSize: "100px",                  // Integer/String
        minSize: "20px",                    // Integer/String
        resizeInterval: 10,                       // Integer
        size: "20%",                       // Integer/String
        zIndex: 1000,                        // Integer
    }
    return $(tagID).LoadingOverlay("show",options);
}

hideLoadingOverLay = function(tagID){
    return $(tagID).LoadingOverlay("hide", true);
}




/*
ols component
 */
//auto-complete/search box in the expension panel
$(document).ready(function() {
    var app = require("ols-autocomplete");
    var instance = new app();
    options = {
        action: function(relativePath, suggestion_ontology, type, iri) {
//                console.log("In overwritten function")
//                console.log("Relative Path: " + relativePath)
//                console.log("Suggested Ontology: " + suggestion_ontology)
//                console.log("Type (optional): " + type)
//                console.log("iri (optional): " + iri)
            elements = {};
            elements['EFO_' + iri.split("EFO_")[1]] = 'EFO_' + iri.split("EFO_")[1];
            //xintodo auto load
            addEFO(elements);
        }
    }
    instance.start(options)
});


//ols-tree
var app = require("ols-treeview");
var instance = new app();

options = {
    checkbox: false,
    checkbox_cascade: '',
    checkbox_three_state: false,
    checkbox_keep_selected_style: false,
    onclick: function(params, node, relativePath, termIRI, type){
        // var isComfirmed =  confirm('Are you sure?');
        // if(isComfirmed){
        //     var clicked = node.node.original.iri;
        //     var efoid = clicked.split('/').slice(-1)[0];
        //     var tmp = {}
        //     tmp[efoid] = clicked;
        //     addEFO(tmp);
        // }else{
        //     console.debug('not comfirmed');
        // }
        var clicked = node.node.original.iri;
        var efoid = clicked.split('/').slice(-1)[0];
        var tmp = {}
        tmp[efoid] = clicked;
        addEFO(tmp);
    },
}


var searchTerm = getMainEFO();
// initialise the tree
instance.draw($("#term-tree"),
              false,
              "efo",
              "terms",
              "http://www.ebi.ac.uk/efo/" + searchTerm,
              "http://www.ebi.ac.uk/ols/",
              options);




//
// findEFOInfoLocal = function(efoid) {
//     var data = getDataFromTag(global_efo_info_tag_id, 'efoInfo')
//     if(data==undefined)
//         return undefined;
//     return data[efoid]
// }
//
// findOntologyInfoLocal = function(ontologyId) {
//     var data = getDataFromTag(global_efo_info_tag_id, 'ontologyInfo')
//     if(data==undefined)
//         return undefined;
//     return data[ontologyId]
// }
//
// findOntologyInfo = function(ontologyId){
//     ontologyId = ontologyId.toLowerCase();
//     var localefoinfo = findOntologyInfoLocal(ontologyId)
//     if (localefoinfo) {
//         return new Promise(function(resolve, reject) {
//             resolve(localefoinfo);
//         })
//     }
//
//     return promiseGet('http://www.ebi.ac.uk/ols/api/ontologies',{'size':99999}).then(JSON.parse).then(function(response){
//         var ontologyInfo = {};
//         response._embedded.ontologies.forEach(function(d){
//             ontologyInfo[d.ontologyId] = d;
//         })
//         addDataToTag(global_efo_info_tag_id,ontologyInfo,'ontologyInfo')
//         return ontologyInfo[ontologyId];
//     }).catch(function(err){
//         console.error('Error when querying ontology info. ' + err);
//         return err;
//     })
// }
//
// findIriByShortForm = function(shortForm){
//     var prefix = shortForm.split('_')[0];
//     var id = shortForm.split('_')[1];
//     var p = findOntologyIdByPrefix(prefix)
//
//     return p.then(function(ontology){
//         return findOntologyInfo(ontology).then(function(o){
//             return {'ontology':ontology,'iri' : o.config.baseUris[0] + id};
//         })
//     }).catch(function(err){
//         console.log('Error finding iri for term ' + shortForm + '. ' + err);
//     })
// }
//
// fetchOntologyInfo=function(){
//     return promiseGet('http://www.ebi.ac.uk/ols/api/ontologies',{'size':99999}).then(JSON.parse).then(function(response){
//         var ontologyInfo = {};
//         var prefix2ontid = {}
//         response._embedded.ontologies.forEach(function(d){
//             ontologyInfo[d.ontologyId] = d;
//             prefix2ontid[d.config.baseUris[0].split('/').slice(-1)[0].split('_')[0]] = d.ontologyId;
//         })
//         addDataToTag(global_efo_info_tag_id,ontologyInfo,'ontologyInfo')
//         addDataToTag(global_efo_info_tag_id,prefix2ontid,'prefix2ontid')
//         return response;
//     }).catch(function(err){
//         console.error('Error when querying ontology info. ' + err);
//         return err;
//     })
// }
//
// findOntologyIdByPrefix = function(prefix) {
//     if(getDataFromTag(global_efo_info_tag_id, 'ontologyInfo')==undefined || getDataFromTag(global_efo_info_tag_id, 'prefix2ontid') == undefined){
//         return fetchOntologyInfo().then(function(){
//             return getDataFromTag(global_efo_info_tag_id, 'prefix2ontid')[prefix]
//         });
//     }else{
//         return new Promise(function(resolve, reject) {
//             resolve(getDataFromTag(global_efo_info_tag_id, 'prefix2ontid')[prefix]);
//         })
//     }
// }
//
//
// findEFOInfo = function(shortForm) {
//     //EFO does not follow the same naming convension
//     //need to due with terms such as 'CHEBI_17234'
//     console.debug(shortForm);
//     return findIriByShortForm(shortForm).then(function(response) {
//         var ont = response.ontology;
//         var iri = response.iri;
//
//         var localefoinfo = findEFOInfoLocal(shortForm)
//         if (localefoinfo) {
//             return new Promise(function(resolve, reject) {
//                 resolve(localefoinfo);
//             })
//         }
//
//         var url = global_ols_api + 'ontologies/' + ont + '/terms/' + encodeURIComponent(encodeURIComponent(iri));
//         return promiseGet(url).then(JSON.parse).then(function(response) {
//             var tmp = {};
//             tmp[shortForm] = response;
//             addDataToTag(global_efo_info_tag_id, tmp, 'efoInfo');
//             return response;
//         }).catch(function(err){
//             console.debug('error when loading efo info for ' + shortForm + '. ' + err);
//             return err;
//         })
//
//     });
// }
//
// findAllHierarchicalDescendantsLocal = function(efoid) {
//     var decendants =getDataFromTag(global_efo_info_tag_id,'efoDecendants');
//     if (decendants == undefined)
//         return decendants
//     return decendants[efoid];
// }
//
// findAllHierarchicalDescendants = function(efoid) {
//     return findEFOInfo(efoid).then(function(response) {
//         console.log('looking for descendants for ' + efoid);
//         console.log(response);
//         if (response.has_children) {
//             var localDescentandInfo = findAllHierarchicalDescendantsLocal(efoid)
//             if (localDescentandInfo) {
//                 return new Promise(function(resolve, reject) {
//                     resolve(localDescentandInfo);
//                 })
//             }
//             //xintodo size >=1000 is set to 1000 by ols, ask?
//             return promiseGet(response._links.hierarchicalDescendants.href + '?size=' +
//                               global_ols_api_all_descendant_limit).then(JSON.parse).then(function(response) {
//                 console.log('all descendants: ');
//                 console.log(response);
//                 var terms = {};
//                 response._embedded.terms.forEach(function(d, i) {
//                     terms[d.short_form] = d;
//                 })
//                 var tmp = {}
//                 tmp[efoid] = Object.keys(terms);
//                 addDataToTag(global_efo_info_tag_id, terms, 'efoInfo');
//                 addDataToTag(global_efo_info_tag_id, tmp, 'efoDecendants')
//                 return response._embedded.terms;
//             })
//         }
//         else {
//             console.log('no descendant.');
//             return new Promise(function(resolve, reject) {
//                 resolve(JSON.parse("[]"));
//             }).then(function() {
//                 tmp = {}
//                 tmp[efoid] = []
//                 addDataToTag(global_efo_info_tag_id, tmp, 'efoDecendants')
//             })
//         }
//     })
// }
//
// findRelatedTermsoLocal = function(efoid) {
//     var data = getDataFromTag(global_efo_info_tag_id, 'relatedEFOs')
//     if(data==undefined)
//         return undefined;
//     return data[efoid]
// }
//
// findRelatedTerms = function(efoid){
//     var relatedTermsLocal = findRelatedTermsoLocal(efoid)
//     if (relatedTermsLocal) {
//         return new Promise(function(resolve, reject) {
//             resolve(relatedTermsLocal);
//         })
//     }
//
//     return searchOLS($('#query').text(),{
//         'ontology': 'efo',
//         'fieldList': 'iri,ontology_name,ontology_prefix,short_form,description,id,label,is_defining_ontology,obo_id,type,logical_description'
//     }).then(function(data){
//         var terms = {};
//         data.response.docs.forEach(function(d) {
//             d.obo_id = d.obo_id.replace(":", "_")
//             terms[d.obo_id] = d
//         })
//         addDataToTag(global_efo_info_tag_id,terms,'relatedEFOs');
//         return terms;
//     }).catch(function(err){
//         console.error('Error when finding related term from OLS for keywork ' + keyword + '. ' + err);
//     });
// }
//
