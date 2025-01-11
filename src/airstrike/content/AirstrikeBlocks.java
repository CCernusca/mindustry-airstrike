package airstrike.content;

import airstrike.blocks.Beacon;
import airstrike.blocks.Launcher;
import airstrike.blocks.OrbitalController;
import airstrike.blocks.OrbitalMonitor;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class AirstrikeBlocks {
    public static Block launcher;
    public static Block orbitalMonitor;
    public static Block beacon;
    public static Block orbitalController;
    public static Block smallSatelliteAssembler;
    public static Block mediumSatelliteAssembler;
    public static Block largeSatelliteAssembler;
    public static Block nukeAssembler;
    public static Block precisionBombAssembler;

    public static void load() {

        // TODO: Balancing

        launcher = new Launcher("launcher") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.silicon, 100, Items.titanium, 50, Items.copper, 70, Items.lead, 30));
            size = 5;
            launchTime = 60f * 20;
            hasPower = true;
            consumePower(100f);
            launchSound = Sounds.missileLaunch;

            alwaysUnlocked = true;
        }};

        orbitalMonitor = new OrbitalMonitor("orbital-monitor") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.silicon, 30, Items.titanium, 10, Items.copper, 20, Items.lead, 10, Items.metaglass, 20));
            size = 2;

            alwaysUnlocked = true;
        }};

        beacon = new Beacon("beacon") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.silicon, 10, Items.copper, 20, Items.lead, 5, Items.surgeAlloy, 1));
            size = 1;

            impactDelay = 1f;
            alwaysUnlocked = true;
        }};

        orbitalController = new OrbitalController("orbital-controller") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.silicon, 50, Items.titanium, 30, Items.surgeAlloy, 5, Items.copper, 30, Items.lead, 10, Items.metaglass, 10));
            size = 2;
            impactDelay = 1f;

            alwaysUnlocked = true;
        }};

        smallSatelliteAssembler = new GenericCrafter("small-satellite-assembler") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 20, Items.lead, 10, Items.silicon, 30, Items.titanium, 10));
            size = 2;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.1f;
            craftTime = 600f;
            itemCapacity = 30;

            consumeItems(with(Items.silicon, 10, Items.lead, 30, Items.copper, 15));
            consumePower(1f);
            outputItems = with(AirstrikeItems.smallSatellite, 1);

            alwaysUnlocked = true;
        }};

        mediumSatelliteAssembler = new GenericCrafter("medium-satellite-assembler") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.titanium, 50, Items.plastanium, 10, Items.silicon, 50, Items.copper, 5));
            size = 2;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.1f;
            craftTime = 1200f;
            itemCapacity = 50;

            consumeItems(with(AirstrikeItems.smallSatellite, 1, Items.silicon, 50, Items.copper, 5, Items.titanium, 50, Items.plastanium, 5));
            consumePower(5f);
            outputItems = with(AirstrikeItems.mediumSatellite, 1);

            alwaysUnlocked = true;
        }};


        largeSatelliteAssembler = new GenericCrafter("large-satellite-assembler") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.titanium, 100, Items.plastanium, 50, Items.surgeAlloy, 10, Items.silicon, 50, Items.copper, 30));
            size = 2;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.1f;
            craftTime = 3600f;
            itemCapacity = 100;

            consumeItems(with(AirstrikeItems.mediumSatellite, 1, Items.titanium, 100, Items.plastanium, 10, Items.thorium, 20, Items.surgeAlloy, 5));
            consumePower(10f);
            consumeLiquid(Liquids.oil, 0.5f);
            outputItems = with(AirstrikeItems.largeSatellite, 1);

            alwaysUnlocked = true;
        }};

        nukeAssembler = new GenericCrafter("nuke-assembler") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 25, Items.lead, 20, Items.silicon, 50, Items.titanium, 30));
            size = 1;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.1f;
            craftTime = 1200f;
            itemCapacity = 70;

            consumeItems(with(Items.thorium, 70, Items.lead, 30, Items.blastCompound, 50, Items.phaseFabric, 10));
            consumePower(10f);
            consumeLiquid(Liquids.cryofluid, 0.1f);
            outputItems = with(AirstrikeItems.nuke, 1);

            alwaysUnlocked = true;
        }};

        precisionBombAssembler = new GenericCrafter("precision-bomb-assembler") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 25, Items.lead, 20, Items.silicon, 30, Items.titanium, 20));
            size = 1;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.1f;
            craftTime = 120f;
            itemCapacity = 30;

            consumeItems(with(Items.titanium, 10, Items.blastCompound, 30));
            consumePower(5f);
            outputItems = with(AirstrikeItems.precisionBomb, 1);

            alwaysUnlocked = true;
        }};
    }
}
