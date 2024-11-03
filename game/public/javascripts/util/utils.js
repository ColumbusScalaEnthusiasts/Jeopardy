/*eslint no-unused-vars: ["error", { "caughtErrors": "all", "caughtErrorsIgnorePattern": "^ignore" }]*/

let factory = null;

export default class Utils {
  constructor(websocketFactory) {
    if (websocketFactory === undefined) {
      factory = (url) => new WebSocket(url);
    } else {
      factory = websocketFactory;
    }
  }

  websocket(url, params) {
    const onMessage = (event) => {
      let msg = null;
      try {
        msg = JSON.parse(event.data);
      }
      catch (ignore) {
        console.log(`Discarding non-JSON WebSocket message: "${event.data}"`);
        return;
      }
      const { type } = msg;
      const { data } = msg;
      const handler = params.events[type];
      if (!handler) {
        console.log(`Discarding unexpected WebSocket message: "${event.data}"`);
        return;
      }
      handler(data);
    };

    const socket = factory(url);
    const ws = {};

    socket.onopen = params.open;
    socket.onclose = params.close;
    if (params.events) { socket.onmessage = onMessage; }

    ws.send = (type, data) => {
      const msg = JSON.stringify({ type, data });
      socket.send(msg);
    };

    ws.replaceEvents = (events) => {
      params.events = events;
    };

    return ws;
  };
}
