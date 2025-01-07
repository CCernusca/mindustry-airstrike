package airstrike.blocks;

import airstrike.OrbitalData;
import airstrike.airstrikeweapons.AirstrikeWeapon;
import airstrike.content.AirstrikeWeapons;
import arc.scene.ui.Button;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.util.LinkedList;

public class OrbitalMonitor extends Block {

    public OrbitalMonitor(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = false;
        configurable = true;
        buildType = OrbitalMonitorBuild::new; // Set buildType to use OrbitalMonitorBuild
    }

    public class OrbitalMonitorBuild extends Building {
        @Override
        public void buildConfiguration(Table table) {
            // Create a Label & Background for the title
            Table titleBackground = new Table();
            titleBackground.background(Styles.black6);
            Label titleLabel = new Label("Available Weapons", Styles.defaultLabel);
            titleBackground.add(titleLabel);
            table.add(titleBackground).size(200f, 50f).row();

            // Create a table to hold the weapon names
            Table weaponsTable = new Table();
            weaponsTable.center().top();
            weaponsTable.background(Styles.black6);

            // Retrieve the available airstrike weapons
            LinkedList<String> weapons = OrbitalData.getOrbitalWeapons();
            // Iterate through the weapons and add them to the weaponsTable
            for (String weaponId : weapons) {
                AirstrikeWeapon weapon = AirstrikeWeapons.get(weaponId);
                if (weapon == null) {
                    Log.err("Invalid weapon");
                    continue;
                }
                String weaponName = weapon.name;
                Label label = new Label(weaponName, Styles.defaultLabel);
                label.setAlignment(Align.center, Align.center);
                weaponsTable.add(label).pad(10).row();
            }

            // Create a ScrollPane to make the weaponsTable scrollable
            ScrollPane scrollPane = new ScrollPane(weaponsTable);
            scrollPane.setFadeScrollBars(false); // Disable fade effect on scrollbars
            // Add the ScrollPane to the main table
            table.add(scrollPane).size(200f, 150f).row();

            // Create a Table to hold the "Close" button
            Table buttonTable = new Table();
            // Create the "Close" button
            Button closeButton = new TextButton("Close", Styles.defaultt);
            // Deselects the block and closes the configuration UI
            closeButton.clicked(this::deselect);
            // Add the "Close" button to the buttonTable
            buttonTable.add(closeButton).size(200f, 50f);
            // Add the buttonTable to the main table below the ScrollPane
            table.add(buttonTable).row();
        }
    }
}
