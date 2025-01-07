package airstrike.content;

import airstrike.airstrikeweapons.AirstrikeWeapon;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.content.Fx;

import java.lang.reflect.Field;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class AirstrikeWeapons {
    public static AirstrikeWeapon nuke;

    public static void load() {

        nuke = new AirstrikeWeapon("airstrikeweapon-nuke", "Nuke") {
            @Override
            public void onImpact(Tile impactTile) {
                // Radius in tiles
                float explosionRadius = 40f;
                float explosionDamage = 10000f;
                float knockbackStrength = 100f;
                float shakeIntensity = 500f;
                float shakeDuration = 50f;

                // Get tile position
                int tilex = impactTile.x;
                int tiley = impactTile.y;
                // Get world position
                float worldx = impactTile.worldx();
                float worldy = impactTile.worldy();

                // Create visual effects
                Effect.shake(shakeIntensity, shakeDuration, worldx, worldy);

                AirstrikeFx.nukeExplosion(explosionRadius, explosionRadius * Vars.tilesize).at(worldx, worldy);

                // Apply damage to units (works in world space)
                Damage.damage(null, worldx, worldy, explosionRadius * Vars.tilesize, explosionDamage, false, true, true, true, null);

                // Apply knockback to units within the explosion radius (works in world space)
                Units.nearby(null, worldx, worldy, explosionRadius * Vars.tilesize, unit -> {
                    if (unit != null && unit.team() != null) {
                        // Calculate direction vector from unit to explosion center
                        float dx = unit.x() - worldx;
                        float dy = unit.y() - worldy;
                        float distance = Mathf.dst(worldx, worldy, unit.x(), unit.y());
                        if (distance < explosionRadius * Vars.tilesize) {
                            // Normalize direction vector
                            dx /= distance;
                            dy /= distance;
                            // Calculate knockback velocity based on distance and control variable
                            float knockbackVelocity = knockbackStrength * (1 - distance / (explosionRadius * Vars.tilesize));
                            // Adjust knockback based on unit size/mass (mass is approximated via hitbox width)
                            float unitMass = unit.type().hitSize;
                            knockbackVelocity /= unitMass;
                            unit.vel().add(dx * knockbackVelocity, dy * knockbackVelocity);
                        }
                    }
                });

                // Apply damage to buildings (works in tile space)
                Damage.tileDamage(null, tilex, tiley, explosionRadius, explosionDamage, null);
            }
        };
    }

    public static AirstrikeWeapon get(String id) {
        for (Field field : AirstrikeWeapons.class.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (type.equals(AirstrikeWeapon.class)) {
                field.setAccessible(true);
                try {
                    AirstrikeWeapon weapon = (AirstrikeWeapon) field.get(null);
                    if (weapon.id.equals(id)) {
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
