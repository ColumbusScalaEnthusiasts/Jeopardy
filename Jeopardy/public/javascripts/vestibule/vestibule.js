var Jeopardy = Jeopardy ? Jeopardy : {};

Jeopardy.Vestibule = {}

Jeopardy.Vestibule.Controller = (function () {
	var self = {}
	
	var websocket = null;
	var view = null;
	
	var handlePlayerList = function (data) {
		view.updatePlayers (data);
	};
	
	var handleSignedIn = function (data) {
		self.playerId = data.id;
		view.displayControls ("READY");
	};
	
	var handleGameStarting = function (data) {
		view.displayBoard ();
		Jeopardy.Board.Controller.initialize (self.playerId, websocket, Jeopardy.Board.View);
	};
	
	var convertToWebSocketUrl = function (location) {
		var match = /http(:\/\/[^\/]*)/.exec (location);
		if (!match) {throw "Can't open WebSocket from URL: " + location;}
		var baseUrl = match[1];
		return "ws" + baseUrl + "/vestibule/wsinit";
	};
	
	var makeWebSocket = function (location) {
		return Jeopardy.Utils.websocket (
			convertToWebSocketUrl (location + ""), {
			open: function () {view.displayControls ("SIGNIN");},
			close: function () {view.closed ();},
			events: {
				playerList: function (data) {handlePlayerList (data);},
				signedIn: function (data) {handleSignedIn (data);},
				gameStarting: function (data) {handleGameStarting (data);}
			}
		});
	}
	
	self.playerId = null;
	
	self.initialize = function (location, viewParam) {
		self.playerId = null;
		websocket = makeWebSocket (location);
		view = viewParam;
		view.initialize (self);
		view.displayControls ("SIGNIN");
	};
	
	self.signIn = function (name) {
		websocket.send ("signIn", {name: name});
	};
	
	self.signalReady = function () {
		websocket.send ("ready", {});
		view.displayControls ("START");
	};
	
	self.startGame = function () {
		websocket.send ("start", {});
	};
	
	self.signOut = function () {
		websocket.send ("signOut", {});
		view.updatePlayers ({players: []});
		view.displayControls ("SIGNIN");
	};
	
	return self;
})();

Jeopardy.Vestibule.View = (function () {
	
	var self = {};
	
	var panelIds = ["sign-in-panel", "ready-panel", "start-panel", "sign-out-panel"];
	
	var controlSets = {
		"SIGNIN": ["sign-in-panel"], 
		"READY": ["ready-panel", "sign-out-panel"], 
		"START": ["start-panel", "sign-out-panel"]
	};
	
	var wireInControls = function () {
		$('#sign-in-button').click (function () {self.controller.signIn ($('#player-name').val ());});
		$('#ready-button').click (self.controller.signalReady);
		$('#start-button').click (self.controller.startGame);
		$('#sign-out-button').click (self.controller.signOut);
	}
	
	var normalizeStatus = function (inputStatus) {
		var outputStatus;
		["signedIn", "ready"].forEach (function (status) {
			if (status === inputStatus) {outputStatus = status;}
		});
		return outputStatus ? outputStatus : "signedIn";
	}
	
	self.controller = null;
	
	self.initialize = function (controller) {
		self.controller = controller;
		wireInControls ();
		$('#vestibule-page-content').show ();
	};
	
	self.closed = function () {};
	
	self.displayControls = function (controlsName) {
		var controlSet = controlSets[controlsName];
		_.each (panelIds, function (panelId) {
			var object = $('#' + panelId);
			if (_.contains (controlSet, panelId)) {
				object.show ();
			}
			else {
				object.hide ();
			}
		});
	};
	
	self.updatePlayers = function (data) {
		$('#player-list tr').remove ();
		_.each (data.players, function (player) {
			player.status = normalizeStatus (player.status);
			$('#player-list').append (
				'<tr class="player-info" id="player-info-' + player.id + '">' +
				    '<td class="player-name" id="player-name-' + player.id + '">' + player.name + '</td>' +
				    '<td class="player-status" id="player-status-' + player.id + '">' + player.status + '</td>' +
				'</tr>'
			);
		});
	};
	
	self.displayBoard = function () {
		$('#vestibule-page-content').hide ();
		$('#board-page-content').show ();
	};
	
	return self;
}) ();