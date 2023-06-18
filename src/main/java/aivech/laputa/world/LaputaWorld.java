package aivech.laputa.world;

public class LaputaWorld {

    public static final LaputaWorldType LAPUTA_WORLD_TYPE = new LaputaWorldType();

    public static void preInit() {

    }

    // The chunk array is organized as vertical columns 256 blocks high.
    // The columns increase along the Z axis, then the X axis.
    public static int getChunkIndex(int x, int y, int z) {
        return y + z * 0x100 + x * 0x1000;
    }
}
