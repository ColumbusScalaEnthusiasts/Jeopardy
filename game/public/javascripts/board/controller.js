export class Controller {
  static create(playerId, websocket, vestibuleView) {
    return new Controller(playerId, websocket, vestibuleView);
  }

  constructor(playerId, websocket, vestibuleView) {
    this.playerId = playerId;
    this.view = vestibuleView;
    this.websocket = websocket;
    this.boardStatus = null;

    this.websocket.replaceEvents({
      boardStatus: (bs) => this.handleBoardStatus(bs),
      askQuestion: (aq) => this.handleAskQuestion(aq),
    });
  }

  findUserPlayer() {
    return this.boardStatus.players.find((player) => player.id === this.playerId)
  }

  findOpponents() {
    return this.boardStatus.players.filter((player) => player.id !== this.playerId);
  }

  handleBoardStatus(boardStatus) {
    this.boardStatus = boardStatus;
    this.view.updateBoard(this.boardStatus.columns);
    //TODO: update all the players
    //View.updatePlayers()
    this.view.displayUserStatus(this.findUserPlayer()); //Remove these in favor of updatePlayers method
    this.view.updateOpponents(this.findOpponents());
  }

  handleAskQuestion = (askQuestion) => {
    this.boardStatus.players = this.boardStatus.players.map((player) => ({ ...player, status: "WaitingForBuzzStatus" }));
    this.view.showQuestionAndBuzzer(askQuestion.id, askQuestion.text);
  }

  chooseQuestion(questionId) {
    if (this.findUserPlayer().status !== "InControlStatus") { return; }
    this.view.displayUserStatus("WaitingForChoiceStatus");
    this.websocket.send("chooseQuestion", { id: questionId });
  };

  buzz() {
    if (this.findUserPlayer().status !== "WaitingForBuzzStatus") { return; }
    this.websocket.send("buzz", {});
  };
}
