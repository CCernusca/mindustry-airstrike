package airstrike.blocks;

import airstrike.content.AirstrikeWeapons;
import airstrike.items.SatelliteItem;
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

                AirstrikeMod.addWeapon(((SatelliteItem) items.first()).getWeapon(), 1);

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

        // Only accepts SatelliteItems
        @Override
        public boolean acceptItem(Building source, Item item) {
            return super.acceptItem(source, item) && item instanceof SatelliteItem;
        }
    }
}
