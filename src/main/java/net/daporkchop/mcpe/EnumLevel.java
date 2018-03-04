package net.daporkchop.mcpe;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

public enum EnumLevel {
    OVERWORLD,
    NETHER,
    //THE_END
    ;

    Level level;

    public static void initLevels() {
        OVERWORLD.level = Server.getInstance().getDefaultLevel();
        NETHER.level = Server.getInstance().getLevelByName("nether");
    }

    public static Level getOtherNetherPair(Level current) {
        if (current == OVERWORLD.level) {
            return NETHER.level;
        } else if (current == NETHER.level) {
            return OVERWORLD.level;
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position moveToNether(Position current) {
        if (current.level == OVERWORLD.level) {
            return new Position(UtilsPE.mRound(current.getFloorX() >> 3, 128), UtilsPE.mRound(current.getFloorY(), 32), UtilsPE.mRound(current.getFloorZ() >> 3, 128), NETHER.level);
        } else if (current.level == NETHER.level) {
            return new Position(UtilsPE.mRound(current.getFloorX() << 3, 1024), UtilsPE.mRound(current.getFloorY(), 32), UtilsPE.mRound(current.getFloorZ() << 3, 1024), OVERWORLD.level);
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public Level getLevel() {
        return level;
    }
}
