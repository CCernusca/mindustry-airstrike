package airstrike;

import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.type.Planet;
import mindustry.world.Tile;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class AirstrikeUtils {

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
     * Returns the name of the current planet if the player is on a planet, or the id of the current sector if not.
     * <p>
     * This method calls {@link #getCurrentPlanet()} and {@link #getCurrentSectorId()} to determine the current location.
     * If the player is on a planet, it returns the name of the planet. Otherwise, it returns the id of the sector.
     * <p>
     * This method is useful for logging purposes, as it provides a human-readable name for the current location.
     * @return the name of the current planet or sector
     */
    public static String getLocation() {
        Planet currentPlanet = getCurrentPlanet();
        if (currentPlanet != null) {
            return currentPlanet.name;
        } else {
            return getCurrentSectorId();
        }
    }
    
    /**
     * Creates an explosion centered at the specified tile, dealing damage to units and buildings and applying knockback to units.
     * <p>
     * This method first applies damage to units within the explosion radius.
     * Then, it applies knockback to units within the explosion radius.
     * Finally, it applies damage to buildings within the explosion radius.
     * <p>
     * The damage and knockback amounts are specified in terms of the tile radius, so the actual damage and knockback amounts applied will be scaled by the tile size.
     * The shake intensity and duration are used to create a screen shake effect.
     * <p>
     * This method is used by the nuke airstrike weapon to create an explosion on impact.
     * @param tile the tile to center the explosion at
     * @param radius the radius of the explosion in tiles
     * @param damage the amount of damage to deal to units
     * @param knockback the amount of knockback to apply to units
     * @param shakeIntensity the intensity of the screen shake effect
     * @param shakeDuration the duration of the screen shake effect
     */
    public static void explosion(Tile tile, float radius, float damage, float knockback, float shakeIntensity, float shakeDuration) {
        // Get tile position
        int tilex = tile.x;
        int tiley = tile.y;
        // Get world position
        float worldx = tile.worldx();
        float worldy = tile.worldy();

        // Apply damage to units (works in world space)
        Damage.damage(null, worldx, worldy, radius * Vars.tilesize, damage, false, true, true, true, null);

        // Apply knockback to units within the explosion radius (works in world space)
        Units.nearby(null, worldx, worldy, radius * Vars.tilesize, unit -> {
            if (unit != null && unit.team() != null) {
                // Calculate direction vector from unit to explosion center
                float dx = unit.x() - worldx;
                float dy = unit.y() - worldy;
                float distance = Mathf.dst(worldx, worldy, unit.x(), unit.y());
                if (distance < radius * Vars.tilesize) {
                    // Normalize direction vector
                    dx /= distance;
                    dy /= distance;
                    // Calculate knockback velocity based on distance and control variable
                    float knockbackVelocity = knockback * (1 - distance / (radius * Vars.tilesize));
                    // Adjust knockback based on unit size/mass (mass is approximated via hitbox width)
                    float unitMass = unit.type().hitSize;
                    knockbackVelocity /= unitMass;
                    unit.vel().add(dx * knockbackVelocity, dy * knockbackVelocity);
                }
            }
        });

        // Apply damage to buildings (works in tile space)
        Damage.tileDamage(null, tilex, tiley, radius, damage, null);
    }
}
