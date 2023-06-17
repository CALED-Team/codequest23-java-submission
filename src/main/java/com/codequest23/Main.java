package com.codequest23;

public class Main {
  public static void main(String[] args) {
    // Create a new instance of the Game class
    Game game = new Game();

    // Continuously read and process turn data until the end signal is received
    while (game.readNextTurnData()) {
      // Respond to the current turn by executing the logic in the respondToTurn() method
      game.respondToTurn();
    }
  }
}
