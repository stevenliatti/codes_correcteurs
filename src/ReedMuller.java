/**
 * Created by stevenliatti on 25.05.17.
 */
public class ReedMuller {
    private Bit g[][];
    private int r;

    public ReedMuller(int r) {
        this.r = r;
        buildG();
    }

    private void buildG() {
        int lines = r + 1;
        int columns = (int) (Math.pow(2, r));
        Bit temp = new Bit(0);
        g = new Bit[lines][columns];
        int alternating = 0;

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < columns; j++) {
                g[i][j] = new Bit(temp);
                alternating++;
                if (alternating == (int) Math.pow(2, i)) {
                    temp = temp.not();
                    alternating = 0;
                }
            }
        }

        for (int i = 0; i < columns; i++) {
            g[r][i] = new Bit(1);
        }
    }
}
