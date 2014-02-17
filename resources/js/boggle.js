$(function() {

  // enables radio button
  $('.btn').button();

  // wire up the Solve button.
  $('#solve-btn').click(function () {
    var board = boardData();
    var alertLow = $('#alert-low');
    alertLow.hide();
    var solution = $('#solution');
    solution.hide();

    $.post("boggle", JSON.stringify(board), function (data){
      var words = data.words.join(" ");
      $('#max-score').text(data.max_score);
      $('#words').text(words);
      solution.show();
    }).fail(function (data) {
      var alertLow = $('#alert-low');
      alertLow.text("Please fill in all rows!");
      alertLow.show();
    });
  });

  // wire up the radio buttons
  $('.size-option').click(function (e) {
    $('#alert-low').hide();
    var alertHigh = $('#alert-high');
    alertHigh.hide();
    var input = $(event.target).find('input');
    var size = input.val();
    if (size == 4) {
      alertHigh.text("4 x 4 Boards wil likely time out on Heroku. Use only if running locally. (sorry)")
      alertHigh.show();
    }
    createBoard(size);
    $('#boggle-container').show();
  });
});

// Creates a row in the boggle board.
function createRow(size) {
  var div = $('<div class="input-group">');
  var input = $('<input type="text" class="form-control col-xs-1">');
  input.attr('size', 1);
  input.attr('maxlength', size);
  div.append(input);
  return div;
}

// Creates a board of the given size and appends it to the DOM
function createBoard(size) {
  var board = $("#boggle-board");
  board.empty();
  for (i=0;i<size;i++) {
    board.append(createRow(size));
  }
}

// Get all the data from the boggle board, returns an array of strings.
function boardData() {
  var board = [];
  $("#boggle-board").find('input').each(function (index) {
    board.push($(this).val());
  });
  return board;
}
