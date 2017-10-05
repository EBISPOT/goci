
var colorMap={"efo": "blue", "uberon":"green", "Wikipedia": "red", "BTO": "yellow", "XAO":"black", "ZFA":"gray"}


// $(document).ready(function() {
//     drawGraph(getMainEFO().replace('_',':'),1);
// }) ;


$('#btn-toggle-oxo').click(function() {
    //To redraw the oxo graph so that it is center to the div
    drawGraph(getMainEFO().replace('_',':'),1);
});

// $('#button-oxo-graph').click(function() {
//     //To redraw the oxo graph so that it is center to the div
//     drawGraph(getMainEFO().replace('_',':'),1);
// });


function drawGraph (curie,distance) {

    var relativePath = 'https://www.ebi.ac.uk/spot/oxo/'
    $.getJSON(relativePath+"api/terms/"+curie+"/graph?distance="+distance, function(json) {})
            .success(function(json){
                var container = document.getElementById('oxo-vis');
                for(var i=0;i<json.nodes.length;i++){
                    json.nodes[i]["label"]=json.nodes[i]["id"]
                    json.nodes[i]["color"]=colorMap[json.nodes[i]["group"]]
                    json.nodes[i]["shape"]="box"
                }

                for(var i=0;i<json.links.length; i++){
                    json.links[i]["arrows"]='to'
                    json.links[i]["color"]=colorMap[json.links[i]["mappingSource"]]
                    json.links[i]["to"]=   json.links[i]["target"]
                    json.links[i]["from"]=   json.links[i]["source"]

                    // console.log(json.links[i]["color"])
                    // if (json.links[i]["color"]===undefined){
                    //     console.log("Undefined ontology found: "+json.links[i]["mappingSource"])
                    // }
                }

                var nodes = new vis.DataSet(json.nodes)
                var edges = new vis.DataSet(json.links)


                var data = {
                    nodes: nodes,
                    edges: edges
                };


                // console.log(nodes)
                // console.log(edges)
                // console.log(data)

                var options = {};
                var network = new vis.Network(container, data, options);
                //CENTER THE NETWORK
                network.fit()


            })
            .fail(function(e){console.log(e);console.log("Webservice call did not work!")})
    ;
}
