package com.codequest23;

public enum ObjectTypes {
  TANK(1),
  BULLET(2),
  WALL(3),
  DESTRUCTIBLE_WALL(4),
  BOUNDARY(5),
  CLOSING_BOUNDARY(6),
  POWERUP(7);

  private final int value;

  ObjectTypes(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
