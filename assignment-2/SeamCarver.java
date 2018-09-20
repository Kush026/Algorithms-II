import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private Picture pic;
    private double[][] energy;
    private boolean isTraspose;

    public SeamCarver(Picture picture) {

        if (picture == null) throw new IllegalArgumentException("Invalid picture");

        pic = new Picture(picture);

        energy = new double[pic.width()][pic.height()];

        for (int i = 0; i < pic.width(); i++) {
            for (int j = 0; j < pic.height(); j++) {
                energy[i][j] = energy(i, j);
            }
        }

        isTraspose = false;
    }

    public Picture picture() {
        return pic;
    }

    public int width() {
        return pic.width();
    }

    public int height() {
        return pic.height();
    }

    public double energy(int x, int y) {
        
        if (!isValidPixel(x, y)) throw new IllegalArgumentException("Invalid pixel, x: "+x+" y: "+y);

        if (x == pic.width()-1 || x == 0 || y == 0 || y == pic.height()-1) return 1000;

        return Math.sqrt(deltaX(x, y)+deltaY(x, y));
    }

    private double deltaX(int x, int y) {
        Color colorL = pic.get(x-1, y);
        Color colorR = pic.get(x+1, y);

        int rx = colorL.getRed()-colorR.getRed();
        int gx = colorL.getGreen()-colorR.getGreen();
        int bx = colorL.getBlue()-colorR.getBlue();

        return rx*rx + gx*gx + bx*bx;
    }

    private double deltaY(int x, int y) {
        Color colorT = pic.get(x, y-1);
        Color colorB = pic.get(x, y+1);

        double ry = colorB.getRed()-colorT.getRed();
        double gy = colorB.getGreen()-colorT.getGreen();
        double by = colorB.getBlue()-colorT.getBlue();

        return ry*ry + gy*gy + by*by;
    }

    public int[] findHorizontalSeam() {

        if (!isTraspose) this.transpose();

        return findVertSeam();
    }

    public int[] findVerticalSeam() {
        if (isTraspose) this.transpose();

        return findVertSeam();
    }

    private int[] findVertSeam() {

        int h = energy[0].length;
        int w = energy.length;

        double[][] disTo = new double[w][h];
        int[][] edgeTo = new int[w][h];

        // initializing the distance
        for (int i = 1; i < h; i++) {
            for (int j = 0; j < w; j++) {
                disTo[j][i] = Double.POSITIVE_INFINITY;
            }
        }

        // processing here
        for (int i = 0; i < h-1; i++) {
            for (int j = 0; j < w; j++) {

                if (j-1 >= 0 && disTo[j][i]+energy[j-1][i+1] < disTo[j-1][i+1]) {
                    disTo[j-1][i+1] = disTo[j][i]+energy[j-1][i+1];
                    edgeTo[j-1][i+1] = j;
                }

                if (disTo[j][i]+energy[j][i+1] < disTo[j][i+1]) {
                    disTo[j][i+1] = disTo[j][i]+energy[j][i+1];
                    edgeTo[j][i+1] = j;
                }

                if (j+1 < w && disTo[j][i]+energy[j+1][i+1] < disTo[j+1][i+1]) {
                    disTo[j+1][i+1] = disTo[j][i]+energy[j+1][i+1];
                    edgeTo[j+1][i+1] = j;
                }
            }
        }
        double min = disTo[0][h-1];
        int minCol = 0;
        for (int i = 1; i < w; i++) {
            if (disTo[i][h-1] < min) {
                minCol = i;
                min = disTo[i][h-1];
            }
        }
        int[] out = new int[h];
        for (int i = h-1; i >= 0; i--) {
            out[i] = minCol;
            minCol = edgeTo[minCol][i];
        }

        return out;
    }

    public void removeHorizontalSeam(int[] seam) {

        if (pic.height() <= 1) throw new IllegalArgumentException("width is less than or equal to 1");
        if (!isValidHorizontalSeam(seam)) throw new IllegalArgumentException("Invalid seam");

        int w = pic.width();
        int h = pic.height();

        Picture newPic = new Picture(w, h-1);

        for (int i = 0; i < w; i++) {
            int col = 0;
            for (int j = 0; j < h; j++) {
                if (seam[i] != j) {
                    newPic.set(i, col, pic.get(i, j));
                    col++;
                }
            }
        }

        pic = newPic;

        if (isTraspose) removeVerticalEnergy(seam);
        else removeHorizontalEnergy(seam);
    }
    public void removeVerticalSeam(int[] seam) {

        if (pic.width() <= 1) throw new IllegalArgumentException("Height is less than or equal to 1");
        if (!isValidVerticalSeam(seam)) throw new IllegalArgumentException("Invalid seam");

        int w = pic.width();
        int h = pic.height();

        Picture newPic = new Picture(w-1, h);

        for (int j = 0; j < h; j++) {
            int row = 0;
            for (int i = 0; i < w; i++) {
                if (seam[j] != i) {
                    newPic.set(row, j, pic.get(i, j));
                    row++;
                }
            }
        }
        pic = newPic;

        if (isTraspose) removeHorizontalEnergy(seam);
        else removeVerticalEnergy(seam);
    }

    private boolean isValidPixel(int x, int y) {
        return x >= 0 && x < pic.width() && y >= 0 && y < pic.height();
    }

    private boolean isValidVerticalSeam(int[] seam) {
        if (seam == null || seam.length != pic.height()) return false;

        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i]-seam[i-1]) > 1) return false;
        }

        return true;
    }

    private void removeVerticalEnergy(int[] seam) {
        int w = energy.length;
        int h = energy[0].length;
        double[][] newEnergy = new double[w-1][h];

        for (int j = 0; j < h; j++) {
            int col = 0;
            for (int i = 0; i < w; i++) {
                if (i != seam[j]) {
                    newEnergy[col][j] = energy[i][j];
                    col++;
                }
            }

            if (isTraspose) {
                int left = seam[j]-1;
                if (left >= 0) newEnergy[left][j] = energy(j, left);
                if (seam[j] < newEnergy.length) newEnergy[seam[j]][j] = energy(j, seam[j]);
            }
            else {
                int left = seam[j]-1;
                if (left >= 0) newEnergy[left][j] = energy(left, j);
                if (seam[j] < newEnergy.length) newEnergy[seam[j]][j] = energy(seam[j], j);
            }
        }

        energy = newEnergy;
    }

    private void removeHorizontalEnergy(int[] seam) {
        int w = energy.length;
        int h = energy[0].length;
        double[][] newEnergy = new double[w][h-1];

        for (int i = 0; i < w; i++) {
            int row = 0;
            for (int j = 0; j < h; j++) {
                if (j != seam[i]) {
                    newEnergy[i][row] = energy[i][j];
                    row++;
                }
            }

            if (isTraspose) {
                int top = seam[i]-1;
                if (top >= 0) newEnergy[i][top] = energy(top, i);
                if (seam[i] < newEnergy[0].length) newEnergy[i][seam[i]] = energy(seam[i], i);
            }
            else {
                int top = seam[i]-1;
                if (top >= 0) newEnergy[i][top] = energy(i, top);
                if (seam[i] < newEnergy[0].length) newEnergy[i][seam[i]] = energy(i, seam[i]);
            }
        }

        energy = newEnergy;
    }

    private boolean isValidHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != pic.width()) return false;

        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i]-seam[i-1]) > 1) return false;
        }

        return true;
    }

    private void transpose() {
        int h = energy.length;
        int w = energy[0].length;
        double[][] transMatrix = new double[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                transMatrix[i][j] = energy[j][i];
            }
        }

        energy = transMatrix;
        isTraspose = !isTraspose;
    }
}