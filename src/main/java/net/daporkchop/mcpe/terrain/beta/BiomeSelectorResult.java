package net.daporkchop.mcpe.terrain.beta;

import cn.nukkit.level.generator.biome.Biome;

public class BiomeSelectorResult {
    public Biome biome;
    public double temp, rain;

    public BiomeSelectorResult(Biome biome, double temp, double rain) {
        this.biome = biome;
        this.temp = temp;
        this.rain = rain;
    }
}
