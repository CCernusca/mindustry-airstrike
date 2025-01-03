package airstrike.content;

import airstrike.world.blocks.Launcher;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class AirstrikeBlocks {
    public static Block launcher;

    public static void load() {
        launcher = new Launcher("launcher") {{
            requirements(Category.effect, BuildVisibility.shown, with(Items.copper, 1));
            size = 5;
            itemCapacity = 1;
            launchTime = 60f * 20;
            hasPower = true;
            consumePower(1f);
        }};
    }
}