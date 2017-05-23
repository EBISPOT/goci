/**
 * Created by xinhe on 19/04/2017.
 */

$(document).ready(function() {
    $('#mainTable').DataTable({
                                  "info": true, "paging": false, "order": [[6, "desc"]]
                              });
    $('#sysTable').DataTable({
                                 "info": false, "paging": false, "order": [[5, "desc"]]
                             });

    $('[data-toggle="confirmation"]').confirmation({ btnOkLabel: "&nbsp;Yes", btnCancelLabel: "&nbsp;No" });
});