package airstrike.blocks;

import airstrike.OrbitalData;
import airstrike.items.AirstrikeWeapon;
import airstrike.items.SatelliteItem;
import arc.*;
import arc.graphics.Color;
import arc.math.*;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.util.Log;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.Vars;

import mindustry.world.meta.StatUnit;
import mindustry.world.modules.ItemModule;

import java.util.LinkedList;

public class Launcher extends LaunchPad {

    public Launcher(String name) {
        super(name);
        this.configurable = false;
        this.itemCapacity = 9999;
        this.acceptsItems = true;
        this.hasPower = true;
        this.consumesPower = true;
    }

    @Override
    public void setBars() {
        super.setBars();

        removeBar("items");

        addBar("satellite", entity -> new Bar(
                () -> (((LauncherBuild) entity).satellite == null ? "No Satellite" : ((LauncherBuild) entity).satellite.localizedName),
                () -> Pal.items,
                () -> (float) (((LauncherBuild) entity).satellite == null ? 0 : 1)
        ));

        addBar("weapons", entity -> new Bar(
                () -> "Weapons: " + ((LauncherBuild) entity).weapons.size(),
                () -> Pal.items,
                () -> (float) ((LauncherBuild) entity).weapons.size() > 0 ? 1 : 0
        ));

        addBar("progress", (LauncherBuild build) -> new Bar(
                () -> Core.bundle.get("bar.launchcooldown"),
                () -> Pal.ammo,
                () -> Mathf.clamp(build.launchCounter / launchTime)
        ));

        addBar("volume", entity -> new Bar(
                () -> "Volume: " + ((LauncherBuild) entity).usedVolume(),
                () -> Pal.items,
                () -> (((LauncherBuild) entity).satellite != null ? ((LauncherBuild) entity).usedVolume() / ((LauncherBuild) entity).satellite.volume : 0)
        ));
    }

    public class LauncherBuild extends LaunchPadBuild {
        public SatelliteItem satellite;
        public LinkedList<AirstrikeWeapon> weapons = new LinkedList<>();

        @Override
        public void updateTile() {

            // Update custom item managers
            weapons.clear();
            if (items != null) {
                for (Item item : Vars.content.items()) {
                    // Iterate over all weapon items
                    if (item instanceof AirstrikeWeapon) {
                        AirstrikeWeapon weapon = (AirstrikeWeapon) item;
                        int amount = items.get(item);
                        for (int i = 0; i < amount; i++) {
                            weapons.add(weapon);
                        }
                    }
                    // Iterate over all satellite items
                    if (item instanceof SatelliteItem) {
                        int amount = items.get(item);
                        if (amount > 0) {
                            satellite = (SatelliteItem) item;
                        }
                        if (amount == 0 && satellite == item) {
                            satellite = null;
                        }
                    }
                }
            }

            // Increment launchCounter and launch when contents are ready and power is available
            if ((launchCounter += edelta()) >= launchTime && satellite != null) {
                // Check if there's enough power to launch
                if (power.status >= 1f) {
                    // Add weapons to orbital data
                    for (AirstrikeWeapon weapon : weapons) {
                        OrbitalData.addOrbitalWeapon(weapon);
                    }
                    // Consume weapons & satellite
                    weapons.clear();
                    satellite = null;
                    // Consume items & power
                    consume();
                    launchSound.at(x, y);
                    Fx.launchPod.at(this);
                    Effect.shake(3f, 3f, this);

                    items.clear();

                    // TODO: Create custom entity for launches
                    // (Stop using the LaunchPad entity)
                    LaunchPayload entity = LaunchPayload.create();
                    entity.set(this);
                    entity.lifetime(120f);
                    entity.team(team);
                    entity.add();

                    launchCounter = 0f;
                }
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            // Override without calling super to disable the destination selection UI
            deselect();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            // Return false to prevent configuration interface
            return false;
        }

        @Override
        public void display(Table table) {
            // display implementation from Building (superclass of Launchpad), to avoid displaying destination

            table.table((t) -> {
                t.left();
                t.add(new Image(this.block.getDisplayIcon(this.tile))).size(32.0F);
                t.labelWrap(this.block.getDisplayName(this.tile)).left().width(190.0F).padLeft(5.0F);
            }).growX().left();
            table.row();
            if (this.team == Vars.player.team()) {
                table.table((bars) -> {
                    bars.defaults().growX().height(18.0F).pad(4.0F);
                    this.displayBars(bars);
                }).growX();
                table.row();
                table.table(this::displayConsumption).growX();
                boolean displayFlow = (this.block.category == Category.distribution || this.block.category == Category.liquid) && this.block.displayFlow;
                if (displayFlow) {
                    String ps = " " + StatUnit.perSecond.localized();
                    ItemModule flowItems = this.flowItems();
                    if (flowItems != null) {
                        table.row();
                        table.left();
                        table.table((l) -> {
                            Bits current = new Bits();
                            Runnable rebuild = () -> {
                                l.clearChildren();
                                l.left();

                                for(Item item : Vars.content.items()) {
                                    if (flowItems.hasFlowItem(item)) {
                                        l.image(item.uiIcon).scaling(Scaling.fit).padRight(3.0F);
                                        l.label(() -> flowItems.getFlowRate(item) < 0.0F ? "..." : Strings.fixed(flowItems.getFlowRate(item), 1) + ps).color(Color.lightGray);
                                        l.row();
                                    }
                                }

                            };
                            rebuild.run();
                            l.update(() -> {
                                for(Item item : Vars.content.items()) {
                                    if (flowItems.hasFlowItem(item) && !current.get(item.id)) {
                                        current.set(item.id);
                                        rebuild.run();
                                    }
                                }

                            });
                        }).left();
                    }

                    if (this.liquids != null) {
                        table.row();
                        table.left();
                        table.table((l) -> {
                            Bits current = new Bits();
                            Runnable rebuild = () -> {
                                l.clearChildren();
                                l.left();

                                for(Liquid liquid : Vars.content.liquids()) {
                                    if (this.liquids.hasFlowLiquid(liquid)) {
                                        l.image(liquid.uiIcon).scaling(Scaling.fit).size(32.0F).padRight(3.0F);
                                        l.label(() -> this.liquids.getFlowRate(liquid) < 0.0F ? "..." : Strings.fixed(this.liquids.getFlowRate(liquid), 1) + ps).color(Color.lightGray);
                                        l.row();
                                    }
                                }

                            };
                            rebuild.run();
                            l.update(() -> {
                                for(Liquid liquid : Vars.content.liquids()) {
                                    if (this.liquids.hasFlowLiquid(liquid) && !current.get(liquid.id)) {
                                        current.set(liquid.id);
                                        rebuild.run();
                                    }
                                }

                            });
                        }).left();
                    }
                }

                if (Vars.net.active() && this.lastAccessed != null) {
                    table.row();
                    table.add(Core.bundle.format("lastaccessed", new Object[]{this.lastAccessed})).growX().wrap().left();
                }

                table.marginBottom(-5.0F);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (item instanceof AirstrikeWeapon) {
                // Check satellite and available volume
                if (satellite != null && usedVolume() + ((AirstrikeWeapon) item).volume <= satellite.volume) {
                    return super.acceptItem(source, item);
                }
            } else if (item instanceof SatelliteItem) {
                // Allow one satellite
                return super.acceptItem(source, item) && satellite == null;
            }
            return false;
        }

        // IMPORTANT: Returns total amount of item which can be in inventory, not amount which can be added at once
        @Override
        public int getMaximumAccepted(Item item) {
            if (item instanceof AirstrikeWeapon) {
                // Allow weaponCapacity weapons
                return findWeaponMax((AirstrikeWeapon) item);
            }
            if (item instanceof SatelliteItem) {
                // Allow exactly one
                return 1;
            }
            return 0;
        }

        /**
         * Calculates the total volume of all weapons in the inventory, excluding the given weapon (if any).
         * @param exclude the weapon to exclude from the calculation
         * @return the total volume of all weapons in the inventory, excluding the given weapon (if any)
         */
        public float usedVolume(AirstrikeWeapon exclude) {
            float volume = 0;
            for (int i = 0; i < weapons.size(); i++) {
                AirstrikeWeapon weapon = weapons.get(i);
                Log.info(weapon.name + " " + weapon.volume);
                if (!weapon.equals(exclude)) {
                    volume += weapon.volume;
                }
            }
            return volume;
        }

        /**
         * Returns the total volume of all weapons in the inventory.
         * This method includes all weapons without excluding any specific one.
         *
         * @return the total volume of all weapons in the inventory
         */
        public float usedVolume() {
            return usedVolume(null);
        }

        /**
         * Calculates the maximum amount of a given weapon which can be stored in the launcher's inventory.
         * <p>
         * This method takes into account the total volume of all other weapons in the inventory, excluding the given weapon,
         * and returns the maximum amount of the given weapon which can fit into the remaining available volume.
         * <p>
         * This method returns the maximum amount of weapons which can be stored, not the amount which can be added at once.
         *
         * @param weapon the weapon for which to calculate the maximum storage amount
         * @return the maximum amount of the given weapon which can be stored in the launcher's inventory
         */
        public int findWeaponMax(AirstrikeWeapon weapon) {
            float volumeOfOtherWeapons = usedVolume(weapon);
            return Mathf.floor((satellite.volume - volumeOfOtherWeapons) / weapon.volume);
        }
    }
}
