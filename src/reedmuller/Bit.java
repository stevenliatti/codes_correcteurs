package reedmuller;

/**
 * Created by stevenliatti on 25.05.17.
 */
public class Bit {
    private int value;

	public static final Bit ZERO = new Bit(0);
	public static final Bit ONE = new Bit(1);

    public Bit(int value) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = value;
    }

    public Bit(Bit other) {
        value = other.value;
    }

    public Bit add(Bit bit) {
        return new Bit((bit.value + value) % 2);
    }

    public Bit mult(Bit bit) {
        return new Bit(bit.value * value);
    }

    public Bit not() {
        return new Bit((value + 1) % 2);
    }

    public static Bit add(Bit one, Bit two) {
        return new Bit((one.value + two.value) % 2);
    }

    public static Bit mult(Bit one, Bit two) {
        return new Bit(one.value * two.value);
    }

    public int v() {
        return value;
    }

    public void v(int value) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bit bit = (Bit) o;

        return value == bit.value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}