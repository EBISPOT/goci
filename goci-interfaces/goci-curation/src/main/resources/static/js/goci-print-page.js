// prefix : /path/   id: number or id  // suffix : empty or /path
function printPage (prefix, id, suffix) {
    var link = prefix+id+suffix;
    var thePopup = window.open( link, "Print", "menubar=0,location=0,height=700,width=700" );
    $('#popup-content').clone().appendTo( thePopup.document.body );
    thePopup.print();
}

