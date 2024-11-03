import * as BoardView from "../board/view.js";
import { Controller as BoardController } from "../board/controller.js";
import { View as VestibuleView } from "./view.js";

export class Controller {
  constructor(utils, location) {
    this.websocket = utils.websocket;
    this.playerId = null;
    this.websocket = this.makeWebSocket(location);
    this.view = VestibuleView.create(this);
    VestibuleView.displayControls("SIGNIN");
  }

  handlePlayerList(data) { VestibuleView.updatePlayers(data); }

  handleSignedIn(data) {
    this.playerId = data.id;
    VestibuleView.displayControls("READY");
  }

  handleGameStarting() {
    VestibuleView.displayBoard();
    return BoardController.create(this.playerId, this.websocket, BoardView);
  }

  static convertToWebSocketUrl(location) {
    const match = /http(:\/\/[^/]*)/u.exec(location);
    if (!match) {
      throw `Can't open WebSocket from URL: ${location}`;
    }
    const baseUrl = match[1];
    return `ws${baseUrl}/vestibule/wsinit`;
  }

  makeWebSocket(location) {
    const open = () => VestibuleView.displayControls("SIGNIN");
    const close = () => this.view.closed();
    return this.websocket(Controller.convertToWebSocketUrl(`${location}`), {
      close,
      events: {
        gameStarting: () => this.handleGameStarting(),
        playerList: data => this.handlePlayerList(data),
        signedIn: data => this.handleSignedIn(data),
      },
      open,
    });
  }

  signIn(name) {
    this.websocket.send("signIn", { name });
  }

  signalReady() {
    this.websocket.send("ready", {});
    VestibuleView.displayControls("START");
  }

  startGame() { this.websocket.send("start", {}); }

  signOut() {
    this.websocket.send("signOut", {});
    VestibuleView.updatePlayers({ players: [] });
    VestibuleView.displayControls("SIGNIN");
  }
}
