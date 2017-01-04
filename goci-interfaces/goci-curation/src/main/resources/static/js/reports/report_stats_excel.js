function InitChart() {


    d3.json(location.pathname+"/getProgressiveQueuesJson", function(error, data) {
        var domainX = [];
        var level1 = [];
        var level2 = [];
        var level3 = [];
        var pointX = "";
        var maxY = 0;
        var i = 0;
        data.forEach(function(d) {

            pointX = d[1]+"/"+d[0];
            domainX[i] = pointX;
            level1[i] = [pointX,d[2]];
            level2[i] = [pointX,d[3]];
            level3[i] = [pointX,d[4]];
            maxY = Math.max(maxY,Math.max(d[2],d[3],d[4]));
            i = i +1;
        });

        var vis = d3.select("#d3ProgressiveQueue"),
            WIDTH = 1000,
            HEIGHT = 500,
            PADDING = 75,
            MARGINS = {
                top: 75,
                right: 75,
                bottom: 75,
                left: 75
            };
        var yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0, maxY+10]);
        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("left");
        var xScale = d3.scale.ordinal()
            .domain(domainX)
            .rangePoints([MARGINS.left, WIDTH - MARGINS.right]);

        var xAxis = d3.svg.axis()
            .scale(xScale)
            .orient("bottom");

        vis.append("svg:g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
            .call(xAxis);

        vis.append("svg:g")
            .attr("class", "y axis")
            .attr("transform", "translate(" + (MARGINS.left) + ",0)")
            .call(yAxis);

        vis.selectAll(".x text")  // select all the text elements for the xaxis
            .attr("transform", function(d) {
                return "translate(" + this.getBBox().height*-2 + "," + this.getBBox().height + ")rotate(-45)";
            });

        var lineGen = d3.svg.line()
            .x(function(d) {
                return xScale(d[0]);
            })
            .y(function(d) {
                return yScale(d[1]);
            })
            .interpolate("basis");

        vis.append("text")
            .attr("text-anchor", "middle")  // this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate("+ (PADDING/2) +","+(HEIGHT/2)+")rotate(-90)")  // text is drawn off the screen top left, move down and out and rotate
            .text("Nr. Studies");

        vis.append("text")
            .attr("text-anchor", "middle")  // this makes it easy to centre the text as the transform is applied to the anchor
            .attr("transform", "translate("+ (WIDTH/2) +","+(HEIGHT-(PADDING/4))+")")  // centre below axis
            .text("Week per Year");

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