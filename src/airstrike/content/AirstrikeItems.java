package airstrike.content;

import airstrike.AirstrikeUtils;
import airstrike.items.AirstrikeWeapon;
import airstrike.items.SatelliteItem;
import airstrike.meta.AirstrikeStat;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.world.Tile;

import java.lang.reflect.Field;

public class AirstrikeItems {
    public static SatelliteItem smallSatellite;
    public static SatelliteItem mediumSatellite;
    public static SatelliteItem largeSatellite;
    public static AirstrikeWeapon nuke;
    public static AirstrikeWeapon precisionBomb;

    public static void load() {

        smallSatellite = new SatelliteItem("small-satellite") {{
            alwaysUnlocked = true;
            volume = 5f;
            color = AirstrikePal.satelliteGrey;
        }};

        mediumSatellite = new SatelliteItem("medium-satellite") {{
            alwaysUnlocked = true;
            volume = 10f;
            color = AirstrikePal.satelliteGrey;
        }};

        largeSatellite = new SatelliteItem("large-satellite") {{
            alwaysUnlocked = true;
            volume = 20f;
            color = AirstrikePal.satelliteGrey;
        }};

        nuke = new AirstrikeWeapon("nuke") {
            // Radius in tiles
            final float explosionRadius = 40f;
            final float explosionDamage = 10000f;
            final float knockbackStrength = 100f;
            final float shakeIntensity = 500f;
            final float shakeDuration = 50f;

            @Override
            public void onImpact(Tile impactTile) {

                // Get tile position
                int tilex = impactTile.x;
                int tiley = impactTile.y;
                // Get world position
                float worldx = impactTile.worldx();
                float worldy = impactTile.worldy();

                // Create visual effects
                Effect.shake(shakeIntensity, shakeDuration, worldx, worldy);

                AirstrikeFx.nukeExplosion(explosionRadius, explosionRadius * Vars.tilesize).at(worldx, worldy);

                AirstrikeUtils.explosion(impactTile, explosionRadius, explosionDamage, knockbackStrength, shakeIntensity, shakeDuration);
            }

            @Override
            public void setStats() {
                super.setStats();
                stats.add(AirstrikeStat.explosionRadius, explosionRadius);
                stats.add(AirstrikeStat.explosionDamage, explosionDamage);
                stats.add(AirstrikeStat.knockbackStrength, knockbackStrength);
            }
        };
        nuke.volume = 10f;
        nuke.color = AirstrikePal.nukeGrey;
        nuke.alwaysUnlocked = true;
        nuke.explosiveness = 5f;
        nuke.radioactivity = 0.5f;

        precisionBomb = new AirstrikeWeapon("precision-bomb") {
            // Radius in tiles
            final float explosionRadius = 2f;
            final float explosionDamage = 1000f;
            final float knockbackStrength = 1f;
            final float shakeIntensity = 5f;
            final float shakeDuration = 10f;

            @Override
            public void onImpact(Tile impactTile) {

                // Get tile position
                int tilex = impactTile.x;
                int tiley = impactTile.y;
                // Get world position
                float worldx = impactTile.worldx();
                float worldy = impactTile.worldy();

                // Create visual effects
                Effect.shake(shakeIntensity, shakeDuration, worldx, worldy);

                AirstrikeFx.nukeExplosion(explosionRadius, explosionRadius * Vars.tilesize).at(worldx, worldy);

                AirstrikeUtils.explosion(impactTile, explosionRadius, explosionDamage, knockbackStrength, shakeIntensity, shakeDuration);
            }

            @Override
            public void setStats() {
                super.setStats();
                stats.add(AirstrikeStat.explosionRadius, explosionRadius);
                stats.add(AirstrikeStat.explosionDamage, explosionDamage);
                stats.add(AirstrikeStat.knockbackStrength, knockbackStrength);
            }
        };
        precisionBomb.volume = 1.5f;
        nuke.color = AirstrikePal.precisionBombGrey;
        precisionBomb.alwaysUnlocked = true;
        precisionBomb.explosiveness = 0.5f;

    };

    public static AirstrikeWeapon getWeapon(String name) {
        for (Field field : AirstrikeItems.class.getDeclaredFields()) {
            if (field.getType().equals(AirstrikeWeapon.class)) {
                field.setAccessible(true);
                try {
                    AirstrikeWeapon weapon = (AirstrikeWeapon) field.get(null);
                    if (weapon.name.equals(name)) {
                        return weapon;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
