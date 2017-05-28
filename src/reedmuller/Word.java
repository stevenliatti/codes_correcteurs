package reedmuller;

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

    public Word addBitToBit(Word other) {
        if (other.size() != this.size()) {
            throw new IllegalArgumentException("The other's size must be equal to " + this.size());
        }
        Word res = new Word(this.size());
        for (int i = 0; i < this.size(); i++) {
            res.value[i] = Bit.add(this.value[i], other.value[i]);
        }
        return res;
    }

    public Word multBitToBit(Word other) {
        if (other.size() != this.size()) {
            throw new IllegalArgumentException("The other's size must be equal to " + this.size());
        }
        Word res = new Word(this.size());
        for (int i = 0; i < this.size(); i++) {
            res.value[i] = Bit.mult(this.value[i], other.value[i]);
        }
        return res;
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
        for (int i = 0; i < size(); i++) {
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

    public static Word intToWord(int n) {
        Word word;
        if (n == 0) {
            word = Word.allWordAt(new Bit(0), 1);
        }
        else if (n == 1) {
            word = Word.allWordAt(new Bit(1), 1);
        }
        else {
            int size = log2(n);
            int i = 0;
            word = new Word(size);

            while (n > 0) {
                int remainder = n % 2;
                word.at(i, new Bit(remainder));
                n = n / 2;
                i++;
            }
        }
        return word;
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

    public static Word intToWord(int n, int size) {
        return wordAtSize(intToWord(n), size);
    }

    public static int wordToInt(Word word) {
        int res = 0;
        for (int i = 0; i < word.size(); i++) {
            res += word.at(i).equals(ZERO) ? 0 : Math.pow(2, i);
        }
        return res;
    }
}
