/* eslint-disable no-undef */
import * as BoardView from "../../../public/javascripts/board/view.js";
import { Controller as BoardController } from '../../../public/javascripts/board/controller.js';
import Utils from "../../../public/javascripts/util/utils.js";
import { Controller as VestibuleController } from '../../../public/javascripts/vestibule/controller.js';
import { View as VestibuleView } from '../../../public/javascripts/vestibule/view.js';

describe("A Vestibule Controller, initialized with mocks", () => {
  let subject = null;
  let view = null;
  let websocket = null;
  let utils = null;

  beforeEach(() => {
    websocket = { replaceEvents: jasmine.createSpy(), send: jasmine.createSpy() };
    utils = new Utils(() => websocket); // I'm doing something wrong here that both this and the next spy are needed
    view = {
      closed: jasmine.createSpy(),
      displayBoard: jasmine.createSpy(),
      displayControls: jasmine.createSpy(),
      updatePlayers: jasmine.createSpy(),
    };

    spyOn(utils, "websocket").and.returnValue(websocket);
    spyOn(BoardController, "create");
    spyOn(VestibuleView, "create").and.returnValue(view);
    spyOn(VestibuleView, "displayControls");
    spyOn(VestibuleView, "updatePlayers");
    spyOn(VestibuleView, "displayBoard");

    subject = new VestibuleController(utils, "http://base.url");
  });

  // Todo - once everythings working this should be proved out by views mocks getting in place
  // This has always been a shitty test
  it("initializes the view", () => {
    expect(VestibuleView.create).toHaveBeenCalledWith(subject);
  });


  it("orders the display of the sign-in panel", () => {
    expect(VestibuleView.displayControls).toHaveBeenCalledWith("SIGNIN");
  });

  it("calls Jeopardy.Utils.websocket", () => {
    expect(utils.websocket).toHaveBeenCalled();
  });

  it("is not signed in", () => {
    expect(subject.playerId).toBe(null);
  });

  describe("creates a WebSocket", () => {
    let close = null;
    let gameStarting = null;
    let open = null;
    let playerList = null;
    let signedIn = null;
    let url = null;

    beforeEach(() => {
      // Todo - cleanup with new functions
      const call = utils.websocket.calls.all()[0];
      url = call.args[0];
      open = call.args[1].open;
      close = call.args[1].close;
      playerList = call.args[1].events.playerList;
      signedIn = call.args[1].events.signedIn;
      gameStarting = call.args[1].events.gameStarting;
    });

    it("with the expected URL", () => {
      expect(url).toBe("ws://base.url/vestibule/wsinit");
    });

    describe("with an open callback that, when called", () => {
      beforeEach(() => {
        open();
      });

      it("instructs the view to display the sign-in control panel", () => {
        expect(VestibuleView.displayControls).toHaveBeenCalledWith("SIGNIN");
      });
    });

    describe("with a close callback that, when called", () => {
      beforeEach(() => {
        close();
      });

      it("tells the view that the connection has been dropped", () => {
        expect(view.closed).toHaveBeenCalled();
      });
    });

    describe("with a playerList callback that, when called", () => {
      let data = null;

      beforeEach(() => {
        data = {
          players: [
            { id: 12345, name: "Jeffy", },
            { id: 23456, name: "Chubs" },
          ]
        };
        playerList(data);
      });

      it("updates the view appropriately", () => {
        expect(VestibuleView.updatePlayers).toHaveBeenCalledWith(data);
      });
    });

    describe("with a signedIn callback that, when called", () => {
      beforeEach(() => {
        signedIn({ id: 12345 });
      });

      it("remembers the player's ID", () => {
        expect(subject.playerId).toBe(12345);
      });

      it("instructs the view to display the ready-button control panel", () => {
        expect(VestibuleView.displayControls).toHaveBeenCalledWith("READY");
      });
    });

    describe("with a gameStarting callback that, when called", () => {

      beforeEach(() => {
        subject.playerId = 1234;
        gameStarting();
      });

      it("instructs the view to display the board", () => {
        expect(VestibuleView.displayBoard).toHaveBeenCalledWith();
      });

      it("hands the player ID, websocket, and Board view over to the Board controller", () => {
        expect(BoardController.create).toHaveBeenCalledWith(1234, websocket, BoardView);
      });
    });
  });

  describe("when signed in", () => {

    beforeEach(() => {
      subject.signIn("Lumpy");
    });

    it("sends a sign-in message on the websocket", () => {
      expect(websocket.send).toHaveBeenCalledWith("signIn", { name: "Lumpy" });
    });

    it("does not yet order the READY panel", () => {
      expect(view.displayControls).not.toHaveBeenCalledWith("READY");
    });

    it("does not yet remember the player's ID", () => {
      expect(subject.playerId).toBe(null);
    });
  });

  describe("when signaled ready", () => {
    beforeEach(() => {
      subject.signalReady();
    });

    it("sends a ready message on the websocket", () => {
      expect(websocket.send).toHaveBeenCalledWith("ready", {});
    });

    it("orders the START panel", () => {
      expect(VestibuleView.displayControls).toHaveBeenCalledWith("START");
    });
  });

  describe("when directed to start", () => {
    beforeEach(() => {
      subject.startGame();
    });

    it("sends a start message on the websocket", () => {
      expect(websocket.send).toHaveBeenCalledWith("start", {});
    });
  });

  describe("when signed out", () => {
    beforeEach(() => {
      subject.signOut();
    });

    it("sends a sign-out message on the websocket", () => {
      expect(websocket.send).toHaveBeenCalledWith("signOut", {});
    });

    it("directs player list to be cleared", () => {
      expect(VestibuleView.updatePlayers).toHaveBeenCalledWith({ players: [] });
    });

    it("orders the SIGNIN panel", () => {
      expect(VestibuleView.displayControls).toHaveBeenCalledWith("SIGNIN");
    });
  });
});

