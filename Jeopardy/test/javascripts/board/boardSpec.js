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
			updateOpponents: jasmine.createSpy (),
			showQuestionAndBuzzer: jasmine.createSpy ()
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
				expect (websocket.send).toHaveBeenCalledWith ("chooseQuestion", {id: 203});
			});
			
			it ("removes Mike from control", function () {
				expect (view.displayUserStatus).toHaveBeenCalledWith ("WaitingForChoiceStatus");
			});
		});
		
		describe ("and Mike hits the buzzer", function () {
			
			beforeEach (function () {
				subject.buzz ();
			});
			
			it ("ignores the buzz because Mike is not in WaitingForBuzzStatus", function () {
				expect (websocket.send).not.toHaveBeenCalled ();
			});
		});
		
		describe ("and a question is asked", function () {
			
			beforeEach (function () {
				boardHandlers.askQuestion ({id: 202, text: "Black, gritty, and viscous"});
			});
			
			it ("instructs view to show question and buzzer", function () {
				expect (view.showQuestionAndBuzzer).toHaveBeenCalledWith (id: 202, "Black, gritty, and viscous");
			});
			
			describe ("and Mike hits the buzzer", function () {
				
				beforeEach (function () {
					subject.buzz ();
				});
				
				it ("relays the buzz to the back end", function () {
					expect (websocket.send).toHaveBeenCalledWith ("buzz", {})
				});
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

describe ("A Board View, initialized,", function () {
	var subject = null;
	var controller = null;
	
	beforeEach (function () {
		$('body').append (
			'<div id="board-page-content">\n' +
			'	<table>\n' +
			'		<thead>\n' +
			'			<tr id="categories">\n' +
			'			</tr>\n' +
			'		</thead>\n' +
			'		<tbody id="questions">\n' +
			'		</tbody>\n' +
			'	</table>\n' +
			'	<div id="center-display">\n' +
			'	</div>\n' +
			'	<table>\n' +
			'		<thead>\n' +
			'			<tr>\n' +
			'				<td>Player Name</td>\n' +
			'				<td>Score</td>\n' +
			'				<td>Status</td>\n' +
			'			</tr>\n' +
			'		</thead>\n' +
			'		<tbody id="board-player-list">\n' +
			'		</tbody>\n' +
			'	</table>\n' +
			'</div>\n'
		);
		subject = Jeopardy.Board.View;
		controller = {
		};
		
		subject.initialize (controller);
	});
	
	afterEach (function () {
		$('#board-page-content').remove ();
	});
	
	describe ("and given a board update", function () {
		
		beforeEach (function () {
			var update = [
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
			];
			subject.updateBoard (update);
		});
		
		it ("sets up categories correctly", function () {
			expect ($("#categories #category-101").html ()).toBe ("Things Not to Drink");
			expect ($("#categories #category-102").html ()).toBe ("Things Not to Eat");
		});
		
		it ("sets up questions correctly", function () {
			expect ($("#questions #question-201").html ()).toBe ("200");
			expect ($("#questions #question-202").html ()).toBe ("400");
			expect ($("#questions #question-203").html ()).toBe ("200");
			expect ($("#questions #question-204").html ()).toBe ("400");
		});
	});
});
