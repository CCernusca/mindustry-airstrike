package airstrike.world.blocks;

import arc.*;
import arc.math.*;
import arc.util.Log;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.*;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.content.Planets;
import mindustry.Vars;
import mindustry.type.Planet;
import mindustry.type.Sector;
import airstrike.AirstrikeMod;

import java.util.HashMap;

public class Launcher extends LaunchPad {

    public Launcher(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("items", entity -> new Bar(
                () -> Core.bundle.format("bar.items", entity.items.total()),
                () -> Pal.items,
                () -> (float) entity.items.total() / itemCapacity
        ));

        addBar("progress", (LauncherBuild build) -> new Bar(
                () -> Core.bundle.get("bar.launchcooldown"),
                () -> Pal.ammo,
                () -> Mathf.clamp(build.launchCounter / launchTime)
        ));
    }

    public class LauncherBuild extends LaunchPadBuild {

        @Override
        public void updateTile() {
            // Increment launchCounter and "launch" items when full
            if ((launchCounter += edelta()) >= launchTime && items.total() > 0) {

                // TODO: Replace items with actual satellites

                // Get current planet, if possible
                Planet currentPlanet = AirstrikeMod.getCurrentPlanet();
                // If on planet, update its satellites with item
                if (currentPlanet != null) {
                    HashMap<Integer, Integer> currentPlanetSatellites = AirstrikeMod.getSatellites(currentPlanet);
                    Item item = items.first();
                    int satelliteCount = 0;
                    if (currentPlanetSatellites != null && currentPlanetSatellites.containsKey((int) item.id)) {
                        satelliteCount = currentPlanetSatellites.get((int) item.id);
                    }
                    currentPlanetSatellites.put((int) item.id, satelliteCount + items.get(item));
                }

                // Debug: Show all satellites
                Log.info(AirstrikeMod.planetSatellites);

                consume(); // Consume resources
                launchSound.at(x, y);
                Fx.launchPod.at(this);
                Effect.shake(3f, 3f, this);

                // TODO: Create custom entity for launches
                // (Stop using the LaunchPad entity)
                LaunchPayload entity = LaunchPayload.create();
                entity.set(this);
                entity.lifetime(120f);
                entity.team(team);
                entity.add();

                // Clear items without transferring them
                items.clear();

                launchCounter = 0f;
            }
        }
    }
}
