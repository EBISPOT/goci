$(window).load(function() {

    // loader settings
    var opts = {
        lines: 9, // The number of lines to draw
        length: 9, // The length of each line
        width: 5, // The line thickness
        radius: 14, // The radius of the inner circle
        color: '#EE3124', // #rgb or #rrggbb or array of colors
        speed: 1.9, // Rounds per second
        trail: 40, // Afterglow percentage
        className: 'spinner', // The CSS class to assign to the spinner
    };

    var target = document.getElementById('d3Graph');

    // trigger loader
    var spinner = new Spinner(opts).spin(target);

    table = $('#datatable_studies_backlog').dataTable();
    oSettings = table.fnSettings();
    table.fnClearTable(this);


    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 800 - margin.left - margin.right,
        height = 600 - margin.top - margin.bottom;

    var parseDate = d3.time.format("%Y%m%d").parse;

    var x = d3.time.scale().range([0, width]);
    var y = d3.scale.linear().range([height, 0]);
    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom").tickPadding(6);
    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left");
    var publishedLine = d3.svg.area()
        .interpolate("basis")
        .x(function(d) { return x(d.eventDay); })
        .y(function(d) { return y(d["studyPublished"]); });
    var createdLine = d3.svg.area()
        .interpolate("basis")
        .x(function(d) { return x(d.eventDay); })
        .y(function(d) { return y(d["studyCreation"]); });
    var area = d3.svg.area()
        .interpolate("basis")
        .x(function(d) { return x(d.eventDay); })
        .y1(function(d) { return y(d["studyPublished"]); });
    var svg = d3.select("#d3Graph").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    d3.json(location.pathname+"/getJsonData", function(error, data) {
        if (error) throw error;
        var subTotalCreated = 0;
        var subTotalPublished = 0;
        var date_datatable = "";
        data.forEach(function(d) {
            date_datatable = d.eventDay.slice(6,8)+'/'+d.eventDay.slice(4,6)+'/'+d.eventDay.slice(0,4);
            d.eventDay = parseDate(d.eventDay);
            d["studyPublished"]= +d["studyPublished"] + subTotalPublished;
            d["studyCreation"] = +d["studyCreation"] + subTotalCreated;
            subTotalPublished = d["studyPublished"];
            subTotalCreated = d["studyCreation"];

            table.oApi._fnAddData(oSettings, new Array(date_datatable,subTotalPublished, subTotalCreated));
        });



        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
        table.fnDraw();
        // stop the loader
        spinner.stop();
        //x.domain(d3.extent(data, function(d) { return d.eventDay; }));
        x.domain([data[0].eventDay, data[data.length-1].eventDay]);
        y.domain([
            d3.min(data, function(d) { return Math.min(d["studyPublished"], d["studyCreation"]); }),
            d3.max(data, function(d) { return Math.max(d["studyPublished"], d["studyCreation"]); })
        ]);
        svg.datum(data);
        svg.append("clipPath")
            .attr("id", "clip-below")
            .append("path")
            .attr("d", area.y0(height));
        svg.append("clipPath")
            .attr("id", "clip-above")
            .append("path")
            .attr("d", area.y0(0));
        svg.append("path")
            .attr("class", "area above")
            .attr("clip-path", "url(#clip-above)")
            .attr("d", area.y0(function(d) { return y(d["studyCreation"]); }));
        svg.append("path")
            .attr("class", "area below")
            .attr("clip-path", "url(#clip-below)")
            .attr("d", area);
        svg.append("path")
            .attr("class", "publishedLine")
            .attr("d", publishedLine);
        svg.append("path")
            .attr("class", "createdLine")
            .attr("d", createdLine);
        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);
        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Number of studies");
    });


});