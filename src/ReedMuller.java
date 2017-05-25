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
