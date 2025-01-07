package airstrike;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import arc.util.Log;
import mindustry.Vars;
import mindustry.type.Planet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

import static airstrike.SatelliteData.planetSatellites;
import static airstrike.SatelliteData.sectorSatellites;

public class AirstrilkeUtils {

    /**
     * Gets all currently active sectors via their save files.
     * Returns a map of planet -> sector.
     * Returns null if sector isn't in campaign.
     *
     * @return a map of planet -> sector, with non-campaign sectors under null
     */
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
                StringBuilder planetName = new StringBuilder(split[0]);
                for (int i = 1; i < split.length - 1; i++) {
                    planetName.append("-").append(split[i]);
                }
                Planet planet = getPlanetByName(planetName.toString());
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

    /**
     * Returns the planet the player is currently on.
     * Returns null if the player is not on a planet (not in campaign).
     *
     * @return the planet the player is currently on, or null
     */
    public static Planet getCurrentPlanet() {
        return Vars.state.getPlanet();
    }

    /**
     * Gets the id of the sector the player is currently in.
     * Gets sector id via save files.
     * Works both on planets and not in campaign.
     *
     * @return the id of the sector the player is currently in
     */
    public static String getCurrentSectorId() {
        String sectorId = Vars.control.saves.getCurrent().file.name().replace(".msav", "");
        if (sectorId.contains("-")) {
            String reverse = new StringBuilder(sectorId).reverse().toString();
            sectorId = new StringBuilder(reverse.split("-")[0]).reverse().toString();
        }
        return sectorId;
    }

    /**
     * Gets the planet with the specified id.
     * <p>
     * Looks through all planets in the game and returns the one with the matching id.
     * If no such planet exists, returns null.
     *
     * @param id the id of the planet to look for
     * @return the planet with the given id, or null if no such planet exists
     */
    public static Planet getPlanetById(String id) {
        for (Planet planet : Vars.content.planets()) {
            if (String.valueOf(planet.id).equals(id)) {
                return planet;
            }
        }
        return null;
    }

    /**
     * Gets the planet with the specified name.
     * <p>
     * Looks through all planets in the game and returns the one with the matching name.
     * If no such planet exists, returns null.
     *
     * @param name the name of the planet to look for
     * @return the planet with the given name, or null if no such planet exists
     */
    public static Planet getPlanetByName(String name) {
        for (Planet planet : Vars.content.planets()) {
            if (planet.name.equals(name)) {
                return planet;
            }
        }
        return null;
    }

    /**
     * Ensures that the data directory for satellite data exists.
     * <p>
     * This method checks if the parent directory of the data file path exists.
     * If it does not exist, it creates the directory along with any necessary
     * parent directories. Logs an informative message about the directory's
     * creation or existence. In case of an IOException, it logs an error message.
     */
    public static void ensureDataDirectoryExists() {
        try {
            // Retrieve the parent directory of the data file
            Path parentDir = Paths.get(Vars.dataDirectory.child(SatelliteData.dataFilePath).parent().absolutePath());
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
}
