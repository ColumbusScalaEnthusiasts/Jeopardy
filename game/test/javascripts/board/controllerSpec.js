/* eslint-disable no-undef */
import { Controller } from "../../../public/javascripts/board/controller.js";

describe("A Board controller, initialized with mocks,", () => {
  let websocket = null;
  let view = null;
  let subject = null;
  let testBoardHandlers = null;

  beforeEach(() => {
    websocket = {
      replaceEvents: jasmine.createSpy().and.callFake((events) => { testBoardHandlers = events; }),
      send: jasmine.createSpy()
    };
    view = {
      updateBoard: jasmine.createSpy(),
      displayUserStatus: jasmine.createSpy(),
      updateOpponents: jasmine.createSpy(),
      showQuestionAndBuzzer: jasmine.createSpy(),
    };
    subject = new Controller(1234, websocket, view);
  });

  it("replaces the websocket's event handlers", () => {
    expect(websocket.replaceEvents).toHaveBeenCalled();
  });

  describe("and sent a boardStatus message showing this player in control", () => {
    let boardStatusMessage = null;

    beforeEach(() => {
      boardStatusMessage = {
        players: [
          { id: 1234, name: "Mike", score: 150, status: "InControlStatus" },
          { id: 2345, name: "Nancy", score: 510, status: "WaitingForChoiceStatus" },
          { id: 3456, name: "Oscar", score: 250, status: "WaitingForChoiceStatus" }
        ],
        columns: [
          {
            category: { id: 101, name: "Things Not to Drink" },
            questions: [
              { id: 201, value: 200 },
              { id: 202, value: 400 }
            ]
          },
          {
            category: { id: 102, name: "Things Not to Eat" },
            questions: [
              { id: 203, value: 200 },
              { id: 204, value: 400 }
            ]
          }
        ]
      };

      testBoardHandlers.boardStatus(boardStatusMessage);
    });

    it("instructs the view to update the board contents", () => {
      expect(view.updateBoard).toHaveBeenCalledWith(boardStatusMessage.columns);
    });

    it("instructs the view to show Mike as the user in control of the board", () => {
      expect(view.displayUserStatus).toHaveBeenCalledWith({ id: 1234, name: 'Mike', score: 150, status: 'InControlStatus' });
    });

    it("instructs the view to show the opponents", () => {
      expect(view.updateOpponents).toHaveBeenCalledWith([boardStatusMessage.players[1],
      boardStatusMessage.players[2]]);
    });

    describe("when Mike chooses a question", () => {

      beforeEach(() => {
        subject.chooseQuestion(203);
      });

      it("relays the chosen question to the back end", () => {
        expect(websocket.send).toHaveBeenCalledWith("chooseQuestion", { id: 203 });
      });

      it("removes Mike from control", () => {
        expect(view.displayUserStatus).toHaveBeenCalledWith("WaitingForChoiceStatus");
      });
    });

    describe("and Mike hits the buzzer", () => {

      beforeEach(() => {
        subject.buzz();
      });

      it("ignores the buzz because Mike is not in WaitingForBuzzStatus", () => {
        expect(websocket.send).not.toHaveBeenCalled();
      });
    });

    describe("and a question is asked", () => {

      beforeEach(() => {
        testBoardHandlers.askQuestion({ id: 202, text: "Black, gritty, and viscous" });
      });

      it("instructs view to show question and buzzer", () => {
        expect(view.showQuestionAndBuzzer).toHaveBeenCalledWith(202, "Black, gritty, and viscous");
      });

      describe("and Mike hits the buzzer", () => {

        beforeEach(() => {
          subject.buzz();
        });

        it("relays the buzz to the back end", () => {
          expect(websocket.send).toHaveBeenCalledWith("buzz", {})
        });
      });
    });
  });

  describe("and sent a boardStatus message showing this player not in control", () => {
    let boardStatusMessage = null;

    beforeEach(() => {
      boardStatusMessage = {
        players: [
          { id: 1234, name: "Mike", score: 150, status: "WaitingForChoiceStatus" },
          { id: 2345, name: "Nancy", score: 510, status: "InControlStatus" }
        ],
        columns: []
      };

      testBoardHandlers.boardStatus(boardStatusMessage);
    });

    describe("when Mike tries to choose a question", () => {

      beforeEach(() => {
        subject.chooseQuestion(4321);
      });

      it("he is ignored because he's not in control", () => {
        expect(websocket.send).not.toHaveBeenCalled();
      });
    });
  });
});
