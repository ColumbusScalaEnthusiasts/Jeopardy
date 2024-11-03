/* eslint-disable no-undef */
import "../../../public/javascripts/jquery-1.8.2.min.js";
import { View } from "../../../public/javascripts/vestibule/view.js";

describe("An initialized Vestibule View", () => {
  const panelIds = ["sign-in-panel", "ready-panel", "start-panel", "sign-out-panel"];
  let controller = null;
  let subject = null;

  beforeEach(() => {
    $('body').append(
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
      '		<div id="sign-out-panel" style="display: none;">' +
      '			<button id="sign-out-button">Sign Out</button>' +
      '		</div>' +
      '	</div>' +
      '	<div id="board-page-content" style="display: none;">' +
      '       SOMETHING TO SEE' +
      '	</div>' +
      '</div>'
    );
    controller = {
      signIn: jasmine.createSpy(),
      signOut: jasmine.createSpy(),
      signalReady: jasmine.createSpy(),
      startGame: jasmine.createSpy(),
    };
    subject = new View(controller);

  });

  afterEach(() => {
    $('#deleteMe').remove();
  });

  it("attaches the controller", () => {
    expect(subject.controller).toBe(controller);
  });

  it("makes the vestibule content visible", () => {
    expect($('#vestibule-page-content').is(':visible')).toBe(true);
  });

  panelIds.forEach((panelId) => {
    it(`leaves the ${panelId} invisible`, () => {
      expect($(`#${panelId}`).is(':visible')).toBe(false);
    });
  });

  const checkControlVisibility = (name, controlsToShow) => {
    describe(`when directed to display the ${name} controls`, () => {
      beforeEach(() => {
        View.displayControls(name);
      });

      panelIds.forEach((panelId) => {
        if (controlsToShow.includes(panelId)) {
          it(`shows the ${panelId}`, () => {
            expect($(`#${panelId}`).is(':visible')).toBe(true);
          });
        } else {
          it(`does not show the ${panelId}`, () => {
            expect($(`#${panelId}`).is(':visible')).toBe(false);
          });
        }
      });
    });
  };

  checkControlVisibility("SIGNIN", ["sign-in-panel"]);
  checkControlVisibility("READY", ["ready-panel", "sign-out-panel"]);
  checkControlVisibility("START", ["start-panel", "sign-out-panel"]);

  describe("when given Jeffy, Chubs, and Pook to display", () => {
    beforeEach(() => {
      View.updatePlayers({
        players: [
          { name: "Jeffy", id: 12345, status: "signedIn" },
          { name: "Chubs", id: 23456, status: "ready" },
          { name: "Pook", id: 34567, status: "unrecognized" }
        ]
      });
    });

    const checkNameDisplay = (name, id, status) => {
      it(`displays name for ${name}`, () => {
        const nameElement = $(`#player-info-${id} #player-name-${id}`);
        expect(nameElement.html()).toBe(name);
      });

      it(`displays ${status} status for ${name}`, () => {
        const statusElement = $(`#player-info-${id} #player-status-${id}`);
        expect(statusElement.html()).toBe(status);
      });
    };

    checkNameDisplay("Jeffy", 12345, "signedIn");
    checkNameDisplay("Chubs", 23456, "ready");
    checkNameDisplay("Pook", 34567, "signedIn");
  });

  describe("when directed to display the SIGNIN controls", () => {
    beforeEach(() => {
      View.displayControls("SIGNIN");
    });

    describe("and the player name is filled out", () => {
      beforeEach(() => {
        $('#player-name').val("Chip");
      });

      const theControllerIsInformed = () => {
        expect(controller.signIn).toHaveBeenCalledWith("Chip");
      }

      describe("and the Sign In button is clicked", () => {
        beforeEach(() => {
          $('#sign-in-button').trigger("click");
        });

        it("the controller is informed", theControllerIsInformed);
      });

      describe("and the Enter key is typed", () => {
        beforeEach(() => {
          $('#player-name').trigger({ type: "keypress", which: 13 });
        });

        it("the controller is informed", theControllerIsInformed);
      });

      describe("and a key other than Enter is typed", () => {

        beforeEach(() => {
          const event = $.Event("keypress");
          event.which = 65;
          $('#player-name').trigger(event);
        });

        it("the controller is not informed", () => {
          expect(controller.signIn).not.toHaveBeenCalled();
        });
      });
    });
  });

  describe("when directed to display the READY controls", () => {
    beforeEach(() => {
      View.displayControls("READY");
    });

    describe("and the Ready button is clicked", () => {

      beforeEach(() => {
        $('#ready-button').trigger("click");
      });

      it("the controller is informed", () => {
        expect(controller.signalReady).toHaveBeenCalled();
      });
    });

    describe("and the Sign Out button is clicked", () => {

      beforeEach(() => {
        $('#sign-out-button').trigger("click");
      });

      it("the controller is informed", () => {
        expect(controller.signOut).toHaveBeenCalled();
      });
    })
  });

  describe("when directed to display the START controls", () => {
    beforeEach(() => {
      View.displayControls("START");
    });

    describe("and the START button is clicked", () => {
      beforeEach(() => {
        $('#start-button').trigger("click");
      });

      it("the controller is informed", () => {
        expect(controller.startGame).toHaveBeenCalled();
      });
    });

    describe("and the Sign Out button is clicked", () => {
      beforeEach(() => {
        $('#sign-out-button').trigger("click");
      });

      it("the controller is informed", () => {
        expect(controller.signOut).toHaveBeenCalled();
      });
    })
  });

  describe("when directed to display the board", () => {
    beforeEach(() => {
      View.displayBoard();
    });

    it("hides vestibule page content", () => {
      expect($("#vestibule-page-content").is(":visible")).toBe(false);
    });

    it("shows board page content", () => {
      expect($("#board-page-content").is(":visible")).toBe(true);
    });
  });
});

