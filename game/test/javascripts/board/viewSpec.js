/* eslint-disable no-undef */

import "../../../public/javascripts/jquery-1.8.2.min.js";
import * as BoardView from "../../../public/javascripts/board/view.js";

describe("A Board View, initialized,", () => {
  let subject = null;

  beforeEach(() => {
    $('body').append(
      '<div id="board-page-content">\n' +
      '	<table>\n' +
      '		<thead>\n' +
      '			<tr id="categories">\n' +
      '			</tr>\n' +
      '		</thead>\n' +
      '		<tbody id="questions">\n' +
      '		</tbody>\n' +
      '	</table>\n' +
      '	<div id="center-display">\n' +
      '	    <div id="user-player-name"></div>\n' +
      '	    <div id="user-player-score"></div>\n' +
      '	    <div id="user-player-instructions"></div>\n' +
      '	</div>\n' +
      '	<table>\n' +
      '		<thead>\n' +
      '			<tr>\n' +
      '				<td>Player Name</td>\n' +
      '				<td>Score</td>\n' +
      '				<td>Status</td>\n' +
      '			</tr>\n' +
      '		</thead>\n' +
      '		<tbody id="board-player-list">\n' +
      '		</tbody>\n' +
      '	</table>\n' +
      '</div>\n'
    );
    subject = BoardView;
  });

  afterEach(() => {
    $('#board-page-content').remove();
  });

  describe("and given a board update", () => {

    beforeEach(() => {
      const update = [
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
      ];
      subject.updateBoard(update);
    });

    it("sets up categories correctly", () => {
      expect($("#categories #category-101").html()).toBe("Things Not to Drink");
      expect($("#categories #category-102").html()).toBe("Things Not to Eat");
    });

    it("sets up questions correctly", () => {
      expect($("#questions #question-201").html()).toBe("200");
      expect($("#questions #question-202").html()).toBe("400");
      expect($("#questions #question-203").html()).toBe("200");
      expect($("#questions #question-204").html()).toBe("400");
    });
  });

  describe("and directed to display player status for controlling player", () => {
    beforeEach(() => {
      const currentPlayer = { id: 123, name: "Tommy", score: 1400, status: "InControlStatus" };
      const opponents = [
        { id: 234, name: "Ursula", score: 1500, status: "WaitingForChoiceStatus" },
        { id: 345, name: "Valentina", score: 1600, status: "WaitingForChoiceStatus" }
      ];
      subject.displayPlayerStatus(currentPlayer, opponents);
    });

    it("displays the user's name", () => {
      expect($("#user-player-name").html()).toBe("Tommy");
    });

    it("displays the user's score", () => {
      expect($("#user-player-score").html()).toBe("1400");
    });

    it("displays hidden status for functional tests", () => {
      expect($("#user-player-instructions").attr("status")).toBe("InControlStatus");
    });

    it("displays the proper instructions for the status", () => {
      expect($("#user-player-instructions").html()).toBe("Make your selection.");
    });

    it("displays the opponents' names", () => {
      expect($("#opponent-name-234").html()).toBe("Ursula");
      expect($("#opponent-name-345").html()).toBe("Valentina");
    });

    it("displays the opponents' scores", () => {
      expect($("#opponent-score-234").html()).toBe("1500");
      expect($("#opponent-score-345").html()).toBe("1600");
    });

    it("displays the opponents' statuses", () => {
      expect($("#opponent-status-234").html()).toBe("WaitingForChoiceStatus");
      expect($("#opponent-status-345").html()).toBe("WaitingForChoiceStatus");
    });

  });

  describe("and directed to display player status for non-controlling player", () => {
    beforeEach(() => {
      const currentPlayer = { id: 123, name: "Tommy", score: 1400, status: "WaitingForChoiceStatus" };
      const opponents = [
        { id: 234, name: "Ursula", score: 1500, status: "InControlStatus" },
        { id: 345, name: "Valentina", score: 1600, status: "WaitingForChoiceStatus" }
      ];
      subject.displayPlayerStatus(currentPlayer, opponents);
    });

    it("displays the user's name", () => {
      expect($("#user-player-name").html()).toBe("Tommy");
    });

    it("displays the user's score", () => {
      expect($("#user-player-score").html()).toBe("1400");
    });

    it("displays hidden status for functional tests", () => {
      expect($("#user-player-instructions").attr("status")).toBe("WaitingForChoiceStatus");
    });

    it("displays the proper instructions for the status", () => {
      expect($("#user-player-instructions").html()).toBe("Wait for Ursula to choose a question...");
    });

    it("displays the opponents' names", () => {
      expect($("#opponent-name-234").html()).toBe("Ursula");
      expect($("#opponent-name-345").html()).toBe("Valentina");
    });

    it("displays the opponents' scores", () => {
      expect($("#opponent-score-234").html()).toBe("1500");
      expect($("#opponent-score-345").html()).toBe("1600");
    });

    it("displays the opponents' statuses", () => {
      expect($("#opponent-status-234").html()).toBe("InControlStatus");
      expect($("#opponent-status-345").html()).toBe("WaitingForChoiceStatus");
    });
  });
});
