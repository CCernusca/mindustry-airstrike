package airstrike;

import airstrike.content.AirstrikeBlocks;
import airstrike.content.AirstrikeItems;
import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.mod.*;
import mindustry.type.Item;
import mindustry.type.Planet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AirstrikeMod extends Mod {
    // Satellite data for planets (planet-id: (item-id: amount))
    public static HashMap<String, HashMap<String, Integer>> planetSatellites;
    // Satellite data for sectors without planets (sector-id: (item-id: amount))
    public static HashMap<String, HashMap<String, Integer>> sectorSatellites;
    // Path to satellite data storage
    private static final String dataFilePath = "mods/airstrike-data/satellite_data.json";

    public AirstrikeMod() {}

    @Override
    public void init() {
        super.init();
        // Register the application listener
        Core.app.addListener(new AirstrikeApplicationListener());
    }

    @Override
    public void loadContent() {
        super.loadContent();
        AirstrikeBlocks.load();
        AirstrikeItems.load();

        ensureDataDirectoryExists();
        loadSatelliteData();
        correctSatelliteData();
    }

    // Gets all currently active sectors via their save files
    // Returns a map of planet -> sector
    // Returns null if sector isn't in campaign
    public static HashMap<Planet, LinkedList<Integer>> getSaves() {
        File[] saveFiles = Vars.saveDirectory.file().listFiles(((dir, name) -> name.endsWith(".msav") && !name.contains("backup")));
        String[] saveNames = new String[saveFiles.length];
        for (int i = 0; i < saveFiles.length; i++) {
            saveNames[i] = saveFiles[i].getName().replace(".msav", "").replace("sector-", "");
        }
        HashMap<Planet, LinkedList<Integer>> planetSectors = new HashMap<>();
        for (String saveName : saveNames) {
            String[] split = saveName.split("-");
            if (split.length == 1) {
                if (!planetSectors.containsKey(null)) {
                    planetSectors.put(null, new LinkedList<>());
                }
                planetSectors.get(null).add(Integer.parseInt(split[0]));
            } else {
                String planetName = split[0];
                for (int i = 1; i < split.length - 1; i++) {
                    planetName += "-" + split[i];
                }
                Planet planet = AirstrikeMod.getPlanetByName(planetName);
                if (planet == null) {
                    Log.err("Unknown planet " + planetName + " in save " + saveName);
                } else {
                    if (!planetSectors.containsKey(planet)) {
                        planetSectors.put(planet, new LinkedList<>());
                    }
                    planetSectors.get(planet).add(Integer.parseInt(split[split.length - 1]));
                }
            }
        }
        return planetSectors;
    }

    // Method to get planet player is on
    // Returns null if player isn't on a planet (not in campaign)
    public static Planet getCurrentPlanet() {
        return Vars.state.getPlanet();
    }

    public static String getCurrentSectorId() {
        String sectorId = Vars.control.saves.getCurrent().file.name().replace(".msav", "");
        if (sectorId.contains("-")) {
            String reverse = new StringBuilder(sectorId).reverse().toString();
            sectorId = new StringBuilder(reverse.split("-")[0]).reverse().toString();
        }
        return sectorId;
    }

    public static void saveSatelliteData() {
        try {
            // Save the data to the file
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Saving satellite data to: @", file.absolutePath());

            // Create a StringBuilder to construct the JSON string
            StringBuilder jsonBuilder = new StringBuilder("{");

            jsonBuilder.append("\"planets\":{");
            // Iterate over each planet and its associated items
            for (Map.Entry<String, HashMap<String, Integer>> entry : planetSatellites.entrySet()) {
                String planetId = entry.getKey();
                HashMap<String, Integer> items = entry.getValue();

                // Append the planet ID and its items to the JSON string
                jsonBuilder.append("\"").append(planetId).append("\":{");
                for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
                    String itemId = itemEntry.getKey();
                    Integer count = itemEntry.getValue();
                    jsonBuilder.append("\"").append(itemId).append("\":").append(count).append(",");
                }
                // Remove the trailing comma and close the planet's JSON object
                if (!items.isEmpty()) {
                    jsonBuilder.setLength(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("},");
            }
            // Remove the trailing comma and close the main JSON object
            if (!planetSatellites.isEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("}");

            jsonBuilder.append(",\"sectors\":{");
            // Iterate over each sector and its associated items
            for (Map.Entry<String, HashMap<String, Integer>> entry : sectorSatellites.entrySet()) {
                String sectorId = entry.getKey();
                HashMap<String, Integer> items = entry.getValue();

                // Append the sector ID and its items to the JSON string
                jsonBuilder.append("\"").append(sectorId).append("\":{");
                for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
                    String itemId = itemEntry.getKey();
                    Integer count = itemEntry.getValue();
                    jsonBuilder.append("\"").append(itemId).append("\":").append(count).append(",");
                }
                // Remove the trailing comma and close the sector's JSON object
                if (!items.isEmpty()) {
                    jsonBuilder.setLength(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("},");
            }
            // Remove the trailing comma and close the main JSON object
            if (!sectorSatellites.isEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("}");

            jsonBuilder.append("}");

            // Write the constructed JSON string to the file
            file.writeString(jsonBuilder.toString());

            Log.info("Satellite data saved successfully.");
        } catch (Exception e) {
            Log.err("Failed to save satellite data.", e);
        }
    }

    public static void loadSatelliteData() {
        try {
            // Locate the file
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Looking for satellite data at: @", file.absolutePath());

            if (file.exists()) {
                // Create a JSON deserializer
                Json json = new Json();
                json.setTypeName(null); // Match what we saved

                // Read the JSON data from the file
                String data = file.readString();
                JsonValue jsonData = json.fromJson(null, data);

                // Deserialize the JSON into the HashMaps
                planetSatellites = new HashMap<>();
                sectorSatellites = new HashMap<>();
                for (JsonValue typeValue : jsonData) {
                    String type = typeValue.name;
                    if (type.equals("planets")) {
                        Log.info("Loading planet satellite data...");
                        for (JsonValue planetValue : typeValue) {
                            String planetId = planetValue.name;
                            HashMap<String, Integer> satellites = new HashMap<>();
                            for (JsonValue itemValue : planetValue) {
                                String itemId = itemValue.name;
                                int amount = itemValue.asInt();
                                satellites.put(itemId, amount);
                            }
                            planetSatellites.put(planetId, satellites);
                        }
                    } else if (type.equals("sectors")) {
                        Log.info("Loading sector satellite data...");
                        for (JsonValue sectorValue : typeValue) {
                            String sectorId = sectorValue.name;
                            HashMap<String, Integer> satellites = new HashMap<>();
                            for (JsonValue itemValue : sectorValue) {
                                String itemId = itemValue.name;
                                int amount = itemValue.asInt();
                                satellites.put(itemId, amount);
                            }
                            sectorSatellites.put(sectorId, satellites);
                        }
                    } else {
                        Log.err("Unknown type " + type + " in satellite data.");
                    }
                }

                Log.info("Satellite data loaded successfully.");
                correctSatelliteData();
            } else {
                Log.info("No satellite data found. Initialising empty.");
                correctSatelliteData();
            }
        } catch (Exception e) {
            Log.err("Failed to load satellite data, initialising empty.", e);
            correctSatelliteData();
        }
    }

    // Method for ensuring satellite data is up to date
    public static void correctSatelliteData() {
        // Saves to check if satellite data is up to date with active planets/sectors
        HashMap<Planet, LinkedList<Integer>> saves = getSaves();
        // Add missing planets/sectors
        for (Planet planet : saves.keySet()) {
            if (planet != null) {
                if (!planetSatellites.containsKey(String.valueOf(planet.id))) {
                    Log.info("Adding Planet " + planet.name + " to satellite data.");
                    planetSatellites.put(String.valueOf(planet.id), new HashMap<String, Integer>());
                }
            } else {
                for (int sectorId : saves.get(null)) {
                    if (!sectorSatellites.containsKey(String.valueOf(sectorId))) {
                        Log.info("Adding Non-Planet sector " + sectorId + " to satellite data.");
                        sectorSatellites.put(String.valueOf(sectorId), new HashMap<String, Integer>());
                    }
                }
            }
        }
        // Remove planets that are no longer active
        LinkedList<String> toRemove = new LinkedList<>();
        for (String planetId : planetSatellites.keySet()) {
            if (!saves.containsKey(getPlanetById(planetId))) {
                Log.info("Removing Planet " + planetId + " from satellite data.");
                toRemove.add(planetId);
            }
            ensureCorrectSatelliteDataItems(planetSatellites.get(planetId));
        }
        for (String planetId : toRemove) {
            planetSatellites.remove(planetId);
        }
        // Remove sectors that are no longer active
        toRemove.clear();
        if (saves.containsKey(null)) {
            for (String sectorId : sectorSatellites.keySet()) {
                if (!saves.get(null).contains(Integer.parseInt(sectorId))) {
                    Log.info("Removing Non-Planet sector " + sectorId + " from satellite data.");
                    toRemove.add(sectorId);
                }
                ensureCorrectSatelliteDataItems(sectorSatellites.get(sectorId));
            }
        } else {
            for (String sectorId : sectorSatellites.keySet()) {
                Log.info("Removing Non-Planet sector " + sectorId + " from satellite data.");
                toRemove.add(sectorId);
            }
        }
        for (String sectorId : toRemove) {
            sectorSatellites.remove(sectorId);
        }
    }

    // Method for ensuring satellite data items are up to date
    public static void ensureCorrectSatelliteDataItems(HashMap<String, Integer> items) {
        LinkedList<String> toRemove = new LinkedList<>();
        for (String itemId : items.keySet()) {
            if (!itemExists(itemId)) {
                Log.info("Removing invalid Item " + itemId + " from satellite data.");
                toRemove.add(itemId);
            }
        }
        for (String itemId : toRemove) {
            items.remove(itemId);
        }
    }

    public static boolean itemExists(String itemId) {
        for (Item item : Vars.content.items()) {
            if (String.valueOf(item.id).equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    // Method for handling the data directory
    private void ensureDataDirectoryExists() {
        try {
            // Retrieve the parent directory of the data file
            Path parentDir = Paths.get(Vars.dataDirectory.child(dataFilePath).parent().absolutePath());
            if (parentDir != null && Files.notExists(parentDir)) {
                // Create the directory, including any necessary but nonexistent parent directories
                Files.createDirectories(parentDir);
                Log.info("Data directory created at: @", parentDir.toAbsolutePath());
            } else {
                Log.info("Data directory found at: @", parentDir.toAbsolutePath());
            }
        } catch (IOException e) {
            // Log the exception using Log.info
            Log.info("Failed to create data directory: @", e.getMessage());
        }
    }

    // Method for retrieving satellite data (from planets)
    public static HashMap<String, Integer> getSatellitesPlanet(String planetId) {
        if (planetSatellites.containsKey(planetId)) {
            HashMap<String, Integer> satellites = planetSatellites.get(planetId);
            if (satellites == null) {
                satellites = new HashMap<String, Integer>();
            }
            return satellites;
        } else {
            return null;
        }
    }

    // Method for retrieving satellite data (from sectors)
    public static HashMap<String, Integer> getSatellitesSector(String sectorId) {
        if (sectorSatellites.containsKey(sectorId)) {
            HashMap<String, Integer> satellites = sectorSatellites.get(sectorId);
            if (satellites == null) {
                satellites = new HashMap<String, Integer>();
            }
            return satellites;
        } else {
            return null;
        }
    }

    public static Planet getPlanetById(String id) {
        for (Planet planet : Vars.content.planets()) {
            if (String.valueOf(planet.id).equals(id)) {
                return planet;
            }
        }
        return null;
    }

    public static Planet getPlanetByName(String name) {
        for (Planet planet : Vars.content.planets()) {
            if (planet.name.equals(name)) {
                return planet;
            }
        }
        return null;
    }
}
