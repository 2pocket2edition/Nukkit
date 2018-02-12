package net.daporkchop.mcpe.betaterrain;

import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.BiomeSelector;
import cn.nukkit.math.NukkitRandom;

public class PorkBiomeSelector extends BiomeSelector {
    public PorkBiomeSelector(NukkitRandom random, Biome fallback) {
        super(random, fallback);
    }

    @Override
    public void recalculate() {
        super.recalculate();
    }

    public BiomeSelectorResult pickBiomeNew(int x, int z, int height)   {
        double temperature = this.getTemperature(x, z);
        double rainfall = this.getRainfall(x, z);

        int biomeId = 0;

        if (height == 1)    {
            biomeId = Biome.OCEAN;
        } else if (height <= 64){
            biomeId = Biome.BEACH;
        } else {
            if (temperature > 0.8) {
                if (rainfall > 0.85){
                    biomeId = Biome.JUNGLE;
                } else if (rainfall > 0.7)  {
                    biomeId = Biome.SWAMP;
                } else if (rainfall > 0.5)  {
                    biomeId = Biome.SAVANNA;
                } else {
                    biomeId = Biome.DESERT;
                }
            } else if (temperature > 0.6)   {
                if (rainfall > 0.5){
                    if (rainfall > 0.75){
                        biomeId = Biome.BIRCH_FOREST;
                    } else {
                        biomeId = Biome.FOREST;
                    }
                } else {
                    biomeId = Biome.PLAINS;
                }
            } else {
                if (rainfall > 0.75){
                    biomeId = Biome.TAIGA;
                } else if (rainfall < 0.5){
                    biomeId = Biome.MOUNTAINS;
                } else {
                    biomeId = Biome.ICE_PLAINS;
                }
            }
        }

        return new BiomeSelectorResult(Biome.getBiome(biomeId), temperature, rainfall);
    }
}
