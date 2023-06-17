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
    // Read the tank ID message
    JsonElement tankIdMessage = Comms.readMessage();
    this.tankId =
        tankIdMessage
            .getAsJsonObject()
            .getAsJsonObject("message")
            .get("your-tank-id")
            .getAsString();

    this.currentTurnMessage = null;
    this.objects = new HashMap<>();

    // Read the initialization messages until the end signal is received
    JsonElement nextInitMessage = Comms.readMessage();
    while (!nextInitMessage.isJsonPrimitive()
        || !nextInitMessage.getAsString().equals(Comms.END_INIT_SIGNAL)) {
      JsonObject objectInfo =
          nextInitMessage
              .getAsJsonObject()
              .getAsJsonObject("message")
              .getAsJsonObject("updated_objects");

      // Store the object information in the map
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
        // Parse the position array
        double[][] position = GameUtils.parsePosition(gameObject.get("position").getAsJsonArray());
        for (double[] singlePosition : position) {
          // Update the biggestX and biggestY values
          biggestX = Math.max(biggestX, singlePosition[0]);
          biggestY = Math.max(biggestY, singlePosition[1]);
        }
      }
    }

    // Set the width and height of the map
    this.width = biggestX;
    this.height = biggestY;
  }

  public boolean readNextTurnData() {
    // Read the next turn message
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

    // Generate a random shoot angle
    double shootAngle = new Random().nextDouble() * 360;

    // Create the message with the shoot angle
    JsonObject message = new JsonObject();
    message.addProperty("shoot", shootAngle);

    // Send the message
    Comms.postMessage(message);
  }
}
