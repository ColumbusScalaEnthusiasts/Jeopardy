var Jeopardy = Jeopardy ? Jeopardy : {};

Jeopardy.Utils = function () {

	var self = {};
	
	// for internal use
	self.makeWebSocket = function (url) {
		return new WebSocket (url);
	};
	
	self.websocket = function (url, params) {
		var ws = {};
		
		var socket = self.makeWebSocket (url)
		
		socket.onopen = params.open;
		socket.onclose = params.close;
		if (params.events) {socket.onmessage = function (event) {
			var msg = null;
			try {
				msg = JSON.parse (event.data);
			}
			catch (error) {
				console.log ('Discarding WebSocket message: "' + event.data + '"');
				return;
			}
			var type = msg.type;
			var data = msg.data;
			var handler = params.events[type];
			if (!handler) {
				console.log ('Discarding WebSocket message: "' + event.data + '"');
				return;
			}
			handler (data);
		}};
		
		ws.send = function (type, data) {
			var msg = JSON.stringify ({type: type, data: data});
			socket.send (msg)
		};
		
		return ws;
	};

	return self;
} ();
