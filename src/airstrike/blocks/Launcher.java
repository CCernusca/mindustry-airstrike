package airstrike.blocks;

import arc.*;
import arc.math.*;
import arc.util.Log;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.SaveIO;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.*;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.content.Planets;
import mindustry.Vars;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.game.Saves;

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

                Log.info(AirstrikeMod.getCurrentSectorId());

                // Get current planet, if possible
                Planet currentPlanet = AirstrikeMod.getCurrentPlanet();
                HashMap<String, Integer> currentSatellites;
                if (currentPlanet != null) {
                    // If on planet, update its satellites with item
                    currentSatellites = AirstrikeMod.getSatellitesPlanet(String.valueOf(currentPlanet.id));
                    Item item = items.first();
                    int satelliteCount = 0;
                    if (currentSatellites != null && currentSatellites.containsKey(String.valueOf(item.id))) {
                        satelliteCount = currentSatellites.get(String.valueOf(item.id));
                    }
                    currentSatellites.put(String.valueOf(item.id), satelliteCount + items.get(item));
                } else {
                    // If not on planet, update sector satellites with item
                    String currentSectorId = AirstrikeMod.getCurrentSectorId();
                    currentSatellites = AirstrikeMod.getSatellitesSector(currentSectorId);
                    Item item = items.first();
                    int satelliteCount = 0;
                    if (currentSatellites != null && currentSatellites.containsKey(String.valueOf(item.id))) {
                        satelliteCount = currentSatellites.get(String.valueOf(item.id));
                    }
                    currentSatellites.put(String.valueOf(item.id), satelliteCount + items.get(item));
                }

                // Debug: Show all satellites in orbit of this planet/sector
                Log.info(currentSatellites);

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
