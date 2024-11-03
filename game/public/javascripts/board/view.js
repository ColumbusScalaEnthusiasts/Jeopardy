export const updateCategories = (categories) => {
  const headings = categories.map((category) => `<td id="category-${category.id}" class="category-name">${category.name}</td>`);
  const html = headings.join("");
  $("#categories").html(html);
};

export const updateQuestions = (questionRows) => {
  const html = questionRows.map((row) => {
    const cells = row.map((question) => `<td id="question-${question.id}" class="question-value">${question.value}</td>`);
    return `<tr>${cells.join("")}</tr>`;
  });
  $("#questions").html(html);
};

export const getInstructionsText = (_player, opponents) => {
  const inControlOpponent = opponents.find((opponent) => opponent.status === 'InControlStatus');
  const instructions = (inControlOpponent) ?
    `Wait for ${inControlOpponent.name} to choose a question...`
    :
    "Make your selection."
    ;
  return instructions
};


export const updateBoard = (boardUpdate) => {
  updateCategories(boardUpdate.map((column) => column.category));
  const questionRows = boardUpdate[0].questions.map((_dummy, index) => boardUpdate.map((column) => column.questions[index]));
  updateQuestions(questionRows);
};

export const updateOpponents = (opponents) => {
  let html = "";
  opponents.forEach((opponent) => {
    html +=
      `<tr class="opponent-row" id="opponent-row-${opponent.id}">` +
      `<td class="opponent-name" id="opponent-name-${opponent.id}">${opponent.name}</td>` +
      `<td class="opponent-score" id="opponent-score-${opponent.id}">${opponent.score}</td>` +
      `<td class="opponent-status" id="opponent-status-${opponent.id}">${opponent.status}</td>` +
      `</tr>`
  });
  $('#board-player-list').html(html);
};

export const displayPlayerStatus = (player, opponents) => {
  $('#user-player-name').html(player.name);
  $('#user-player-score').html(player.score);

  const instructions = $('#user-player-instructions');
  instructions.attr("status", player.status);
  instructions.html(getInstructionsText(player, opponents));

  updateOpponents(opponents);
};
