package airstrike;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeWeapons;
import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import arc.util.serialization.Json.JsonSerializable;
import mindustry.Vars;
import mindustry.type.Planet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SatelliteData {
    // Satellite data for planets (planet-id: (item-id: amount))
    public static HashMap<String, HashMap<String, Integer>> planetSatellites = new HashMap<>();
    // Satellite data for sectors without planets (sector-id: (item-id: amount))
    public static HashMap<String, HashMap<String, Integer>> sectorSatellites = new HashMap<>();
    // Path to satellite data storage
    public static final String dataFilePath = "mods/airstrike-data/satellite_data.json";

    public SatelliteData() {
    }

    /**
     * Retrieves the satellite data for the specified planet.
     * If the planet has no associated satellite data, a new empty map is returned.
     *
     * @param planetName the name of the planet for which to retrieve satellite data
     * @return a map of weapon IDs to their counts for the given planet,
     *         or null if the planet has no satellite data
     */
    public static HashMap<String, Integer> getSatellitesPlanet(String planetName) {
        if (planetSatellites.containsKey(planetName)) {
            HashMap<String, Integer> satellites = planetSatellites.get(planetName);
            if (satellites == null) {
                satellites = new HashMap<String, Integer>();
            }
            return satellites;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the satellite data for the specified sector.
     * If the sector has no associated satellite data, a new empty map is returned.
     *
     * @param sectorId the id of the sector for which to retrieve satellite data
     * @return a map of weapon IDs to their counts for the given sector,
     *         or null if the sector has no satellite data
     */
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

    /**
     * Retrieves the satellite data for the current location of the player.
     * <p>
     * If the player is currently on a planet, it returns the satellite data
     * associated with that planet. If the player is not on a planet, it returns
     * the satellite data associated with the current sector.
     *
     * @return a map of weapon IDs to their counts for the current planet or sector, or null if the location has no satellite data
     */
    public static HashMap<String, Integer> getSatellites() {
        Planet currentPlanet = AirstrilkeUtils.getCurrentPlanet();
        if (currentPlanet == null) {
            return getSatellitesSector(AirstrilkeUtils.getCurrentSectorId());
        } else {
            return getSatellitesPlanet(currentPlanet.name);
        }
    }

    /**
     * Retrieves the satellite data associated with the given planet or sector.
     * <p>
     * If the given ID is associated with a planet, it returns the satellite data
     * associated with that planet. If the given ID is associated with a sector,
     * it returns the satellite data associated with that sector.
     *
     * @param planetNameOrSectorId the ID of the planet or sector for which to
     *                              retrieve satellite data
     * @return a map of weapon IDs to their counts for the given planet or sector,
     *         or null if the location has no satellite data
     */
    public static HashMap<String, Integer> getSatellites(String planetNameOrSectorId) {
        if (planetSatellites.containsKey(planetNameOrSectorId)) {
            return getSatellitesPlanet(planetNameOrSectorId);
        } else {
            return getSatellitesSector(planetNameOrSectorId);
        }
    }

    /**
     * Returns the total number of satellites in orbit of the current sector or planet.
     * <p>
     * This is the sum of the counts of all weapons in orbit of the current sector or planet.
     *
     * @return the total number of satellites in orbit of the current sector or planet
     */
    public static int getWeaponCount() {
        int totalWeaponCount = 0;
        for (int weaponCount : getSatellites().values()) {
            totalWeaponCount += weaponCount;
        }
        return totalWeaponCount;
    }

    /**
     * Returns the total number of weapons in orbit of the specified planet or sector.
     * <p>
     * This method calculates the sum of the counts of all weapons associated with
     * the given planet or sector ID.
     *
     * @param planetNameOrSectorId the ID of the planet or sector for which to retrieve the weapon count
     * @return the total number of weapons in orbit of the specified planet or sector
     */
    public static int getWeaponCount(String planetNameOrSectorId) {
        int totalWeaponCount = 0;
        for (int weaponCount : getSatellites(planetNameOrSectorId).values()) {
            totalWeaponCount += weaponCount;
        }
        return totalWeaponCount;
    }

    /**
     * Returns the number of weapons in orbit of the current sector or planet with the given weapon ID.
     * <p>
     * If the weapon ID is not present in the satellite data, returns 0.
     *
     * @param weaponId the weapon ID to look for
     * @return the number of weapons with the given ID in orbit of the current sector or planet
     */
    public static int getWeaponCountOfWeapon(String weaponId) {
        HashMap<String, Integer> satellites = getSatellites();
        return satellites.getOrDefault(weaponId, 0);
    }

    /**
     * Returns the total number of weapons in orbit of all planets.
     * <p>
     * This method calculates the sum of the counts of all weapons
     * associated with each planet in the satellite data.
     *
     * @return the total number of weapons in orbit of all planets
     */
    public static int getTotalWeaponCountPlanet() {
        int totalWeaponCount = 0;
        for (String planetName : planetSatellites.keySet()) {
            for (int weaponCount : planetSatellites.get(planetName).values()) {
                totalWeaponCount += weaponCount;
            }
        }
        return totalWeaponCount;
    }

    /**
     * Returns the total number of weapons in orbit of all sectors.
     * <p>
     * This method calculates the sum of the counts of all weapons
     * associated with each sector in the satellite data.
     *
     * @return the total number of weapons in orbit of all sectors
     */
    public static int getTotalWeaponCountSector() {
        int totalWeaponCount = 0;
        for (String sectorId : sectorSatellites.keySet()) {
            for (int weaponCount : sectorSatellites.get(sectorId).values()) {
                totalWeaponCount += weaponCount;
            }
        }
        return totalWeaponCount;
    }

    /**
     * Retrieves an array of weapon IDs for all weapons in orbit of the current sector or planet.
     * <p>
     * The array is a flattened version of the map returned by {@link #getSatellites()}.
     * All weapons are repeated according to their count in the map.
     *
     * @return a flattened array of weapon IDs
     */
    public static String[] getWeapons() {
        String[] weapons = new String[getWeaponCount()];
        HashMap<String, Integer> satellites = getSatellites();
        int index = 0;
        for (String weaponId : satellites.keySet()) {
            int weaponCount = satellites.get(weaponId);
            for (int i = 0; i < weaponCount; i++) {
                weapons[index] = weaponId;
                index++;
            }
        }
        return weapons;
    }

    /**
     * Retrieves an array of weapon IDs for all weapons in orbit of the given sector or planet.
     * <p>
     * The array is a flattened version of the map returned by {@link #getSatellites(String)}.
     * All weapons are repeated according to their count in the map.
     *
     * @param planetNameOrSectorId the name of the planet or the id of the sector to retrieve weapons for
     * @return a flattened array of weapon IDs
     */
    public static String[] getWeapons(String planetNameOrSectorId) {
        String[] weapons = new String[getWeaponCountOfWeapon(planetNameOrSectorId)];
        HashMap<String, Integer> satellites = getSatellites(planetNameOrSectorId);
        int index = 0;
        for (String weaponId : satellites.keySet()) {
            int weaponCount = satellites.get(weaponId);
            for (int i = 0; i < weaponCount; i++) {
                weapons[index] = weaponId;
                index++;
            }
        }
        return weapons;
    }

    /**
     * Retrieves an array of all weapon IDs in orbit of all planets.
     * <p>
     * The array is a flattened version of the map returned by {@link #getSatellitesPlanet(String)}.
     * All weapons are repeated according to their count in the map.
     *
     * @return a flattened array of all weapon IDs in orbit of all planets
     */
    public static String[] getTotalWeaponsPlanet() {
        String[] weapons = new String[getTotalWeaponCountPlanet()];
        int index = 0;
        for (String planetName : planetSatellites.keySet()) {
            for (String weaponId : planetSatellites.get(planetName).keySet()) {
                int weaponCount = planetSatellites.get(planetName).get(weaponId);
                for (int i = 0; i < weaponCount; i++) {
                    weapons[index] = weaponId;
                    index++;
                }
            }
        }
        return weapons;
    }

    /**
     * Retrieves an array of all weapon IDs of all weapons in orbit of all sectors.
     * <p>
     * The array is a flattened version of the map returned by {@link #getSatellitesSector(String)}.
     * All weapons are repeated according to their count in the map.
     *
     * @return a flattened array of weapon IDs
     */
    public static String[] getTotalWeaponsSector() {
        String[] weapons = new String[getTotalWeaponCountSector()];
        int index = 0;
        for (String sectorId : sectorSatellites.keySet()) {
            for (String weaponId : sectorSatellites.get(sectorId).keySet()) {
                int weaponCount = sectorSatellites.get(sectorId).get(weaponId);
                for (int i = 0; i < weaponCount; i++) {
                    weapons[index] = weaponId;
                    index++;
                }
            }
        }
        return weapons;
    }

    /**
     * Adds a given number of weapons of the given type to the given planet in the satellite data.
     * <p>
     * If the planet is not in the satellite data, an error log message is generated.
     *
     * @param planetName the name of the planet to add the weapons to
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addWeaponToPlanet(String planetName, AirstrikeWeapon weapon, int amount) {
        HashMap<String, Integer> satellites = getSatellitesPlanet(planetName);
        int weaponCount = getWeaponCountOfWeapon(weapon.id);
        if (satellites == null) {
            Log.err("Planet " + planetName + " not in satellite data");
            return;
        }
        satellites.put(weapon.id, weaponCount + amount);
    }

    /**
     * Adds a specified number of weapons of a given type to the specified sector in the satellite data.
     * <p>
     * If the sector is not present in the satellite data, an error log message is generated.
     *
     * @param sectorId the ID of the sector to add the weapons to
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addWeaponToSector(String sectorId, AirstrikeWeapon weapon, int amount) {
        HashMap<String, Integer> satellites = getSatellitesSector(sectorId);
        int weaponCount = getWeaponCountOfWeapon(weapon.id);
        if (satellites == null) {
            Log.err("Sector " + sectorId + " not in satellite data");
            return;
        }
        satellites.put(weapon.id, weaponCount + amount);
    }

    /**
     * Adds a specified number of weapons of a given type to the current sector or planet in the satellite data.
     * <p>
     * If the player is not on a planet, the sector the player is currently in is used.
     * If the sector is not present in the satellite data, an error log message is generated.
     * If the planet is not present in the satellite data, an error log message is generated.
     *
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addWeapon(AirstrikeWeapon weapon, int amount) {
        Planet planet = AirstrilkeUtils.getCurrentPlanet();
        if (planet == null) {
            addWeaponToSector(AirstrilkeUtils.getCurrentSectorId(), weapon, amount);
        } else {
            addWeaponToPlanet(planet.name, weapon, amount);
        }
    }

    /**
     * Adds one weapon of the given type to the current sector or planet in the satellite data.
     * <p>
     * If the player is not on a planet, the sector the player is currently in is used.
     * If the sector is not present in the satellite data, an error log message is generated.
     * If the planet is not present in the satellite data, an error log message is generated.
     *
     * @param weapon the weapon type to add
     * @see #addWeapon(AirstrikeWeapon, int)
     */
    public static void addWeapon(AirstrikeWeapon weapon) {
        addWeapon(weapon, 1);
    }

    /**
     * Removes a specified number of weapons of a given type from the given planet in the satellite data.
     * <p>
     * If the planet is not present in the satellite data, an error log message is generated and the method returns false.
     * If there are less than the given amount of the given weapon type in the satellite data for the given planet,
     * the method returns false.
     * Otherwise, the method removes the given amount of the given weapon type from the satellite data for the given planet,
     * and returns true.
     *
     * @param planetName the name of the planet from which to remove the weapons
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeWeaponFromPlanet(String planetName, AirstrikeWeapon weapon, int amount) {
        HashMap<String, Integer> satellites = getSatellitesPlanet(planetName);
        int weaponCount = getWeaponCountOfWeapon(weapon.id);
        if (satellites == null) {
            Log.err("Planet " + planetName + " not in satellite data");
            return false;
        }
        if (weaponCount < amount) {
            return false;
        }
        satellites.put(weapon.id, weaponCount - amount);
        return true;
    }

    /**
     * Removes a specified number of weapons of a given type from the given sector in the satellite data.
     * <p>
     * If the sector is not present in the satellite data, an error log message is generated and the method returns false.
     * If there are less than the given amount of the given weapon type in the satellite data for the given sector,
     * the method returns false.
     * Otherwise, the method removes the given amount of the given weapon type from the satellite data for the given sector,
     * and returns true.
     *
     * @param sectorId the ID of the sector from which to remove the weapons
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeWeaponFromSector(String sectorId, AirstrikeWeapon weapon, int amount) {
        HashMap<String, Integer> satellites = getSatellitesSector(sectorId);
        int weaponCount = getWeaponCountOfWeapon(weapon.id);
        if (satellites == null) {
            Log.err("Sector " + sectorId + " not in satellite data");
            return false;
        }
        if (weaponCount < amount) {
            return false;
        }
        satellites.put(weapon.id, weaponCount - amount);
        return true;
    }

    /**
     * Removes a specified number of weapons of a given type from the current planet or sector in the satellite data.
     * <p>
     * If the current location is a planet, this method calls
     * {@link #removeWeaponFromPlanet(String, AirstrikeWeapon, int)} with the planet's name and the given weapon and amount.
     * If the current location is a sector, this method calls
     * {@link #removeWeaponFromSector(String, AirstrikeWeapon, int)} with the sector's ID and the given weapon and amount.
     *
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeWeapon(AirstrikeWeapon weapon, int amount) {
        Planet planet = AirstrilkeUtils.getCurrentPlanet();
        if (planet == null) {
            return removeWeaponFromSector(AirstrilkeUtils.getCurrentSectorId(), weapon, amount);
        } else {
            return removeWeaponFromPlanet(planet.name, weapon, amount);
        }
    }

    /**
     * Removes a single weapon of the given type from the current planet or sector in the satellite data.
     * <p>
     * This method is equivalent to calling {@link #removeWeapon(AirstrikeWeapon, int)} with the given weapon and an amount of 1.
     * <p>
     * If the current location is a planet, this method calls
     * {@link #removeWeaponFromPlanet(String, AirstrikeWeapon, int)} with the planet's name and the given weapon and amount.
     * If the current location is a sector, this method calls
     * {@link #removeWeaponFromSector(String, AirstrikeWeapon, int)} with the sector's ID and the given weapon and amount.
     *
     * @param weapon the weapon type to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeWeapon(AirstrikeWeapon weapon) {
        return removeWeapon(weapon, 1);
    }

    /**
     * Ensures that the satellite data is up to date with the active planets and sectors in the game.
     * <p>
     * This method is called automatically by the mod whenever the game saves or loads.
     * It ensures that the satellite data is up to date with the active planets and sectors in the game, by adding any missing planets or sectors and removing any inactive ones.
     * <p>
     * This method is useful for ensuring that the satellite data is always up to date and correct, even if the user manually edits the save files.
     */
    public static void correctSatelliteData() {
        // Saves to check if satellite data is up to date with active planets/sectors
        if (planetSatellites == null) {
            planetSatellites = new HashMap<>();
        }
        if (sectorSatellites == null) {
            sectorSatellites = new HashMap<>();
        }
        HashMap<Planet, LinkedList<Integer>> saves = AirstrilkeUtils.getSaves();
        // Add missing planets/sectors
        for (Planet planet : saves.keySet()) {
            if (planet != null) {
                if (!planetSatellites.containsKey(String.valueOf(planet.name))) {
                    Log.info("Adding untracked Planet " + planet.name + " to satellite data.");
                    planetSatellites.put(String.valueOf(planet.name), new HashMap<String, Integer>());
                }
            } else {
                for (int sectorId : saves.get(null)) {
                    if (!sectorSatellites.containsKey(String.valueOf(sectorId))) {
                        Log.info("Adding untracked non-planet Sector " + sectorId + " to satellite data.");
                        sectorSatellites.put(String.valueOf(sectorId), new HashMap<String, Integer>());
                    }
                }
            }
        }
        // Remove planets that are no longer active
        LinkedList<String> toRemove = new LinkedList<>();
        for (String planetName : planetSatellites.keySet()) {
            if (AirstrilkeUtils.getPlanetByName(planetName) == null || !saves.containsKey(AirstrilkeUtils.getPlanetByName(planetName))) {
                Log.info("Removing invalid Planet " + planetName + " from satellite data.");
                toRemove.add(planetName);
            }
            correctSatelliteDataWeapons(planetSatellites.get(planetName));
        }
        for (String planetId : toRemove) {
            planetSatellites.remove(planetId);
        }
        // Remove sectors that are no longer active
        toRemove.clear();
        if (saves.containsKey(null)) {
            for (String sectorId : sectorSatellites.keySet()) {
                if (!saves.get(null).contains(Integer.parseInt(sectorId))) {
                    Log.info("Removing invalid non-planet sector " + sectorId + " from satellite data.");
                    toRemove.add(sectorId);
                }
                correctSatelliteDataWeapons(sectorSatellites.get(sectorId));
            }
        } else {
            for (String sectorId : sectorSatellites.keySet()) {
                Log.info("Removing invalid non-planet sector " + sectorId + " from satellite data.");
                toRemove.add(sectorId);
            }
        }
        for (String sectorId : toRemove) {
            sectorSatellites.remove(sectorId);
        }
    }

    /**
     * Removes any invalid weapons from the given map of weapons.
     * <p>
     * A weapon is considered invalid if it is not present in the {@link AirstrikeWeapons} map.
     * <p>
     * This method is idempotent and does not modify the map if it does not contain any invalid weapons.
     *
     * @param weapons the map of weapons to clean up
     */
    public static void correctSatelliteDataWeapons(HashMap<String, Integer> weapons) {
        LinkedList<String> toRemove = new LinkedList<>();
        for (String weaponId : weapons.keySet()) {
            if (AirstrikeWeapons.get(weaponId) == null) {
                Log.info("Removing invalid weapon " + weaponId + " from satellite data.");
                toRemove.add(weaponId);
            }
        }
        for (String itemId : toRemove) {
            weapons.remove(itemId);
        }
    }

    /**
     * Saves the current satellite data to the data file.
     * <p>
     * This method will overwrite any existing data in the file.
     * <p>
     * The data is saved in a JSON format, with two main objects: "planets" and "sectors".
     * The "planets" object has each planet as a key and its associated items as a value.
     * The "sectors" object has each sector as a key and its associated items as a value.
     * Each item is represented as a key-value pair, with the item ID as the key and the count as the value.
     * <p>
     * This method ensures that the data directory exists before attempting to save the data using {@link AirstrilkeUtils#ensureDataDirectoryExists()}.
     * It also calls {@link #correctSatelliteData()} to remove any invalid weapons from the data before saving.
     */
    public static void saveSatelliteData() {
        AirstrilkeUtils.ensureDataDirectoryExists();

        correctSatelliteData();

        try {
            // Save the data to the file
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Saving satellite data to: @", file.absolutePath());

            // Create a StringBuilder to construct the JSON string
            StringBuilder jsonBuilder = new StringBuilder("{");

            jsonBuilder.append("\"planets\":{");
            // Iterate over each planet and its associated items
            for (Map.Entry<String, HashMap<String, Integer>> entry : planetSatellites.entrySet()) {
                String planetName = entry.getKey();
                HashMap<String, Integer> items = entry.getValue();

                // Append the planet ID and its items to the JSON string
                jsonBuilder.append("\"").append(planetName).append("\":{");
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

    /**
     * Loads the satellite data from a JSON file and initializes the planet and sector satellite maps.
     * <p>
     * This method attempts to locate the satellite data file in the specified directory. If the file exists,
     * it reads the JSON content and deserializes it into HashMaps representing the satellite data for planets
     * and sectors. If the file does not exist or an error occurs during loading, empty data structures are
     * initialized instead.
     * <p>
     * After loading the data, it invokes {@link #correctSatelliteData()} to ensure the data is up-to-date
     * with the current game state.
     * <p>
     * Logs informative messages regarding the loading process and any errors encountered.
     */
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
                            String planetName = planetValue.name;
                            HashMap<String, Integer> satellites = new HashMap<>();
                            for (JsonValue itemValue : planetValue) {
                                String itemId = itemValue.name;
                                int amount = itemValue.asInt();
                                satellites.put(itemId, amount);
                            }
                            planetSatellites.put(planetName, satellites);
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
}
