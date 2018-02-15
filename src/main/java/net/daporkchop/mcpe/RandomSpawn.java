package net.daporkchop.mcpe;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.twoptwoe.mobplugin.utils.Utils;

public class RandomSpawn {
    private static TIntSet unsafe_blocks = new TSynchronizedIntSet(new TIntHashSet());

    static {
        unsafe_blocks.addAll(new int[] {
                Block.AIR,
                Block.WATER,
                Block.STILL_WATER,
                Block.LAVA,
                Block.STILL_LAVA,
                Block.FIRE,
                Block.CACTUS,
                Block.MAGMA,
                Block.NETHER_PORTAL,
                Block.END_PORTAL
        });
    }

    public static Position getSpawnPos(Level level) {
        Position pos = new Position(0, 128, 0, level);

        for (int tries = 0; tries < 512; tries++)   {
            int xPos = Utils.rand(-256, 256),
            zPos = Utils.rand(-256, 256),
            yPos = level.getHighestBlockAt(xPos, zPos);
            int under = level.getBlockIdAt(xPos, yPos - 1, zPos);
            if (unsafe_blocks.contains(under))  {
                continue;
            }
            pos = new Position(xPos + 0.5f, yPos, zPos + 0.5f, level);
            break;
        }

        return pos;
    }
}
