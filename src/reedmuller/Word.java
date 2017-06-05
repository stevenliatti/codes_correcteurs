package reedmuller;

import java.math.BigInteger;
import java.util.Arrays;

import static reedmuller.Bit.*;

/**
 * Classe représentant un mot composé de bits.
 *
 * @author Raed Abdennadher
 * @author Steven Liatti
 */
public class Word {
    private Bit[] value;

    /**
     * Construit un mot vide de longeur size.
     *
     * @param size la taille du mot
     */
    public Word(int size) {
        value = new Bit[size];
    }

    /**
     * Construit un mot à partir d'un autre. Copie profonde.
     *
     * @param other un autre mot
     */
    public Word(Word other) {
        value = new Bit[other.size()];
        for (int i = 0; i < other.size(); i++) {
            value[i] = new Bit(other.value[i]);
        }
    }

    /**
     * Construit un mot à partir d'un tableau de bits.
     *
     * @param bitArray un tableau de bits
     */
    public Word(Bit[] bitArray) {
        value = new Bit[bitArray.length];
        for (int i = 0; i < bitArray.length; i++) {
            value[i] = new Bit(bitArray[i]);
        }
    }

    /**
     * Retourne la taille du mot.
     *
     * @return la taille du mot
     */
    public int size() {
        return value.length;
    }

    /**
     * Retourne la valeur du bit à l'indice i.
     *
     * @param i l'indice
     * @return le bit à l'indice donné
     */
    public Bit at(int i) {
        if (i >= this.size() && i < 0) {
            throw new IllegalArgumentException("Index must be less or equal to " + (this.size() - 1));
        }
        return value[i];
    }

    /**
     * Modifie le bit par celui fournit à l'indice i.
     *
     * @param i l'indice
     * @param bit le nouveau bit
     */
    public void at(int i, Bit bit) {
        if (i >= this.size() && i < 0) {
            throw new IllegalArgumentException("Index must be less or equal to " + (this.size() - 1));
        }
        value[i] = new Bit(bit);
    }

    /**
     * Retourne le mot courant + 1. Propagation de la retenue
     *
     * @return le mot courant + 1
     */
    public Word plusOne() {
        Word newWord = new Word(this);;
        newWord.value[0] = add(value[0], ONE);
        if (newWord.value[0].equals(ONE)) {
            return newWord;
        }
        else {
            int i = 1;
            do {
                newWord.value[i] = add(newWord.value[i], ONE);
                i++;
            } while (newWord.value[i - 1].equals(ZERO) && i < size());
        }
        return newWord;
    }

    /**
     * Retourne l'inverse d'un mot, bit à bit.
     *
     * @return l'inverse bit à bit
     */
    public Word not() {
        Word newWord = new Word(size());
        for (int i = 0; i < size(); i++) {
            newWord.value[i] = value[i].not();
        }
        return newWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return Arrays.equals(value, word.value);
    }

	/**
	 * Dans cette représentation, les bits de poids forts sont à tout à
	 * gauche et ceux de poids faible tout à droite (comme dans le sens
	 * de lecture).
	 *
	 * @return le mot sous forme de String
	 */
    public String reverse() {
        String str = "";
        for (int i = size() - 1; i >= 0; i--) {
            str += value[i] + "";
        }
        return str;
    }

	/**
	 * Construit le string inverse de reverse() (comme dans le cours finalement).
	 *
	 * @return le mot sous forme de String
	 */
	@Override
    public String toString() {
	    String str = "";
	    for (int i = 0; i < size(); i++) {
		    str += value[i] + "";
	    }
	    return str;
    }

    /**
     * Retourne un mot de taille size avec tous ses bits à la valeur donnée.
     *
     * @param bit la valeur des bits
     * @param size la taille du mot
     * @return un nouveau mot
     */
    public static Word allWordAt(Bit bit, int size) {
        Word word = new Word(size);
        for (int i = 0; i < size; i++) {
            word.value[i] = new Bit(bit);
        }
        return word;
    }

    /**
     * Calcule le log en base 2 arrondi à l'entier supérieur (exemple pour x = 4 => log2 = 3,
     * x = 8 => log2 = 4, x = 16 => log2 = 5, etc.)
     *
     * @param x Un entier positif
     * @return Le log en base 2 arrondi à l'entier supérieur
     */
    public static long log2(long x) {
        if (x < 0) {
            throw new IllegalArgumentException("x must be 0 or greater");
        }
        if (x == 0 || x == 1) {
            return 1;
        }
        return ((long) (Math.log10(x) / Math.log10(2))) + 1;
    }

    /**
     * Calcule le complément à deux d'un @{@link BigInteger}.
     *
     * @param n un BigInteger
     * @return le complément à deux
     */
    private static BigInteger twoComplement(BigInteger n){
        BigInteger tempBg = new BigInteger(n.abs().toString());
        Word tempWord = bigIntToWord(tempBg);
        tempWord = tempWord.not();
        tempWord = tempWord.plusOne();
        return wordToBigInt(tempWord);
    }

    /**
     * Convertit un BigInteger en mot.
     *
     * @param n un BigInteger
     * @return un nouveau mot
     */
    public static Word bigIntToWord(BigInteger n) {
        String reverse;
        if (n.signum() < 0) {
            reverse = twoComplement(n).toString(2);
        }
        else {
            reverse = n.toString(2);
        }
        Bit word[] = new Bit[reverse.length()];
        for (int i = 0; i < reverse.length(); i++) {
            word[i] = new Bit(reverse.charAt(reverse.length() - i - 1));
        }
        return new Word(word);
    }

    /**
     * Convertit un mot d'une certaine taille vers une taille plus grande.
     *
     * @param origin le mot d'origine
     * @param size la taille souhaitée
     * @return le nouveau mot à la bonne taille
     */
    private static Word wordAtSize(Word origin, int size) {
        if (size < origin.size() || size == 0) {
            throw new IllegalArgumentException("size must be greater or equal to origin size");
        }
        if (size == origin.size()) {
            return origin;
        }
        Word word = new Word(size);
        for (int i = 0; i < origin.size(); i++) {
            word.at(i, origin.at(i));
        }
        for (int i = origin.size(); i < size; i++) {
            word.at(i, new Bit(0));
        }
        return word;
    }

    /**
     * Convertit un BigInteger en mot à la taille donnée.
     *
     * @param n un BigInteger
     * @param size la taille donnée
     * @return un nouveau mot
     */
    public static Word bigIntToWord(BigInteger n, int size) {
        return wordAtSize(bigIntToWord(n), size);
    }

    /**
     * Convertit un mot en BigInteger.
     *
     * @param word le mot à convertir
     * @return un BigInteger
     */
    public static BigInteger wordToBigInt(Word word) {
        return new BigInteger(word.reverse(), 2);
    }
}
