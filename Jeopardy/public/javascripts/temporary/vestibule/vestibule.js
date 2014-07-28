var Jeopardy = Jeopardy ? Jeopardy : {}

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
	
	var convertToWebSocketUrl = function (location) {
		return location.replace ('http', 'ws') + "/wsinit"
	};
	
	self.playerId = null;
	
	self.initialize = function (location, viewParam) {
		self.playerId = null;
		websocket = $.websocket (
			convertToWebSocketUrl (location + ""), {
			open: function () {view.displayControls ("SIGNIN");},
			close: function () {view.closed ();},
			events: {
				playerList: function (data) {handlePlayerList (data);},
				signedIn: function (data) {handleSignedIn (data);}
			}
		});
		view = viewParam;
		view.initialize (self);
	};
	
	self.signIn = function (name) {
		websocket.send ("signIn", {name: name});
	};
	
	return self;
})();

Jeopardy.Vestibule.View = (function () {
	
	var self = {};
	
	var panels = {"SIGNIN": "sign-in-panel", "READY": "ready-panel", "START": "start-panel"};
	
	self.controller = null;
	
	self.initialize = function (controller) {
		self.controller = controller;
		$('#vestibule-page-content').show ();
	};
	
	self.displayControls = function (panelName) {
		Object.keys (panels).forEach (function (key) {
			var object = $('#' + panels[key]);
			if (key === panelName) {
				object.show ();
			}
			else {
				object.hide ();
			}
		});
	};
	
	self.updatePlayers = function (data) {
		$('#player-list tr').remove ();
		data.players.forEach (function (player) {
			$('#player-list').append (
				'<tr class="player-info" id="player-info-' + player.id + '">' +
				    '<td class="player-name" id="player-name-' + player.id + '">' + player.name + '</td>' +
				'</tr>'
			);
		});
	};
	
	return self;
}) ();