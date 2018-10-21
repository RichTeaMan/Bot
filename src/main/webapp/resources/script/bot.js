var videoInterval = null;

function createVideoStream(fps) {
    if (null != videoInterval) {
        clearInterval(videoInterval);
    }
    var interval = 1000 / fps;
    videoInterval = setInterval(function(){ fetchVideoImage("videoImg"); }, interval);

    updateInput('fpsTextInput', fps);
    updateInput('fpsRangeInput', fps);
}

function updateLeftMotor(speed) {
    var speedInt = parseInt(speed, 10);

    if (!isNaN(speedInt)) {
        updateInput('leftMotorTextInput', speedInt);
        updateInput('leftMotorRangeInput', speedInt);

        $.get(`/pwm1?pwm=${speedInt}`)
        .fail(function(data) {
            console.log("Failed to update left motor:");
            console.log(data);
        });
    }
}

function updateRightMotor(speed) {
    var speedInt = parseInt(speed, 10);

    if (!isNaN(speedInt)) {
        updateInput('rightMotorTextInput', speedInt);
        updateInput('rightMotorRangeInput', speedInt);

        $.get(`/pwm2?pwm=${speedInt}`)
        .fail(function(data) {
            console.log("Failed to update right motor:");
            console.log(data);
        });
    }
}

function fetchVideoImage(destinationElementId) {
    $(`#${destinationElementId}`).attr("src", "/cam?" + new Date().getTime());

    $.get(`/revs1`)
    .done(function(data) {
        console.log(data);
        $("#revs").text(data);
    })
    .fail(function(data) {
        console.log("Failed to get revs:");
        console.log(data);
    });
}

function updateInput(textInput, val) {
    var element = $(`#${textInput}`);
    if (element) {
        var oldVal = element.val();
        if (oldVal !== val) {
            element.val(val);
        }
    }
}
