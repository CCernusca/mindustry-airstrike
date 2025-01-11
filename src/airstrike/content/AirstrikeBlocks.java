package airstrike.content;

import airstrike.blocks.Beacon;
import airstrike.blocks.Launcher;
import airstrike.blocks.OrbitalController;
import airstrike.blocks.OrbitalMonitor;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class AirstrikeBlocks {
    public static Block launcher;
    public static Block orbitalMonitor;
    public static Block beacon;
    public static Block orbitalController;

    public static void load() {
        launcher = new Launcher("launcher") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 1));
            size = 5;
            launchTime = 60f * 20;
            hasPower = true;
            consumePower(100f);
            alwaysUnlocked = true;
            launchSound = Sounds.missileLaunch;
        }};

        orbitalMonitor = new OrbitalMonitor("orbital-monitor") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 1));
            size = 2;
            alwaysUnlocked = true;
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 1));
            size = 1;
            alwaysUnlocked = true;
            impactDelay = 1f;
        }};

        orbitalController = new OrbitalController("orbital-controller") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 1));
            size = 2;
            alwaysUnlocked = true;
            impactDelay = 1f;
        }};
    }
}
