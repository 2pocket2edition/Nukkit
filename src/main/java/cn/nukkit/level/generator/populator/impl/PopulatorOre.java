package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import net.twoptwoe.mobplugin.utils.Utils;

/**
 * @author DaPorkchop_
 */
public class PopulatorOre extends Populator {
    private final int replaceId;
    private final OreType[] oreTypes;

    public PopulatorOre(int replaceId, OreType[] oreTypes) {
        this.replaceId = replaceId;
        this.oreTypes = oreTypes;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (OreType type : this.oreTypes) {
            for (int i = 0; i < type.clusterCount; i++) {
                int x = (chunkX << 4) | Utils.rand(0, 15);
                int z = (chunkZ << 4) | Utils.rand(0, 15);
                int y = NukkitMath.randomRange(random, type.minHeight, type.maxHeight);
                if (level.getBlockIdAt(x, y, z) != replaceId) {
                    continue;
                }
                type.spawn(level, random, replaceId, x, y, z);
            }
        }
    }
}
