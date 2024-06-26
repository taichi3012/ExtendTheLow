package onim.en.etl;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Prefs {

  private static Prefs instance;

  private Prefs() {
    instance = this;
  }

  public static Prefs get() {
    return instance == null ? instance = new Prefs() : instance;
  }

  public static void load() {
    Path path = ExtendTheLow.configPath.resolve("general.json");
    Gson gson = new Gson();
    String json;
    try {
      if (Files.exists(path)) {
        json = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
      } else {
        instance = new Prefs();
        return;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    instance = gson.fromJson(json, Prefs.class);
  }

  public static void save() {
    new Thread(() -> {
      Path path = ExtendTheLow.configPath.resolve("general.json");
      Gson gson = new Gson();
      try {
        Files.write(path, Arrays.asList(gson.toJson(get()).split("\n")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }).start();
  }

  public static void reset() {
    instance = null;

    new Thread(() -> {
      Path path = ExtendTheLow.configPath.resolve("general.json");
      try {
        if (Files.exists(path))
          Files.delete(path);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }).start();
  }

  public boolean betterFont = false;

  public boolean customTheLowStatus = true;

  public boolean invertTheLowStatus = false;

  public boolean debugMode = true;

  public boolean smartHealthBar = true;

  public boolean improveChestBackgroundRender = true;
}
