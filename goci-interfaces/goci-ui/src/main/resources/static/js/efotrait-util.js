/**
 * Created by xin on 19/04/2017.
 */


var global_color_url = 'http://wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMapping/';
var global_ols_api = 'http://www.ebi.ac.uk/ols/api/';
var global_ols = 'http://www.ebi.ac.uk/ols/';
var global_ols_seach_api =  global_ols_api + 'search';
var global_ols_restful_api_ontology =  global_ols_api + 'ontologies';

var global_efo_info_tag_id = '#efo-info';
var global_pmc_api = 'http://www.ebi.ac.uk/europepmc/webservices/rest/search';
var global_oxo_api = 'http://www.ebi.ac.uk/spot/oxo/api/';


new Clipboard('#sharable_link_btn', {
    text: function (trigger) {
        return $('#sharable_link').val();
    }
});


/**
 * debug function
 */
_peak = function() {
    console.log($(global_efo_info_tag_id).data());
}

/**
 * debug function
 */
_cleanDataTag = function(tagID){
    tagID=tagID || global_efo_info_tag_id;
    Object.keys($(tagID).data()).forEach(function(i){
        $(tagID).removeData(i)
    })
}


/**
 * Helper functions for parsing HATEOAS RESTFUL response
 */
var RESTFUL_Response = {
    hasPage : function(response){
        return response.page != undefined;
    },
    hasNext : function(response){
        return response._links.next!= undefined;
    },
    pageNumber : function(response){
        return response.page.number;
    },
    pageSize : function(response){
        return response.page.size;
    },
    totalElements : function(response){
        return response.page.totalElements;
    },
    totalPages :  function(response){
        return response.page.totalPages;
    },
    _embedded : function(response){
        return response._embedded;

    }
}


/**
 * get data asyn with promise
 * @param {String} url
 * @param {hash} params
 * @param {Boolean} debug
 * @returns {Promise}
 */
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


/**
 * get data asyn with promise, recursively querying if the response has 'next' link.
 * The return Promise contains a hash with keys coresponding to the _embedded keys.
 * @param {String} url
 * @param {hash} params
 * @returns {Promise}
 * @example promiseGetRESTFUL_HATEOAS('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000400/hierarchicalDescendants',{'size':5})
 * @example promiseGetRESTFUL_HATEOAS('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000408/hierarchicalDescendants',{'size':1000})
 *
 */
function promiseGetRESTFUL_HATEOAS(url,params){

    //following the link recursively
    var query = function(url,params,result){
        return promiseGet(url,params).then(JSON.parse).then(function(response) {
            if(!result){
                result = []
            }
            result = result.concat(RESTFUL_Response._embedded(response));
            console.log(RESTFUL_Response.pageNumber(response)+1 + '/' + RESTFUL_Response.totalPages(response) + ' done!')
            if(RESTFUL_Response.hasNext(response)){
                var next = response._links.next.href;
                //no need to parse params, this already included in the 'next' url
                return query(next,undefined,result);
            }
            return result
        })
    }

    //merge the result into an hash
    return query(url,params).then(function(result){
        var resultArray = {};
        result.map(function(r){
            //all keys in _embedded
            var keys = Object.keys(r);
            keys.map(function(k){
                if(!resultArray[k]){
                    resultArray[k] = [];
                }
                resultArray[k] = resultArray[k].concat(r[k])
            })
        })
        return resultArray;
    })
}

/**
 * get data asyn with promise, workout the pages and query them at the same time.
 * The return Promise contains a hash with keys coresponding to the _embedded keys.
 * @param {String} url
 * @param {hash} params
 * @returns {Promise}
 * @example promiseGetRESTFUL_fast('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000400/hierarchicalDescendants',{'size':5})
 * @example promiseGetRESTFUL_fast('http://www.ebi.ac.uk/ols/api/ontologies/efo/terms/http%253A%252F%252Fwww.ebi.ac.uk%252Fefo%252FEFO_0000408/hierarchicalDescendants',{'size':1000})
 */
function promiseGetRESTFUL(url,params){
    if(params == undefined){
        params = {};
    }
    return promiseGet(url,params).then(JSON.parse).then(function(response) {
        var totalPages = RESTFUL_Response.totalPages(response)
        var totalElements = RESTFUL_Response.totalElements(response)
        if(params.size == undefined){
            params.size = RESTFUL_Response.pageSize(response);
        }

        if(RESTFUL_Response.hasPage(response)){
            var allurls = $.map($(Array(totalPages)), function(val, i) {
                return url + '?page=' + i  + '&size=' +params.size;
            })
            return Promise.all(allurls.map(function(url) {
                // console.log('querying ' + url);
                return promiseGet(url).then(JSON.parse).then(function(response) {
                    console.log(RESTFUL_Response.pageNumber(response) + ' done!');
                    return RESTFUL_Response._embedded(response);
                })
            }))
        }else{
            return response.RESTFUL_Response._embedded(response);
        }
    }).then(function(result){
        console.log('all done');
        var resultArray = {};
        result.map(function(r){
            //all keys in _embedded
            var keys = Object.keys(r);
            keys.map(function(k){
                if(!resultArray[k]){
                    resultArray[k] = [];
                }
                resultArray[k] = resultArray[k].concat(r[k])
            })
        })
        return resultArray;
    }).then(function(resultArray){
        return resultArray;
    })
}


/**
 * OLS FUNCTIONS
 * @type {{getOntologyInfo: OLS.getOntologyInfo, getPrefix2OntologyId: OLS.getPrefix2OntologyId, getOntologyIdByPrefix: OLS.getOntologyIdByPrefix, getIriByShortForm: OLS.getIriByShortForm, getOntologyByShortForm: OLS.getOntologyByShortForm, searchOLS: OLS.searchOLS, getRelatedTerms: OLS.getRelatedTerms, getEFOInfo: OLS.getEFOInfo, getOLSLinkAPI: OLS.getOLSLinkAPI, getOLSLink: OLS.getOLSLink, getHierarchicalDescendants: OLS.getHierarchicalDescendants}}
 */
var OLS = {
    /**
     * Load ontology info from ols api.
     * Lazy load.
     * @returns {Promise}
     * @example OLS.getOntologyInfo()
     * @example http://www.ebi.ac.uk/ols/api/ontologies
     */
    getOntologyInfo:function() {
        var _parseOntologies = function(response){
            var ontologyInfo = {};
            response.ontologies.forEach(function(d) {
                ontologyInfo[d.ontologyId] = d;
            })
            return ontologyInfo;
        }

        var dataPromise = getDataFromTag(global_efo_info_tag_id, 'ontologyInfo');
        if (dataPromise == undefined) {
            //lazy load
            console.log('Loading Ontology Info...')
            dataPromise = promiseGetRESTFUL(global_ols_restful_api_ontology,
                                            {'size': 1000}).then(_parseOntologies).catch(function(err) {
                console.error('Error when loading ontology info! ' + err);
            })
            //cache data
            $(global_efo_info_tag_id).data('ontologyInfo',dataPromise);
        }else{
            console.debug('Loading Ontology Info from cache.')
        }
        return dataPromise;
    },

    /**
     * Mapping for ontology name and abbrvation
     * For example, 'ordo' is used for Orphanet
     * This is needed to workout the ols link
     * Lazy load.
     * @returns {Promise} - hash containing prefix2ontology mapping
     * @example getPrefix2OntologyId()
     */
    getPrefix2OntologyId:function(){
        var dataPromise = getDataFromTag(global_efo_info_tag_id, 'prefix2ontId');
        if (dataPromise == undefined) {
            //lazy load
            console.log('Loading prefix2ontId...')
            dataPromise = OLS.getOntologyInfo().then(function(ontoInfo){
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
    },

    /**
     * giving a prefix, for example, EFO, return the ontology name which is 'efo'
     * Lazy load.
     * @param {String} prefix - The prefix is usually the first part of an ontology id (EFO_0000400)
     * @returns {Promise} - the name of the ontology
     * @example OLS.getOntologyIdByPrefix('Orphanet')
     */
    getOntologyIdByPrefix : function(prefix) {
        return OLS.getPrefix2OntologyId().then(function(p2o){
            return p2o[prefix]
        })
    },

    /**
     * Get ontology term iri from short form. The iri looks like this:
     * http://www.ebi.ac.uk/efo/EFO_0000400
     * Lazy load.
     * @param {String} shortForm - ontology id short form, like EFO_0000400.
     * @return {Promise} getIriByShortForm('EFO_0000400')
     */
    getIriByShortForm : function(shortForm){
        var prefix = shortForm.split('_')[0];
        var id = shortForm.split('_')[1];
        var ont = OLS.getOntologyByShortForm(shortForm);

        return ont.then(function(ontName){
            return OLS.getOntologyInfo().then(function(ontologies){
                return ontologies[ontName].config.baseUris[0] + id;
            })
        }).catch(function(err){
            console.log('Error finding iri for term ' + shortForm + '. ' + err);
        })
    },

    /**
     * Get ontology name by ontology term short form.
     * For example, EFO_0000400 is an 'efo' term.
     * Lazy load.
     * @param {String} shortForm - ontology id short form, like EFO_0000400.
     * @example OLS.getOntologyByShortForm('EFO_0000400')
     */
    getOntologyByShortForm : function(shortForm){
        var prefix = shortForm.split('_')[0];
        var id = shortForm.split('_')[1];
        p = OLS.getOntologyIdByPrefix(prefix);

        return p.then(function(ontology){
            return ontology;
        }).catch(function(err){
            console.log('Error finding iri for term ' + shortForm + '. ' + err);
        })
    },

    /**
     * Search the ols seach API (solr).
     * @param {String}keyword Search keyword.
     * @param {}params - paramaters in hash. use solr parameter to filter the result.
     * @returns {Promise} - search result in Json
     * @example searchOLS('EFO_0000400',{
            'ontology': 'efo',
            'fieldList': 'iri,ontology_name,ontology_prefix,short_form,description,id,label,is_defining_ontology,obo_id,type,logical_description'
        })
     */
    searchOLS : function(keyword,params){
        params = $.extend({}, params, {'q':keyword});
        return promiseGet(global_ols_seach_api,params).then(JSON.parse).then(function(data){
            console.log("data returned by ols search:");
            console.log(data);
            return data;
        })

    },

    /**
     * Find related term for a give efo term using ols solr search api. This is primarily from the 'logical_description' field.
     * Lazy load.
     * @param {String} efoid
     * @returns {Promise} - solr result in JSON
     * @example OLS.getRelatedTerms*'EFO_0000400')
     */
    getRelatedTerms : function(efoid){
        var queryRelatedTerms = function(efoid){
            console.log('Loading related terms...')
            return OLS.searchOLS(efoid,{
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
    },

    /**
     * Query OLS for efo term information.
     * Lazy load.
     * @param efoid
     * @returns {Promise}
     * @example OLS.getEFOInfo('EFO_0000400')
     */
    getEFOInfo : function(efoid){
        var queryEFOInfo = function(efoid){
            return OLS.getOLSLinkAPI(efoid).then(function(url){
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
    },

    /**
     * Get the OLS API link for a give efo term.
     * Lazy load.
     * @param {String} efoid
     * @returns {Promise} a link to the ols api for the queryed term
     * @example OLS.getOLSLinkAPI('EFO_0000400')
     */
    getOLSLinkAPI : function(efoid){
        var ont = OLS.getOntologyByShortForm(efoid);
        var iri = OLS.getIriByShortForm(efoid);
        return Promise.all([ont,iri]).then(function(arrayPromise) {
            var url = global_ols_api + 'ontologies/' + arrayPromise[0] + '/terms/' +
                    encodeURIComponent(encodeURIComponent(arrayPromise[1]));
            return url
        })
    },

    /**
     * Get the OLS link for a give efo term. This is return a link to the term page.
     * @param {String} efoid
     * @returns {Promise}
     */
    getOLSLink : function(efoid){
        var ont = OLS.getOntologyByShortForm(efoid);
        var iri = OLS.getIriByShortForm(efoid);
        return Promise.all([ont,iri]).then(function(arrayPromise) {
            var url = global_ols + 'ontologies/' + arrayPromise[0] + '/terms?iri=' +
                    encodeURIComponent(arrayPromise[1]);
            return url
        })
    },

    /**
     * Query ols for hierarchical descendants for a give efo term.
     * If the data is available in the cache, load it fron cache. Other wise, query from ols.
     * Currently ols query has a size limit of 1000, and it is set to 1000 to reduce the number of query needed.
     * For query of high level efo terms, for example, EFO_0000408 disease which has 9k descendant, this will be slow.
     * @param String efoid
     * @return Promise - hash contain
     * @example OLS.getHierarchicalDescendants('EFO_0000400')
     */
    getHierarchicalDescendants : function(efoid){
        var parseResponse = function(response){
            var terms = {};
            response.terms.forEach(function(d, i) {
                terms[d.short_form] = d;
            })
            var tmp = {}
            tmp[efoid] = terms;
            return tmp;
        }
        var queryDescendant = function(efoid){
            var efoinfo = OLS.getEFOInfo(efoid)
            return efoinfo.then(function(response){
                if (response.has_children) {
                    return promiseGetRESTFUL(response._links.hierarchicalDescendants.href,{'size':1000})
                            .then(parseResponse);
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
    },
}


//http://www.ebi.ac.uk/europepmc/webservices/rest/search?&query=ext_id:26831199%20src:med&resulttype=core&format=json
var EPMC = {
    /**
     * Query PMC for paper info. PMC uses their own id, but can optionally accept pubmed id.
     * @param {String} pubmed_id
     * @returns {Promise}
     * @example EPMC.getFromEPMC('')
     */
    getByPumbedId : function(pubmed_id){
        return promiseGet(global_pmc_api,
                          {
                              'query': 'ext_id:'+pubmed_id + '%20src:med',
                              'resulttype' :  'core',
                              'format' : 'json'
                          }).then(JSON.parse);
    },

    searchResult : {
        paper : function(EPMCresult){
            return EPMCresult.resultList.result[0];
        },
        citedByCount : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).citedByCount;
        },
        firstPublicationDate : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).firstPublicationDate;
        },
        journalInfo : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).journalInfo;
        },
        abstractText : function(EPMCresult){
            return EPMC.searchResult.paper(EPMCresult).abstractText;
        },
    },
}


/**
 * Get color for a give efo term by querying the parentMapping api.
 * The give back the prefered parent term and the predefined color of that parent term.
 * The color will be use in badges and plots, and it is consistence with the digram.
 * Lazy load.
 * @param {String} efoid
 * @returns {Promise} - json result.
 */
getColourForEFO = function(efoid) {
    var queryColour = function(efoid){
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

/**
 * Query SOLR for all available efo traits.
 * @return Promise
 * @example http://localhost:8280/gwas/api/search/efotrait?&q=*:*&fq=resourcename:efotrait&group.limit=99999&fl=shortForm
 */
getAvailableEFOs=function(){
    var dataPromise = getDataFromTag(global_efo_info_tag_id,'availableEFOs');
    if(dataPromise == undefined){
        //lazy load
        console.log('Loading all available EFOs in Gwas Catalog...')
        dataPromise =  promiseGet('/gwas/api/search/efotrait', {
            'q': '*:*',
            'fq': 'resourcename:efotrait',
            'group.limit': 99999,
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

/**
 * Add a promise to a tag's data attribute. The data attribute contains a hash.
 * If the hash key exist, the new promise data is merged to the old one.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param Promise promise - the promise, fullfilled or pending
 * @param String key - the has key for Promise
 * @param Boolean overwriteWarning - Default value is false. if true, print log to idicate overwritting.
 * @return undefined
 * @example addPromiseToTag('#efoInfo',promise,'key',false)
 */
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

/**
 * query a promise from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, the promise is returned, otherwise, a new empty resolved promise is returned.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param String key - the has key for promise
 * @return Promise
 * @example getPromiseFromTag('#efoInfo','key')
 */
getPromiseFromTag = function(tagID,key){
    var dataPromise =  getDataFromTag(tagID,key)
    if (dataPromise == undefined) {
        dataPromise = new Promise(function(resolve){
            resolve({});
        })
    }
    return dataPromise;
}

/**
 * Add user hash data to a tag's data attribute. The data attribute contains a hash.
 * If the hash key exist, the user hash data is merged to the existing one.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param {} hash - an hash
 * @param String key - the has key for Promise
 * @param Boolean overwriteWarning - Default value is false. if true, print log to idicate overwritting.
 * @return undefined
 * @example addDataToTag('#efoInfo',{'name':'xin'},'key',false)
 */
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

/**
 * query data from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, the date is returned, otherwise, a new empty hash is returned.
 * @param String tagID - the html tag used to store the data, with the '#'
 * @param String key - the has key for promise
 * @return {}
 * @example getDataFromTag('#efoInfo','key')
 */
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


/**
 * remove hash data from a tag's data attribute with a key. The data attribute contains a hash.
 * If the hash key exist, it is removed and return true, otherwise, return false.
 * @param {String} tagID - the html tag used to store the data, with the '#'
 * @param {String} key - the has key for date
 * @param {String} tobeDelete - the has key to be removed
 * @return {Boolean} true if success, otherwise false.
 * @example removeDataFromTag('#efoInfo','key','xin')
 */
removeDataFromTag = function(tagID, key, tobeDelete){
    var tmp = getDataFromTag(tagID,key);
    if(Object.keys(tmp).indexOf(tobeDelete) != -1){
        delete tmp[tobeDelete];
        return true;
    }
    return false;
}


/**
 * When ploting with descendants, the number of efos increase dramatically.
 * This is to remove the efos that have no annotation in GWAS catalog,
 * thus querying/ploting only those have at lease one annotation
 * @param []String toBeFilter - array of efoIDs
 * @return []String - array of efoIDs that has at least one annotation in the GWAS CATALOG
 * @example filterAvailableEFOs(['EFO_0000400','EFO_1234567'])
 */
filterAvailableEFOs = function(toBeFilter) {
    return getAvailableEFOs().then(function(availableEFOs){
        if(availableEFOs)
            return toBeFilter.filter(function(n) {
                return Object.keys(availableEFOs).indexOf(n) !== -1;
            });
    })
}






/**
 * Query oxo for cross reference of an given term.
 * Note, oxo uses 'EFO:0000400' instead of 'EFO_0000400'.
 * Lazy load.
 * @param {String} efoid
 * @returns {Promise}
 */
getOXO = function(efoid){
    var queryOXO = function(efoid){
        return promiseGet(global_oxo_api + '/mappings',
                          {
                              'fromId': efoid.replace('_',':'),
                          }
        ).then(JSON.parse).then(function(data){
            var tmp = {}
            tmp[efoid] = data;
            return tmp;
        });
    }


    var dataPromise = getPromiseFromTag(global_efo_info_tag_id, 'efo2OXO');
    return dataPromise.then(function(data) {
        if ($.inArray(efoid, Object.keys(data)) == -1) {
            //efo info is not currently loaded
            console.log('Loading oxo for ' + efoid)
            dataPromise = queryOXO(efoid);
            return dataPromise.then(function(data){
                //add to tag
                addPromiseToTag(global_efo_info_tag_id,dataPromise,'efo2OXO');
                return data[efoid];
            })
        }else {
            //efo colour is has been loaded perviously
            console.debug('Loading oxo from cache for ' + efoid);
            return data[efoid]
        }
    })
}

/**
 * The main efo is defined by the url, as a main entry of the page. It is stored in 'mainEFOInfo'
 * in the date attribute of the <global_efo_info_tag_id>
 * @return String efoID - 'EFO_0000400'
 * @example getMainEFO()
 */
getMainEFO = function(){
    return $('#query').text();
}

/**
 * Return a hash object where the hash keys are all the current selected terms
 * this are the terms in the cart, not including descendants.
 * @returns {Hash}
 */
getCurrentSelected = function(){
    return $(global_efo_info_tag_id).data('selectedEfos');
}

/**
 * Ruturn an array of efos that are currently checked for descendants in the cart.
 * @returns {Array} efoids
 */
whichDescendant = function(){
    // return $(".cart-item-cb:checkbox:checked").map(function() { return this.id.split("selected_cb_")[1]; }).get();
    var tmp = getDataFromTag(global_efo_info_tag_id,'whichDescendant')
    if(tmp == undefined)
        return []

    return Object.keys(tmp);
}

/**
 * A sharable link is generated dynamically as the page changes. This function is call
 * every time the page updated, to update the link so the users can share their result.
 */
updateSharableLink = function(){
    $("#sharable_link").attr('value', window.location.origin + window.location.pathname + '?included=' +
                             Object.keys(getCurrentSelected()).join(','));
    hideLoadingOverLay('#sharable_link_btn');
}


/**
 * Find studies for a given efoid from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} studies
 */
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


/**
 * Find associations for a given efoid from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} associations
 */
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


/**
 * Find studies for an array of efoids from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * xintodo can be refact to findStudiesForEFO? same as the findAssociationForEFOs
 * @param {String} efoid
 * @returns {Hash} studies
 */
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

/**
 * Find association for an array of efoids from the solr result(global variable), return empty hash if the solr result is undefined
 * This function search the highlighting result from solr.
 * Require solr result.
 * @param {String} efoid
 * @returns {Hash} studies
 */
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


/**
 * an association can be annotated to more than one efo. find them all. This is using solr highlighting where the
 * efo for an association is wrap by <b/> tag.
 * require solr result.
 * @param {String} association_id - This is the solr id, not the rsid.
 * @param {Hash} data - solr search result
 * @returns {array} efoids - an array of efoids annotated to the given association
 */
var findAllEFOsforAssociation = function(association_id,data) {
    var terms = data.highlighting[association_id].shortForm;
    terms.forEach(function(d, i) {
        //xintodo only efo?
        terms[i] = d.match(/<b>(\w*_\d*)<\/b>/)[1];
    })
    return terms;
}


/**
 * work out which efo is the highlighted efo. Currently return the first one come in the list
 * require solr result.
 * xintodo can this be the most significant one?
 * xintodo refactor findAllEFOsforAssociation into this.
 * @param {String} traits
 * @param association
 * @returns {*}
 * @private
 */
findhighlightEFOForAssociation = function(association_id,data) {
    var allEFOs = findAllEFOsforAssociation(association_id,data)
    return allEFOs[0];
}


/**
 * Study sorting functions.
 */
var studySorting = {

    add : function (a, b){
        return parseInt(a) + parseInt(b);
    },

    sortByPublishDate : function(array){
        var publishDate = {};

        $.each(array, function(index, value) {
            publishDate[value.id] = value.publicationDate;
        })

        var keysSorted = Object.keys(publishDate).sort(function(a, b) {
            return Date.parse(publishDate[b]) - Date.parse(publishDate[a])
        })
        return keysSorted;
    },


    sortByCatalogPublishDate : function(array){
        var catalogPublishDate = {};

        $.each(array, function(index, value) {
            catalogPublishDate[value.id] = value.catalogPublishDate;
        })

        var keysSorted = Object.keys(catalogPublishDate).sort(function(a, b) {
            return Date.parse(catalogPublishDate[b]) - Date.parse(catalogPublishDate[a])
        })
        return keysSorted;
    },


    // sortBySampleSize : function(array){
    //     // studies['study:8072'].initialSampleDescription.split(',').join('').match(/(\d*)/g).filter(Number).reduce(_add,0)
    //     // "155 Korean ancestry medication non-adherent diabetes cases, 80 Korean ancestry medication non-adherent hypertensive cases, 240 Korean ancestry medication adherent diabetes cases, 827 Korean ancestry medication adherent hypertensive cases"
    //     var sampleSize = {};
    //
    //     $.each(array, function(index, value) {
    //         var init = value.initialSampleDescription
    //         var total = init.split(',').join('').match(/(\d*)/g).filter(Number).reduce(studySorting.add,0)
    //         sampleSize[value.id] = total;
    //     })
    //
    //     //desc
    //     var keysSorted = Object.keys(sampleSize).sort(function(a, b){
    //         return parseInt(sampleSize[b]) - parseInt(sampleSize[a])
    //     })
    //     return keysSorted;
    // },


    sortByInitialSampleSize : function(array){
        // "initial|NR|NR|European|11522|NA"
        // "replication|NR|NR|European|4955|NA"
        var sampleSize = {};

        var isInitial = function(ancestryLinkString){
            return ancestryLinkString.match(/^initial/) != null
        }
        var InitialSampleSize = function(ancestryLinkString){
            return ancestryLinkString.split('|')[4]
        }



        $.each(array, function(index, value) {
            var init = value.ancestryLinks
            var total = init.filter(isInitial)
                    .map(InitialSampleSize)
                    .reduce(studySorting.add,0)
            sampleSize[index] = total;
        })

        //desc
        var keysSorted = Object.keys(sampleSize).sort(function(a, b){
            return parseInt(sampleSize[b]) - parseInt(sampleSize[a])
        });
        return keysSorted;
    },
}

/**
 * work out which study is the highlighted study for an efo trait.
 * Currently find the one with largest initial sample size.
 * This require the solr search data.
 * @param String efoid
 * @return {Object} - study_solr_doc
 * @example findHighlightedStudiesForEFO('EFO_0000400')
 */
findHighlightedStudiesForEFO = function(efoid) {
    var studies = findStudiesForEFO(efoid);
    var sorted_index = studySorting.sortByInitialSampleSize(studies);
    // return studies[sorted_index[sorted_index.length - 1]];
    return studies[sorted_index[0]];
}

/**
 * return html to display information for a data point(association) in the locus plot when moveover the datapoint
 * @param {Hash} association_solr_doc
 * @returns {String} The html as string
 */
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

/**
 * Display highlighted study on the page
 * @param highlightedStudy
 */
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

    EPMC.getByPumbedId(highlightedStudy.pubmedId).then(function(data) {
        var paperDetail = data.resultList.result[0];
//                    $("#efotrait-highlighted-study-abstract").html(longContent("efotrait-highlighted-study-abstract_div",
//                                                                               paperDetail.abstractText,''));
//         $('#efotrait-highlighted-study-abstract').html(createPopover('detail',
//                                                                      'abstract',
//                                                                      paperDetail.abstractText));
        $('#efotrait-highlighted-study-abstract').html(EPMC.searchResult.abstractText(data));

        return paperDetail;
    }).catch(function(err) {
        console.warning('Error when loading data from PMC! ' + err);
    }).then(function(){
        hideLoadingOverLay('#highlight-study-panel-loading');
    });
}

/**
 * display in efo information on the page

 * @param efotraitId
 */
displayEfoTraitInfo = function(efoinfo) {
    var efotrait_link = efoinfo.iri;
    var efotrait_id = efoinfo.short_form;
    var synonym = efoinfo.synonyms;
    var efotrait_label = efoinfo.label;
    addDataToTag(global_efo_info_tag_id, efoinfo, 'mainEFOInfo');
    $("#efotrait-description").html(displayArrayAsList(efoinfo.description));
    $("#efotrait-id").html(setExternalLink(efotrait_link, efotrait_id));
    $("#efotrait-label").html(efotrait_label);
    // $("#efotrait-label").html(createPopover(efotrait_label,
    //                                         'description',
    //                                         displayArrayAsList(efoinfo.description)));
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
}

/**
 * display association table
 * @param {Object} data - association solr docs
 * @param {Boolean} cleanBeforeInsert
 */
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
        $('#association-table').bootstrapTable('removeAll');
    }

    var data_json = []
    $.each(data, function(index,asso) {

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
        riskAllele = setExternalLinkText('/gwas/beta/variants/' + riskAllele_rsid,riskAlleleLabel);

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
        study += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
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
                                                   field: 'reportedGenes',
                                                   title: 'Reported gene(s)',
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
        $('#association-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: 5,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#association-table-loading')
}

/**
 * display study table
 * @param {Object} data - study solr docs
 * @param {Boolean} cleanBeforeInsert
 */
function displayEfotraitStudies(data, cleanBeforeInsert) {
    //by default, we clean the table before inserting data
    if (cleanBeforeInsert === undefined) {
        cleanBeforeInsert = true;
    }


    var study_ids = [];
    if(cleanBeforeInsert){
        $('#study-table').bootstrapTable('removeAll');
    }

    var data_json = []
    $.each(data, function(index, asso) {
        var tmp={};
        var study_id = asso.id;
        if (jQuery.inArray(study_id, study_ids) == -1) {


            study_ids.push(study_id);

            // Author
            var author = asso.author_s;
            var publicationDate = asso.publicationDate;
            var pubDate = publicationDate.split("-");
            var pubmedId = asso.pubmedId;
            var study_author = setQueryUrl(author, author);
            study_author += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
            tmp['Author'] = study_author;

            // Publication date
            var p_date = asso.publicationDate;
            var publi = p_date.split('T')[0];
            tmp['publi'] = publi;


            // Journal
            tmp['Journal'] = asso.publication;

            // Title
            tmp['Title'] = asso.title;

            // Initial sample desc
            var initial_sample_text = '-';
            if (asso.initialSampleDescription) {

                initial_sample_text = displayArrayAsList(asso.initialSampleDescription.split(', '));
                if(asso.initialSampleDescription.split(', ').length>1)
                    initial_sample_text = initial_sample_text.html()
            }
            tmp['initial_sample_text'] = initial_sample_text;


            // Replicate sample desc
            var replicate_sample_text = '-';
            if (asso.replicateSampleDescription) {
                replicate_sample_text = displayArrayAsList(asso.replicateSampleDescription.split(', '));
                if(asso.replicateSampleDescription.split(', ').length>1)
                    replicate_sample_text = replicate_sample_text.html()
            }
            tmp['replicate_sample_text'] = replicate_sample_text;


            // ancestralGroups
            var ancestral_groups_text = '-';
            if (asso.ancestralGroups) {
                ancestral_groups_text = displayArrayAsList(asso.ancestralGroups);
                if(asso.ancestralGroups.length>1)
                    ancestral_groups_text = ancestral_groups_text.html()
            }
            tmp['ancestral_groups_text'] = ancestral_groups_text;

            data_json.push(tmp)
        }
    });
    // Study count //
    $(".study_count").html(study_ids.length);

    if (study_ids.length == 1) {
        $(".study_label").html("Study");
    }

    $('#study-table').bootstrapTable({
                                         exportDataType: 'all',
                                         columns: [{
                                             field: 'Author',
                                             title: 'Author',
                                             sortable: true
                                         }, {
                                             field: 'publi',
                                             title: 'Publication Date',
                                             sortable: true
                                         }, {
                                             field: 'Journal',
                                             title: 'Journal',
                                             sortable: true
                                         },{
                                             field: 'Title',
                                             title: 'Title',
                                             sortable: true
                                         },{
                                             field: 'initial_sample_text',
                                             title: 'Initial sample description',
                                             sortable: true
                                         },{
                                             field: 'replicate_sample_text',
                                             title: 'Replication sample description',
                                             sortable: true
                                         },{
                                             field: 'ancestral_groups_text',
                                             title: 'Ancestral groups',
                                             sortable: true
                                         }],
                                         data: data_json,

                                     });
    $('#study-table').bootstrapTable('load',data_json)
    if(data_json.length>5){
        $('#study-table').bootstrapTable('refreshOptions',{pagination: true,pageSize: 5,pageList: [5,10,25,50,100,'All']})
    }
    hideLoadingOverLay('#study-table-loading')
}

// //display association table
// function displayEfotraitAssociations(data, cleanBeforeInsert) {
//     //by default, we clean the table before inserting data
//     if (cleanBeforeInsert === undefined) {
//         cleanBeforeInsert = true;
//     }
//
//     var asso_count = data.length;
//
//     $(".association_count").html(asso_count);
//
//     if (asso_count == 1) {
//         $(".association_label").html("Association");
//     }
//
//     if(cleanBeforeInsert){
//         $("#association-table-body tr").remove();
//     }
//
//     $.each(data, function(index,asso) {
//
//         var row = $('<tr/>');
//
//         // Risk allele
//         var riskAllele = asso.strongestAllele[0];
//         var riskAlleleLabel = riskAllele;
//         var riskAllele_rsid = riskAllele;
//         if (riskAlleleLabel.match(/\w+-.+/)) {
//             riskAlleleLabel = riskAllele.split('-').join('-<b>')+'</b>';
//             riskAllele_rsid = riskAllele.split('-')[0];
//         }
//         // This is now linking to the variant page instead of the search page
//         // riskAllele = setQueryUrl(riskAllele,riskAlleleLabel);
//         riskAllele = setExternalLinkText('/gwas/beta/variants/' + riskAllele_rsid,riskAlleleLabel);
//
//         row.append(newCell(riskAllele));
//
//         // Risk allele frequency
//         var riskAlleleFreq = asso.riskFrequency;
//         row.append(newCell(riskAlleleFreq));
//
//         // p-value
//         var pValue = asso.pValueMantissa;
//         if (pValue) {
//             var pValueExp = " x 10<sup>" + asso.pValueExponent + "</sup>";
//             pValue += pValueExp;
//             if (asso.qualifier) {
//                 if (asso.qualifier[0].match(/\w/)) {
//                     pValue += " " + asso.qualifier.join(',');
//                 }
//             }
//             row.append(newCell(pValue));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // OR
//         var orValue = asso.orPerCopyNum;
//         if (orValue) {
//             if (asso.orDescription) {
//                 orValue += " " + asso.orDescription;
//             }
//             row.append(newCell(orValue));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // Beta
//         var beta = asso.betaNum;
//         if (beta) {
//             if (asso.betaUnit) {
//                 beta += " " + asso.betaUnit;
//             }
//             if (asso.betaDirection) {
//                 beta += " " + asso.betaDirection;
//             }
//             row.append(newCell(beta));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // CI
//         var ci = (asso.range) ? asso.range : '-';
//         row.append(newCell(ci));
//         // Reported genes
//         var genes = [];
//         var reportedGenes = asso.reportedGene;
//         if (reportedGenes) {
//             $.each(reportedGenes, function(index, gene) {
//                 genes.push(setQueryUrl(gene));
//             });
//             row.append(newCell(genes.join(', ')));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // Reported traits
//         var traits = [];
//         var reportedTraits = asso.traitName;
//         if (reportedTraits) {
//             $.each(reportedTraits, function(index, trait) {
//                 traits.push(setQueryUrl(trait));
//             });
//             row.append(newCell(traits.join(', ')));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // Mapped traits
//         var mappedTraits = asso.mappedLabel;
//         if (mappedTraits) {
//             row.append(newCell(mappedTraits.join(', ')));
//         } else {
//             row.append(newCell('-'));
//         }
//
//         // Study
//         var author = asso.author_s;
//         var publicationDate = asso.publicationDate;
//         var pubDate = publicationDate.split("-");
//         var pubmedId = asso.pubmedId;
//         var study = setQueryUrl(author, author + " - " + pubDate[0]);
//         study += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
//         row.append(newCell(study));
//
//         var studyId = asso.studyId;
//
//         // Populate the table
//         $("#association-table-body").append(row);
//         hideLoadingOverLay('#association-table-loading')
//     });
// }
//
// //display study table
// function displayEfotraitStudies(data, cleanBeforeInsert) {
//     //by default, we clean the table before inserting data
//     if (cleanBeforeInsert === undefined) {
//         cleanBeforeInsert = true;
//     }
//
//
//     var study_ids = [];
//     if(cleanBeforeInsert){
//         $("#study-table-body tr").remove();
//     }
//     $.each(data, function(index, asso) {
//         var study_id = asso.id;
//         if (jQuery.inArray(study_id, study_ids) == -1) {
//
//             var row = $('<tr/>');
//
//             study_ids.push(study_id);
//
//             // Author
//             var author = asso.author_s;
//             var publicationDate = asso.publicationDate;
//             var pubDate = publicationDate.split("-");
//             var pubmedId = asso.pubmedId;
//             var study_author = setQueryUrl(author, author);
//             study_author += '<div><small>'+setExternalLink(EPMC+pubmedId,'PMID:'+pubmedId)+'</small></div>';
//             row.append(newCell(study_author));
//
//             // Publication date
//             var p_date = asso.publicationDate;
//             var publi = p_date.split('T')[0];
//             row.append(newCell(publi));
//
//             // Journal
//             row.append(newCell(asso.publication));
//
//             // Title
//             row.append(newCell(asso.title));
//
//             // Initial sample desc
//             var initial_sample_text = '-';
//             if (asso.initialSampleDescription) {
//                 initial_sample_text = displayArrayAsList(asso.initialSampleDescription.split(', '));
//             }
//             row.append(newCell(initial_sample_text));
//
//             // Replicate sample desc
//             var replicate_sample_text = '-';
//             if (asso.replicateSampleDescription) {
//                 replicate_sample_text = displayArrayAsList(asso.replicateSampleDescription.split(', '));
//             }
//             row.append(newCell(replicate_sample_text));
//
//             // ancestralGroups
//             var ancestral_groups_text = '-';
//             if (asso.ancestralGroups) {
//                 ancestral_groups_text = displayArrayAsList(asso.ancestralGroups);
//             }
//             row.append(newCell(ancestral_groups_text));
//
//             // Populate the table
//             $("#study-table-body").append(row);
//         }
//     });
//     // Study count //
//     $(".study_count").html(study_ids.length);
//
//     if (study_ids.length == 1) {
//         $(".study_label").html("Study");
//     }
//
//     hideLoadingOverLay('#study-table-loading')
// }

/**
 * Display an overlay spinner on a tag
 * https://gasparesganga.com/labs/jquery-loading-overlay/
 * @param {String} tagID
 * @returns undefined
 * @example showLoadingOverLay('#efoInfo')
 */
showLoadingOverLay = function(tagID){
    var options = {
        color: "rgba(255, 255, 255, 0.8)",   // String
        custom: "",                // String/DOM Element/jQuery Object
        fade: [100, 1500],                      // Boolean/Integer/String/Array
        fontawesome: "",                          // String
//            image: "data:image/gif;base32,...",  // String
        imagePosition: "center center",             // String
        maxSize: "100px",                  // Integer/String
        minSize: "20px",                    // Integer/String
        resizeInterval: 10,                       // Integer
        size: "20%",                       // Integer/String
        zIndex: 1000,                        // Integer
    }
    $(tagID).LoadingOverlay("show",options);
}

/**
 * Hide an overlay spinner on a tag
 * https://gasparesganga.com/labs/jquery-loading-overlay/
 * @param {String} tagID
 * @returns undefined
 * @example hideLoadingOverLay('#efoInfo')
 */
hideLoadingOverLay = function(tagID){
    return $(tagID).LoadingOverlay("hide", true);
}

/**
 * Check if an efo in the cart has been checked for descendants.
 * @param {String} efoid
 * @returns {boolean}
 */
isDescendantRequired = function(efoid){
    return whichDescendant().indexOf(efoid) != -1
}

/**
 * check if we should always include descendants for the efos in the cart
 * @returns {boolean}
 */
isAlwaysDescendant = function(){
    return $("#cb-query-include-descendants").is(":checked");
}

/**
 * check if the efo is the mainEFO. mainEFO is defined by the url.
 * @returns {boolean}
 */
isMainEFO = function(efoid){
    return efoid == getMainEFO();
}


// Create a popover to display content
createPopover = function(label,header,content){
    var content_text = $('<a></a>');
    content_text.html(label);
    content_text.popover({title: header, content: content, animation : true,
                             delay: {show: 100, hide: 200},
                             placement :'auto right',
                             trigger : 'hover',
                             html: true,
                             template: '<div class="popover" role="tooltip" style="width: 100%;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"><div class="data-content"></div></div></div>'
                         });
    return content_text;
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
//     return OLS.searchOLS($('#query').text(),{
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




