package reedmuller;

/**
 * Classe représentant un bit.
 *
 * @author Raed Abdennadher
 * @author Steven Liatti
 */
public class Bit {
    private int value;

	public static final Bit ZERO = new Bit(0);
	public static final Bit ONE = new Bit(1);

    /**
     * Construit un bit à partir d'un entier (0 ou 1).
     *
     * @param value 0 ou 1
     */
    public Bit(int value) {
        if (value > 1 || value < 0) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = value;
    }

    /**
     * Construit un bit à partir d'un caractère ('0' ou '1').
     *
     * @param c '0' ou '1'
     */
    public Bit(char c) {
        if ((c != '0') && (c != '1')) {
            throw new IllegalArgumentException("The value must be 0 or 1");
        }
        this.value = Character.getNumericValue(c);
    }

    /**
     * Construit un bit à partir d'un autre.
     *
     * @param other un autre bit
     */
    public Bit(Bit other) {
        value = other.value;
    }

    /**
     * Retourne l'inverse du bit courant.
     *
     * @return !this
     */
    public Bit not() {
        return new Bit((value + 1) % 2);
    }

    /**
     * Addition entre deux bits. Sans retenue.
     *
     * @param one le 1er bit
     * @param two le 2ème bit
     * @return la somme des bits
     */
    public static Bit add(Bit one, Bit two) {
        return new Bit((one.value + two.value) % 2);
    }

    /**
     * Multiplication entre deux bits. Sans retenue.
     *
     * @param one le 1er bit
     * @param two le 2ème bit
     * @return le produit des bits
     */
    public static Bit mult(Bit one, Bit two) {
        return new Bit(one.value * two.value);
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
