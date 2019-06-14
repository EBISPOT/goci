function updateFirstAuthor(study_id, new_author_id, callback) {
    console.log(new_author_id);
    var action = contextPath + "studies/" + study_id + "/changeFirstAuthor/" + new_author_id;
    
    jQuery.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json; charset=utf-8'
        },
        type: "POST",
        url: action
    }).success(function (data, textStatus, jqXHR) {
        callback(data);
    }).fail(function( jqXHR, textStatus ) {
        callback(jQuery.parseJSON('{"error":" Request failed: '+textStatus+'"}'));
    });
}

function changeFirstAuthor(study_id, new_author_id, new_author) {
    
    var callback = function(msg) {
        var alertType = document.getElementById('changeAuthorAlert').classList;
        if (alertType.contains(" alert-success")) {alertType.remove("alert-success");}
        if (alertType.contains(" alert-danger")) {alertType.remove("alert-danger");}
       
        if (msg.error == undefined) {
            alertType.add("alert-success");
            document.getElementById('ChangeAuthorText').innerHTML="First Author changed with success!";
            document.getElementById('firstAuthorStatic').innerHTML = new_author;
        }
        else {
            alertType.add("alert-danger");
            document.getElementById('ChangeAuthorText').innerHTML='Something went wrong. First author not changed.';
        }
        
        document.getElementById('changeAuthorDiv').style.display = 'block';
    };
    
    updateFirstAuthor(study_id, new_author_id, callback);
}


// Get the <span> element that closes the modal
var span = document.getElementsByClassName("closePureJS")[0];

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    document.getElementById('changeAuthorDiv').style.display = "none";
}