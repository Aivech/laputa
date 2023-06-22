package aivech.laputa.world;

import java.util.List;
import java.util.Random;

import aivech.laputa.world.noise.XXHasher;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class LaputaOverworldChunkProvider implements IChunkProvider {

    private final World world;
    private final boolean doFeatureGen;
    private final Random rand;
    private final NoiseGeneratorOctaves field_147431_j;
    private final NoiseGeneratorOctaves field_147432_k;
    private final NoiseGeneratorOctaves field_147429_l;
    private final NoiseGeneratorOctaves noiseGen6;
    private final double[] chunkLattice;
    private final float[] parabolicField;
    private BiomeGenBase[] biomesForGeneration;
    private double[] field_147426_g;
    private double[] field_147427_d;
    private double[] field_147428_e;
    private double[] field_147425_f;
    private WorldType worldType;

    private final XXHasher hasher;

    public LaputaOverworldChunkProvider(World world) {
        this.world = world;
        this.doFeatureGen = world.getWorldInfo()
            .isMapFeaturesEnabled();

        this.rand = new Random(world.getSeed());

        // TODO: REPLACE
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        NoiseGeneratorPerlin field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        NoiseGeneratorOctaves noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);

        this.chunkLattice = new double[825];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }
        // end todo

        assert world.getWorldInfo()
            .getTerrainType() == LaputaWorldType.INSTANCE;
        this.worldType = LaputaWorldType.INSTANCE;

        this.hasher = new XXHasher(world.getSeed());
    }

    @Override
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return true;
    }

    @Override
    public Chunk provideChunk(int chunk_x, int chunk_z) {

        Block[] ablock = new Block[65536];
        byte[] abyte = new byte[65536];


        int latticeX = 16/4 + 1;
        int latticeZ = 16/4 + 1;
        int latticeY = 256/4 + 1;

        int latticeSize = latticeX * latticeZ * latticeY;

        long[] hashNoise = new long[latticeSize];


        for (int i = 0; i < latticeX; i++) {
            for (int j = 0; j < latticeZ; j++) {
                int u = chunk_x * 4 + i;
                int v = chunk_z * 4 + j;

                for(int k = 0; k < latticeY; k++) {
                    hashNoise[i*25+j*5+k] = this.hasher.hash(u,v,k);
                }

            }
        }
        this.placeBlocksByNoise(hashNoise, ablock, abyte);

        // this.interpolateBlocksFromLattice(chunk_x, chunk_z, ablock, abyte);

        this.biomesForGeneration = this.world.getWorldChunkManager()
            .loadBlockGeneratorData(this.biomesForGeneration, chunk_x * 16, chunk_z * 16, 16, 16);

        Chunk chunk = new Chunk(this.world, ablock, abyte, chunk_x, chunk_z);
        byte[] abyte1 = chunk.getBiomeArray();

        for (int k = 0; k < abyte1.length; ++k) {
            abyte1[k] = (byte) biomesForGeneration[k].biomeID;
        }

        chunk.generateSkylightMap();

        return chunk;
    }

    private void placeBlocksByNoise(long[] hashNoise, Block[] block, byte[] meta) {
        // int center = 80;

        for(int slcX = 0; slcX < 4; slcX++) {
            for(int colZ = 0; colZ < 4; colZ++) {
                for(int cubY = 0; cubY < (256/4); cubY++) {

                    long hash000 = hashNoise[slcX*25    +colZ*5     +cubY];
                    long hash100 = hashNoise[(slcX+1)*25+colZ*5     +cubY];
                    long hash010 = hashNoise[slcX*25    +(colZ+1)*5 +cubY];
                    long hash110 = hashNoise[(slcX+1)*25+(colZ+1)*5 +cubY];
                    long hash001 = hashNoise[slcX*25    +colZ*5     +cubY+1];
                    long hash101 = hashNoise[(slcX+1)*25+colZ*5     +cubY+1];
                    long hash011 = hashNoise[slcX*25    +(colZ+1)*5 +cubY+1];
                    long hash111 = hashNoise[(slcX+1)*25+(colZ+1)*5 +cubY+1];

                    float f000 = XXHasher.getHashFloat(hash000,0);
                    float f100 = XXHasher.getHashFloat(hash100,0);
                    float f010 = XXHasher.getHashFloat(hash010,0);
                    float f110 = XXHasher.getHashFloat(hash110,0);
                    float f001 = XXHasher.getHashFloat(hash001,0);
                    float f101 = XXHasher.getHashFloat(hash101,0);
                    float f011 = XXHasher.getHashFloat(hash011,0);
                    float f111 = XXHasher.getHashFloat(hash111,0);

                    int x0 = slcX*4;
                    int x1 = slcX*4 + 3;
                    int z0 = colZ*4;
                    int z1 = colZ*4 + 3;
                    int y0 = cubY*4;
                    int y1 = cubY*4 + 3;

                    block[x0 << 12 | z0 << 8 | y0] = Blocks.wool;
                    block[x0 << 12 | z1 << 8 | y0] = Blocks.wool;
                    block[x1 << 12 | z0 << 8 | y0] = Blocks.wool;
                    block[x1 << 12 | z1 << 8 | y0] = Blocks.wool;
                    block[x0 << 12 | z0 << 8 | y1] = Blocks.wool;
                    block[x0 << 12 | z1 << 8 | y1] = Blocks.wool;
                    block[x1 << 12 | z0 << 8 | y1] = Blocks.wool;
                    block[x1 << 12 | z1 << 8 | y1] = Blocks.wool;

                    byte c000 = (byte) MathHelper.clamp_int((int) Math.floor(f000*16f),0,15);
                    byte c100 = (byte) MathHelper.clamp_int((int) Math.floor(f100*16f),0,15);
                    byte c010 = (byte) MathHelper.clamp_int((int) Math.floor(f010*16f),0,15);
                    byte c110 = (byte) MathHelper.clamp_int((int) Math.floor(f110*16f),0,15);
                    byte c001 = (byte) MathHelper.clamp_int((int) Math.floor(f001*16f),0,15);
                    byte c101 = (byte) MathHelper.clamp_int((int) Math.floor(f101*16f),0,15);
                    byte c011 = (byte) MathHelper.clamp_int((int) Math.floor(f011*16f),0,15);
                    byte c111 = (byte) MathHelper.clamp_int((int) Math.floor(f111*16f),0,15);

                    meta[x0 << 12 | z0 << 8 | y0] = (byte) (hash000 & 0xF);
                    meta[x0 << 12 | z1 << 8 | y0] = (byte) (hash010 & 0xF);
                    meta[x1 << 12 | z0 << 8 | y0] = (byte) (hash100 & 0xF);
                    meta[x1 << 12 | z1 << 8 | y0] = (byte) (hash110 & 0xF);
                    meta[x0 << 12 | z0 << 8 | y1] = (byte) (hash001 & 0xF);
                    meta[x0 << 12 | z1 << 8 | y1] = (byte) (hash011 & 0xF);
                    meta[x1 << 12 | z0 << 8 | y1] = (byte) (hash101 & 0xF);
                    meta[x1 << 12 | z1 << 8 | y1] = (byte) (hash111 & 0xF);
                }
            }
        }
    }

    private void interpolateBlocksFromLattice(int chunkX, int chunkZ, Block[] chunkBlocks, byte[] chunkMeta) {
        byte b0 = 63;
        this.biomesForGeneration = this.world.getWorldChunkManager()
            .getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        this.createChunkLatticeFromNoise(chunkX * 4, chunkZ * 4);

        for (int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125D;
                    double d1 = this.chunkLattice[k1 + k2];
                    double d2 = this.chunkLattice[l1 + k2];
                    double d3 = this.chunkLattice[i2 + k2];
                    double d4 = this.chunkLattice[j2 + k2];
                    double d5 = (this.chunkLattice[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.chunkLattice[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.chunkLattice[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.chunkLattice[j2 + k2 + 1] - d4) * d0;

                    for (int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i3 = 0; i3 < 4; ++i3) {
                            int j3 = i3 + k * 4 << 12 | j1 * 4 << 8 | k2 * 8 + l2;
                            short short1 = 256;
                            j3 -= short1;
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for (int k3 = 0; k3 < 4; ++k3) {
                                if ((d15 += d16) > 0.0D && (d15) < 4.0D) {
                                    chunkBlocks[j3 += short1] = Blocks.wool;

                                    switch ((byte) Math.floor(d15 / 0.6D)) {
                                        case 0 -> chunkMeta[j3] = 14;
                                        // red
                                        case 1 -> chunkMeta[j3] = 1;
                                        // orange
                                        case 2 -> chunkMeta[j3] = 4;
                                        // yellow
                                        case 3 -> chunkMeta[j3] = 5;
                                        // lime
                                        case 4 -> chunkMeta[j3] = 13;
                                        // green
                                        case 5 -> chunkMeta[j3] = 8;
                                        case 6 -> chunkMeta[j3] = 7;
                                        default -> chunkMeta[j3] = 15;
                                    }

                                }
                                /*
                                 * else if (k2 * 8 + l2 < b0)
                                 * {
                                 * chunkBlocks[j3 += short1] = Blocks.water;
                                 * }
                                 */
                                else {
                                    chunkBlocks[j3 += short1] = null;
                                }
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
    }

    private void createChunkLatticeFromNoise(int chunk_x, int chunk_z) {
        double magic3 = 684.412D;
        double magic4 = 684.412D;
        double magic1 = 512.0D;
        double magic2 = 512.0D;
        this.field_147426_g = this.noiseGen6
            .generateNoiseOctaves(this.field_147426_g, chunk_x, chunk_z, 5, 5, 200.0D, 200.0D, 0.5D);
        this.field_147427_d = this.field_147429_l.generateNoiseOctaves(
            this.field_147427_d,
            chunk_x,
            0,
            chunk_z,
            5,
            33,
            5,
            8.555150000000001D,
            4.277575000000001D,
            8.555150000000001D);
        this.field_147428_e = this.field_147431_j.generateNoiseOctaves(
            this.field_147428_e,
            chunk_x,
            0,
            chunk_z,
            5,
            33,
            5,
            684.412D,
            684.412D,
            684.412D);
        this.field_147425_f = this.field_147432_k.generateNoiseOctaves(
            this.field_147425_f,
            chunk_x,
            0,
            chunk_z,
            5,
            33,
            5,
            684.412D,
            684.412D,
            684.412D);
        boolean flag1 = false;
        boolean flag = false;
        int latticeIndex = 0;
        int i1 = 0;
        double magic5 = 8.5D;

        for (int j1 = 0; j1 < 5; ++j1) // x and z 1-5
        {
            for (int k1 = 0; k1 < 5; ++k1) {
                float f = 0.0F;
                float f1 = 0.0F;
                float f2 = 0.0F;
                byte b0 = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

                for (int l1 = -b0; l1 <= b0; ++l1) {
                    for (int i2 = -b0; i2 <= b0; ++i2) {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f3 = biomegenbase1.rootHeight;
                        float f4 = biomegenbase1.heightVariation;

                        if (this.worldType == WorldType.AMPLIFIED && f3 > 0.0F) {
                            f3 = 1.0F + f3 * 2.0F;
                            f4 = 1.0F + f4 * 4.0F;
                        }

                        float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight) {
                            f5 /= 2.0F;
                        }

                        f += f4 * f5;
                        f1 += f3 * f5;
                        f2 += f5;
                    }
                }

                f /= f2;
                f1 /= f2;
                f = f * 0.9F + 0.1F;
                f1 = (f1 * 4.0F - 1.0F) / 8.0F;
                double d12 = this.field_147426_g[i1] / 8000.0D;

                if (d12 < 0.0D) {
                    d12 = -d12 * 0.3D;
                }

                d12 = d12 * 3.0D - 2.0D;

                if (d12 < 0.0D) {
                    d12 /= 2.0D;

                    if (d12 < -1.0D) {
                        d12 = -1.0D;
                    }

                    d12 /= 1.4D;
                    d12 /= 2.0D;
                } else {
                    if (d12 > 1.0D) {
                        d12 = 1.0D;
                    }

                    d12 /= 8.0D;
                }

                ++i1;
                double d13 = (double) f1;
                double d14 = (double) f;
                d13 += d12 * 0.2D;
                d13 = d13 * 8.5D / 8.0D;
                double d5 = 8.5D + d13 * 4.0D;

                for (int j2 = 0; j2 < 33; ++j2) {
                    double d6 = ((double) j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

                    if (d6 < 0.0D) {
                        d6 *= 4.0D;
                    }

                    double d7 = this.field_147428_e[latticeIndex] / magic1;
                    double d8 = this.field_147425_f[latticeIndex] / magic2;
                    double d9 = (this.field_147427_d[latticeIndex] / 10.0D + 1.0D) / 2.0D;
                    double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

                    if (j2 > 29) {
                        double d11 = (double) ((float) (j2 - 29) / 3.0F);
                        d10 = d10 * (1.0D - d11) + -10.0D * d11;
                    }

                    this.chunkLattice[latticeIndex] = d10;
                    ++latticeIndex;
                }
            }
        }
    }

    @Override
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
        return this.provideChunk(p_73158_1_, p_73158_2_);
    }

    @Override
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {

    }

    @Override
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public String makeString() {
        return null;
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        return null;
    }

    @Override
    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
        int p_147416_5_) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int p_82695_1_, int p_82695_2_) {

    }

    @Override
    public void saveExtraData() {

    }
}
