/* eslint-disable no-undef */

const ENTER_KEY = 13;

const controlSets = {
  "SIGNIN": ["signInPanel"],
  "READY": ["readyPanel", "signOutPanel"],
  "START": ["startPanel", "signOutPanel"]
};

const panelIds = {
  signInPanel: "sign-in-panel",
  readyPanel: "ready-panel",
  startPanel: "start-panel",
  signOutPanel: "sign-out-panel"
};

export class View {
  static create(controller) {
    return new View(controller);
  }

  constructor(controller) {
    this.controller = controller;
    this.wireInControls();
    $('#vestibule-page-content').show();
  }

  signIn() {
    this.controller.signIn($('#player-name').val());
  };

  wireInControls() {
    const sin = () => this.signIn();
    $('#sign-in-button').on("click", () => sin());
    $('#player-name').on("keypress", (event) => {
      if (event.which === ENTER_KEY) {
        sin();
      }
    });
    $('#ready-button').on("click", () => this.controller.signalReady());
    $('#start-button').on("click", () => this.controller.startGame());
    $('#sign-out-button').on("click", () => this.controller.signOut());
  }

  static normalizeStatus(inputStatus) {
    let outputStatus = "signedIn";
    ["signedIn", "ready"].forEach((status) => {
      if (status === inputStatus) {
        outputStatus = status;
      }
    });
    return outputStatus;
  };

  closed() { }

  static displayControls(controlsName) {
    const controlSet = controlSets[controlsName];
    Object.entries(panelIds).forEach(([key, panelId]) => {
      const object = $(`#${panelId}`);
      if (controlSet.includes(key)) {
        object.show();
      } else {
        object.hide();
      }
    });
  };

  static updatePlayers(data) {
    $('#player-list tr').remove();
    data.players.forEach((player) => {
      player.status = View.normalizeStatus(player.status);
      $('#player-list')
        .append(`<tr class="player-info" id="player-info-${player.id}">` +
          `<td class="player-name" id="player-name-${player.id}">${player.name}</td>` +
          `<td class="player-status" id="player-status-${player.id}">${player.status}</td>` +
          `</tr>`);
    });
  };

  static displayBoard() {
    $('#vestibule-page-content').hide();
    $('#board-page-content').show();
  };
}
