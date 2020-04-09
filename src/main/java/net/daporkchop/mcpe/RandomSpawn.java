package net.daporkchop.mcpe;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.generic.BaseFullChunk;
import lombok.experimental.UtilityClass;
import net.twoptwoe.mobplugin.utils.Utils;

import java.util.BitSet;
import java.util.Random;

@UtilityClass
public class RandomSpawn {
    private final BitSet UNSAFE_BLOCKS = new BitSet();

    private final int RADIUS    = 256;
    private final int MAX_TRIES = 2048;

    static {
        UNSAFE_BLOCKS.set(BlockID.AIR);
        UNSAFE_BLOCKS.set(BlockID.WATER);
        UNSAFE_BLOCKS.set(BlockID.STILL_WATER);
        UNSAFE_BLOCKS.set(BlockID.LAVA);
        UNSAFE_BLOCKS.set(BlockID.STILL_LAVA);
        UNSAFE_BLOCKS.set(BlockID.FIRE);
        UNSAFE_BLOCKS.set(BlockID.CACTUS);
        UNSAFE_BLOCKS.set(BlockID.MAGMA);
        UNSAFE_BLOCKS.set(BlockID.NETHER_PORTAL);
        UNSAFE_BLOCKS.set(BlockID.END_PORTAL);
    }

    public void init(Server server) {
        final int radius = (RADIUS >> 4) + 1;

        for (Level level : server.getLevels().values()) {
            server.getLogger().info("Generating spawn chunks in level " + level.getName());
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    //load all chunks once to ensure they're generated
                    level.getChunk(x, z, true).setChanged();
                }
            }
            server.getLogger().info("Saving level " + level.getName());
            level.save();
            server.getLogger().info("Level " + level.getName() + " is ready!");
        }
    }

    public Position getSpawnPos(Level level, Random random) {
        return getSpawnPos(level, random, new Position(0, 128, 0, level), RADIUS, MAX_TRIES);
    }

    public Position getSpawnPos(Level level, Random random, Position pos, int radius, int maxTries) {
        for (int tries = 0; tries < maxTries; tries++) {
            int x = random.nextInt(radius << 1) - radius + pos.getFloorX();
            int z = random.nextInt(radius << 1) - radius + pos.getFloorZ();
            BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
            for (int y = 255; y > 0; y--) {
                if (!UNSAFE_BLOCKS.get(chunk.getBlockId(x & 0xF, y, z & 0xF))
                        && (y + 1 >= 256 || chunk.getBlockId(x & 0xF, y + 1, z & 0xF) == BlockID.AIR)
                        && (y + 2 >= 256 || chunk.getBlockId(x & 0xF, y + 2, z & 0xF) == BlockID.AIR)) {
                    level.getServer().getLogger().info("Generated spawn position at " + new Position(x + 0.5d, y + 1.001d, z + 0.5d, level));
                    return new Position(x + 0.5d, y + 1.001d, z + 0.5d, level);
                }
            }
        }

        return pos;
    }

    public boolean isUnsafe(int id) {
        return UNSAFE_BLOCKS.get(id);
    }
}
