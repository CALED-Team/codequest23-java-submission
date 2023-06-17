package com.codequest23;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game {
  private String tankId;
  private Map<String, JsonObject> objects;
  private double width;
  private double height;
  private JsonElement currentTurnMessage;

  public Game() {
    JsonElement tankIdMessage = Comms.readMessage();
    this.tankId =
        tankIdMessage
            .getAsJsonObject()
            .getAsJsonObject("message")
            .get("your-tank-id")
            .getAsString();

    this.currentTurnMessage = null;
    this.objects = new HashMap<>();

    JsonElement nextInitMessage = Comms.readMessage();
    while (!nextInitMessage.isJsonPrimitive()
        || !nextInitMessage.getAsString().equals(Comms.END_INIT_SIGNAL)) {
      JsonObject objectInfo =
          nextInitMessage
              .getAsJsonObject()
              .getAsJsonObject("message")
              .getAsJsonObject("updated_objects");

      for (Map.Entry<String, JsonElement> entry : objectInfo.entrySet()) {
        String objectId = entry.getKey();
        JsonObject objectData = entry.getValue().getAsJsonObject();
        objects.put(objectId, objectData);
      }

      nextInitMessage = Comms.readMessage();
    }

    // Find the boundaries to determine the map size
    double biggestX = Double.MIN_VALUE;
    double biggestY = Double.MIN_VALUE;

    for (JsonObject gameObject : objects.values()) {
      int objectType = gameObject.get("type").getAsInt();
      if (objectType == ObjectTypes.BOUNDARY.getValue()) {
        double[][] position = GameUtils.parsePosition(gameObject.get("position").getAsJsonArray());
        for (double[] singlePosition : position) {
          biggestX = Math.max(biggestX, singlePosition[0]);
          biggestY = Math.max(biggestY, singlePosition[1]);
        }
      }
    }

    this.width = biggestX;
    this.height = biggestY;
  }

  public boolean readNextTurnData() {
    this.currentTurnMessage = Comms.readMessage();

    if (this.currentTurnMessage.isJsonPrimitive()
        && this.currentTurnMessage.getAsString().equals(Comms.END_SIGNAL)) {
      return false;
    }

    // Delete objects that have been removed
    for (JsonElement deletedObjectId :
        this.currentTurnMessage
            .getAsJsonObject()
            .getAsJsonObject("message")
            .getAsJsonArray("deleted_objects")) {
      String id = deletedObjectId.getAsString();
      this.objects.remove(id);
    }

    // Update objects with new or updated data
    JsonObject updatedObjects =
        this.currentTurnMessage
            .getAsJsonObject()
            .getAsJsonObject("message")
            .getAsJsonObject("updated_objects");
    for (Map.Entry<String, JsonElement> entry : updatedObjects.entrySet()) {
      String objectId = entry.getKey();
      JsonObject objectData = entry.getValue().getAsJsonObject();
      this.objects.put(objectId, objectData);
    }

    return true;
  }

  public void respondToTurn() {
    // Write your code here... For demonstration, this bot just shoots randomly every turn.

    double shootAngle = new Random().nextDouble() * 360;
    JsonObject message = new JsonObject();
    message.addProperty("shoot", shootAngle);

    Comms.postMessage(message);
  }
}
