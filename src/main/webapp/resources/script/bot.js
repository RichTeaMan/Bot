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

function fetchVideoImage(destinationElementId) {
    $(`#${destinationElementId}`).attr("src", "/cam?" + new Date().getTime());
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
