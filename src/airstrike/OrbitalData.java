package airstrike;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeWeapons;
import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.type.Planet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class OrbitalData {
    // Orbital data for planets (planet-id: (weapon1-id, weapon2-id, ...))
    public static HashMap<String, LinkedList<String>> planetOrbitalWeapons = new HashMap<>();
    // Orbital data for sectors without planets (sector-id: (weapon1-id, weapon2-id, ...))
    public static HashMap<String, LinkedList<String>> sectorOrbitalWeapons = new HashMap<>();
    // Path to orbital data storage
    public static final String dataFilePath = "saves/airstrike-data/orbital_data.json";

    public OrbitalData() {}

    /**
     * Retrieves the orbital data for the specified planet.
     * If the planet has no associated orbital data, a new empty list is returned.
     *
     * @param planetName the name of the planet for which to retrieve orbital data
     * @return a list of weapon IDs for the given planet,
     *         or null if the planet has no orbital data
     */
    public static LinkedList<String> getOrbitalWeaponsOfPlanet(String planetName) {
        if (planetOrbitalWeapons.containsKey(planetName)) {
            LinkedList<String> orbitalWeapons = planetOrbitalWeapons.get(planetName);
            if (orbitalWeapons == null) {
                orbitalWeapons = new LinkedList<>();
            }
            return orbitalWeapons;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the orbital data for the specified sector.
     * If the sector has no associated orbital data, a new empty list is returned.
     *
     * @param sectorId the id of the sector for which to retrieve orbital data
     * @return a list of weapon IDs for the given sector,
     *         or null if the sector has no orbital data
     */
    public static LinkedList<String> getOrbitalWeaponsOfSector(String sectorId) {
        if (sectorOrbitalWeapons.containsKey(sectorId)) {
            LinkedList<String> orbitalWeapons = sectorOrbitalWeapons.get(sectorId);
            if (orbitalWeapons == null) {
                orbitalWeapons = new LinkedList<>();
            }
            return orbitalWeapons;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the orbital data associated with the given planet or sector.
     * <p>
     * If the given ID is associated with a planet, it returns the orbital data
     * associated with that planet. If the given ID is associated with a sector,
     * it returns the orbital data associated with that sector.
     *
     * @param planetNameOrSectorId the ID of the planet or sector for which to
     *                              retrieve orbital data
     * @return a list of weapon IDs for the given planet or sector,
     *         or null if the location has no orbital data
     */
    public static LinkedList<String> getOrbitalWeapons(String planetNameOrSectorId) {
        if (planetOrbitalWeapons.containsKey(planetNameOrSectorId)) {
            return getOrbitalWeaponsOfPlanet(planetNameOrSectorId);
        } else {
            return getOrbitalWeaponsOfSector(planetNameOrSectorId);
        }
    }

    /**
     * Retrieves the orbital data for the current location of the player.
     * <p>
     * If the player is currently on a planet, it returns the orbital data
     * associated with that planet. If the player is not on a planet, it returns
     * the orbital data associated with the current sector.
     *
     * @return a list of weapon IDs for the current planet or sector, or null if the location has no orbital data
     */
    public static LinkedList<String> getOrbitalWeapons() {
        return getOrbitalWeapons(AirstrilkeUtils.getLocation());
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
    public static int getOrbitalWeaponCount(String planetNameOrSectorId) {
        return getOrbitalWeapons(planetNameOrSectorId).size();
    }

    /**
     * Returns the count of a specific weapon in orbit of the specified planet or sector.
     * <p>
     * This method searches the orbital weapons associated with the given planet or sector ID
     * and counts how many of them match the specified weapon ID.
     *
     * @param planetNameOrSectorId the ID of the planet or sector for which to retrieve the weapon count
     * @param weaponId the ID of the weapon to count
     * @return the number of weapons with the given ID in orbit of the specified planet or sector
     */
    public static int getOrbitalWeaponCount(String planetNameOrSectorId, String weaponId) {
        LinkedList<String> orbitalWeapons = getOrbitalWeapons(planetNameOrSectorId);
        return (int) orbitalWeapons.stream().filter(weapon -> weapon.equals(weaponId)).count();
    }

    /**
     * Returns the total number of orbital weapons for current sector or planet.
     * <p>
     * This is the sum of the counts of all weapons in orbit of the current sector or planet.
     *
     * @return the total number of orbital weapons for the current sector or planet
     */
    public static int getCurrentOrbitalWeaponCount() {
        return getOrbitalWeaponCount(AirstrilkeUtils.getLocation());
    }

    /**
     * Returns the number of weapons in orbit of the current sector or planet with the given weapon ID.
     * <p>
     * If the weapon ID is not present in the orbital data, returns 0.
     *
     * @param weaponId the weapon ID to look for
     * @return the number of weapons with the given ID in orbit of the current sector or planet
     */
    public static int getCurrentOrbitalWeaponCount(String weaponId) {
        return getOrbitalWeaponCount(AirstrilkeUtils.getLocation(), weaponId);
    }

    /**
     * Adds a given number of weapons of the given type to the given planet in the orbital data.
     * <p>
     * If the planet is not in the orbital data, an error log message is generated.
     *
     * @param planetName the name of the planet to add the weapons to
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addOrbitalWeaponToPlanet(String planetName, AirstrikeWeapon weapon, int amount) {
        LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfPlanet(planetName);
        if (orbitalWeapons == null) {
            Log.err("Planet " + planetName + " not in orbital data");
            return;
        }
        for (int i = 0; i < amount; i++) {
            orbitalWeapons.add(weapon.id);
        }
    }

    /**
     * Adds a specified number of weapons of a given type to the specified sector in the orbital data.
     * <p>
     * If the sector is not present in the orbital data, an error log message is generated.
     *
     * @param sectorId the ID of the sector to add the weapons to
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addOrbitalWeaponToSector(String sectorId, AirstrikeWeapon weapon, int amount) {
        LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfSector(sectorId);
        if (orbitalWeapons == null) {
            Log.err("Sector " + sectorId + " not in orbital data");
            return;
        }
        for (int i = 0; i < amount; i++) {
            orbitalWeapons.add(weapon.id);
        }
    }

    /**
     * Adds a specified number of weapons of a given type to the current sector or planet in the orbital data.
     * <p>
     * If the player is not on a planet, the sector the player is currently in is used.
     * If the sector is not present in the orbital data, an error log message is generated.
     * If the planet is not present in the orbital data, an error log message is generated.
     *
     * @param weapon the weapon type to add
     * @param amount the number of weapons to add
     */
    public static void addOrbitalWeapon(AirstrikeWeapon weapon, int amount) {
        Planet planet = AirstrilkeUtils.getCurrentPlanet();
        if (planet == null) {
            addOrbitalWeaponToSector(AirstrilkeUtils.getCurrentSectorId(), weapon, amount);
        } else {
            addOrbitalWeaponToPlanet(planet.name, weapon, amount);
        }
    }

    /**
     * Adds one weapon of the given type to the current sector or planet in the orbital data.
     * <p>
     * If the player is not on a planet, the sector the player is currently in is used.
     * If the sector is not present in the orbital data, an error log message is generated.
     * If the planet is not present in the orbital data, an error log message is generated.
     *
     * @param weapon the weapon type to add
     * @see #addOrbitalWeapon(AirstrikeWeapon, int)
     */
    public static void addOrbitalWeapon(AirstrikeWeapon weapon) {
        addOrbitalWeapon(weapon, 1);
    }

    /**
     * Removes a specified number of weapons of a given type from the given planet in the orbital data.
     * <p>
     * If the planet is not present in the orbital data, an error log message is generated and the method returns false.
     * If there are less than the given amount of the given weapon type in the orbital data for the given planet,
     * the method returns false.
     * Otherwise, the method removes the given amount of the given weapon type from the orbital data for the given planet,
     * and returns true.
     *
     * @param planetName the name of the planet from which to remove the weapons
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeOrbitalWeaponFromPlanet(String planetName, AirstrikeWeapon weapon, int amount) {
        LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfPlanet(planetName);
        int weaponCount = getCurrentOrbitalWeaponCount(weapon.id);
        if (orbitalWeapons == null) {
            Log.err("Planet " + planetName + " not in orbital data");
            return false;
        }
        if (weaponCount < amount) {
            return false;
        }
        for (int i = 0; i < amount; i++) {
            orbitalWeapons.remove(weapon.id);
        }
        return true;
    }

    /**
     * Removes a specified number of weapons of a given type from the given sector in the orbital data.
     * <p>
     * If the sector is not present in the orbital data, an error log message is generated and the method returns false.
     * If there are less than the given amount of the given weapon type in the orbital data for the given sector,
     * the method returns false.
     * Otherwise, the method removes the given amount of the given weapon type from the orbital data for the given sector,
     * and returns true.
     *
     * @param sectorId the ID of the sector from which to remove the weapons
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeOrbitalWeaponFromSector(String sectorId, AirstrikeWeapon weapon, int amount) {
        LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfSector(sectorId);
        int weaponCount = getCurrentOrbitalWeaponCount(weapon.id);
        if (orbitalWeapons == null) {
            Log.err("Sector " + sectorId + " not in orbital data");
            return false;
        }
        if (weaponCount < amount) {
            return false;
        }
        for (int i = 0; i < amount; i++) {
            orbitalWeapons.remove(weapon.id);
        }
        return true;
    }

    /**
     * Removes a specified number of weapons of a given type from the current planet or sector in the orbital data.
     * <p>
     * If the current location is a planet, this method calls
     * {@link #removeOrbitalWeaponFromPlanet(String, AirstrikeWeapon, int)} with the planet's name and the given weapon and amount.
     * If the current location is a sector, this method calls
     * {@link #removeOrbitalWeaponFromSector(String, AirstrikeWeapon, int)} with the sector's ID and the given weapon and amount.
     *
     * @param weapon the weapon type to remove
     * @param amount the number of weapons to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeOrbitalWeapon(AirstrikeWeapon weapon, int amount) {
        Planet planet = AirstrilkeUtils.getCurrentPlanet();
        if (planet == null) {
            return removeOrbitalWeaponFromSector(AirstrilkeUtils.getCurrentSectorId(), weapon, amount);
        } else {
            return removeOrbitalWeaponFromPlanet(planet.name, weapon, amount);
        }
    }

    /**
     * Removes a single weapon of the given type from the current planet or sector in the orbital data.
     * <p>
     * This method is equivalent to calling {@link #removeOrbitalWeapon(AirstrikeWeapon, int)} with the given weapon and an amount of 1.
     * <p>
     * If the current location is a planet, this method calls
     * {@link #removeOrbitalWeaponFromPlanet(String, AirstrikeWeapon, int)} with the planet's name and the given weapon and amount.
     * If the current location is a sector, this method calls
     * {@link #removeOrbitalWeaponFromSector(String, AirstrikeWeapon, int)} with the sector's ID and the given weapon and amount.
     *
     * @param weapon the weapon type to remove
     * @return true if the removal was successful, false otherwise
     */
    public static boolean removeOrbitalWeapon(AirstrikeWeapon weapon) {
        return removeOrbitalWeapon(weapon, 1);
    }

    /**
     * Ensures that the orbital data is up to date with the active planets and sectors in the game.
     * <p>
     * This method is called automatically by the mod whenever the game saves or loads.
     * It ensures that the orbital data is up to date with the active planets and sectors in the game, by adding any missing planets or sectors and removing any inactive ones.
     * <p>
     * This method is useful for ensuring that the orbital data is always up to date and correct, even if the user manually edits the save files.
     */
    public static void correctOrbitalData() {
        if (planetOrbitalWeapons == null) {
            planetOrbitalWeapons = new HashMap<>();
        }
        if (sectorOrbitalWeapons == null) {
            sectorOrbitalWeapons = new HashMap<>();
        }
        // Saves to check if orbital data is up to date with active planets/sectors (active = in saves - has been played)
        HashMap<Planet, LinkedList<Integer>> saves = AirstrilkeUtils.getSaves();
        // Add missing planets/sectors
        for (Planet planet : saves.keySet()) {
            if (planet != null) {
                if (!planetOrbitalWeapons.containsKey(String.valueOf(planet.name))) {
                    Log.info("Adding untracked Planet " + planet.name + " to orbital data.");
                    planetOrbitalWeapons.put(String.valueOf(planet.name), new LinkedList<String>());
                }
            } else {
                for (int sectorId : saves.get(null)) {
                    if (!sectorOrbitalWeapons.containsKey(String.valueOf(sectorId))) {
                        Log.info("Adding untracked non-planet Sector " + sectorId + " to orbital data.");
                        sectorOrbitalWeapons.put(String.valueOf(sectorId), new LinkedList<String>());
                    }
                }
            }
        }
        // Remove planets that are no longer active
        LinkedList<String> toRemove = new LinkedList<>();
        for (String planetName : planetOrbitalWeapons.keySet()) {
            if (AirstrilkeUtils.getPlanetByName(planetName) == null || !saves.containsKey(AirstrilkeUtils.getPlanetByName(planetName))) {
                Log.info("Removing invalid Planet " + planetName + " from orbital data.");
                toRemove.add(planetName);
            }
            LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfPlanet(planetName);
            if ((orbitalWeapons != null)) {
                correctOrbitalDataWeapons(orbitalWeapons);
            }
        }
        for (String planetId : toRemove) {
            planetOrbitalWeapons.remove(planetId);
        }
        // Remove sectors that are no longer active
        toRemove.clear();
        if (saves.containsKey(null)) {
            for (String sectorId : sectorOrbitalWeapons.keySet()) {
                if (!saves.get(null).contains(Integer.parseInt(sectorId))) {
                    Log.info("Removing invalid non-planet sector " + sectorId + " from orbital data.");
                    toRemove.add(sectorId);
                }
                LinkedList<String> orbitalWeapons = getOrbitalWeaponsOfSector(sectorId);
                if ((orbitalWeapons != null)) {
                    correctOrbitalDataWeapons(orbitalWeapons);
                }
            }
        } else {
            for (String sectorId : sectorOrbitalWeapons.keySet()) {
                Log.info("Removing invalid non-planet sector " + sectorId + " from orbital data.");
                toRemove.add(sectorId);
            }
        }
        for (String sectorId : toRemove) {
            sectorOrbitalWeapons.remove(sectorId);
        }
    }

    /**
     * Removes any invalid weapons from the given list of weapons.
     * <p>
     * A weapon is considered invalid if it is not present in the {@link AirstrikeWeapons} fields.
     * <p>
     * This method is idempotent and does not modify the list if it does not contain any invalid weapons.
     *
     * @param weapons the list of weapons to clean up
     */
    public static void correctOrbitalDataWeapons(LinkedList<String> weapons) {
        LinkedList<String> toRemove = new LinkedList<>();
        for (String weaponId : weapons) {
            if (AirstrikeWeapons.get(weaponId) == null) {
                Log.info("Removing invalid weapon " + weaponId + " from orbital data.");
                toRemove.add(weaponId);
            }
        }
        for (String itemId : toRemove) {
            weapons.remove(itemId);
        }
    }

    /**
     * Saves the current orbital data to the data file.
     * <p>
     * This method will overwrite any existing data in the file.
     * <p>
     * The data is saved in a JSON format, with two main objects: "planets" and "sectors".
     * The "planets" object has each planet as a key and its associated items as a value.
     * The "sectors" object has each sector as a key and its associated items as a value.
     * Each item is represented as a key-value pair, with the item ID as the key and the count as the value.
     * <p>
     * This method ensures that the data directory exists before attempting to save the data using {@link AirstrilkeUtils#ensureDataDirectoryExists()}.
     * It also calls {@link #correctOrbitalData()} to remove any invalid weapons from the data before saving.
     */
    public static void saveOrbitalData() {
        AirstrilkeUtils.ensureDataDirectoryExists();

        correctOrbitalData();

        try {
            // Save the data to the file
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Saving orbital data to: @", file.absolutePath());

            // Create a StringBuilder to construct the JSON string
            StringBuilder jsonBuilder = new StringBuilder("{");

            jsonBuilder.append("\"planets\":{");
            // Iterate over each planet and its associated items
            for (Map.Entry<String, LinkedList<String>> entry : planetOrbitalWeapons.entrySet()) {
                String planetName = entry.getKey();
                LinkedList<String> items = entry.getValue();

                // Append the planet ID and its items to the JSON string
                jsonBuilder.append("\"").append(planetName).append("\":[");
                for (String item : items) {
                    jsonBuilder.append("\"").append(item).append("\",");
                }
                // Remove the trailing comma and close the planet's JSON object
                if (!items.isEmpty()) {
                    jsonBuilder.setLength(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("],");
            }
            // Remove the trailing comma and close the main JSON object
            if (!planetOrbitalWeapons.isEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("}");

            jsonBuilder.append(",\"sectors\":{");
            // Iterate over each sector and its associated items
            for (Map.Entry<String, LinkedList<String>> entry : sectorOrbitalWeapons.entrySet()) {
                String sectorId = entry.getKey();
                LinkedList<String> items = entry.getValue();

                // Append the sector ID and its items to the JSON string
                jsonBuilder.append("\"").append(sectorId).append("\":[");
                for (String item : items) {
                    jsonBuilder.append("\"").append(item).append("\",");
                }
                if (!items.isEmpty()) {
                    jsonBuilder.setLength(jsonBuilder.length() - 1);
                }
                jsonBuilder.append("],");
            }
            // Remove the trailing comma and close the main JSON object
            if (!sectorOrbitalWeapons.isEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
            }
            jsonBuilder.append("}");

            jsonBuilder.append("}");

            // Write the constructed JSON string to the file
            file.writeString(jsonBuilder.toString());

            Log.info("orbital data saved successfully.");
        } catch (Exception e) {
            Log.err("Failed to save orbital data.", e);
        }
    }

    /**
     * Loads the orbital data from a JSON file and initializes the planet and sector orbital data.
     * <p>
     * This method attempts to locate the orbital data file in the specified directory. If the file exists,
     * it reads the JSON content and deserializes it into HashMaps representing the orbital data for planets
     * and sectors. If the file does not exist or an error occurs during loading, empty data structures are
     * initialized instead.
     * <p>
     * After loading the data, it invokes {@link #correctOrbitalData()} to ensure the data is up-to-date
     * with the current game state.
     * <p>
     * Logs informative messages regarding the loading process and any errors encountered.
     */
    public static void loadOrbitalData() {
        try {
            // Locate the file
            Fi file = Vars.dataDirectory.child(dataFilePath);
            Log.info("Looking for orbital data at: @", file.absolutePath());

            if (file.exists()) {
                // Create a JSON deserializer
                Json json = new Json();
                json.setTypeName(null); // Match what we saved

                // Read the JSON data from the file
                String data = file.readString();
                JsonValue jsonData = json.fromJson(null, data);

                // Deserialize the JSON into the HashMaps
                planetOrbitalWeapons = new HashMap<>();
                sectorOrbitalWeapons = new HashMap<>();
                for (JsonValue typeValue : jsonData) {
                    String type = typeValue.name;
                    if (type.equals("planets")) {
                        Log.info("Loading planet orbital data...");
                        for (JsonValue planetValue : typeValue) {
                            String planetName = planetValue.name;
                            LinkedList<String> orbitalWeapons = new LinkedList<>();
                            for (JsonValue itemValue : planetValue) {
                                String itemId = itemValue.toString();
                                orbitalWeapons.add(itemId);
                            }
                            planetOrbitalWeapons.put(planetName, orbitalWeapons);
                        }
                    } else if (type.equals("sectors")) {
                        Log.info("Loading sector orbital data...");
                        for (JsonValue sectorValue : typeValue) {
                            String sectorId = sectorValue.name;
                            LinkedList<String> orbitalWeapons = new LinkedList<>();
                            for (JsonValue itemValue : sectorValue) {
                                String itemId = itemValue.toString();
                                orbitalWeapons.add(itemId);
                            }
                            sectorOrbitalWeapons.put(sectorId, orbitalWeapons);
                        }
                    } else {
                        Log.err("Unknown type " + type + " in orbital data.");
                    }
                }

                Log.info("orbital data loaded successfully.");
                correctOrbitalData();
            } else {
                Log.info("No orbital data found. Initialising empty.");
                correctOrbitalData();
            }
        } catch (Exception e) {
            Log.err("Failed to load orbital data, initialising empty.", e);
            correctOrbitalData();
        }
    }
}
