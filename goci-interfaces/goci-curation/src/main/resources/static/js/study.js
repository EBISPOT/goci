/**
 * Created by emma on 11/12/14.
 *
 */

$(document).ready(function () {
    $("#add-study").submit(function (event) {
        var formdata = $(this).serializeArray();
        event.preventDefault();
        var study = parseStudyFormData(formdata);
        var response = submitStudy(study);
// redirect to response resource
    });
    console.log("Submit handler bound");
});

$('#inputDiseaseTrait').pillbox();

function parseStudyFormData(formdata) {
// translate form data into study json payload
    var study = {};
    $(formdata).each(function () {
        var $next = $(this).get()[0];
        var field = $next["name"];
        var value = $next["value"];
        if (validate(field, value)) {
            study[field] = value;
        }
        else {
// is the unrecognised field a form value?
            if ($("#add-study:has(#" + field + ")") && !(field.substring(0, "_".length) === "_")) {
                console.log("No validation checks for form field '" + field + "', adding attribute to study");
                study[field] = value;
            }
            else {
                console.log("Excluding field '" + field + "' from the created study");
            }
        }
    });
    return study;
}


function validate(field, value) {
    switch (field.toLowerCase()) {
        case "author" :
            return validateAuthor(value);
        case "title" :
            return validateTitle(value);
        case "publication" :
            return validatePublication(value);
        case "platform" :
            return validatePlatform(value);
        case "studydate" :
            return validateStudyDate(value);
        case "pubmedid":
            return validatePubmedId(value)
        default:
            console.log("Unrecognised field '" + field + "'");
            return false;
    }
}
function validateAuthor(author) {

    if (author == null || author == "") {
        alert("Author name must be filled out");
        return false;
    }
    else return true;
}
function validateTitle(title) {

    if (title == null || title == "") {
        alert("Title name must be filled out");
        return false;
    }
    else return true;
}
function validatePublication(publication) {
    return true;
}
function validatePubmedId(pubmedId) {
    // Check its numeric
    if (isNaN(pubmedId)) {
        alert(pubmedId + " is not a number");
        return false;
    }
    else return true;

}
function validatePlatform(platform) {
    return true;
}

// Do some date validation
function validateStudyDate(studyDate) {

    // Code from http://studentduniya.in/validate-date-format-yyyymmdd-javascript/
    var validatePattern = /^(\d{4})(\/|-)(\d{1,2})(\/|-)(\d{1,2})$/;
    var dateValues = studyDate.match(validatePattern);

    if (studyDate == null || studyDate == "") {
        alert("Study date is empty");
        return false;
    }

    else if (dateValues == null) {
        alert("Date does not match pattern YYYY-MM-DD");
        return false;
    }

    var dtYear = dateValues[1];
    var dtMonth = dateValues[3];
    var dtDay = dateValues[5];

    if (dtMonth < 1 || dtMonth > 12) {
        alert("Month value is out of range 1-12");
        return false;
    }

    else if (dtDay < 1 || dtDay > 31) {
        alert("Day value is out of range 1-31")
        return false;
    }

    else if ((dtMonth == 4 || dtMonth == 6 || dtMonth == 9 || dtMonth == 11) && dtDay == 31) {
        alert("April, June, Sept, Nov only have 30 days");
        return false;
    }

    else if (dtMonth == 2) {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay > 29 || (dtDay == 29 && !isleap))
            alert("Leap year");
        return false;
    }

    else return true;

}
function submitStudy(study) {
    var studyJson = JSON.stringify(study);
    $.ajax({
        type: "POST",
        url: "/api/studies",
        data: studyJson,
        success: handleResponse,
        contentType: "application/json"
    });
}
function handleResponse(response, status, xhr) {
    window.location = xhr.getResponseHeader("Location").replace("api/", "");
}