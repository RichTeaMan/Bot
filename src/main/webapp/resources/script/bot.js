function beginVideo() {
    setInterval(function(){ populateGameGrid("videoImg"); }, 100);
}

function populateGameGrid(destinationElementId) {

    $(`#${destinationElementId}`).attr("src", "/cam?" + new Date().getTime());
}
