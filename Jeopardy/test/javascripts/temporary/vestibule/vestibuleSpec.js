
describe ("A Vestibule Controller, initialized with mocks", function () {
	var subject = null;
	var websocket = null;
	var view = null;
	
	beforeEach (function () {
		subject = Jeopardy.Vestibule.Controller;
		websocket = {send: jasmine.createSpy ()};
		spyOn ($, "websocket").andReturn (websocket);
		view = {
			initialize: jasmine.createSpy (),
			displayControls: jasmine.createSpy (),
			updatePlayers: jasmine.createSpy (),
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
	
	it ("calls $.websocket", function () {
		expect ($.websocket).toHaveBeenCalled ();
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
		
		beforeEach (function () {
			var call = $.websocket.calls[0];
			url = call.args[0];
			open = call.args[1].open;
			close = call.args[1].close;
			playerList = call.args[1].events.playerList;
			signedIn = call.args[1].events.signedIn;
		});
		
		it ("with the expected URL", function () {
			expect (url).toBe ("ws://base.url/wsinit");
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
});

describe ("A Vestibule View, initialized", function () {
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
			'	</div>' +
			'</div>'
		);
		subject = Jeopardy.Vestibule.View;
		controller = {
			signIn: jasmine.createSpy (),
			signalReady: jasmine.createSpy (),
			startGame: jasmine.createSpy ()
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
	
	it ("leaves the sign-in pane and button panels all invisible", function () {
		expect ($('#sign-in-panel').is (':visible')).toBe (false);
		expect ($('#ready-panel').is (':visible')).toBe (false);
		expect ($('#start-panel').is (':visible')).toBe (false);
	});
	
	var checkControlVisibility = function (name, friendlyName) {
		var friendlyNames = ["sign-in", "ready", "start"];
		
		describe ("when directed to display the " + friendlyName + " controls", function () {
			
			beforeEach (function () {
				subject.displayControls (name);
			});
			
			it ("shows the " + friendlyName + " controls but none of the others", function () {
				friendlyNames.forEach (function (candidate) {
					expect ($('#' + candidate + '-panel').is (':visible')).toBe (candidate === friendlyName);
				});
			});
		});
	};
	
	checkControlVisibility ("SIGNIN", "sign-in");
	checkControlVisibility ("READY", "ready");
	checkControlVisibility ("START", "start");
	
	describe ("when given Jeffy and Chubs to display", function () {
		
		beforeEach (function () {
			subject.updatePlayers ({players: [
			    {name: "Jeffy", id: 12345}, 
			    {name: "Chubs", id: 23456}
			]});
		});
		
		var checkNameDisplay = function (name, id) {
			it ("displays " + name, function () {
				var nameElement = $('#player-info-' + id + ' #player-name-' + id);
				expect (nameElement.html ()).toBe (name);
			});
		};
		
		checkNameDisplay ("Jeffy", 12345);
		checkNameDisplay ("Chubs", 23456);
	});
	
	describe ("when all controls are made visible", function () {
		
		beforeEach (function () {
			$('#sign-in-panel').show ();
			$('#ready-panel').show ();
			$('#start-panel').show ();
		});
		
		describe ("and the sign-in panel is filled out and submitted", function () {
			
			beforeEach (function () {
				$('#player-name').val ("Chip");
				$('#sign-in-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signIn).toHaveBeenCalledWith ("Chip");
			});
		});
		
		describe ("and the Ready button is clicked", function () {
			
			beforeEach (function () {
				$('#ready-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.signalReady).toHaveBeenCalled ();
			});
		});
		
		describe ("and the START button is clicked", function () {
			
			beforeEach (function () {
				$('#start-button').click ();
			});
			
			it ("the controller is informed", function () {
				expect (controller.startGame).toHaveBeenCalled ();
			});
		});
	});
});
