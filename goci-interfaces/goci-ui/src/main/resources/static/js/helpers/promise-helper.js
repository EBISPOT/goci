/** First refactoring action: common js - DRY. From Xin original code */

//************************** helper functions **************************
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
function promiseGet(url, params,debug)

{
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
            url = url + '?' + params_str.substring(1);
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
 * get data asyn with promise. This is first implemented to query efo colour but turn out to be slower
 * then making multiple GET request.
 * need to refactor to change header
 * @param {String} url
 * @param {hash} params
 * @param {Boolean} debug
 * @returns {Promise}
 */
function promisePost(url, params={}, header='application/json') {
    
    if (!url.startsWith("http")) {
        url = window.location.origin + url
    }
    
    // Return a new promise.
    return new Promise(function(resolve, reject) {
        var req = new XMLHttpRequest();
        
        if(header == 'application/json'){
            params = JSON.stringify(params)
        }
        if(header == 'application/x-www-form-urlencoded') {
            params = Object.keys(params).map(function (key) {
                return encodeURIComponent(key) + '=' + encodeURIComponent(params[key]);
            }).join('&');
        }
        
        
        req.open('POST', url);
        
        req.setRequestHeader("Content-Type", header);
        
        
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
        req.send(params);
    });
    
    
    // //testing how fast the post method works
    // //"{"1":472.7800000000002,"10":1723.4450000000002,"50":8417.69,"100":16249.485000000002,"200":30406.670000000002,"205":28694.609999999986,"208":30608.684999999823,"209":29462.99499999988,"210":30634.399999999907}"
    // var size = [1,10,50,100,200,400,800,1000]
    // var size = [220,240,260,280,300]
    // var result = {};
    // var t0 = performance.now();
    // size.forEach(function(s){
    //     getAvailableEFOs().then(function(x) {
    //         t0 = performance.now();
    //         return getColourForEFOs(Object.keys(x).slice(0, s))
    //     }).then(console.log).then(function(){
    //         var t1 = performance.now();
    //         console.warn(s + " took " + (t1 - t0) + " milliseconds.")
    //         result[s.toString()] = t1 - t0;
    //     })
    // });
    // result
    //
    // //testing using get
    // var size = [1,10,50,100,200,400,800,1000]
    // var size = [1500]
    // var result = {};
    // var t0 = performance.now();
    // size.forEach(function(s){
    //     getAvailableEFOs().then(function(x) {
    //         t0 = performance.now();
    //         return Promise.all( Object.keys(x).slice(0, s).map(getColourForEFO))
    //     }).then(console.log).then(function(){
    //         var t1 = performance.now();
    //         console.warn(s + " took " + (t1 - t0) + " milliseconds.")
    //         result[s.toString()] = t1 - t0;
    //     })
    // });
    // result
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
            console.debug(RESTFUL_Response.pageNumber(response)+1 + '/' + RESTFUL_Response.totalPages(response) + ' done!')
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
                    console.debug(RESTFUL_Response.pageNumber(response) + ' done!');
                    return RESTFUL_Response._embedded(response);
                })
            }))
        }else{
            return response.RESTFUL_Response._embedded(response);
        }
    }).then(function(result){
        console.debug('all done');
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
