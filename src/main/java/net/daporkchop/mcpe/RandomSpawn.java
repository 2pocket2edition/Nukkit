package net.daporkchop.mcpe;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@UtilityClass
public class RandomSpawn {
    private final BitSet UNSAFE_BLOCKS = new BitSet();

    private final int RADIUS    = 256;
    private final int MAX_TRIES = 128;

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
        List<Position> positions = new ArrayList<>(maxTries);
        for (int tries = 0; tries < maxTries; tries++) {
            int x = random.nextInt(radius << 1) - radius + pos.getFloorX();
            int z = random.nextInt(radius << 1) - radius + pos.getFloorZ();
            BaseFullChunk chunk = level.getChunk(x >> 4, z >> 4);
            for (int y = 255; y > 0; y--) {
                if (!UNSAFE_BLOCKS.get(chunk.getBlockId(x & 0xF, y, z & 0xF))
                        && (y + 1 >= 256 || chunk.getBlockId(x & 0xF, y + 1, z & 0xF) == BlockID.AIR)
                        && (y + 2 >= 256 || chunk.getBlockId(x & 0xF, y + 2, z & 0xF) == BlockID.AIR)) {
                    level.getServer().getLogger().info("Generated spawn position at " + new Position(x + 0.5d, y + 1.001d, z + 0.5d, level));
                    positions.add(new Position(x + 0.5d, y + 1.001d, z + 0.5d, level));
                    break;
                }
            }
        }

        return positions.stream().max(Comparator.comparingDouble(Position::getY)).orElse(pos);
    }

    public Position getSafeSpawnNear(Position position) {
        if (isSafe(position = position.add(0.5d, 0.001d, 0.5d))) {
            position.level.getServer().getLogger().info("Spawn position at " + position + " was already safe");
            return position;
        }
        for (int r = 1; r <= 2; r++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        Position p = position.add(dx, dy, dz);
                        if (isSafe(p)) {
                            position.level.getServer().getLogger().info("Found safe spawn position at " + p);
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isSafe(Position position) {
        int x = position.getFloorX() & 0xF;
        int y = position.getFloorY() - 1;
        int z = position.getFloorZ() & 0xF;
        FullChunk chunk = position.getChunk();
        return y >= 0 && !UNSAFE_BLOCKS.get(chunk.getBlockId(x, y, z))
                && (y + 1 >= 256 || chunk.getBlockId(x, y + 1, z) == BlockID.AIR)
                && (y + 2 >= 256 || chunk.getBlockId(x, y + 2, z) == BlockID.AIR);
    }

    public boolean isUnsafe(int id) {
        return UNSAFE_BLOCKS.get(id);
    }
}
