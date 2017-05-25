/**
 * Created by stevenliatti on 25.05.17.
 */
public class Bit {
    private byte value;

    public Bit(byte value) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = value;
    }

    public Bit add(Bit bit) {
        return new Bit((byte) ((bit.value + value) % 2));
    }

    public Bit mult(Bit bit) {
        return new Bit((byte) (bit.value * value));
    }

    public static Bit add(Bit one, Bit two) {
        return new Bit((byte) ((one.value + two.value) % 2));
    }

    public static Bit mult(Bit one, Bit two) {
        return new Bit((byte) (one.value * two.value));
    }

    public byte v() {
        return value;
    }

    public void v(byte value) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
