/**
 * Created by xinhe on 29/06/2017.
 */


var getStudy = function(FEATURES,needTakeLog) {
    return promiseGet(window.location.pathname.split('/curation/')[0]+'/curation/studyscore').then(JSON.parse)
}


var calculateScore=function(studies,FEATURES,needTakeLog){
    var FEATURES_NOMALIZED = score.normalizeBySum(Object.values(FEATURES),1,Object.keys(FEATURES))
    studies_raw = studies;
    //debug
//        studies[999999999999] = {
//            Author : 'abc',
//            PubmedId : '12345',
//            GenomeWideCoverage : 1,
//            InitialSampleSize : 9999999,
//            GenomeWideCoverage : 1,
//            SummaryStatisticsAvailable : 1, //dont know yet
//            UserRequested : 1, //dont know yet
//            ReplicationStageIncluded : 1,
//            PublicationDate : '2005-04-25 00:00:00.0',
//        }


    //CLEAN STUDY DATA, replace null,na,undefined,false to 0, true to 1
    var _cleanData=function(data){
        var value = data;
        //clean
        if(value == undefined | value==null | value === 'false' | value == "NA") {
            value = 0;
        }
        if(value == 'true'){
            value = 1;
        }
        if(value == 'PublicationDate'){
            value = new Date() - new Date(value)
        }
        return  value;
    }
    studies_clean=JSON.parse(JSON.stringify(studies))
    Object.keys(studies_clean).map((study_id)=>{
        Object.keys(studies_clean[study_id]).map((feature)=>{
            //remove element that is not a feature
            if(Object.keys(FEATURES_NOMALIZED).indexOf(feature) == -1){
                delete studies_clean[study_id][feature]
            }else{
                studies_clean[study_id][feature] = _cleanData(studies_clean[study_id][feature])
            }
        })
    })
    console.log('Clean null,na,undefined,false and true...');
    console.log(studies_clean);


    //group all feature together for normalization across studies
    groupByFeatures={};
    Object.keys(FEATURES).map((feature)=>{
        groupByFeatures[feature] ={};
        Object.keys(studies_clean).map((study)=>{
            groupByFeatures[feature][study] = studies_clean[study][feature]
        })
    })
    console.log('Group by features:');
    console.log(groupByFeatures);

    //normalize study features across all studies
    groupByFeatures_nomalized = JSON.parse(JSON.stringify(groupByFeatures))
    Object.keys(groupByFeatures_nomalized).map((feature)=>{
        var featureObject = groupByFeatures_nomalized[feature];
        if(needTakeLog.indexOf(feature) != -1){
            //take log before nomalize
            featureObject = score.takeLog(Object.values(groupByFeatures_nomalized[feature]),Object.keys(groupByFeatures_nomalized[feature]));
        }
        groupByFeatures_nomalized[feature] = score.normalizeByMax(Object.values(featureObject),100,Object.keys(featureObject))
    })
    console.log('Group by features nomalized:');
    console.log(groupByFeatures_nomalized);


    //assign back normalized features
    studies_nomalized = JSON.parse(JSON.stringify(studies_clean))
    Object.keys(groupByFeatures_nomalized).map((feature)=>{
        Object.keys(groupByFeatures_nomalized[feature]).map((study)=>{
            studies_nomalized[study][feature]=groupByFeatures_nomalized[feature][study]
        })
    })
    console.log('study nomalized:');
    console.log(studies_nomalized);

    //Apply feature weight to study features
    studies_nomalized_weighted = JSON.parse(JSON.stringify(studies_nomalized))
    Object.keys(studies_nomalized_weighted).map((study_id)=>{
        Object.keys(studies_nomalized_weighted[study_id]).map((feature)=>{
            studies_nomalized_weighted[study_id][feature] = studies_nomalized_weighted[study_id][feature] * FEATURES_NOMALIZED[feature]
        })
    })
    console.log('study nomalized weighted:');
    console.log(studies_nomalized_weighted);

    //sum study features for final score
    studies_score={}
    Object.keys(studies_nomalized_weighted).map((study)=>{
        studies_score[study] = Object.values(studies_nomalized_weighted[study]).reduce(score.sum,0)
    });
    console.log('study score:');
    console.log(studies_score);


    return studies_score;
//        fakePrintMatrix(studies,FEATURES_NOMALIZED)
//        fakePrintMatrix(studies_clean,FEATURES_NOMALIZED)
//        fakePrintMatrix(studies_nomalized,FEATURES_NOMALIZED)
//        fakePrintMatrix(studies_nomalized_weighted,FEATURES_NOMALIZED,studies_score)
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

var score = {
    sum : (a,b)=>{return a+b},
    normalizeByMax : (array,scale=1,keys=undefined) => {
        array = array.map(parseFloat);
        var min = Math.min(...array)
        var max = Math.max(...array)
        var normalized = array.map((v)=> {
            if (min == max) return 1*scale;
            return scale * (v - min)  / (max - min);
        })
        if(keys != undefined){
            return hashFromArrays(keys,normalized);
        }
        return normalized;
    },
    takeLog : (array,keys=undefined) => {
        array = array.map(parseFloat);
        var normalized = array.map((v)=>{
            //avoid 0
            return Math.log(v+1);
        })
        if(keys != undefined){
            return hashFromArrays(keys,normalized);
        }
        return normalized;
    },
    normalizeBySum : (array,scale=1,keys=undefined) => {
        var total = array.reduce(score.sum,0);
        var normalized = array.map((v)=> {
            if (total ==0 ) return 0;
            return scale * v/total;
        })
        if(keys != undefined){
            return hashFromArrays(keys,normalized);
        }
        return normalized;
    },
}






hashFromArrays = function(arr_keys,arr_values){
    if(arr_keys.length != arr_values.length){
        console.error('the number of keys(' + arr_keys + ') must be the same of the number of values(' + arr_values + ')!')
        return undefined;
    }
    var hash = {}
    arr_keys.forEach(function(i) {
        hash[i] = arr_values.shift();
    })
    return hash;
}


objectMap = function(object,func) {
    var keys = Object.keys(object);
    var values = Object.values(object)
    return hashFromArrays(keys, values.map(func))
}


function buildMap(obj,deep=true) {
    var map
    if(typeof map == 'undefined')
        map=new Map();

    Object.keys(obj).forEach(key => {
        var tmp=obj[key];
        if(deep){
            if(obj[key] !== null && typeof obj[key] === 'object'){
                tmp = buildMap(obj[key],true)
            }
        }
        map.set(key, tmp);
    });
    return map;
}

function buildObject(map,deep=true) {
    var object
    if (typeof object == 'undefined')
        object = {};

    map.forEach(function(value, key) {
        var tmp = Object.assign({}, {[key]: value})

        if (deep) {
            if (value !== null && typeof value === 'object') {
                tmp = buildObject(value, true)
            }
        }
        object[key] = tmp;
    });
    return object
}

function printResult(studies,FEATURES){
    var array = [];
    array.push([''].concat(Object.keys(FEATURES)))
    Object.keys(studies).map((study_id)=>{
        var row = [study_id];
        Object.keys(FEATURES).map((feature)=>{
            row = row.concat(studies[study_id][feature])
        })
        array.push(row);
        return row;
    })
    var A = matrix(array);
//        console.log(A([],[]))
    console.log(
            JSON.stringify(A([],[])).replace(/\],\[/g, "],\n[")
    );
}


fakePrintMatrix = function(studies,features,score=undefined,colsep='\t\t\t',rowsep='\n'){
    console.log('\t'+Object.keys(features).join('\t') + rowsep);
    console.log('nomalized weight\t' + Object.values(features).join(colsep)+ rowsep);
    var matrixString = ''
    Object.keys(studies).map((study_id)=>{
        matrixString = matrixString.concat(study_id + colsep);
        Object.keys(features).map((feature)=>{
            matrixString = matrixString.concat(studies[study_id][feature]+colsep);
        })
        if(score){
            matrixString = matrixString.concat(score[study_id]);
        }
        matrixString = matrixString.concat(rowsep);

    })
    console.log(matrixString)
}
