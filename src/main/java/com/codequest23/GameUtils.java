package com.codequest23;

import com.google.gson.JsonArray;

public class GameUtils {
  public static double[][] parsePosition(JsonArray positionArray) {
    double[][] position = new double[positionArray.size()][2];
    for (int i = 0; i < positionArray.size(); i++) {
      JsonArray singlePosition = positionArray.get(i).getAsJsonArray();
      position[i][0] = singlePosition.get(0).getAsDouble();
      position[i][1] = singlePosition.get(1).getAsDouble();
    }
    return position;
  }
}
