/**
 * Created by Dani on 23/09/16.
 */


$(document).ready(function() {


    if ($('#publish_flag').val() == 'true') {
        setInputsToReadOnly();
    }
});


function setInputsToReadOnly(){

    $('.dataForm').find('input').attr('readonly', true);
    $('.dataForm').find('textarea').attr('readonly', true);
    $('.dataForm').find(':checkbox').attr('disabled', true);
    $('.dataForm').find(':button').attr('disabled', true);
    $('.dataForm').find(':radio').attr('disabled', true);
    $('.dataForm').find('select').attr('disabled', true);
    $('.dataForm').find('button').attr('disabled', true);
    $('.dataForm').find('a').attr('disabled', true)

    $('.always-clickable').attr('disabled', false);
}