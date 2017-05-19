/*
 This is the EFOWAS locus zoom plot
 */

// Genome base pairs static data
var genome_data = [
    {chr: 1, base_pairs: 249250621},
    {chr: 2, base_pairs: 243199373},
    {chr: 3, base_pairs: 198022430},
    {chr: 4, base_pairs: 191154276},
    {chr: 5, base_pairs: 180915260},
    {chr: 6, base_pairs: 171115067},
    {chr: 7, base_pairs: 159138663},
    {chr: 8, base_pairs: 146364022},
    {chr: 9, base_pairs: 141213431},
    {chr: 10, base_pairs: 135534747},
    {chr: 11, base_pairs: 135006516},
    {chr: 12, base_pairs: 133851895},
    {chr: 13, base_pairs: 115169878},
    {chr: 14, base_pairs: 107349540},
    {chr: 15, base_pairs: 102531392},
    {chr: 16, base_pairs: 90354753},
    {chr: 17, base_pairs: 81195210},
    {chr: 18, base_pairs: 78077248},
    {chr: 19, base_pairs: 59128983},
    {chr: 20, base_pairs: 63025520},
    {chr: 21, base_pairs: 48129895},
    {chr: 22, base_pairs: 51304566},
    {chr: 23, base_pairs: 156040895},
    {chr: 24, base_pairs: 57227415},

];

//use to work out where the variant is on the plot when the whole x axis was used to represent all chromosomes
//       [{"chr": 1, "base_pairs": 249250621, "genome_start": 0, "genome_end": 249250621, "tickpoint": 124625311},
//        {"chr": 2, "base_pairs": 243199373, "genome_start": 249250621, "genome_end": 492449994, "tickpoint": 370850308}]
var genome_data_merged = [];
var genome_end = 0;
genome_data.forEach(function(d, i) {
    genome_data_merged.push({});
    genome_data_merged[i].chr = d.chr;
    genome_data_merged[i].base_pairs = d.base_pairs;

    genome_data_merged[i].genome_start = genome_end;

    genome_end += d.base_pairs;
    genome_data_merged[i].genome_end = genome_end

    genome_data_merged[i].tickpoint = genome_data_merged[i].genome_start + Math.round(d.base_pairs / 2);

});

LocusZoom.Data.EfoWASSource = LocusZoom.Data.Source.extend(function(init) {
    this.parseInit(init);
}, "EfoWASLZ");

//we are not using api to fetch data
LocusZoom.Data.EfoWASSource.prototype.getURL = function(state, chain, fields) {
    return "";
};

//any post process after getting the JSON
//we are not using api to fetch data, during init, blank data will be pass to the plot
//when efo term(s) being selected, these will be replaced with the efo-assoication data
LocusZoom.Data.EfoWASSource.prototype.parseResponse = function(resp, chain, fields, outnames, trans) {
    var data = JSON.parse([]);
    return {header: chain.header, body: data};
};

//adding extra information for plotting for the efo association (association doc in this case)
LocusZoom.Data.EfoWASSource.prototype.parseResponse = function(resp, chain, fields, outnames, trans) {
    var data = JSON.parse(resp);
    data.forEach(function(d, i) {
        data[i].chr = data[i].chromosomeName[0];
        data[i].bp = data[i].chromosomePosition[0];
        data[i].pval = Math.pow(10, data[i].pValueExponent);
        data[i].phewas_string = data[i].accessionId;
        data[i].x = transferLocation(data[i].chr, data[i].bp);

    });
    return {header: chain.header, body: data};
};

//control  x,y,and points
LocusZoom.Layouts.add("data_layer", "efowas_pvalues", {
    id: "efowaspvalues",
    type: "scatter",
    point_shape: "circle",
    point_size: 30,
    tooltip_positioning: "vertical",
    id_field: "{{namespace}}id",
    fields: ["{{namespace}}phewas"],
    always_hide_legend: false,
    x_axis: {
        field: "{{namespace}}x",
        floor: 0,
        ceiling: 3094301596
    },
    y_axis: {
        axis: 1,
        field: "{{namespace}}pval|neglog10",
        floor: 0,
        upper_buffer: 0.1
    },
    //xintodo Set defaul color for categories
    color: {
        field: "{{namespace}}category",
        scale_function: "categorical_bin",
        parameters: {
            categories: ["infectious diseases", "neoplasms", "endocrine/metabolic", "hematopoietic",
                "mental disorders", "neurological", "sense organs", "circulatory system", "respiratory",
                "digestive", "genitourinary", "pregnancy complications", "dermatologic", "musculoskeletal",
                "congenital anomalies", "symptoms", "injuries  poisonings"],
            values: ["rgb(57,59,121)", "rgb(82,84,163)", "rgb(107,110,207)", "rgb(156,158,222)", "rgb(99,121,57)",
                "rgb(140,162,82)", "rgb(181,207,107)", "rgb(140,109,49)", "rgb(189,158,57)", "rgb(231,186,82)",
                "rgb(132,60,57)", "rgb(173,73,74)", "rgb(214,97,107)", "rgb(231,150,156)", "rgb(123,65,115)",
                "rgb(165,81,148)", "rgb(206,109,189)", "rgb(222,158,214)"],
            null_value: "#B8B8B8"
        }
    },
    legend: [],
    fill_opacity: 0.7,
    //xintodo datapoint tooltips
    tooltip: {
        closable: true,
        show: {or: ["highlighted", "selected"]},
        hide: {and: ["unhighlighted", "unselected"]},
        html: "<div><strong>{{{{namespace}}phewas_string}}</strong></div>" +
        "<div>efos: <strong>{{{{namespace}}pval|scinotation}}</strong></div>" +
        "<div>prefered EFO: <strong>{{{{namespace}}preferedEFO}}</strong></div>" +
        "<div>Parent EFO: <strong>{{{{namespace}}category}}</strong></div>"
    },
    behaviors: {
        onmouseover: [
            {action: "set", status: "highlighted"}
        ],
        onmouseout: [
            {action: "unset", status: "highlighted"}
        ],
        onclick: [
            {action: "toggle", status: "selected", exclusive: true}
        ],
        onshiftclick: [
            {action: "toggle", status: "selected"}
        ]
    },
//        label: {
//            text: "{{{{namespace}}phewas_string}}",
//            spacing: 8,
//            lines: {
//                style: {
//                    "stroke-width": "2px",
//                    "stroke": "#333333",
//                    "stroke-dasharray": "2px 2px"
//                }
//            },
//            filters: [
//                {
//                    field: "{{namespace}}pval|neglog10",
//                    operator: ">=",
//                    value: 5
//                }
//            ],
//            style: {
//                "font-size": "10px",
//                "font-weight": "bold",
//                "fill": "#333333"
//            }
//        }
});

//init x,y ticks, adding data layer
LocusZoom.Layouts.add("panel", "efowas", {
    id: "efowas",
    width: 800,
    height: 300,
    min_width: 800,
    min_height: 300,
    proportional_width: 1,
    margin: {top: 20, right: 50, bottom: 120, left: 50},
    inner_border: "rgb(210, 210, 210)",
    dashboard: (function() {
        var l = LocusZoom.Layouts.get("dashboard", "standard_panel", {unnamespaced: true});
        l.components.push({
                              type: "toggle_legend",
                              position: "right"
                          });
//            l.components.push({
//                                  type: "resize_to_data",
//                                  position: "right"
//                              });
        return l;
    })(),
    axes: {
        x: {
            label: "Genomic Position (number denotes chromosome)",
            label_offset: 35,
            ticks: [
                {
                    x: 124625310,
                    text: "1",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 370850307,
                    text: "2",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 591461209,
                    text: "3",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 786049562,
                    text: "4",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 972084330,
                    text: "5",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1148099493,
                    text: "6",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1313226358,
                    text: "7",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1465977701,
                    text: "8",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1609766427,
                    text: "9",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1748140516,
                    text: "10",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 1883411148,
                    text: "11",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2017840353,
                    text: "12",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2142351240,
                    text: "13",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2253610949,
                    text: "14",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2358551415,
                    text: "15",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2454994487,
                    text: "16",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2540769469,
                    text: "17",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2620405698,
                    text: "18",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2689008813,
                    text: "19",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2750086065,
                    text: "20",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2805663772,
                    text: "21",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: 2855381003,
                    text: "22",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: genome_data_merged[22].tickpoint,
                    text: "X",
                    style: {
                        "fill": "rgb(120, 120, 186)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
                {
                    x: genome_data_merged[23].tickpoint,
                    text: "Y",
                    style: {
                        "fill": "rgb(0, 0, 66)",
                        "text-anchor": "center",
                        "font-size": "13px",
                        "font-weight": "bold"
                    },
                    transform: "translate(10, 5)  rotate(35)"
                },
            ]
        },
        y1: {
            label: "-log10 p-value",
            label_offset: 28
        }
    },
    legend: {
        orientation: "vertical",
        origin: {x: 55, y: 40},
        hidden: true
    },
    data_layers: [
        LocusZoom.Layouts.get("data_layer", "significance", {unnamespaced: true}),
        LocusZoom.Layouts.get("data_layer", "efowas_pvalues", {unnamespaced: true})
    ]
});

//the plot, adding efowas panel
LocusZoom.Layouts.add("plot", "standard_efowas", {
    width: 800,
    height: 600,
    min_width: 800,
    min_height: 600,
    responsive_resize: true,
    dashboard: LocusZoom.Layouts.get("dashboard", "standard_plot", {unnamespaced: true}),
    panels: [
        LocusZoom.Layouts.get("panel", "efowas", {unnamespaced: true, proportional_height: 0.45})
    ],
    mouse_guide: false
});

//given a location, map it to the plot xais
transferLocation = function(chr, position) {
    return genome_data_merged[chr - 1].genome_start + position;
}

//replot receive a list of association_docs from solr search
readlodLocusZoom = function(plot_id, data_association) {
    //adding information to association doc
    LocusZoom.Data.EfoWASSource.prototype.parseResponse = function(resp, chain, fields, outnames, trans) {
        var data = JSON.parse(JSON.stringify(data_association.docs));
        console.log("Parsing association doc to locus zoom.");
        console.log(data_association.docs);
        data.forEach(function(d, i, object) {
//                if (d.shortForm.length > 1) {
//                    console.log(d);
//                }

            if (d.chromLocation) {
                //we only plot those association which have chromosome information
                data[i].chr = d.chromLocation[0].split(":")[0];
                if (data[i].chr == 'X') {
                    data[i].chr = 23;
                }
                if (data[i].chr == 'Y') {
                    data[i].chr = 24;
                }
                data[i].bp = parseInt(d.chromLocation[0].split(":")[1]);
                data[i].pval = Math.pow(10, d.pValueExponent);
                data[i].phewas_string = d.rsId[0];
                data[i].x = transferLocation(data[i].chr, data[i].bp);
            }
            else {
                //some association don't have chromosome information
                object.splice(i, 1);
            }

        });
        return {header: chain.header, body: data};
    };

    var data_sources = new LocusZoom.DataSources()
            .add("base", ["EfoWASLZ", {url: "www.api.com"}]);


    var layout = LocusZoom.Layouts.get("plot", "standard_efowas");
    layout.panels[0].margin.top = 32;
    layout.panels[0].data_layers[0].offset = 7.30103; // Higher offset for line of GWAS significance than the default 4.522

    //xintodo create the color palate here
    //find all efos for plotting color
    var legend = [];
    var colorMap = {};

//
//        data_association.docs.forEach(function(d, i) {
//            //algorithm for select efo for association
////            allEFO.push(d.mappedUri[0]);
//            allEFO.push( d.preferedEFO);
//        })
//        //unique only
//        allEFO = allEFO.filter(function(value, index, self) {
//            return self.indexOf(value) === index;
//        })
//        //random rgb color at, legend does not work atm
//        allEFO.forEach(function(d, i) {
//            col = 'rgb(' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) +
//                    ',' + (Math.floor(Math.random() * 256)) + ')';
//            allColor.push(col);
//            legend.push({
//                            shape: "rect",
//                            color: col,
//                            width: 9,
//                            label: d
//                        }
//            )
//        })

    function hexToRgb(hex) {
        var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
//            return result ? {
//                        r: parseInt(result[1], 16),
//                        g: parseInt(result[2], 16),
//                        b: parseInt(result[3], 16)
//                    } : null;
        return result ? 'rgb(' + parseInt(result[1], 16) + ','
                + parseInt(result[2], 16) + ','
                + parseInt(result[3], 16) + ')' : 'rgb(255,255,255)';
    }

    data_association.docs.forEach(function(d, i) {
//            colorMap[d.preferedEFO] = hexToRgb(d.preferedColor);
        colorMap[d.category] = hexToRgb(d.preferedColor);
    })
    legend_catgories = [];
    legend_color = [];
    for (var key in colorMap) {
        legend_catgories.push(key);
        legend_color.push(colorMap[key]);
        legend.push({
                        shape: "rect",
                        color: colorMap[key],
                        width: 9,
                        label: key
                    }
        )
    }
    layout.panels[0].data_layers[1].color.parameters.categories = legend_catgories;
    layout.panels[0].data_layers[1].color.parameters.values = legend_color;
    layout.panels[0].data_layers[1].legend = legend;


    // Generate the plot
    var plot = LocusZoom.populate(plot_id, data_sources, layout);
}