package reedmuller;

import java.math.BigInteger;

import static java.lang.Math.*;
import static reedmuller.Bit.add;
import static reedmuller.Bit.mult;
import static reedmuller.Word.bigIntToWord;
import static reedmuller.Word.wordToBigInt;

/**
 * Classe implémentant les algorithmes de Reed Muller.
 *
 * @author Raed Abdennadher
 * @author Steven Liatti
 */
public class ReedMuller {
	private static final boolean DEBUG_RM = false;

    private Word g[];
    private int h[][];
    private Integer r;
    private Integer startDim;
    private Integer endDim;

    private void printG() {
	    for (int i = 0; i < startDim; i++) {
		    System.out.println(g[i]);
	    }
    }

    private void printH() {
	    for (int i = 0; i < endDim; i++) {
		    for (int j = 0; j < endDim; j++) {
			    System.out.print(h[i][j] + "\t");
		    }
		    System.out.println();
	    }
    }

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
        h = buildH(r);
        // imprime les matrices G et H en mode debug
        if (DEBUG_RM) {
	        printG();
	        printH();
        }
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
	 * Retourne l'ordre du code.
	 *
	 * @return l'ordre du code
	 */
	public Integer getR() {
		return r;
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

    /**
     * Calcule la transformation F d'un mot bruité.
     *
     * @param valid un mot du code, codé
     * @param noised un mot reçu, bruité
     * @return la transformation F d'un mot bruité
     */
    private int transformationF(Word valid, Word noised) {
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
            f = transformationF(encode(tempWord), noised);
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
	 * Construit la matrice H. Récursive.
	 *
	 * @param r l'ordre courant
	 * @return la mtrice H
	 */
	private int[][] buildH(int r) {
    	if (r == 1) {
		    int tempH[][] = new int[2][2];
			tempH[0][0] = 1;
		    tempH[0][1] = 1;
		    tempH[1][0] = 1;
		    tempH[1][1] = -1;
		    return tempH;
		}
		else {
    		int size = (int) pow(2, r);
    		int subH[][] = buildH(r - 1);
		    int tempH[][] = new int[size][size];

		    for (int i = 0; i < size / 2; i++) {
			    for (int j = 0; j < size / 2; j++) {
				    tempH[i][j] = subH[i][j];
			    }
		    }
            for (int i = 0; i < size; i++) {
                for (int j = size / 2; j < size; j++) {
                    tempH[i][j] = subH[i % (size / 2)][j % (size / 2)];
                }
            }
		    for (int i = size / 2; i < size; i++) {
			    for (int j = 0; j < size / 2; j++) {
				    tempH[i][j] = subH[i % (size / 2)][j];
			    }
		    }
		    for (int i = size / 2; i < size; i++) {
			    for (int j = size / 2; j < size; j++) {
				    tempH[i][j] = -subH[i % (size / 2)][j % (size / 2)];
			    }
		    }

		    return tempH;
		}
    }

    /**
     * Débruite et décode un mot selon la méthode de la recherche rapide.
     *
     * @param noised un mot bruité
     * @return le mot corrigé et décodé si trouvé
     */
    public Word fastSearch(Word noised) {
        if (noised.size() != endDim) {
            throw new IllegalArgumentException("The word's length is false (good length = " + endDim + ")");
        }
		int fHat[] = new int[endDim];
		int f[] = new int[endDim];
		int max = Integer.MIN_VALUE;
		Integer index = 0;

	    for (int i = 0; i < endDim; i++) {
		    f[i] = (int) pow(-1, noised.at(i).v());
	    }

	    for (int i = 0; i < endDim; i++) {
		    for (int j = 0; j < endDim; j++) {
			    fHat[i] += f[j] * h[j][i];
		    }
		    if (abs(fHat[i]) > max) {
		    	max = abs(fHat[i]);
		    	index = i;
		    }
	    }

        index = fHat[index] < 0 ? (index + endDim) % (2 * endDim) : index;
        return bigIntToWord(new BigInteger(index.toString()));
    }
}
