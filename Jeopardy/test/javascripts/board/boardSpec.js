describe ("A Board controller, initialized with mocks,", function () {
	var websocket = null;
	var view = null;
	var subject = null;
	var boardHandlers = null;
	
	beforeEach (function () {
		websocket = {
			replaceEvents: jasmine.createSpy ().andCallFake (function (events) {boardHandlers = events;}),
			send: jasmine.createSpy ()
		};
		view = {
			initialize: jasmine.createSpy (),
			updateBoard: jasmine.createSpy (),
			displayUserStatus: jasmine.createSpy (),
			updateOpponents: jasmine.createSpy ()
		};
		subject = Jeopardy.Board.Controller;
		
		subject.initialize (1234, websocket, view);
	});
	
	it ("replaces the websocket's event handlers", function () {
		expect (websocket.replaceEvents).toHaveBeenCalled ();
	});
	
	it ("initializes the view", function () {
		expect (view.initialize).toHaveBeenCalledWith (subject);
	});
	
	describe ("and sent a boardStatus message showing this player in control", function () {
		var boardStatusMessage = null;
		
		beforeEach (function () {
			boardStatusMessage = {
				players: [
				    {id: 1234, name: "Mike", score: 150, status: "InControlStatus"},
				    {id: 2345, name: "Nancy", score: 510, status: "WaitingForChoiceStatus"},
				    {id: 3456, name: "Oscar", score: 250, status: "WaitingForChoiceStatus"}
				],
				columns: [
				    {
				    	category: {id: 101, name: "Things Not to Drink"},
				    	questions: [
				    	    {id: 201, value: 200},
				    	    {id: 202, value: 400}
				    	]
				    },
				    {
				    	category: {id: 102, name: "Things Not to Eat"},
				    	questions: [
				    	    {id: 203, value: 200},
				    	    {id: 204, value: 400}
				    	]
				    }
				]
			};
		
			boardHandlers.boardStatus (boardStatusMessage);
		});
	
		it ("instructs the view to update the board contents", function () {
			expect (view.updateBoard).toHaveBeenCalledWith (boardStatusMessage.columns);
		});
		
		it ("instructs the view to show Mike as the user in control of the board", function () {
			expect (view.displayUserStatus).toHaveBeenCalledWith ("InControlStatus");
		});
	
		it ("instructs the view to show the opponents", function () {
			expect (view.updateOpponents).toHaveBeenCalledWith ([boardStatusMessage.players[1], 
			    boardStatusMessage.players[2]]);
		});
		
		describe ("when Mike chooses a question", function () {
			
			beforeEach (function () {
				subject.chooseQuestion (203);
			});
			
			it ("relays the chosen question to the back end", function () {
				expect (websocket.send).toHaveBeenCalledWith ({
					type: "chooseQuestion",
					data: {categoryIndex: 1, rowIndex: 0}
				});
			});
			
			it ("removes Mike from control", function () {
				expect (view.displayUserStatus).toHaveBeenCalledWith ("WaitingForChoiceStatus");
			});
		});
	});
	
	describe ("and sent a boardStatus message showing this player not in control", function () {
		var boardStatusMessage = null;
		
		beforeEach (function () {
			boardStatusMessage = {
				players: [
				    {id: 1234, name: "Mike", score: 150, status: "WaitingForChoiceStatus"},
				    {id: 2345, name: "Nancy", score: 510, status: "InControlStatus"}
				],
				columns: []
			};
		
			boardHandlers.boardStatus (boardStatusMessage);
		});
		
		describe ("when Mike tries to choose a question", function () {
			
			beforeEach (function () {
				subject.chooseQuestion (4321);
			});
			
			it ("he is ignored because he's not in control", function () {
				expect (websocket.send).not.toHaveBeenCalled ();
			});
		});
	});
});
