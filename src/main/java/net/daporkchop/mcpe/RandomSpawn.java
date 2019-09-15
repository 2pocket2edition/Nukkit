package net.daporkchop.mcpe;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.twoptwoe.mobplugin.utils.Utils;

public class RandomSpawn {
    private static final TIntSet UNSAFE_BLOCKS = new TIntHashSet();

    static {
        UNSAFE_BLOCKS.addAll(new int[]{
                BlockID.AIR,
                BlockID.WATER,
                BlockID.STILL_WATER,
                BlockID.LAVA,
                BlockID.STILL_LAVA,
                BlockID.FIRE,
                BlockID.CACTUS,
                BlockID.MAGMA,
                BlockID.NETHER_PORTAL,
                BlockID.END_PORTAL
        });
    }

    public static Position getSpawnPos(Level level) {
        return getSpawnPos(level, new Position(0, 128, 0, level), 256, 2048);
    }

    public static Position getSpawnPos(Level level, Position pos, int radius, int maxTries) {
        for (int tries = 0; tries < maxTries; tries++) {
            int xPos = Utils.rand(-radius, radius + 1) + pos.getFloorX(),
                    zPos = Utils.rand(-radius, radius + 1) + pos.getFloorZ(),
                    yPos = Utils.rand(5, 256);
            if (UNSAFE_BLOCKS.contains(level.getBlockIdAt(xPos, yPos, zPos))
                    || !(yPos + 1 >= 255 || level.getBlockIdAt(xPos, yPos + 1, zPos) == BlockID.AIR)
                    || !(yPos + 2 >= 255 || level.getBlockIdAt(xPos, yPos + 2, zPos) == BlockID.AIR)) {
                continue;
            }
            pos = new Position(xPos + 0.5d, yPos + 1.001d, zPos + 0.5d, level);
            break;
        }

        return pos;
    }

    public static boolean isUnsafe(int id) {
        return UNSAFE_BLOCKS.contains(id);
    }
}
