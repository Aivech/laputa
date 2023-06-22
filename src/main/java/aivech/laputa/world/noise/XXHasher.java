package aivech.laputa.world.noise;

import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;

import java.nio.ByteBuffer;

public class XXHasher {
    // public static XXHash64 HASHER = XXHashFactory.fastestJavaInstance().hash64();
    private final long seed;
    private final XXHash64 hasher;

    private final ByteBuffer hashbytes;
    public XXHasher(long seed) {
        this.seed = seed;
        this.hasher = XXHashFactory.fastestJavaInstance().hash64();

        this.hashbytes = ByteBuffer.allocate(4*3);
    }

    public long hash(int a1, int a2, int a3) {
        this.hashbytes.clear();
        this.hashbytes.putInt(a1).putInt(a2).putInt(a3);
        this.hashbytes.rewind();
        // assert this.hasher.hash(this.hashbytes, this.seed) == HASHER.hash(this.hashbytes.rewind(), this.seed);
        return this.hasher.hash(this.hashbytes, this.seed);
    }

    public long hash(int a1, int a2) {
        return this.hash(a1, a2, 0);
    }

    public static float getHashFloat(long hash, int index) {
        if (index > 3) throw new IndexOutOfBoundsException();
        return ((hash >> (index*4)) & 0xFF) * (2f/255f) - 1f;
    }
}
