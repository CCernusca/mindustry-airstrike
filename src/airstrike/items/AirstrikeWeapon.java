package airstrike.items;

import arc.util.Timer;
import mindustry.type.Item;
import mindustry.world.Tile;

public abstract class AirstrikeWeapon extends Item {
    public float volume;
    // Method for weapons to implement defining what happens on impact
    public abstract void onImpact(Tile impactTile);

    public AirstrikeWeapon(String name) {
        super(name);
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
