package net.daporkchop.mcpe.betaterrain;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.*;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import net.daporkchop.mcpe.betaterrain.noise.NoiseGeneratorOctaves2D;
import net.daporkchop.mcpe.betaterrain.noise.NoiseGeneratorOctaves3D;

import java.util.*;

import static cn.nukkit.block.Block.*;

public class BetaGenerator extends Generator {
    private final Set<Populator> generationPopulators = new HashSet<>();
    private final Set<Populator> populators = new HashSet<>();
    private ChunkManager level;
    private NukkitRandom nukkitRandom;
    private NoiseGeneratorOctaves3D noiseGen1;
    private NoiseGeneratorOctaves3D noiseGen2;
    private NoiseGeneratorOctaves3D noiseGen3;
    private NoiseGeneratorOctaves3D noiseGen4;
    private NoiseGeneratorOctaves3D noiseGen5;
    private NoiseGeneratorOctaves3D noiseGen6;
    private NoiseGeneratorOctaves3D noiseGen7;
    private NoiseGeneratorOctaves2D noiseGenTemp;
    private NoiseGeneratorOctaves2D noiseGenRain;
    private double noise[];
    private double sandNoise[] = new double[256];
    private double gravelNoise[] = new double[256];
    private double stoneNoise[] = new double[256];
    private double noise3[];
    private double noise1[];
    private double noise2[];
    private double noise6[];
    private double noise7[];
    private double noiseTemp[];
    private double noiseRain[];
    private Random rand;

    public BetaGenerator() {
        this(new HashMap<>());
    }

    public BetaGenerator(Map<String, Object> options) {

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;

        rand = new Random(level.getSeed());
        noiseGen1 = new NoiseGeneratorOctaves3D(rand, 16, false);
        noiseGen2 = new NoiseGeneratorOctaves3D(rand, 16, false);
        noiseGen3 = new NoiseGeneratorOctaves3D(rand, 8, false);
        noiseGen4 = new NoiseGeneratorOctaves3D(rand, 4, false);
        noiseGen5 = new NoiseGeneratorOctaves3D(rand, 4, false);
        noiseGen6 = new NoiseGeneratorOctaves3D(rand, 10, false);
        noiseGen7 = new NoiseGeneratorOctaves3D(rand, 16, false);
        noiseGenTemp = new NoiseGeneratorOctaves2D(rand, 4);
        noiseGenRain = new NoiseGeneratorOctaves2D(rand, 4);

        this.generationPopulators.add(new PopulatorGroundCover());
        this.populators.add(new PopulatorCaves());
        this.populators.add(new PopulatorRavines());

        PopulatorOre ores = new PopulatorOre();
        ores.setOreTypes(new OreType[]{
                new OreType(new BlockOreCoal(), 20, 17, 0, 128),
                new OreType(new BlockOreIron(), 20, 9, 0, 64),
                new OreType(new BlockOreRedstone(), 8, 8, 0, 16),
                new OreType(new BlockOreLapis(), 1, 7, 0, 16),
                new OreType(new BlockOreGold(), 2, 9, 0, 32),
                new OreType(new BlockOreDiamond(), 1, 8, 0, 16),
                new OreType(new BlockDirt(), 10, 33, 0, 128),
                new OreType(new BlockGravel(), 8, 33, 0, 128),
                new OreType(new BlockStone(BlockStone.GRANITE), 10, 33, 0, 80),
                new OreType(new BlockStone(BlockStone.DIORITE), 10, 33, 0, 80),
                new OreType(new BlockStone(BlockStone.ANDESITE), 10, 33, 0, 80)
        });
        this.populators.add(ores);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        generateTerrain(chunkX, chunkZ, chunk);

        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom);
        }
    }

    public void initRand(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        initRand(chunkX, chunkZ);

        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom);
        }

        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        Biome biome = Biome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Map<String, Object> getSettings() {
        return null;
    }

    @Override
    public String getName() {
        return "Overworld";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 128, 0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    public void generateTerrain(int x, int z, FullChunk terrain) {
        byte byte0 = 4;
        byte oceanHeight = 63;
        int k = byte0 + 1;
        byte b2 = 17;
        int l = byte0 + 1;
        noise = initNoiseField(noise, x * byte0, 0, z * byte0, k, b2, l);
        for (int xPiece = 0; xPiece < byte0; xPiece++) {
            for (int zPiece = 0; zPiece < byte0; zPiece++) {
                for (int yPiece = 0; yPiece < 16; yPiece++) {
                    double d = 0.125D;
                    double d1 = noise[((xPiece) * l + (zPiece)) * b2 + (yPiece)];
                    double d2 = noise[((xPiece) * l + (zPiece + 1)) * b2 + (yPiece)];
                    double d3 = noise[((xPiece + 1) * l + (zPiece)) * b2 + (yPiece)];
                    double d4 = noise[((xPiece + 1) * l + (zPiece + 1)) * b2 + (yPiece)];
                    double d5 = (noise[((xPiece) * l + (zPiece)) * b2 + (yPiece + 1)] - d1) * d;
                    double d6 = (noise[((xPiece) * l + (zPiece + 1)) * b2 + (yPiece + 1)] - d2) * d;
                    double d7 = (noise[((xPiece + 1) * l + (zPiece)) * b2 + (yPiece + 1)] - d3) * d;
                    double d8 = (noise[((xPiece + 1) * l + (zPiece + 1)) * b2 + (yPiece + 1)] - d4) * d;
                    for (int l1 = 0; l1 < 8; l1++) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;
                        for (int i2 = 0; i2 < 4; i2++) {
                            int xLoc = i2 + xPiece * 4;
                            int yLoc = yPiece * 8 + l1;
                            int zLoc = zPiece * 4;
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;
                            for (int k2 = 0; k2 < 4; k2++) {
                                double d17 = 1 - (yPiece / 16);
                                int block = AIR;
                                if (yPiece * 8 + l1 < oceanHeight) {
                                    if (d17 < 0.5D && yPiece * 8 + l1 >= oceanHeight - 1) {
                                        block = ICE;
                                    } else {
                                        block = STILL_WATER;
                                    }
                                }
                                if (d15 > 0.0D) {
                                    block = STONE;
                                }
                                terrain.setBlock(xLoc, yLoc, zLoc, block);
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

        for (int relX = 0; relX < 16; relX++)    {
            for (int relZ = 0; relZ < 16; relZ++)    {
                Biome biome = chooseBiome(relX, relZ, terrain.getHighestBlockAt(relX, relZ));
                replaceBlocksForBiome(relX, relZ, terrain, biome);
                terrain.setBiomeId(relX, relZ, biome.getId());
                //TODO: set correct color instead of the bad meme that nukkit uses
            }
        }
    }

    private double[] initNoiseField(double array[], int xPos, int yPos, int zPos, int xSize, int ySize, int zSize) {
        if (array == null) {
            array = new double[xSize * ySize * zSize];
        }
        double d0 = 684.412D;
        double d1 = 684.412D;
        noise6 = noiseGen6.generateNoiseArray(noise6, xPos, zPos, xSize, zSize, 1.121D, 1.121D, 0.5D);
        noise7 = noiseGen7.generateNoiseArray(noise7, xPos, zPos, xSize, zSize, 200D, 200D, 0.5D);
        noise3 = noiseGen3.generateNoiseArray(noise3, xPos, yPos, zPos, xSize, ySize, zSize,
                d0 / 80D, d1 / 160D, d0 / 80D);
        noise1 = noiseGen1.generateNoiseArray(noise1, xPos, yPos, zPos, xSize, ySize, zSize,
                d0, d1, d0);
        noise2 = noiseGen2.generateNoiseArray(noise2, xPos, yPos, zPos, xSize, ySize, zSize,
                d0, d1, d0);

        double d = 0.03125D;
        sandNoise = noiseGen4.generateNoiseArray(sandNoise, xPos * 4, zPos * 4, 0.0D, 16, 16, 1, d, d, 1.0D);
        gravelNoise = noiseGen4.generateNoiseArray(gravelNoise, xPos * 4, 109.0134D, zPos * 16, 16, 1, 16, d, 1.0D, d);
        stoneNoise = noiseGen5.generateNoiseArray(stoneNoise, xPos * 4, zPos * 4, 0.0D, 16, 16, 1, d * 2D, d * 2D, d * 2D);

        noiseTemp = noiseGenTemp.generateNoiseArray(noiseTemp, xPos * 4, zPos * 4, 16, 16, 0.025D, 0.025D, 0.25D);
        noiseRain = noiseGenRain.generateNoiseArray(noiseRain, xPos * 4, zPos * 4, 16, 16, 0.025D, 0.025D, 0.25D);

        int k1 = 0;
        int l1 = 0;
        for (int x = 0; x < xSize; x++) {
            for (int z = 0; z < zSize; z++) {
                double d3 = 1;
                double d4 = 1.0D - d3;
                d4 *= d4;
                d4 *= d4;
                d4 = 1.0D - d4;
                double d5 = (noise6[l1] + 256D) / 512D;
                d5 *= d4;
                if (d5 > 1.0D) {
                    d5 = 1.0D;
                }
                double d6 = noise7[l1] / 8000D;
                if (d6 < 0.0D) {
                    d6 = -d6 * 0.3D;
                }
                d6 = d6 * 3D - 2D;
                if (d6 < 0.0D) {
                    d6 /= 2D;
                    if (d6 < -1D) {
                        d6 = -1D;
                    }
                    d6 /= 1.4D;
                    d6 /= 2D;
                    d5 = 0.0D;
                } else {
                    if (d6 > 1.0D) {
                        d6 = 1.0D;
                    }
                    d6 /= 8D;
                }
                if (d5 < 0.0D) {
                    d5 = 0.0D;
                }
                d5 += 0.5D;
                d6 = (d6 * (double) ySize) / 16D;
                double d7 = (double) ySize / 2D + d6 * 4D;
                l1++;
                for (int y = 0; y < ySize; y++) {
                    double d8 = 0.0D;
                    double d9 = (((double) y - d7) * 12D)
                            / d5;
                    if (d9 < 0.0D) {
                        d9 *= 4D;
                    }
                    double d10 = noise1[k1] / 512D;
                    double d11 = noise2[k1] / 512D;
                    double d12 = (this.noise3[k1] / 10D + 1.0D) / 2D;
                    if (d12 < 0.0D) {
                        d8 = d10;
                    } else if (d12 > 1.0D) {
                        d8 = d11;
                    } else {
                        d8 = d10 + (d11 - d10) * d12;
                    }
                    d8 -= d9;
                    if (y > ySize - 4) {
                        double d13 = (double) ((float) (y - (ySize - 4)) / 3F);
                        d8 = d8 * (1.0D - d13) + -10D * d13;
                    }
                    array[k1] = d8;
                    k1++;
                }
            }
        }
        return array;
    }

    public void replaceBlocksForBiome(int x, int z, FullChunk terrain, Biome biome) {
        boolean sand = sandNoise[x + z * 16] + rand.nextDouble() * 0.2D > 0.0D;
        boolean gravel = gravelNoise[x + z * 16] + rand.nextDouble() * 0.2D > 3D;
        int depth = (int) (stoneNoise[x + z * 16] / 3D + 3D + rand.nextDouble() * 0.25D);
        byte oceanHeight = 63;
        int prevDepth = -1;
        int topBlock = biome.getGroundCover()[0].getId();
        int fillerBlock = biome.getGroundCover()[1].getId();
        for (int y = 127; y >= 0; y--) {
            if (y <= rand.nextInt(5)) {
                terrain.setBlock(z, y, x, BEDROCK);
                continue;
            }
            int block = terrain.getBlockId(z, y, x);
            if (block == AIR) {
                prevDepth = -1;
                continue;
            }
            if (block != STONE) {
                continue;
            }
            if (prevDepth == -1) {
                if (depth <= 0) {
                    topBlock = AIR;
                    fillerBlock = STONE;
                } else if (y >= oceanHeight - 4 && y <= oceanHeight + 1) {
                    topBlock = biome.getGroundCover()[0].getId();
                    fillerBlock = biome.getGroundCover()[1].getId();
                    if (gravel) {
                        topBlock = AIR;
                        fillerBlock = GRAVEL;
                    }
                    if (sand) {
                        topBlock = SAND;
                        fillerBlock = SAND;
                    }
                }
                if (y < oceanHeight && topBlock == AIR) {
                    topBlock = STILL_WATER;
                }
                prevDepth = depth;
                if (y >= oceanHeight - 1) {
                    terrain.setBlock(z, y, x, topBlock);
                } else {
                    terrain.setBlock(z, y, x, fillerBlock);
                }
                continue;
            }
            if (prevDepth <= 0) {
                continue;
            }
            prevDepth--;
            terrain.setBlock(z, y, x, fillerBlock);
            if (prevDepth == 0 && fillerBlock == SAND) {
                prevDepth = rand.nextInt(4);
                fillerBlock = SANDSTONE;
            }
        }
    }

    private Biome chooseBiome(int x, int z, int height) {
        int id;
        if (height <= 63)   {
            id = Biome.OCEAN;
        } else if (height <= 67)    {
            id = Biome.BEACH;
        } else {
            int index = (x << 4) | z;
            double temp = noiseTemp[index];
            double rain = noiseRain[index];

            if (temp > 0.8) {
                if (rain > 0.85){
                    id = Biome.JUNGLE;
                } else if (rain > 0.7)  {
                    id = Biome.SWAMP;
                } else if (rain > 0.55)  {
                    id = Biome.SAVANNA;
                } else {
                    id = Biome.DESERT;
                }
            } else if (temp > 0.6)   {
                if (rain > 0.5){
                    if (rain > 0.75){
                        id = Biome.BIRCH_FOREST;
                    } else {
                        id = Biome.FOREST;
                    }
                } else {
                    id = Biome.PLAINS;
                }
            } else {
                if (rain > 0.75){
                    id = Biome.TAIGA;
                } else if (rain < 0.5){
                    id = Biome.MOUNTAINS;
                } else {
                    id = Biome.ICE_PLAINS;
                }
            }
        }

        return Biome.getBiome(id);
    }
}
