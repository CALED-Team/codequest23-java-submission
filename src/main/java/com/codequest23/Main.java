package com.codequest23;

public class Main {
  public static void main(String[] args) {
    Game game = new Game();
    while (game.readNextTurnData()) {
      game.respondToTurn();
    }
  }
}
