package airstrike.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class AirstrikeStat {

    public static final Stat

            volume = new Stat("volume"),
            impactDelay = new Stat("impact-delay", StatCat.function),
            explosionRadius = new Stat("explosion-radius", StatCat.function),
            explosionDamage = new Stat("explosion-damage", StatCat.function),
            knockbackStrength = new Stat("knockback-strength", StatCat.function)

    ;
}
