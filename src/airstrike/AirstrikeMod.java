package airstrike;

import airstrike.content.AirstrikeBlocks;
import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.*;
import mindustry.type.Planet;

import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class AirstrikeMod extends Mod{
    // Satellite data (planet-id: (item-id: amount))
    public static HashMap<Integer, HashMap<Integer, Integer>> planetSatellites;
    // Path to satellite data storage
    private static final String dataFilePath = "mods/airstrike-data/satellite_data.json";

    public AirstrikeMod(){
    }

    @Override
    public void init() {
        super.init();
        // Register the application listener
        Core.app.addListener(new AirstrikeApplicationListener());
    }

    @Override
    public void loadContent(){
        super.loadContent();
        AirstrikeBlocks.load();

        ensureDataDirectoryExists();
        loadSatelliteData();
    }

    // Method to get planet player is on
    // Returns null if player isn't on a planet (not in campaign)
    public static Planet getCurrentPlanet() {
        return Vars.state.getPlanet();
    }

    // Save satellite data to defined path
    public static void saveSatelliteData() {
        try {
            String data = encodeSatelliteData(planetSatellites);
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Saving satellite data at: @", file.absolutePath());
            file.writeString(data);
            Log.info("Saved satellite data: " + data);
        } catch (Exception e) {
            Log.err("Failed to save satellite data.", e);
        }
    }

    private static String encodeSatelliteData(HashMap<Integer, HashMap<Integer, Integer>> planetSatellites) {
        String data = "{";
        for (int planetId : planetSatellites.keySet()) {
            data = data.concat("\"" + planetId + "\":" + "{");
            HashMap<Integer, Integer> satellites = planetSatellites.get(planetId);
            for (int itemId : satellites.keySet()) {
                data = data.concat("\"" + itemId + "\":" + satellites.get(itemId) + ",");
            }
            if (data.charAt(data.length() - 1) == ',') {
                data = data.substring(0, data.length() - 1);
            }
            data = data.concat("},");
        }
        if (data.charAt(data.length() - 1) == ',') {
            data = data.substring(0, data.length() - 1);
        }
        data = data.concat("}");
        return data;
    }

    // Load satellite data from defined path
    public static void loadSatelliteData() {
        try {
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Looking for satellite data at: @", file.absolutePath());
            if (file.exists()) {
                String data = file.readString();
                planetSatellites = decodeSatelliteData(data);
                Log.info("Satellite data loaded successfully.");
                correctSatelliteData();
            } else {
                initialiseSatelliteData();
                Log.info("No satellite data found.");
            }
            Log.info("Loaded satellite data: " + planetSatellites);
        } catch (Exception e) {
            Log.err("Failed to load satellite data.", e);
            initialiseSatelliteData();
        }
    }

    private static HashMap<Integer, HashMap<Integer, Integer>> decodeSatelliteData(String data) {
        HashMap<Integer, HashMap<Integer, Integer>> planetSatellites = new HashMap<>();
        data = data.substring(1, data.length() - 2);
        String[] planetData = data.split("},");
        for (String planetString : planetData) {
            planetString = planetString.concat("}");
            String[] planetParts = planetString.split(":");
            int planetId = Integer.parseInt(planetParts[0].substring(1, planetParts[0].length() - 1));
            String satelliteData = "";
            for (int i = 1; i < planetParts.length; i++) {
                satelliteData = satelliteData.concat(planetParts[i] + ":");
            }
            satelliteData = satelliteData.substring(0, satelliteData.length() - 1);
            satelliteData = satelliteData.substring(1, satelliteData.length() - 1);
            HashMap<Integer, Integer> satellites = new HashMap<>();
            if (!satelliteData.isEmpty()) {
                for (String satelliteString : satelliteData.split(",")) {
                    String[] satelliteParts = satelliteString.split(":");
                    int itemId = Integer.parseInt(satelliteParts[0].substring(1, satelliteParts[0].length() - 1));
                    int itemAmount = Integer.parseInt(satelliteParts[1]);
                    satellites.put(itemId, itemAmount);
                }
            }
            planetSatellites.put(planetId, satellites);
        }
        return planetSatellites;
    }

    public static void correctSatelliteData() {
        for (Planet planet : Vars.content.planets()) {
            if (!planetSatellites.containsKey((int) planet.id)) {
                planetSatellites.put((int) planet.id, new HashMap<Integer, Integer>());
                Log.info("Added unknown planet " + planet.name + " to satellite data.");
            }
        }
    }

    // Method for creating the satellite data save structures
    public static void initialiseSatelliteData() {
        planetSatellites = new HashMap<>();
        for (Planet planet : Vars.content.planets()) {
            planetSatellites.put((int) planet.id, new HashMap<Integer, Integer>());
        }
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

    // Method for retrieving satellite data
    public static HashMap<Integer, Integer> getSatellites(Planet planet) {
        if (planetSatellites.containsKey((int) planet.id)) {
            HashMap<Integer, Integer> satellites = planetSatellites.get((int) planet.id);
            if (satellites == null) {
                satellites = new HashMap<Integer, Integer>();
            }
            return satellites;
        } else {
            return null;
        }
    }

    public static Planet getPlanetById(int id) {
        for (Planet planet : Vars.content.planets()) {
            if (planet.id == id) {
                return planet;
            }
        }
        return null;
    }
}
