package airstrike;

import airstrike.content.AirstrikeBlocks;
import mindustry.mod.*;

public class AirstrikeMod extends Mod{

    public AirstrikeMod(){
    }

    @Override
    public void loadContent(){
        AirstrikeBlocks.load();
    }

}
