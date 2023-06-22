package aivech.laputa.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class LaputaWorldType extends WorldType {

    public static final LaputaWorldType INSTANCE = new LaputaWorldType();

    public LaputaWorldType() {
        super("laputa");
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        return new LaputaOverworldChunkProvider(world);
    }

    // TODO: use own biome layer
    @Override
    public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer) {
        GenLayer ret = new GenLayerBiome(200L, parentLayer, this);
        ret = GenLayerZoom.magnify(1000L, ret, 2);
        ret = new GenLayerBiomeEdge(1000L, ret);
        return ret;
    }

    @Override
    public double getHorizon(World world) {
        return 0.0D;
    }
    @Override
    public float getCloudHeight() {return 192F;}
}
