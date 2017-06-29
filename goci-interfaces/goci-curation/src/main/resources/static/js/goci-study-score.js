/**
 * Created by xinhe on 29/06/2017.
 */
//
// const matrix = require('/Users/xinhe/node_modules/matrix-js/lib');
//
// hashFromArrays = function(arr_keys,arr_values){
//     if(arr_keys.length != arr_values.length){
//         console.error('the number of keys(' + arr_keys + ') must be the same of the number of values(' + arr_values + ')!')
//         return undefined;
//     }
//     var hash = {}
//     arr_keys.forEach(function(i) {
//         hash[i] = arr_values.shift();
//     })
//     return hash;
// }
//
//
// objectMap = function(object,func) {
//     var keys = Object.keys(object);
//     var values = Object.values(object)
//     return hashFromArrays(keys, values.map(func))
// }
//
//
// var score = {
//     sum : (a,b)=>{return a+b},
//     normalizeByMax : (array,scale=1,keys=undefined) => {
//         array = array.map(parseFloat);
//         var min = Math.min(...array)
//         var max = Math.max(...array)
//         var normalized = array.map((v)=> {
//             if (min == max) return 1*scale;
//             return scale * (v - min)  / (max - min);
//         })
//         if(keys != undefined){
//             return hashFromArrays(keys,normalized);
//         }
//         return normalized;
//     },
//     takeLog : (array,keys=undefined) => {
//         array = array.map(parseFloat);
//         var normalized = array.map((v)=>{
//             //avoid 0
//             return Math.log(v+1);
//         })
//         if(keys != undefined){
//             return hashFromArrays(keys,normalized);
//         }
//         return normalized;
//     },
//     normalizeBySum : (array,scale=1,keys=undefined) => {
//         var total = array.reduce(score.sum,0);
//         var normalized = array.map((v)=> {
//             if (total ==0 ) return 0;
//             return scale * v/total;
//         })
//         if(keys != undefined){
//             return hashFromArrays(keys,normalized);
//         }
//         return normalized;
//     },
// }
//
// /**
//  * get data asyn with promise
//  * @param {String} url
//  * @param {hash} params
//  * @param {Boolean} debug
//  * @returns {Promise}
//  */
// function promiseGet(url, params,debug) {
//
//     if(debug == undefined){
//         debug = false
//     }
//
//     if (!url.startsWith("http")) {
//         url = window.location.origin + url
//     }
//
//     // Return a new promise.
//     return new Promise(function(resolve, reject) {
//         // Do the usual XHR stuff
//         var req = new XMLHttpRequest();
//
//         params = params || {}
//         if (Object.keys(params).length > 0) {
//             var params_str = '';
//             Object.keys(params).forEach(function(key, index) {
//                 params_str = params_str + '&' + key + '=' + this[key];
//             }, params);
//             url = url + '?' + params_str.substring(1);
//         }
//
//         req.open('GET', url);
//         if(debug){
//             console.log('promise get from :' + url);
//         }
//
//         req.onload = function() {
//             // This is called even on 404 etc
//             // so check the status
//             if (req.status == 200) {
//                 // Resolve the promise with the response text
//                 resolve(req.response);
//             }
//             else {
//                 // Otherwise reject with the status text
//                 // which will hopefully be a meaningful error
//                 reject(Error(req.statusText));
//             }
//         };
//
//         // Handle network errors
//         req.onerror = function() {
//             reject(Error("Network Error"));
//         };
//
//         // Make the request
//         req.send();
//     });
// }
//
// fakePrintMatrix = function(studies,features,score=undefined,colsep='\t\t\t',rowsep='\n'){
//     console.log('\t'+Object.keys(features).join('\t') + rowsep);
//     console.log('nomalized weight\t' + Object.values(features).join(colsep)+ rowsep);
//     var matrixString = ''
//     Object.keys(studies).map((study_id)=>{
//         matrixString = matrixString.concat(study_id + colsep);
//         Object.keys(features).map((feature)=>{
//             matrixString = matrixString.concat(studies[study_id][feature]+colsep);
//         })
//         if(score){
//             matrixString = matrixString.concat(score[study_id]);
//         }
//         matrixString = matrixString.concat(rowsep);
//
//     })
//     console.log(matrixString)
// }
//
// function getSortedKeys(obj) {
//     var keys = []; for(var key in obj) keys.push(key);
//     return keys.sort(function(a,b){return obj[b]-obj[a]});
// }
//
//
//
//
//
//
//
// studies = {'21427606':{'InitialSampleSize':'1733','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'0','UserRequested':'false'},'19744811':{'InitialSampleSize':'0','PublicationDate':'2017-06-01 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Circ Cardiovasc Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21430514':{'InitialSampleSize':'0','PublicationDate':'2017-06-14 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'0','UserRequested':'false'},'21430510':{'InitialSampleSize':'0','PublicationDate':'2017-06-10 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Heart Rhythm','ReplicateSampleSize':'0','UserRequested':'false'},'20509659':{'InitialSampleSize':'0','PublicationDate':'2017-06-01 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Stroke','ReplicateSampleSize':'0','UserRequested':'false'},'21346716':{'InitialSampleSize':'0','PublicationDate':'2017-05-25 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Twin Res Hum Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21429671':{'InitialSampleSize':'1586','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'true','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'300','UserRequested':'false'},'21405446':{'InitialSampleSize':'0','PublicationDate':'2017-06-12 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21427632':{'InitialSampleSize':'1733','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'0','UserRequested':'false'},'21405426':{'InitialSampleSize':'1733','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'true','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'318','UserRequested':'false'},'21311803':{'InitialSampleSize':'0','PublicationDate':'2017-05-31 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Diabetes','ReplicateSampleSize':'0','UserRequested':'false'},'19605897':{'InitialSampleSize':'626','PublicationDate':'2017-06-01 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'J Nutr Intermed Metab','ReplicateSampleSize':'0','UserRequested':'false'},'21405442':{'InitialSampleSize':'0','PublicationDate':'2017-06-12 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21405422':{'InitialSampleSize':'7750','PublicationDate':'2017-06-06 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'true','GenomeWideCoverage':'1','Publication':'Sci Rep','ReplicateSampleSize':'2030','UserRequested':'false'},'21430529':{'InitialSampleSize':'0','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Spine (Phila Pa 1976)','ReplicateSampleSize':'0','UserRequested':'false'},'21430523':{'InitialSampleSize':'0','PublicationDate':'2017-06-19 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Sci Rep','ReplicateSampleSize':'0','UserRequested':'false'},'21430526':{'InitialSampleSize':'0','PublicationDate':'2017-06-20 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Transl Psychiatry','ReplicateSampleSize':'0','UserRequested':'false'},'21430520':{'InitialSampleSize':'0','PublicationDate':'2017-06-19 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'PLoS Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21405450':{'InitialSampleSize':'0','PublicationDate':'2017-06-13 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Am J Med Genet B Neuropsychiatr Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21405430':{'InitialSampleSize':'31250','PublicationDate':'2017-06-09 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'true','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'9483','UserRequested':'false'},'21311811':{'InitialSampleSize':'1560','PublicationDate':'2017-05-31 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Brain Lang','ReplicateSampleSize':'0','UserRequested':'false'},'21405458':{'InitialSampleSize':'0','PublicationDate':'2017-05-26 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Commun','ReplicateSampleSize':'0','UserRequested':'false'},'21405438':{'InitialSampleSize':'0','PublicationDate':'2017-06-12 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Genet','ReplicateSampleSize':'0','UserRequested':'false'},'21405454':{'InitialSampleSize':'1293','PublicationDate':'2017-06-05 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Blood','ReplicateSampleSize':'0','UserRequested':'false'},'21405434':{'InitialSampleSize':'0','PublicationDate':'2017-06-12 00:00:00.0','SummaryStatisticsAvailable':'false','ReplicationStageIncluded':'false','GenomeWideCoverage':'1','Publication':'Nat Genet','ReplicateSampleSize':'0','UserRequested':'false'}};
//
//
// var FEATURES = {
//     InitialSampleSize : 10.5,
// //        ReplicateSampleSize : 8.09,
// //        GenomeWideCoverage : 8.95,
// //        SummaryStatisticsAvailable : 8.56, //dont know yet
// //        UserRequested : 8.56, //dont know yet
//     ReplicationStageIncluded : 8.09,
// //        PublicationDate : 6.7,
//
// }
// var needTakeLog = ['InitialSampleSize','PublicationDate']
// FEATURES_NOMALIZED = score.normalizeBySum(Object.values(FEATURES),1,Object.keys(FEATURES))
//
//
//     studies_raw = studies;
//     //debug
// //        studies[999999999999] = {
// //            Author : 'abc',
// //            PubmedId : '12345',
// //            GenomeWideCoverage : 1,
// //            InitialSampleSize : 9999999,
// //            GenomeWideCoverage : 1,
// //            SummaryStatisticsAvailable : 1, //dont know yet
// //            UserRequested : 1, //dont know yet
// //            ReplicationStageIncluded : 1,
// //            PublicationDate : '2005-04-25 00:00:00.0',
// //        }
//     //CLEAN STUDY DATA, replace null,na,undefined,false to 0, true to 1
//     studies_clean=JSON.parse(JSON.stringify(studies))
//     Object.keys(studies_clean).map((study_id)=>{
//         Object.keys(studies_clean[study_id]).map((feature)=>{
//             //remove element that is not a feature
//             if(Object.keys(FEATURES_NOMALIZED).indexOf(feature) == -1){
//                 delete studies_clean[study_id][feature]
//             }else{
//                 var value = studies_clean[study_id][feature];
//                 //clean
//                 if(value == undefined | value==null | value === 'false' | value == "NA") {
//                     value = 0;
//                 }
//                 if(value == 'true'){
//                     value = 1;
//                 }
//                 if(feature == 'PublicationDate'){
//                     value = new Date() - new Date(value)
//                 }
//                 studies_clean[study_id][feature] = value;
//             }
//         })
//     })
//     console.log('Clean null,na,undefined,false and true...');
//     console.log(studies_clean);
//
//
//     //group all feature together for nomolization across studies
//     groupByFeatures={};
//     Object.keys(FEATURES).map((feature)=>{
//         groupByFeatures[feature] ={};
//         Object.keys(studies_clean).map((study)=>{
//             groupByFeatures[feature][study] = studies_clean[study][feature]
//         })
//     })
//     console.log('Group by features:');
//     console.log(groupByFeatures);
//
//     groupByFeatures_nomalized = JSON.parse(JSON.stringify(groupByFeatures))
//     Object.keys(groupByFeatures_nomalized).map((feature)=>{
//         var featureObject = groupByFeatures_nomalized[feature];
//         if(needTakeLog.indexOf(feature) != -1){
//             //take log before nomalize
//             featureObject = score.takeLog(Object.values(groupByFeatures_nomalized[feature]),Object.keys(groupByFeatures_nomalized[feature]));
//         }
//         groupByFeatures_nomalized[feature] = score.normalizeByMax(Object.values(featureObject),100,Object.keys(featureObject))
//     })
//     console.log('Group by features nomalized:');
//     console.log(groupByFeatures_nomalized);
//
//     studies_nomalized = JSON.parse(JSON.stringify(studies_clean))
//     Object.keys(groupByFeatures_nomalized).map((feature)=>{
//         Object.keys(groupByFeatures_nomalized[feature]).map((study)=>{
//             studies_nomalized[study][feature]=groupByFeatures_nomalized[feature][study]
//         })
//     })
//     console.log('study nomalized:');
//     console.log(studies_nomalized);
//
//
//     studies_nomalized_weighted = JSON.parse(JSON.stringify(studies_nomalized))
//     Object.keys(studies_nomalized_weighted).map((study_id)=>{
//         Object.keys(studies_nomalized_weighted[study_id]).map((feature)=>{
//             studies_nomalized_weighted[study_id][feature] = studies_nomalized_weighted[study_id][feature] * FEATURES_NOMALIZED[feature]
//         })
//     })
//     console.log('study nomalized weighted:');
//     console.log(studies_nomalized_weighted);
//
//     studies_score={}
//     Object.keys(studies_nomalized_weighted).map((study)=>{
//         studies_score[study] = Object.values(studies_nomalized_weighted[study]).reduce(score.sum,0)
//     });
//     console.log('study score:');
//     console.log(studies_score);
//
//     fakePrintMatrix(studies,FEATURES_NOMALIZED)
//     fakePrintMatrix(studies_clean,FEATURES_NOMALIZED)
//     fakePrintMatrix(studies_nomalized,FEATURES_NOMALIZED)
//     fakePrintMatrix(studies_nomalized_weighted,FEATURES_NOMALIZED,studies_score)
//
//     studies_ordered = getSortedKeys(studies_score)
// //     studies_ordered.map((key)=>{
// //
// //         var title = Object.keys(studies_raw[key]).map((features)=>{
// //             if(Object.keys(FEATURES).indexOf(features) != -1){
// //                 return features + ':' +studies_raw[key][features];
// //             }
// //             return '';
// //         }).filter((x)=>{return x!='';}).join('\n');
// //
// //         var title2 = Object.keys(studies_nomalized[key]).map((features)=>{
// //             if(Object.keys(FEATURES).indexOf(features) != -1){
// //                 return features + ':' +studies_nomalized[key][features];
// //             }
// //             return '';
// //         }).filter((x)=>{return x!='';}).join('\n');
// //
// //         var item = $('<a />', {
// //             class: 'list-group-item col-xs-11',
// //             title: key,
// //         }).appendTo($('#study-score'));
// // //            item.append(key);
// //         item.append(studies[key]['PubmedId'] + ' ' + studies[key]['Author']);
// //
// //
// //         $('<span />', { class: 'badge', text: studies_score[key].toFixed(2), title:title + '\n\n' +title2,}).appendTo(item);
// //         var link = $('<span />', { class: 'glyphicon glyphicon-new-window external-link'}).css({'float':'right'}).appendTo(item);
// //
// //         link.click(() => {
// //             window.open(window.location.pathname+'/'+key, '_blank');
// //         })
// //     })
//
//
// //https://www.npmjs.com/package/matrix-js
//
//
// function printResult(studies,FEATURES){
//     var array = [];
//     array.push([''].concat(Object.keys(FEATURES)))
//     Object.keys(studies).map((study_id)=>{
//         var row = [study_id];
//         Object.keys(FEATURES).map((feature)=>{
//             row = row.concat(studies[study_id][feature];)
//         })
//         array.push(row);
//         return row;
//     })
//     var A = matrix(array);
//     console.log(A([],[]))
// }
//
//
//
// A([],0).reduce(function(total,currentValue){
//     return total.concat(currentValue)
// },[])
//
// console.log(A([],[]))
