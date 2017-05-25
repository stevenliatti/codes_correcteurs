package reedmuller;

import static java.lang.Math.pow;
/**
 * Created by stevenliatti on 25.05.17.
 */
public class ReedMuller {
    private Bit g[][];
    private int r;
    private int startDimension;
    private int endDimension;

    public ReedMuller(int r) {
        this.r = r;
        startDimension = r + 1;
        endDimension = (int) (pow(2, r));
        buildG();
    }

    public static void printWord(Bit[] word) {
        for (int i = 0; i < word.length; i++) {
            System.out.print(word[i] + "");
        }
        System.out.println();
    }

    public static int log2(int x) {
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

    public Bit[] encode(Bit[] word) {
        if (word.length != r + 1) {
            throw new IllegalArgumentException("The word's length is false (good length = " + startDimension + ")");
        }
        Bit wordEncoded[] = new Bit[endDimension];
        for (int i = 0; i < endDimension; i++) {
            wordEncoded[i] = new Bit(0);
            for (int j = 0; j < startDimension; j++) {
                wordEncoded[i] = wordEncoded[i].add(Bit.mult(word[j], g[j][i]));
            }
        }
        return wordEncoded;
    }

    private void buildG() {
        Bit temp = new Bit(0);
        g = new Bit[startDimension][endDimension];
        int alternating = 0;

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < endDimension; j++) {
                g[i][j] = new Bit(temp);
                alternating++;
                if (alternating == (int) pow(2, i)) {
                    temp = temp.not();
                    alternating = 0;
                }
            }
        }

        for (int i = 0; i < endDimension; i++) {
            g[r][i] = new Bit(1);
        }
    }
}
