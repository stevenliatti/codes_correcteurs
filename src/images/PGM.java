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
 * Created by stevenliatti on 30.05.17.
 */
public class PGM {
    private static final String CODE = "P2";
    private int width;
    private int height;
    private int greyLevel;
    private List<String> values;
    private ReedMuller rm;

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

    public PGM encode() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getStartDimension());
            Word encoded = rm.encode(bigIntToWord);
            list.add(wordToBigInt(encoded).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    public PGM decode() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDimension());
            Word decoded = rm.decode(bigIntToWord);
            list.add(wordToBigInt(decoded).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    public PGM denoise() {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDimension());
            Word denoised = rm.semiExhaustiveSearch(bigIntToWord);
            list.add(wordToBigInt(denoised).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

    public PGM noise(double probability) {
        List<String> list = new ArrayList<>(values.size());
        for (String n : values) {
            Word bigIntToWord = bigIntToWord(new BigInteger(n), rm.getEndDimension());
            Word noised = rm.noise(bigIntToWord, probability);
            list.add(wordToBigInt(noised).toString());
        }
        return new PGM(width, height, greyLevel, list);
    }

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

    public static void write(PGM image, String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        writeAndNewLine(bw, CODE);
        writeAndNewLine(bw, "# Generated with Java master race");
        writeAndNewLine(bw, image.width + " " + image.height);
        writeAndNewLine(bw, Integer.toString(image.greyLevel));
        for (String v : image.values) {
            bw.write(v + "\n");
        }
        bw.close();
    }
}
