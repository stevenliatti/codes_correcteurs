package reedmuller;

import java.math.BigInteger;
import java.util.Arrays;

import static reedmuller.Bit.*;

/**
 * Created by stevenliatti on 28.05.17.
 */
public class Word {
    private Bit[] value;

    public Word(int size) {
        value = new Bit[size];
    }

    public Word(Word other) {
        value = new Bit[other.size()];
        for (int i = 0; i < other.size(); i++) {
            value[i] = new Bit(other.value[i]);
        }
    }

    public Word(Bit[] bitArray) {
        value = new Bit[bitArray.length];
        for (int i = 0; i < bitArray.length; i++) {
            value[i] = new Bit(bitArray[i]);
        }
    }

    public int size() {
        return value.length;
    }

    public Bit at(int i) {
        if (i >= this.size() && i < 0) {
            throw new IllegalArgumentException("Index must be less or equal to " + (this.size() - 1));
        }
        return value[i];
    }

    public void at(int i, Bit bit) {
        if (i >= this.size() && i < 0) {
            throw new IllegalArgumentException("Index must be less or equal to " + (this.size() - 1));
        }
        value[i] = new Bit(bit);
    }

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

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(value, word.value);
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = size() - 1; i >= 0; i--) {
            str += value[i] + "";
        }
        return str;
    }

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

    public static Word bigIntToWord(BigInteger n) {
        String reverse = n.toString(2);
        Bit word[] = new Bit[reverse.length()];
        for (int i = 0; i < reverse.length(); i++) {
            word[i] = new Bit(reverse.charAt(reverse.length() - i - 1));
        }
        return new Word(word);
    }

    public static Word wordAtSize(Word origin, int size) {
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

    public static Word bigIntToWord(BigInteger n, int size) {
        return wordAtSize(bigIntToWord(n), size);
    }

    public static BigInteger wordToBigInt(Word word) {
        return new BigInteger(word.toString(), 2);
    }
}
