function loadColumnChart(columnChartData) {

    am4core.useTheme(am4themes_animated);

    // Create chart instance
    var chart2 = am4core.create("chartdiv2", am4charts.XYChart3D);

    // Add data
    chart2.data = columnChartData;

    // Create axes
    let categoryAxis = chart2.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = "trait";
    categoryAxis.renderer.labels.template.rotation = 270;
    categoryAxis.renderer.labels.template.hideOversized = false;
    categoryAxis.renderer.minGridDistance = 20;
    categoryAxis.renderer.labels.template.horizontalCenter = "right";
    categoryAxis.renderer.labels.template.verticalCenter = "middle";
    categoryAxis.tooltip.label.rotation = 270;
    categoryAxis.tooltip.label.horizontalCenter = "right";
    categoryAxis.tooltip.label.verticalCenter = "middle";

    categoryAxis.renderer.inside = true;
    categoryAxis.renderer.grid.template.disabled = true;
    categoryAxis.renderer.labels.template.fontSize = 12;

    let labelTemplate = categoryAxis.renderer.labels.template;
    labelTemplate.rotation = -90;
    labelTemplate.horizontalCenter = "left";
    labelTemplate.verticalCenter = "middle";
    labelTemplate.dy = 0; // moves it a bit down;
    labelTemplate.inside = false; // this is done to avoid settings which are not suitable when label is rotated

    let valueAxis = chart2.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = "Studies";
    valueAxis.title.fontWeight = "bold";
    valueAxis.renderer.labels.template.fontSize = 10;

    // Create series
    var series = chart2.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.valueY = "studies";
    series.dataFields.categoryX = "trait";
    series.name = "Visits";
    series.tooltipText = "{categoryX}: [bold]{valueY}[/]";
    series.columns.template.fillOpacity = .8;

    var columnTemplate = series.columns.template;
    columnTemplate.strokeWidth = 2;
    columnTemplate.strokeOpacity = 1;
    columnTemplate.stroke = am4core.color("#FFFFFF");

    columnTemplate.adapter.add("fill", function(fill, target) {
        return chart2.colors.getIndex(target.dataItem.index);
    })

    columnTemplate.adapter.add("stroke", function(stroke, target) {
        return chart2.colors.getIndex(target.dataItem.index);
    })

    chart2.cursor = new am4charts.XYCursor();
    chart2.cursor.lineX.strokeOpacity = 0;
    chart2.cursor.lineY.strokeOpacity = 0;

}