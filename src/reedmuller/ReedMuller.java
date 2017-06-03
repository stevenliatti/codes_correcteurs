package reedmuller;

import java.math.BigInteger;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import static reedmuller.Bit.*;
import static reedmuller.Word.*;

/**
 * Classe implémentant les algorithmes de Reed Muller.
 *
 * @author Raed Abdennadher
 * @author Steven Liatti
 */
public class ReedMuller {
    private Word g[];
    private Integer r;
    private Integer startDim;
    private Integer endDim;

    /**
     * Construit un code ReedMuller d'ordre r.
     *
     * @param r l'ordre du code
     */
    public ReedMuller(int r) {
        this.r = r;
        startDim = r + 1;
        endDim = (int) (pow(2, r));
        buildG();
    }

    /**
     * Construit la matrice G pour encoder les mots.
     */
    private void buildG() {
        Bit temp = new Bit(0);
        g = new Word[startDim];
        int alternating = 0;

        for (int i = 0; i < r; i++) {
            g[i] = new Word(endDim);
            for (int j = 0; j < endDim; j++) {
                g[i].at(j, temp);
                alternating++;
                if (alternating == (int) pow(2, i)) {
                    temp = temp.not();
                    alternating = 0;
                }
            }
        }
        g[r] = new Word(endDim);
        for (int i = 0; i < endDim; i++) {
            g[r].at(i, new Bit(1));
        }
    }

    /**
     * Retourne la dimension des mots non codés.
     *
     * @return la dimension des mots non codés
     */
    public int getStartDim() {
        return startDim;
    }

    /**
     * Retourne la dimension des mots codés.
     *
     * @return la dimension des mots codés
     */
    public int getEndDim() {
        return endDim;
    }

    /**
     * Retourne un mot codé selon l'algo.
     *
     * @param word un mot
     * @return le mot codé
     */
    public Word encode(Word word) {
        if (word.size() != startDim) {
            throw new IllegalArgumentException("The word's length is false (good length = " + startDim + ")");
        }
        Word wordEncoded = new Word(endDim);
        for (int i = 0; i < endDim; i++) {
            wordEncoded.at(i, new Bit(0));
            for (int j = 0; j < startDim; j++) {
                wordEncoded.at(i, add(mult(word.at(j), g[j].at(i)), wordEncoded.at(i)));
            }
        }
        return wordEncoded;
    }

    /**
     * Retourne un mot décodé selon l'algo.
     *
     * @param word un mot codé
     * @return le mot décodé
     */
	public Word decode(Word word) {
		if (word.size() != endDim) {
			throw new IllegalArgumentException("The word's length is false (good length = " + endDim + ")");
		}
		Word wordDecoded = new Word(startDim);
		Bit xR = new Bit(word.at(0));
		wordDecoded.at(r, xR);

		Word w = new Word(endDim);
		for (int i = 0; i < endDim; i++) {
			w.at(i, add(mult(xR, g[r].at(i)), word.at(i)));
		}
		for (int i = 0; i < r; i++) {
			wordDecoded.at(i, w.at((int) pow(2, i)));
		}

		return wordDecoded;
	}

    /**
     * Bruite les bits d'un mot donné (normalement encodé) selon une probabilité.
     *
     * @param good un mot
     * @param probability la probabilité de bruiter un bit courant
     * @return le mot bruité
     */
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

    /**
     * Calcule la distance de Hamming entre deux bits.
     *
     * @param one le 1er bit
     * @param two le 2ème bit
     * @return la distance de Hamming entre deux bits
     */
	private static int hammingDistance(Bit one, Bit two) {
        return one.equals(two) ? 0 : 1;
    }

    /**
     * Calcule la distance de Hamming entre deux mots (bit à bit).
     *
     * @param one le 1er mot
     * @param two le 2ème mot
     * @return la distance de Hamming entre deux mots
     */
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
        if (y.size() != endDim && z.size() != endDim) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDim + ")");
        }
        int res = 0;
        for (int i = 0; i < endDim; i++) {
            res += (int) pow(-1, add(y.at(i), z.at(i)).v());
        }
        return res;
    }

    /**
     * Calcule la transformation F d'un mot bruité.
     *
     * @param valid un mot du code, codé
     * @param noised un mot reçu, bruité
     * @return la transformation F d'un mot bruité
     */
    private int transformationF2(Word valid, Word noised) {
        if (valid.size() != endDim && noised.size() != endDim) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDim + ")");
        }
        return endDim - 2 * hammingDistance(valid, noised);
    }

    /**
     * Débruite un mot selon la méthode de la recherche semi-exhaustive.
     *
     * @param noised un mot bruité
     * @return le mot corrigé si trouvé
     */
    public Word semiExhaustiveSearch(Word noised) {
        if (noised.size() != endDim) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDim + ")");
        }
        int min = Integer.MAX_VALUE;
        int tempMin;
        Word word = new Word(startDim);
        Word tempWord = Word.allWordAt(new Bit(0), startDim);
        BigInteger brLike = new BigInteger(endDim.toString());
        int f;
        for (int i = 0; i < endDim; i++) {
            f = transformationF2(encode(tempWord), noised);
            tempMin = f >= 0 ? (endDim - f) / 2 : (endDim + f) / 2;
            if (min > tempMin) {
                min = tempMin;
                if (f < 0) {
                    word = bigIntToWord(brLike.add(wordToBigInt(tempWord)));
                }
                else {
                    word = tempWord;
                }
            }
            tempWord = tempWord.plusOne();
        }

        return encode(word);
    }

    /**
     * Débruite un mot selon la méthode de la recherche rapide.
     *
     * @param noised un mot bruité
     * @return le mot corrigé si trouvé
     */
    public Word fastSearch(Word noised) {
        Bit q;
        Bit newF[] = new Bit[endDim];
        Word tempWord = allWordAt(ZERO, endDim);
        for (Integer i = 0; i < r; i++) {
            for (Integer k = 0; k < endDim; k++) {
                q = tempWord.at(i).not();
                if (q.equals(ZERO)) {
                    //newF[k] =
                }
            }
        }
        return null;
    }
}
