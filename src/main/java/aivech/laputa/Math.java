package aivech.laputa;

public class Math {
    /**
     * Interpolate linearly between two values
     * @param a Value at t = 0
     * @param b Value at t = 1
     * @param t Position to find the value at
     * @return The interpolated value
     */
    public static float lerpf (float a, float b, float t) {
        return a + (b-a)*t;
    }

    public static float bilerpf (float a00, float a01, float a10, float a11, float u, float v) {
        float a = lerpf(a00, a10, u);
        float b = lerpf(a01, a11, u);
        return lerpf(a, b, v);
    }
}
