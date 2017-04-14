package com.example.mafangbom.projet;

import android.graphics.Bitmap;

/**
 * Created by mafangbom on 14/04/17.
 */

public interface AppFonctions {
    public Bitmap toGray(Bitmap bitmap);
    public Bitmap contrast(Bitmap bitmap, int cmin, int cmax);
    public Bitmap grayLevelExtension(Bitmap bitmap);
    public Bitmap luminosity(Bitmap bitmap, int percent);
    public Bitmap saturation(Bitmap bitmap, int percent);
    public Bitmap keepHueGray(Bitmap bitmap, int min, int max);
    public Bitmap hsv360(Bitmap bitmap, int angle);
    public Bitmap convolution(Bitmap bitmap, int [][] filter);
    public int [][] gaussianFilter(int radius, double sigma);
    public int [][] averageFilter(int radius);
    public Bitmap sobelPrewitt(Bitmap bitmap, int [][] filter1, int [][]filter2, int result);
    public Bitmap laplacian(Bitmap bitmap, int [][] filter);
    public Bitmap invert(Bitmap bitmap);
    public Bitmap pencilEffect(Bitmap bitmap);
    public void wallpaper(Bitmap bitmap);
    public void save(Bitmap bitmap);

}
