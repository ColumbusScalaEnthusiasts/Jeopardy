import Utils from '../../../public/javascripts/util/utils.js';

describe("Jeopardy.Utils.websocket, when its environment is mocked out", () => {
  let websocket = null;
  let utils = null;
  let mockWebSocket = null;

  beforeEach(() => {
    mockWebSocket = { send: jasmine.createSpy() };
    utils = new Utils(() => mockWebSocket);
  });

  describe("and it is called without an events parameter", () => {

    beforeEach(() => {
      websocket = utils.websocket("Throckmorton", {
        open: "open handler",
        close: "close handler",
      });
    });

    it("applies the supplied open handler to the underlying WebSocket", () => {
      expect(mockWebSocket.onopen).toBe("open handler");
    });

    it("applies the supplied close handler to the underlying WebSocket", () => {
      expect(mockWebSocket.onclose).toBe("close handler");
    });

    it("leaves the underlying WebSocket's onmessage handler undefined", () => {
      expect(mockWebSocket.onmessage).toBeUndefined();
    });

    describe("and used to send a message", () => {

      beforeEach(() => {
        websocket.send("opcode", { name: "value" });
      });

      it("delegates properly to the underlying WebSocket", () => {
        expect(mockWebSocket.send).toHaveBeenCalledWith(JSON.stringify({ type: "opcode", data: { name: "value" } }))
      });
    });
  });

  describe("and it is called with just an events parameter", () => {
    let output = null;

    beforeEach(() => {
      websocket = utils.websocket("Throckmorton", {
        events: {
          birthday(data) { output = data.message },
          christmas(data) { output = data.message }
        }
      });
    });

    it("leaves the underlying WebSocket's open handler undefined", () => {
      expect(mockWebSocket.onopen).toBeUndefined();
    });

    it("leaves the underlying WebSocket's close handler undefined", () => {
      expect(mockWebSocket.onclose).toBeUndefined();
    });

    describe("and the underlying WebSocket receives one kind of expected message", () => {

      beforeEach(() => {
        mockWebSocket.onmessage({ data: JSON.stringify({ type: "birthday", data: { message: "Happy birthday to me!" } }) });
      });

      it("its event handler operates as expected", () => {
        expect(output).toBe("Happy birthday to me!");
      });
    });

    describe("and the underlying WebSocket receives another kind of expected message", () => {

      beforeEach(() => {
        mockWebSocket.onmessage({ data: JSON.stringify({ type: "christmas", data: { message: "Merry Christmas!" } }) });
      });

      it("its event handler operates as expected", () => {
        expect(output).toBe("Merry Christmas!");
      });
    });

    describe("and the underlying WebSocket receives a non-JSON message", () => {

      beforeEach(() => {
        spyOn(window.console, "log");
        mockWebSocket.onmessage({ data: "Nobody expects the Spanish Inquisition!" });
      });

      it("ignores the message and logs about it", () => {
        expect(window.console.log).toHaveBeenCalledWith('Discarding non-JSON WebSocket message: "Nobody expects the Spanish Inquisition!"');
      });
    });

    describe("and the event handlers are replaced", () => {

      let replacementEvents = null;

      beforeEach(() => {
        replacementEvents = {
          chanukah(data) { output = data.message }
        };

        websocket.replaceEvents(replacementEvents);
      });

      describe("and an event that would have been handled previously is received", () => {

        beforeEach(() => {
          spyOn(window.console, "log");
          mockWebSocket.onmessage({ data: JSON.stringify({ type: "christmas", data: { message: "Merry Christmas!" } }) });
        });

        it("logs about the message", () => {
          expect(window.console.log).toHaveBeenCalledWith(`Discarding unexpected WebSocket message: "${JSON.stringify({ type: "christmas", data: { message: "Merry Christmas!" } })}"`);
        });
      });

      describe("and an event handled by the new event handlers is received", () => {

        beforeEach(() => {
          mockWebSocket.onmessage({ data: JSON.stringify({ type: "chanukah", data: { message: "Happy Chanukah!" } }) });
        });

        it("calls the new handler", () => {
          expect(output).toBe("Happy Chanukah!");
        });
      });
    });
  });
});
