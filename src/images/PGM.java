package images;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by stevenliatti on 30.05.17.
 */
public class PGM {
    private static final String CODE = "P2";
    private int width;
    private int height;
    private int greyLevel;
    private List<Integer> values;

    public PGM(int width, int height, int greyLevel, List<Integer> values) {
        this.width = width;
        this.height = height;
        this.greyLevel = greyLevel;
        this.values = values;
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

    private void writeAndNewLine(BufferedWriter bw, String str) throws IOException {
        bw.write(str);
        bw.newLine();
    }

    public void write(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        writeAndNewLine(bw, CODE);
        writeAndNewLine(bw, "# Generated with Java master race");
        writeAndNewLine(bw, width + " " + height);
        writeAndNewLine(bw, Integer.toString(greyLevel));
        for (int v : values) {
            bw.write(v + " ");
        }
        bw.close();
    }

    public static PGM read(String filename) throws IOException, IllegalArgumentException {
        PGM pgm;
        int width = 0;
        int height = 0;
        int greyLevel = 0;
        List<Integer> list = new ArrayList<>();
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
                        list.add(Integer.parseInt(st.nextToken()));
                    }
                    break;
            }
            i++;
        }
        br.close();
        pgm = new PGM(width, height, greyLevel, list);
        return pgm;
    }
}
