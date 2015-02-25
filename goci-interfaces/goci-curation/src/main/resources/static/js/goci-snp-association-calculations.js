// Calculates various values used in association form

// Calculate OR per copy num
$(document).ready(function() {
    $("#calculate-or").click(function() {
        var orPerCopyRecip = document.forms["snp-association-form"]["orPerCopyRecip"].value;
        calculateOrPerCopyNum(orPerCopyRecip);
    });
});

function calculateOrPerCopyNum(orPerCopyRecip) {
    var orPerCopyNum;
    orPerCopyNum=Math.round(100/orPerCopyRecip)/100;
    console.log(orPerCopyNum);
    document.getElementById("orPerCopyNum").value = orPerCopyNum;
}

// Build orPerCopyRange
$(document).ready(function() {
    $("#calculate-or-range").click(function() {
        var orPerCopyStdError = document.forms["snp-association-form"]["orPerCopyStdError"].value;
        var orPerCopyNum = document.forms["snp-association-form"]["orPerCopyNum"].value;
        setRange(orPerCopyStdError, orPerCopyNum);
    });
});


/* This method calculates the confidence interval based on the standard error
 taken from Kent's Coldfusion code.*/

function setRange(orPerCopyStdError, orPerCopyNum){
    delta = Math.round(100000 * orPerCopyStdError * 1.96) / 100000;
    ORpercopylow = orPerCopyNum - delta;
    ORpercopyhigh = (1 * orPerCopyNum) + delta;

    if (ORpercopylow < .001) {
        ORpercopylow2 = ORpercopylow.toFixed(5);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(5);
    }
    else if (ORpercopylow < .01) {
        ORpercopylow2 = ORpercopylow.toFixed(4);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(4);
    }
    else if (ORpercopylow < .1) {
        ORpercopylow2 = ORpercopylow.toFixed(3);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(3);
    }
    else {
        ORpercopylow2 = ORpercopylow.toFixed(2);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(2);
    }

    document.getElementById("orPerCopyRange").value = '[' + ORpercopylow2 + '-' + ORpercopyhigh2 + ']';
}