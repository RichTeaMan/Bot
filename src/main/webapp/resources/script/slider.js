// use document.getElementById('id').innerHTML = 'text' to change text in a paragraph, for example.

var slider = {

  get_position: function() {
    var marker_pos = $('#marker').position();
    var left_pos = marker_pos.left + slider.marker_size / 2;
    var top_pos = marker_pos.top + slider.marker_size / 2;

    slider.position = {
      left: left_pos,
      top: top_pos,
      x: Math.round(slider.round_factor.x * (left_pos * slider.xmax / slider.width)) / slider.round_factor.x,
      y: Math.round((slider.round_factor.y * (slider.height - top_pos) * slider.ymax / slider.height)) / slider.round_factor.y,
    };

    return slider.position;
  },

  display_position: function() {
    document.getElementById("coord").innerHTML = 'x: ' + slider.position.x.toString() + '<br> y: ' + slider.position.y.toString();
  },

  draw: function(x_size, y_size, xmax, ymax, marker_size, round_to) {

    if ((x_size === undefined) && (y_size === undefined) && (xmax === undefined) && (ymax === undefined) && (marker_size === undefined) && (round_to === undefined)) {
      x_size = 150;
      y_size = 150;
      xmax = 1;
      ymax = 1;
      marker_size = 20;
      round_to = 2;
    };

    slider.marker_size = marker_size;
    slider.height = y_size;
    slider.width = x_size;
    slider.xmax = xmax;
    slider.ymax = ymax;
    round_to = Math.pow(10, round_to);
    slider.round_factor = {
      x: round_to,
      y: round_to,
    };

    $("#markerbounds").css({
      "width": (x_size + marker_size).toString() + 'px',
      "height": (y_size + marker_size).toString() + 'px',
    });
    $("#box").css({
      "width": x_size.toString() + 'px',
      "height": y_size.toString() + 'px',
      "top": marker_size / 2,
      "left": marker_size / 2,
    });
    $("#marker").css({
      "width": marker_size.toString() + 'px',
      "height": marker_size.toString() + 'px',
    });

    $("#coord").css({
      "top": x_size + marker_size / 2
    });

    $("#widget").css({
      "width": (x_size + marker_size).toString() + 'px',
    });

    slider.get_position();
    slider.display_position();

  },

};

$("#marker").draggable({
  containment: "#markerbounds",
  drag: function() {
    writePwm(slider.get_position());
    slider.display_position();
  },
});

//syntax for rendering is:
//  slider.render(width, height, width-range, height-range, marker size, output decimal places)
var height = 400;
var width = 400;
slider.draw(height,width,1000,1000,20,2);
var marker_pos = $('#marker');
marker_pos.css('top', (slider.height - slider.marker_size) / 2);
marker_pos.css('left', (slider.width - slider.marker_size) / 2);

function writePwm(position) {
  var hWidth = slider.xmax / 2;
  var hHeight = slider.ymax / 2;
  var mag = (position.y - hHeight) * 2;
  var left = mag;
  var right = mag;

  var x = position.x - hWidth

  if (x > 0) {
    left = ((hWidth - x) / hWidth) * mag;
  }
  if (x < 0) {
    right = ((hWidth + x) / hWidth) * mag;
  }
  $("#pwm").html(`Left: ${left} Right: ${right}`);
  updateLeftMotor(left);
  updateRightMotor(right);
}
