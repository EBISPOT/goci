/**
 * Created by xinhe on 31/05/2017.
 */


$( document ).ready(function() {
    //add multi sort to table as default sorting, this cannot be added to table as html because the double-quote escape problem
    $('#mainTable').attr('data-sort-priority',
                         '[{"sortName":"year","sortOrder":"desc"},{"sortName":"month","sortOrder":"desc"},{"sortName":"curator","sortOrder":"desc"}]')
});


monthSorter = function(a, b) {
    var monthNames = {
        "January": 1,
        "February": 2,
        "March": 3,
        "April": 4,
        "May": 5,
        "June": 6,
        "July": 7,
        "August": 8,
        "September": 9,
        "October": 10,
        "November": 11,
        "December": 12
    };
    if (monthNames[a] < monthNames[b]) return -1;
    if (monthNames[a] > monthNames[b]) return 1;
    return 0;
}