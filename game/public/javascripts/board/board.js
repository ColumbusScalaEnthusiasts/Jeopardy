var Jeopardy = Jeopardy ? Jeopardy : {};

Jeopardy.Board = {}

Jeopardy.Board.Controller = (function () {
	var self = {};
	
	var playerId = null;
	var websocket = null;
	var view = null;
	var boardStatus = null;
	
	var findUserPlayer = function () {
		return _.find (boardStatus.players, function (player) {return player.id === playerId});
	};
	
	var findOpponents = function () {
		return _.filter (boardStatus.players, function (player) {return player.id !== playerId});
	};
	
	var handleBoardStatus = function (boardStatusParam) {
		boardStatus = boardStatusParam;
		view.updateBoard (boardStatus.columns);
		view.displayUserStatus (findUserPlayer ());
		view.updateOpponents (findOpponents ());
	};
	
	var handleAskQuestion = function (askQuestion) {
		_.each (boardStatus.players, function (player) {player.status = "WaitingForBuzzStatus";});
		view.showQuestionAndBuzzer (askQuestion.id, askQuestion.text);
	};
	
	var eventHandlers = {
		boardStatus: handleBoardStatus,
		askQuestion: handleAskQuestion
	};
	
	self.initialize = function (playerIdParam, websocketParam, viewParam) {
		playerId = playerIdParam;
		view = viewParam;
		websocket = websocketParam;
		
		websocket.replaceEvents (eventHandlers);
		view.initialize (self);
	};
	
	self.chooseQuestion = function (questionId) {
		if (findUserPlayer ().status !== "InControlStatus") {return;}
		view.displayUserStatus ("WaitingForChoiceStatus");
		websocket.send ("chooseQuestion", {id: questionId});
	};
	
	self.buzz = function () {
		if (findUserPlayer ().status !== "WaitingForBuzzStatus") {return;}
		websocket.send ("buzz", {});
	};
	
	return self;
}) ();

Jeopardy.Board.View = (function () {
	var self = {};
	
	var updateCategories = function (categories) {
		var headings = _.map (categories, function (category) {
			return '<td id="category-' + category.id + '" class="category-name">' + category.name + '</td>';
		});
		var html = headings.join ("");
		$("#categories").html (html);
	};
	
	var updateQuestions = function (questionRows) {
		var html = _.map (questionRows, function (row) {
			var cells = _.map (row, function (question) {
				return '<td id="question-' + question.id + '" class="question-value">' + question.value + '</td>';
			});
			return '<tr>' + cells.join ("") + '</tr>';
		});
		$("#questions").html (html);
	};
	
	self.initialize = function (controller) {
		
	};
	
	self.updateBoard = function (boardUpdate) {
		updateCategories (_.map (boardUpdate, function (column) {return column.category}));
		var questionRows = _.map (boardUpdate[0].questions, function (dummy, index) {
			return _.map (boardUpdate, function (column) {return column.questions[index];});
		});
		updateQuestions (questionRows);
	};
	
	var INSTRUCTIONS = {
		InControlStatus: "Make your selection.",
		WaitingForChoiceStatus: "Wait for a question to be chosen..."
	};
	
	self.displayUserStatus = function (player) {
		$('#user-player-name').html (player.name);
		$('#user-player-score').html (player.score);
		var instructions = $('#user-player-instructions');
		instructions.attr ("status", player.status);
		instructions.html (INSTRUCTIONS[player.status]);
	};
	
	self.updateOpponents = function (opponents) {
		var html = "";
		_.each (opponents, function (opponent) {
			html +=
				'<tr class="opponent-row" id="opponent-row-' + opponent.id + '">' +
				'<td class="opponent-name" id="opponent-name-' + opponent.id + '">' + opponent.name + '</td>' +
				'<td class="opponent-score" id="opponent-score-' + opponent.id + '">' + opponent.score + '</td>' +
				'<td class="opponent-status" id="opponent-status-' + opponent.id + '">' + opponent.status + '</td>' +
				'</tr>'
		});
		$('#board-player-list').html (html);
	}
	
	return self;
}) ();