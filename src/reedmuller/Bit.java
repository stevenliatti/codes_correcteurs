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

    /**
     * Calcule le log en base 2 arrondi à l'entier supérieur (exemple pour x = 4 => log = 5)
     *
     * @param x Un entier positif
     * @return Le log en base 2 arrondi à l'entier supérieur
     */
    private static int log2(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be 0 or greater");
        }
        if (x == 0 || x == 1) {
            return 1;
        }
        return ((int) (Math.log10(x) / Math.log10(2))) + 1;
    }

    public static Bit[] intToBitArray(int n) {
        Bit array[];
        if (n == 0) {
            array = new Bit[1];
            array[0] = new Bit(0);
        }
        else if (n == 1) {
            array = new Bit[1];
            array[0] = new Bit(1);
        }
        else {
            int size = log2(n);
            int i = 0;
            array = new Bit[size];

            while (n > 0) {
                int remainder = n % 2;
                array[i] = new Bit(remainder);
                n = n / 2;
                i++;
            }
        }
        return array;
    }

    public static Bit[] arrayAtSize(Bit[] origin, int size) {
        if (size < origin.length || size == 0) {
            throw new IllegalArgumentException("size must be greater or equal to origin size");
        }
        if (size == origin.length) {
            return origin;
        }
        Bit array[] = new Bit[size];
        for (int i = 0; i < origin.length; i++) {
            array[i] = origin[i];
        }
        for (int i = origin.length; i < size; i++) {
            array[i] = new Bit(0);
        }
        return array;
    }
}
