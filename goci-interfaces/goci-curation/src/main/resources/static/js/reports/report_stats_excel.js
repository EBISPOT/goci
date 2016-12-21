function InitChart() {

    var vis = d3.select("#d3ProgressiveQueue"),
        WIDTH = 1000,
        HEIGHT = 500,
        MARGINS = {
            top: 20,
            right: 20,
            bottom: 20,
            left: 50
        },
        xScale = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([27, 52]),
        yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0, 160]),
        xAxis = d3.svg.axis()
            .scale(xScale),
        yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("left");

    vis.append("svg:g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
        .call(xAxis);
    vis.append("svg:g")
        .attr("class", "y axis")
        .attr("transform", "translate(" + (MARGINS.left) + ",0)")
        .call(yAxis);

    var lineGen = d3.svg.line()
        .x(function(d) {
            return xScale(d[0]);
        })
        .y(function(d) {
            return yScale(d[1]);
        })
       .interpolate("basis");

    d3.json(location.pathname+"/getProgressiveQueuesJson", function(error, data) {
        var level1 = [];
        var level2 = [];
        var level3 = [];
        var i = 0;
        data.forEach(function(d) {
            level1[i] = [d[1],d[2]];
            level2[i] = [d[1],d[3]];
            level3[i] = [d[1],d[4]];
            i = i +1;
        });

        vis.append('svg:path')
            .attr('d', lineGen(level1))
            .attr('stroke', 'green')
            .attr('stroke-width', 2)
            .attr('fill', 'none');

        vis.append('svg:path')
            .attr('d', lineGen(level2))
            .attr('stroke', 'blue')
            .attr('stroke-width', 2)
            .attr('fill', 'none');

        vis.append('svg:path')
            .attr('d', lineGen(level3))
            .attr('stroke', 'red')
            .attr('stroke-width', 2)
            .attr('fill', 'none');

    });

}

$(window).load(function() {


    InitChart();

    $( "#generateStats" ).click(function() {
        $("#generateStats").attr("disabled", true);
        $("#downloadStatsExcel").attr("disabled", true);
        $.ajax({
            url: location.pathname+"/generateStats",
            beforeSend: function() {
                document.getElementById("loader").style.display = "block";
                document.getElementById("ButtonsMenu").style.display = "none";
            },
            error: function() {
                alert('An error has occurred');
                $("#generateStats").removeAttr("disabled");
                $("#downloadStatsExcel").removeAttr("disabled");
                document.getElementById("loader").style.display = "none";
                document.getElementById("ButtonsMenu").style.display = "block";
            },
            success: function(data) {
                $("#generateStats").removeAttr("disabled");
                $("#downloadStatsExcel").removeAttr("disabled");
                document.getElementById("loader").style.display = "none";
                document.getElementById("ButtonsMenu").style.display = "block";
                console.log(data);
            },
            complete: function() { console.log("complete");},
            type: 'GET'
        });
        return false;
    });

});