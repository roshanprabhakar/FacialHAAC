public class ConvolutionKernel {

    private int[][] kernel;
    private int[][] original;

    public ConvolutionKernel(int[][] kernel) {
        this.kernel = kernel;
        this.original = deepClone(kernel);
    }

    public void setValue(int x, int y, int value) {
        kernel[y][x] = value;
    }

    public void setRow(int row, int value) {
        for (int c = 0; c < kernel[row].length; c++) {
            kernel[row][c] = value;
        }
    }

    public void setColumn(int col, int value) {
        for (int r = 0; r < kernel.length; r++) {
            kernel[r][col] = value;
        }
    }

    public void reset() {
        kernel = original;
    }

    private int[][] deepClone(int[][] kernel) {
        int[][] out = new int[kernel.length][kernel[0].length];
        for (int r = 0; r < kernel.length; r++) {
            out[r] = kernel[r].clone();
        }
        return out;
    }

    public int[][] getKernel() {return this.kernel;}

    public int value(int r, int c) {return kernel[r][c];}

    public int getHeight() {return kernel.length;}

    public int getWidth() {return kernel[0].length;}
}
