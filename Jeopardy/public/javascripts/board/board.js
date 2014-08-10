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
		view.displayUserStatus (findUserPlayer ().status);
		view.updateOpponents (findOpponents ());
	};
	
	var findQuestion = function (questionId) {
		var categoryIndex = null;
		var rowIndex = null;
		_.each (boardStatus.columns, function (column, cIdx) {
			_.each (column.questions, function (question, qIdx) {
				if (question.id === questionId) {
					categoryIndex = cIdx;
					rowIndex = qIdx;
				}
			});
		});
		return {categoryIndex: categoryIndex, rowIndex: rowIndex};
	};
	
	var eventHandlers = {
		boardStatus: handleBoardStatus
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
		var coordinates = findQuestion (questionId);
		websocket.send ({type: "chooseQuestion", 
			data: {categoryIndex: coordinates.categoryIndex, rowIndex: coordinates.rowIndex}});
	};
	
	return self;
}) ();
