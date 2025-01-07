package airstrike.airstrikeweapons;

import arc.util.Timer;
import mindustry.world.Tile;

public abstract class AirstrikeWeapon {
    public String id;
    public String name;
    // Method for weapons to implement defining what happens on impact
    public abstract void onImpact(Tile impactTile);

    public AirstrikeWeapon(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Calls onImpact at impactTile after impactDelay
    public void impact(Tile impactTile, float impactDelay) {
        // Schedule the impact after the delay
        Timer.schedule(() -> {
            // Ensure thread safety
            synchronized (this) {
                this.onImpact(impactTile);
            }
        }, impactDelay);
    }

}
