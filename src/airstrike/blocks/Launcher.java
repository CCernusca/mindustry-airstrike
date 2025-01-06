package airstrike.blocks;

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
import mindustry.io.SaveIO;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.content.Planets;
import mindustry.Vars;
import mindustry.game.Saves;

import airstrike.AirstrikeMod;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.ItemModule;

import java.util.HashMap;

public class Launcher extends LaunchPad {

    public Launcher(String name) {
        super(name);
        this.configurable = false;
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
    }
}
