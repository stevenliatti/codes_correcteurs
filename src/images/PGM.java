package images;

import reedmuller.ReedMuller;
import reedmuller.Word;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static reedmuller.Word.*;

/**
 * Classe représentant une image au format PGM.
 *
 * @author Raed Abdennadher
 * @author Steven Liatti
 */
public class PGM {
    private static final String CODE = "P2";
    private int width;
    private int height;
    private int greyLevel;
    private List<String> values;
    private ReedMuller rm;

    /**
     * Construit une image PGM à partir des ses dimensions, un niveau de gris max
     * et la liste de ses valeurs des pixels.
     *
     * @param width la largeur
     * @param height la hauteur
     * @param greyLevel le niveau de gris
     * @param values la liste des valeurs pour les pixels
     */
    public PGM(int width, int height, int greyLevel, List<String> values) {
        this.width = width;
        this.height = height;
        this.greyLevel = greyLevel;
        this.values = values;
        this.rm = new ReedMuller((int) log2(greyLevel) - 1);
    }

    @Override
    public String toString() {
        return "PGM{" +
                "width=" + width +
                ", height=" + height +
                ", greyLevel=" + greyLevel +
                ", values=" + values +
                '}';
    }

    /**
     * Encode une image PGM avec l'algo de ReedMuller.
     *
     * @return l'image encodée
     */
    public PGM encode() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getStartDim());
            Word encoded = rm.encode(bigIntToWord);
            list.add(wordToBigInt(encoded).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    /**
     * Décode une image PGM avec l'algo de ReedMuller.
     *
     * @return l'image décodée
     */
    public PGM decode() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDim());
            Word decoded = rm.decode(bigIntToWord);
            list.add(wordToBigInt(decoded).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    /**
     * Bruite une image PGM selon une certaine probabilité.
     *
     * @param probability la probabilité
     * @return l'image bruitée
     */
    public PGM noise(double probability) {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDim());
            Word noised = rm.noise(bigIntToWord, probability);
            list.add(wordToBigInt(noised).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    /**
     * Débruite une image PGM avec l'algo de ReedMuller.
     *
     * @return l'image débruitée
     */
    public PGM denoise() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDim());
            Word denoised = rm.semiExhaustiveSearch(bigIntToWord);
            list.add(wordToBigInt(denoised).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    /**
     * Construit une image PGM à partir d'un fichier.
     *
     * @param filename le nom du fichier
     * @return une image PGM
     * @throws IOException si le fichier pas trouvé par exemple
     * @throws IllegalArgumentException si le fichier n'est pas conforme
     */
    public static PGM read(String filename) throws IOException, IllegalArgumentException {
        PGM pgm;
        int width = 0;
        int height = 0;
        int greyLevel = 0;
        List<String> list = new ArrayList<>();
        int i = 1;
        StringTokenizer st;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            switch (i) {
                case 1:
                    if (!line.equals(CODE)) {
                        throw new IllegalArgumentException("The file is not a PGM file");
                    }
                    break;
                case 2:
                    break;
                case 3:
                    st = new StringTokenizer(line);
                    width = Integer.parseInt(st.nextToken());
                    height = Integer.parseInt(st.nextToken());
                    break;
                case 4:
                    greyLevel = Integer.parseInt(line);
                    break;
                default:
                    st = new StringTokenizer(line);
                    while (st.hasMoreElements()) {
                        list.add(st.nextToken());
                    }
                    break;
            }
            i++;
        }
        br.close();
        pgm = new PGM(width, height, greyLevel, list);
        return pgm;
    }

    private static void writeAndNewLine(BufferedWriter bw, String str) throws IOException {
        bw.write(str);
        bw.newLine();
    }

    /**
     * Écris une image PGM sur le disque.
     *
     * @param image l'image PGM
     * @param filename le nom du fichier
     * @throws IOException si le fichier pas trouvé par exemple
     */
    public static void write(PGM image, String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        writeAndNewLine(bw, CODE);
        writeAndNewLine(bw, "# Generated with Java master race");
        writeAndNewLine(bw, image.width + " " + image.height);
        writeAndNewLine(bw, Integer.toString(image.greyLevel));
        for (String v : image.values) {
            bw.write(v + " ");
        }
        bw.close();
    }
}
