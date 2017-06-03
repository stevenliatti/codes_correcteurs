package reedmuller;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static reedmuller.Bit.*;
/**
 * Created by stevenliatti on 25.05.17.
 */
public class ReedMuller {
    private Word g[];
    private int r;
    private int startDimension;
    private int endDimension;

    public ReedMuller(int r) {
        this.r = r;
        startDimension = r + 1;
        endDimension = (int) (pow(2, r));
        buildG();
    }

    private void buildG() {
        Bit temp = new Bit(0);
        g = new Word[startDimension];
        int alternating = 0;

        for (int i = 0; i < r; i++) {
            g[i] = new Word(endDimension);
            for (int j = 0; j < endDimension; j++) {
                g[i].at(j, temp);
                alternating++;
                if (alternating == (int) pow(2, i)) {
                    temp = temp.not();
                    alternating = 0;
                }
            }
        }
        g[r] = new Word(endDimension);
        for (int i = 0; i < endDimension; i++) {
            g[r].at(i, new Bit(1));
        }
    }

    public int getR() {
        return r;
    }

    public int getStartDimension() {
        return startDimension;
    }

    public int getEndDimension() {
        return endDimension;
    }

    public Word encode(Word word) {
        if (word.size() != startDimension) {
            throw new IllegalArgumentException("The word's length is false (good length = " + startDimension + ")");
        }
        Word wordEncoded = new Word(endDimension);
        for (int i = 0; i < endDimension; i++) {
            wordEncoded.at(i, new Bit(0));
            for (int j = 0; j < startDimension; j++) {
                wordEncoded.at(i, add(mult(word.at(j), g[j].at(i)), wordEncoded.at(i)));
            }
        }
        return wordEncoded;
    }

	public Word decode(Word word) {
		if (word.size() != endDimension) {
			throw new IllegalArgumentException("The word's length is false (good length = " + endDimension + ")");
		}
		Word wordDecoded = new Word(startDimension);
		Bit xR = new Bit(word.at(0));
		wordDecoded.at(r, xR);

		Word w = new Word(endDimension);
		for (int i = 0; i < endDimension; i++) {
			w.at(i, add(mult(xR, g[r].at(i)), word.at(i)));
		}
		for (int i = 0; i < r; i++) {
			wordDecoded.at(i, w.at((int) pow(2, i)));
		}

		return wordDecoded;
	}
	
	private static int hammingDistance(Bit one, Bit two) {
        return one.equals(two) ? 0 : 1;
    }
    
    private static int hammingDistance(Word one, Word two) {
        if (one.size() != two.size()) {
            throw new IllegalArgumentException("The length's words must be the same");
        }
        int res = 0;
        for (int i = 0; i < one.size(); i++) {
            res += hammingDistance(one.at(i), two.at(i));
        }
        return res;
    }

    private int transformationF(Word y, Word z) {
        if (y.size() != endDimension && z.size() != endDimension) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDimension + ")");
        }
        int res = 0;
        for (int i = 0; i < endDimension; i++) {
            res += (int) pow(-1, add(y.at(i), z.at(i)).v());
        }
        return res;
    }

    private int transformationF2(Word y, Word z) {
        if (y.size() != endDimension && z.size() != endDimension) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDimension + ")");
        }
        return endDimension - 2 * hammingDistance(y, z);
    }

    public Word noise(Word good, double probability) {
        if (probability < 0.0 && probability >= 1.0) {
            throw new IllegalArgumentException("probability must be between 0.0 and 1.0");
        }
        Word noised = new Word(good);
        for (int i = 0; i < good.size(); i++) {
            if (random() < probability) {
                noised.at(i, noised.at(i).not());
            }
        }
        return noised;
    }

    public Word semiExhaustiveSearch(Word noised) {
        if (noised.size() != endDimension) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDimension + ")");
        }
        int min = Integer.MAX_VALUE;
        int tempMin;
        Word word = new Word(startDimension);
        Word tempWord = Word.allWordAt(new Bit(0), startDimension);
        int f;
        for (int i = 0; i < endDimension; i++) {
            f = transformationF2(encode(tempWord), noised);
            tempMin = f >= 0 ? (endDimension - f) / 2 : (endDimension + f) / 2;
            if (min > tempMin) {
                min = tempMin;
                word = tempWord;
            }
            tempWord = tempWord.plusOne();
        }

        return encode(word);
    }
}
