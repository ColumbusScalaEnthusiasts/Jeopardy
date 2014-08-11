
describe ("A Vestibule Controller, initialized with mocks", function () {
	var subject = null;
	var websocket = null;
	var view = null;
	
	beforeEach (function () {
		subject = Jeopardy.Vestibule.Controller;
		websocket = {send: jasmine.createSpy ()};
		spyOn (Jeopardy.Utils, "websocket").andReturn (websocket);
		view = {
			initialize: jasmine.createSpy (),
			displayControls: jasmine.createSpy (),
			updatePlayers: jasmine.createSpy (),
			displayBoard: jasmine.createSpy (),
			closed: jasmine.createSpy ()
		};
		
		subject.initialize ("http://base.url", view);
	});
	
	it ("initializes the view", function () {
		expect (view.initialize).toHaveBeenCalledWith (subject);
	});
	
	it ("orders the display of the sign-in panel", function () {
		expect (view.displayControls).toHaveBeenCalledWith ("SIGNIN");
	});
	
	it ("calls Jeopardy.Utils.websocket", function () {
		expect (Jeopardy.Utils.websocket).toHaveBeenCalled ();
	});
	
	it ("is not signed in", function () {
		expect (subject.playerId).toBe (null);
	});

	describe ("creates a WebSocket", function () {
		var url = null;
		var open = null;
		var close = null;
		var playerList = null;
		var signedIn = null;
		var gameStarting = null;
		
		beforeEach (function () {
			var call = Jeopardy.Utils.websocket.calls[0];
			url = call.args[0];
			open = call.args[1].open;
			close = call.args[1].close;
			playerList = call.args[1].events.playerList;
			signedIn = call.args[1].events.signedIn;
			gameStarting = call.args[1].events.gameStarting;
		});
		
		it ("with the expected URL", function () {
			expect (url).toBe ("ws://base.url/vestibule/wsinit");
		});
		
		describe ("with an open callback that, when called", function () {
			
			beforeEach (function () {
				open ();
			});
			
			it ("instructs the view to display the sign-in control panel", function () {
				expect (view.displayControls).toHaveBeenCalledWith ("SIGNIN");
			});
		});
		
		describe ("with a close callback that, when called", function () {
			
			beforeEach (function () {
				close ();
			});
			
			it ("tells the view that the connection has been dropped", function () {
				expect (view.closed).toHaveBeenCalled ();
			});
		});
		
		describe ("with a playerList callback that, when called", function () {
			var data = null;
			
			beforeEach (function () {
				data = {players: [
  				    {name: "Jeffy", id: 12345}, 
				    {name: "Chubs", id: 23456}
				]};
				playerList (data);
			});
			
			it ("updates the view appropriately", function () {
				expect (view.updatePlayers).toHaveBeenCalledWith (data);
			});
		});
		
		describe ("with a signedIn callback that, when called", function () {
			
			beforeEach (function () {
				signedIn ({id: 12345});
			});
			
			it ("remembers the player's ID", function () {
				expect (subject.playerId).toBe (12345);
			});
			
			it ("instructs the view to display the ready-button control panel", function () {
				expect (view.displayControls).toHaveBeenCalledWith ("READY");
			});
		});
		
		describe ("with a gameStarting callback that, when called", function () {
			
			beforeEach (function () {
				subject.playerId = 1234;
				spyOn (Jeopardy.Board.Controller, 'initialize');
				gameStarting ();
			});
			
			it ("instructs the view to display the board", function () {
				expect (view.displayBoard).toHaveBeenCalledWith ();
			});
			
			it ("hands the player ID, websocket, and Board view over to the Board controller", function () {
				expect (Jeopardy.Board.Controller.initialize).toHaveBeenCalledWith (1234, websocket, 
						Jeopardy.Board.View);
			});
		});
	});
	
	describe ("when signed in", function () {
		
		beforeEach (function () {
			subject.signIn ("Lumpy");
		});
		
		it ("sends a sign-in message on the websocket", function () {
			expect (websocket.send).toHaveBeenCalledWith ("signIn", {name: "Lumpy"});
		});
		
		it ("does not yet order the READY panel", function () {
			expect (view.displayControls).not.toHaveBeenCalledWith ("READY");
		});
		
		it ("does not yet remember the player's ID", function () {
			expect (subject.playerId).toBe (null);
		});
	});
	
	describe ("when signaled ready", function () {
		
		beforeEach (function () {
			subject.signalReady ();
		});
		
		it ("sends a ready message on the websocket", function () {
			expect (websocket.send).toHaveBeenCalledWith ("ready", {});
		});
		
		it ("orders the START panel", function () {
			expect (view.displayControls).toHaveBeenCalledWith ("START");
		});
	});
	
	describe ("when directed to start", function () {
		
		beforeEach (function () {
			subject.startGame ();
		});
		
		it ("sends a start message on the websocket", function () {
			expect (websocket.send).toHaveBeenCalledWith ("start", {});
		});
	});
	
	describe ("when signed out", function () {
		
		beforeEach (function () {
			subject.signOut ();
		});
		
		it ("sends a sign-out message on the websocket", function () {
			expect (websocket.send).toHaveBeenCalledWith ("signOut", {});
		});
		
		it ("directs player list to be cleared", function () {
			expect (view.updatePlayers).toHaveBeenCalledWith ({players: []});
		});
		
		it ("orders the SIGNIN panel", function () {
			expect (view.displayControls).toHaveBeenCalledWith ("SIGNIN");
		});
	});
});

describe ("A Vestibule View, initialized", function () {
	var panelIds = ["sign-in-panel", "ready-panel", "start-panel", "sign-out-panel"];
	var subject = null;
	var controller = null;
	
	beforeEach (function () {
		$('body').append (
			'<div id="deleteMe">' +
			'	<div id="vestibule-page-content" style="display: none;">' +
			'		<div id="player-list"></div>' +
		 	'		<div id="sign-in-panel" style="display: none;">' +
			'		    <input type="text" id="player-name"/>' +
			'		    <button id="sign-in-button">Sign In</button>' +
		 	'		</div>' +
		 	'		<div id="ready-panel" style="display: none;">' +
		 	'			<button id="ready-button">Ready</button>' +
		 	'		</div>' +
		 	'		<div id="start-panel" style="display: none;">' +
		 	'			<button id="start-button">START</button>' +
		 	'		</div>' +
		 	'		<div id="sign-out-panel" style="display: none;">' +
		 	'			<button id="sign-out-button">Sign Out</button>' +
		 	'		</div>' +
			'	</div>' +
			'	<div id="board-page-content" style="display: none;">' +
			'       SOMETHING TO SEE' +
			'	</div>' +
			'</div>'
		);
		subject = Jeopardy.Vestibule.View;
		controller = {
			signIn: jasmine.createSpy (),
			signalReady: jasmine.createSpy (),
			startGame: jasmine.createSpy (),
			signOut: jasmine.createSpy ()
		};
		
		subject.initialize (controller);
	});
	
	afterEach (function () {
		$('#deleteMe').remove ();
	});
	
	it ("attaches the controller", function () {
		expect (subject.controller).toBe (controller);
	});
	
	it ("makes the vestibule content visible", function () {
		expect ($('#vestibule-page-content').is (':visible')).toBe (true);
	});
	
	_.each (panelIds, function (panelId) {
		it ("leaves the " + panelId + " invisible", function () {
			expect ($('#' + panelId).is (':visible')).toBe (false);
		});
	});
	
	var checkControlVisibility = function (name, controlsToShow) {
		
		describe ("when directed to display the " + name + " controls", function () {
			
			beforeEach (function () {
				subject.displayControls (name);
			});

			_.each (panelIds, function (panelId) {
				if (_.contains (controlsToShow, panelId)) {
					it ("shows the " + panelId, function () {
						expect ($('#' + panelId).is (':visible')).toBe (true);
					});
				}
				else {
					it ("does not show the " + panelId, function () {
						expect ($('#' + panelId).is (':visible')).toBe (false);
					});
				}
			});
		});
	};
	
	checkControlVisibility ("SIGNIN", ["sign-in-panel"]);
	checkControlVisibility ("READY", ["ready-panel", "sign-out-panel"]);
	checkControlVisibility ("START", ["start-panel", "sign-out-panel"]);
	
	describe ("when given Jeffy, Chubs, and Pook to display", function () {
		
		beforeEach (function () {
			subject.updatePlayers ({players: [
			    {name: "Jeffy", id: 12345, status: "signedIn"}, 
			    {name: "Chubs", id: 23456, status: "ready"},
			    {name: "Pook", id: 34567, status: "unrecognized"}
			]});
		});
		
		var checkNameDisplay = function (name, id, status) {
			it ("displays name for " + name, function () {
				var nameElement = $('#player-info-' + id + ' #player-name-' + id);
				expect (nameElement.html ()).toBe (name);
			});

			it ("displays " + status + " status for " + name, function () {
				var statusElement = $('#player-info-' + id + ' #player-status-' + id);
				expect (statusElement.html ()).toBe (status);
			});
		};
		
		checkNameDisplay ("Jeffy", 12345, "signedIn");
		checkNameDisplay ("Chubs", 23456, "ready");
		checkNameDisplay ("Pook", 34567, "signedIn");
	});
	
	describe ("when directed to display the SIGNIN controls", function () {
		
		beforeEach (function () {
			subject.displayControls ("SIGNIN");
		});
		
		describe ("and the player name is filled out and submitted", function () {
			
			beforeEach (function () {
				$('#player-name').val ("Chip");
				$('#sign-in-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signIn).toHaveBeenCalledWith ("Chip");
			});
		});
	});
	
	describe ("when directed to display the READY controls", function () {
		
		beforeEach (function () {
			subject.displayControls ("READY");
		});

		describe ("and the Ready button is clicked", function () {
			
			beforeEach (function () {
				$('#ready-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signalReady).toHaveBeenCalled ();
			});
		});
		
		describe ("and the Sign Out button is clicked", function () {
			
			beforeEach (function () {
				$('#sign-out-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signOut).toHaveBeenCalled ();
			});
		})
	});
	
	describe ("when directed to display the START controls", function () {
		
		beforeEach (function () {
			subject.displayControls ("START");
		});

		describe ("and the START button is clicked", function () {
			
			beforeEach (function () {
				$('#start-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.startGame).toHaveBeenCalled ();
			});
		});
		
		describe ("and the Sign Out button is clicked", function () {
			
			beforeEach (function () {
				$('#sign-out-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signOut).toHaveBeenCalled ();
			});
		})
	});
	
	describe ("when directed to display the board", function () {
		beforeEach (function ( ){
			subject.displayBoard ();
		});
		
		it ("hides vestibule page content", function () {
			expect ($("#vestibule-page-content").is (":visible")).toBe (false);
		});
		
		it ("shows board page content", function () {
			expect ($("#board-page-content").is (":visible")).toBe (true);
		});
	});
});
