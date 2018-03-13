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
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulatorOre extends Populator {
    private final int replaceId;
    private OreType[] oreTypes = new OreType[0];

    public PopulatorOre() {
        this(Block.STONE);
    }

    public PopulatorOre(int id) {
        this.replaceId = id;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
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

    public void setOreTypes(OreType[] oreTypes) {
        this.oreTypes = oreTypes;
    }
}
