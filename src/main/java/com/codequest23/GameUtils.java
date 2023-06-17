package com.codequest23;

import com.google.gson.JsonArray;

public class GameUtils {
  // Parse the position data from a JsonArray and convert it to a 2D array of doubles
  public static double[][] parsePosition(JsonArray positionArray) {
    double[][] position = new double[positionArray.size()][2];
    for (int i = 0; i < positionArray.size(); i++) {
      JsonArray singlePosition = positionArray.get(i).getAsJsonArray();
      position[i][0] = singlePosition.get(0).getAsDouble(); // Extract the X-coordinate
      position[i][1] = singlePosition.get(1).getAsDouble(); // Extract the Y-coordinate
    }
    return position;
  }
}
