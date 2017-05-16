/**
 * Created by xin on 19/04/2017.
 */


var global_color_url = 'http://wwwdev.ebi.ac.uk/gwas/beta/rest/api/parentMapping/';
var global_ols_api = 'http://www.ebi.ac.uk/ols/api/';
var global_ols_seach_api =  global_ols_api + 'search';
var global_ols_api_all_descendant_limit = 99999;
var global_efo_info_tag_id = '#efo-info';
var global_efo_selected_tag_id = '#selected-efos';

new Clipboard('#sharable_link_btn');


_peak = function() {
    console.debug($(global_efo_info_tag_id).data());
    console.debug($(global_efo_selected_tag_id).data());
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
function promiseGet(url, params) {
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
