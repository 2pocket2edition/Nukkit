package net.daporkchop.mcpe.betaterrain;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockStone;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.level.generator.populator.PopulatorGroundCover;
import cn.nukkit.level.generator.populator.PopulatorOre;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import net.daporkchop.mcpe.betaterrain.noise.NoiseGeneratorOctaves3D;

import java.util.*;

public class BetaGenerator extends Generator {
    private PorkBiomeSelector selector;
    private ChunkManager level;
    private Set<Populator> populators = new HashSet<>();
    private Set<Populator> generationPopulators = new HashSet<>();
    private Random random;
    private NukkitRandom nukkitRandom;
    private double[] noise1;
    private double[] noise2;
    private double[] noise3;
    private double[] noise;
    private double[] sandNoise;
    private double[] gravelNoise;
    private double[] stoneNoise;
    private double[] noise6;
    private double[] noise7;
    private NoiseGeneratorOctaves3D gen1;
    private NoiseGeneratorOctaves3D gen2;
    private NoiseGeneratorOctaves3D gen3;
    private NoiseGeneratorOctaves3D gen4;
    private NoiseGeneratorOctaves3D gen5;
    private NoiseGeneratorOctaves3D gen6;
    private NoiseGeneratorOctaves3D gen7;
    private NoiseGeneratorOctaves3D genTrees;

    public BetaGenerator() {
        this(new HashMap<>());
    }

    public BetaGenerator(Map<String, Object> options) {
        //Nothing here. Just used for future update.
    }

    @Override
    public int getId() {
        return TYPE_INFINITE;
    }

    @Override
    public Map<String, Object> getSettings() {
        return null;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.nukkitRandom = random;
        random.setSeed(level.getSeed());
        this.random = new Random(level.getSeed());
        this.level = level;
        this.selector = new PorkBiomeSelector(new NukkitRandom(level.getSeed()), Biome.getBiome(Biome.OCEAN));
        this.generationPopulators.add(new PopulatorGroundCover());
        PopulatorOre ores = new PopulatorOre();
        ores.setOreTypes(new OreType[]{
                new OreType(Block.get(Block.COAL_ORE), 20, 16, 0, 128),
                new OreType(Block.get(Block.IRON_ORE), 20, 8, 0, 64),
                new OreType(Block.get(Block.REDSTONE_ORE), 1, 7, 0, 16),
                new OreType(Block.get(Block.LAPIS_ORE), 2, 6, 0, 32),
                new OreType(Block.get(Block.GOLD_ORE), 4, 8, 0, 32),
                new OreType(Block.get(Block.DIAMOND_ORE), 2, 7, 0, 16),
                new OreType(Block.get(Block.DIRT), 20, 32, 0, 128),
                new OreType(Block.get(Block.GRAVEL), 10, 16, 0, 128),
                new OreType(Block.get(Block.STONE, BlockStone.DIORITE), 6, 32, 0, 128),
                new OreType(Block.get(Block.STONE, BlockStone.ANDESITE), 6, 32, 0, 128),
                new OreType(Block.get(Block.STONE, BlockStone.GRANITE), 6, 32, 0, 128),
        });
        this.populators.add(ores);

        this.random.setSeed(level.getSeed());
        this.gen1 = new NoiseGeneratorOctaves3D(this.random, 16, false);
        this.gen2 = new NoiseGeneratorOctaves3D(this.random, 16, false);
        this.gen3 = new NoiseGeneratorOctaves3D(this.random, 8, false);
        this.gen4 = new NoiseGeneratorOctaves3D(this.random, 4, false);
        this.gen5 = new NoiseGeneratorOctaves3D(this.random, 4, false);
        this.gen6 = new NoiseGeneratorOctaves3D(this.random, 10, false);
        this.gen7 = new NoiseGeneratorOctaves3D(this.random, 16, false);
        this.genTrees = new NoiseGeneratorOctaves3D(this.random, 8, false);
    }

    public String getName()

    {
        return "porkworld";
    }

    public Vector3 getSpawn()

    {
        return new Vector3(0.5, 128, 0.5);
    }

    public void populateChunk(int chunkX, int chunkZ) {
        this.random.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom);
        }

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        Biome biome = Biome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    public void generateChunk(int x, int z) {
        FullChunk chunk = this.level.getChunk(x, z);
        this.random.setSeed(0xdeadbeef ^ (x << 8) ^ z ^ this.level.getSeed());
        
        /*
         * temp = array(
         * 256
         * );
         * rain = array(
         * 256
         * );
         *
         * for (xx = 0; xx < 16; ++ xx) {
         * for (zz = 0; zz < 16; ++ zz) {
         * out = this.pickBiome(x * 16 + xx, z * 16 + zz);
         * chunk.setBiomeId(xx, zz, out.biome.getId());
         * temp[xx * 16 + zz] = out.temp;
         * rain[xx * 16 + zz] = out.rain;
         * }
         * }
         */

        byte byte0 = 4;
        byte oceanHeight = 64;
        byte k = (byte) (byte0 + 1);
        byte b2 = 17;
        byte l = (byte) (byte0 + 1);
        this.initNoiseField(x * byte0, 0, z * byte0, k, b2, l);

        for (int xPiece = 0; xPiece < byte0; xPiece++) {
            for (int zPiece = 0; zPiece < byte0; zPiece++) {
                for (int yPiece = 0; yPiece < 16; yPiece++) {
                    double d = 0.125d;
                    double d1 = this.noise[((xPiece + 0) * l + (zPiece + 0)) * b2 + (yPiece + 0)];
                    double d2 = this.noise[((xPiece + 0) * l + (zPiece + 1)) * b2 + (yPiece + 0)];
                    double d3 = this.noise[((xPiece + 1) * l + (zPiece + 0)) * b2 + (yPiece + 0)];
                    double d4 = this.noise[((xPiece + 1) * l + (zPiece + 1)) * b2 + (yPiece + 0)];
                    double d5 = (this.noise[((xPiece + 0) * l + (zPiece + 0)) * b2 + (yPiece + 1)] - d1) * d;
                    double d6 = (this.noise[((xPiece + 0) * l + (zPiece + 1)) * b2 + (yPiece + 1)] - d2) * d;
                    double d7 = (this.noise[((xPiece + 1) * l + (zPiece + 0)) * b2 + (yPiece + 1)] - d3) * d;
                    double d8 = (this.noise[((xPiece + 1) * l + (zPiece + 1)) * b2 + (yPiece + 1)] - d4) * d;
                    for (int l1 = 0; l1 < 8; l1++) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;
                        for (int i2 = 0; i2 < 4; i2++) {
                            int xLoc = i2 + xPiece * 4;
                            int yLoc = yPiece * 8 + l1;
                            int zLoc = 0 + zPiece * 4;
                            double d14 = 0.25;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;
                            for (int k2 = 0; k2 < 4; k2++) {
                                double d17 = 1 - (yPiece / 16);
                                int block = Block.AIR;
                                if (yLoc < 64) {
                                    if (d17 < 0.5 && yLoc >= 64 - 1) {
                                        block = Block.ICE;
                                    } else {
                                        block = Block.WATER;
                                    }
                                }
                                if (d15 > 0.0) {
                                    block = Block.STONE;
                                }
                                chunk.setBlockId(xLoc, yLoc, zLoc, block);
                                zLoc++;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                int highest = chunk.getHighestBlockAt(xx, zz);
                chunk.setBiomeId(xx, zz, this.pickBiome(x * 16 + xx, z * 16 + zz, chunk.getBlockId(xx, highest, zz) == Block.WATER ? 1 : highest).biome.getId());
            }
        }

        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, x, z, this.nukkitRandom);
        }
    }

    public void initNoiseField(int posX, int posY, int posZ, int xSize, int ySize, int zSize) {
        this.noise = new double[xSize * ySize * zSize];
        double d0 = 684.412;
        double d1 = 684.412;

        this.noise6 = this.gen6.generateNoiseArray2(this.noise6, posX, posZ, xSize, zSize, 1.121, 1.121, 0.5);
        this.noise7 = this.gen7.generateNoiseArray2(this.noise7, posX, posZ, xSize, zSize, 200, 200, 0.5);
        this.noise3 = this.gen3.generateNoiseArray(this.noise3, posX, posY, posZ, xSize, ySize, zSize, d0 / 80, d1 / 160, d0 / 80);
        this.noise1 = this.gen1.generateNoiseArray(this.noise1, posX, posY, posZ, xSize, ySize, zSize, d0, d1, d0);
        this.noise2 = this.gen2.generateNoiseArray(this.noise2, posX, posY, posZ, xSize, ySize, zSize, d0, d1, d0);

        int k1 = 0;
        int l1 = 0;
        //double i2 = 16 / xSize;

        for (int x = 0; x < xSize; x++) {
            //double k2 = x * i2 + i2 / 2;
            for (int z = 0; z < zSize; z++) {
                //double i3 = z * i2 + i2 / 2;
                //double d2 = 1;
                double d3 = 1;
                // d2 = temp[k2 * 16 + i3];
                // d3 = rain[k2 * 16 + i3] * d2;
                double d4 = 1.0 - d3;
                d4 *= d4;
                d4 *= d4;
                d4 = 1.0 - d4;
                double d5 = (this.noise6[l1] + 256) / 512;
                d5 *= d4;
                if (d5 > 1.0) {
                    d5 = 1.0;
                }
                double d6 = this.noise7[l1] / 8000;
                if (d6 < 0.0) {
                    d6 = -d6 * 0.3;
                }
                d6 = d6 * 3 - 2;
                if (d6 < 0.0) {
                    d6 /= 2;
                    if (d6 < -1) {
                        d6 = -1;
                    }
                    d6 /= 1.4;
                    d6 /= 2;
                    d5 = 0.0;
                } else {
                    if (d6 > 1.0) {
                        d6 = 1.0;
                    }
                    d6 /= 8;
                }
                if (d5 < 0.0) {
                    d5 = 0.0;
                }
                d5 += 0.5;
                d6 = (d6 * ySize) / 16;
                double d7 = ySize / 2 + d6 * 4;
                l1++;
                for (int y = 0; y < ySize; y++) {
                    double d8 = 0.0;
                    double d9 = ((y - d7) * 12) / d5;
                    if (d9 < 0.0) {
                        d9 *= 4;
                    }
                    double d10 = this.noise1[k1] / 512;
                    double d11 = this.noise2[k1] / 512;
                    double d12 = (this.noise3[k1] / 10 + 1.0) / 2;
                    if (d12 < 0.0) {
                        d8 = d10;
                    } else if (d12 > 1.0) {
                        d8 = d11;
                    } else {
                        d8 = d10 + (d11 - d10) * d12;
                    }
                    d8 -= d9;
                    if (y > ySize - 4) {
                        double d13 = ((y - (ySize - 4)) / 3);
                        d8 = d8 * (1.0 - d13) + -10 * d13;
                    }
                    this.noise[k1] = d8;
                    k1++;
                }
            }
        }
    }

    public BiomeSelectorResult pickBiome(int x, int z, int height) {
        // return Biome.getBiome(Biome.MOUNTAINS);
        long hash = x * 2345803 ^ z * 9236449 ^ this.level.getSeed();
        hash *= hash + 223;
        int xNoise = (int) (hash >> 20 & 3);
        int zNoise = (int) (hash >> 22 & 3);
        if (xNoise == 3) {
            xNoise = 1;
        }
        if (zNoise == 3) {
            zNoise = 1;
        }

        return this.selector.pickBiomeNew(x + xNoise - 1, z + zNoise - 1, height);
    }

}
