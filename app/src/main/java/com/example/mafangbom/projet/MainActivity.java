package com.example.mafangbom.projet;



import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;
//test

public class MainActivity extends AppCompatActivity implements AppFonctions {
    private static final int CAMERA_REQUEST = 1888;
    private Toolbar toolbar;
    public ImageView imageToUpload;
    public Bitmap currentBitmap;
    public  Bitmap modifiedBitmap;
    private static final int PICK_IMAGE = 100;
    static int [][] LAPLACIEN4 = {{0,1,0},{1,-4,1},{0,1,0}};
    static int [][] LAPLACIEN8 = {{1,1,1},{1,-8,1},{1,1,1}};
    static int [][] SOBEL1 = {{-1,0,1},{-2 ,0, 2},{-1,0,1}};
    static int [][] SOBEL2 = {{-1,-2,-1},{0 ,0, 0},{1,2,1}};
    static int [][] PREWITT1 = {{-1,0,1},{-1,0,1},{-1,0,1}};
    static int [][] PREWITT2 = {{-1,-1,-1},{0,0,0},{1,1,1}};
    static int [][] gaussien = {{1, 2, 3, 2, 1}, {2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
    static int [][] MOYENNE3 = {{1,1,1},{1,1,1},{1,1,1}};
    static int [][] MOYENNE5 = {{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1}};
    static int MIN_YELLOW = 60;
    static int MAX_YELLOW = 50;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        setTitle("Projet Android");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        PhotoViewAttacher photoView = new PhotoViewAttacher(imageToUpload);
        photoView.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    //test commi
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gray:

                imageToUpload.setImageBitmap(toGray(modifiedBitmap));
                break;
            case R.id.contrasteMin:
                imageToUpload.setImageBitmap(contrast(modifiedBitmap,128,255));
                break;
            case R.id.contrasteMoy:
                imageToUpload.setImageBitmap(contrast(modifiedBitmap,64,191));
                break;
            case R.id.contrasteMax:
                imageToUpload.setImageBitmap(contrast(modifiedBitmap,0,255));
                break;
            case R.id.grayLevelExtension:
                imageToUpload.setImageBitmap(grayLevelExtension(modifiedBitmap));
                break;
            case R.id.blueGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,250,210));
                break;
            case R.id.yellowGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,MIN_YELLOW,MAX_YELLOW));
                break;
            case R.id.magentaGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,344,300));
                break;
            case R.id.redGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,0,345));
                break;
            case R.id.greenGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,140,90));
                break;
            case  R.id.lum25:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,25));
                break;
            case R.id.lum50:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,50));
                break;
            case R.id.lum75:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,75));
                break;
            case  R.id.lum100:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,100));
                break;
            case R.id.lumM25:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,-25));
                break;
            case R.id.lumM50:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,-50));
                break;
            case R.id.lumM75:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,-75));
                break;
            case R.id.lumM100:
                imageToUpload.setImageBitmap(luminosity(modifiedBitmap,-100));
                break;
            case R.id.gallery:
                onPickImage();
                break;
            case  R.id.sat25:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,25));
                break;
            case R.id.sat50:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,50));
                break;
            case R.id.sat75:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,75));
                break;
            case R.id.sat100:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,100));
                break;
            case R.id.satM25:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,-25));
                break;
            case R.id.satM50:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,-50));
                break;
            case R.id.satM75:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,-75));
                break;
            case R.id.satM100:
                imageToUpload.setImageBitmap(saturation(modifiedBitmap,-100));
                break;
            case R.id.rouge:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,360));
                break;
            case R.id.magenta:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,330));
                break;
            case R.id.vert:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,120));
                break;
            case R.id.bleu:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,225));
                break;
            case  R.id.cyan:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,180));
                break;
            case R.id.sepia:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,35));
                break;
            case R.id.jaune:
                imageToUpload.setImageBitmap(hsv360(modifiedBitmap,60));
                break;

            case  R.id.laplacian:
                imageToUpload.setImageBitmap(laplacian(modifiedBitmap,LAPLACIEN8));
                break;
            case R.id.sobel:
                imageToUpload.setImageBitmap(sobelPrewitt(modifiedBitmap,SOBEL1,SOBEL2,4));
                break;
            case R.id.prewitt:
                imageToUpload.setImageBitmap(sobelPrewitt(modifiedBitmap,PREWITT1,PREWITT2,3));
                break;
            case R.id.moyenne:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,MOYENNE5));
                break;
            case R.id.gaussian:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,gaussien));
                break;
            case R.id.action_reset:
                modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(),true);
                imageToUpload.setImageBitmap(modifiedBitmap);
                break;
            case R.id.invert:
                imageToUpload.setImageBitmap(invert(modifiedBitmap));
                break;
            case R.id.wallpaper:
                wallpaper(modifiedBitmap);
                break;
            case R.id.pencilEffect:
                imageToUpload.setImageBitmap(pencilEffect(modifiedBitmap));
                break;
            case R.id.save:
                save(modifiedBitmap);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void onPickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_REQUEST) {
                if (data != null) {
                    currentBitmap = (Bitmap) data.getExtras().get("data");
                    modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(),true);
                    imageToUpload.setImageBitmap(modifiedBitmap);
                }
            }
            if (requestCode == PICK_IMAGE) {
                currentBitmap = ImagePicker.getImageFromResult(this, Activity.RESULT_OK, data);
                modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
                imageToUpload.setImageBitmap(modifiedBitmap);
                // TODO use bitmap

            }
        }}


    public Bitmap toGray(Bitmap bitmap) {

        int width = bitmap.getWidth(),height = bitmap.getHeight(),red, blue,green;
        int[] pixelTab = new int[width*height];
        bitmap.getPixels(pixelTab, 0,width, 0, 0, width, height);

        for (int i = 0; i < pixelTab.length; ++i) {
             red = Color.red(pixelTab[i]);
             green = Color.green(pixelTab[i]);
             blue = Color.blue(pixelTab[i]);
            red = (int) (0.3 * red + 0.59 * green + 0.11 * blue);
            pixelTab[i] = Color.rgb(red, red, red);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }


    public Bitmap contrast(Bitmap bitmap, int cmin, int cmax){

        int width = bitmap.getWidth(),height = bitmap.getHeight(),min = 255, max = 0, a = 0;
        int [] pixelTab = new int [width*height],pixelTab2 = new int [width*height],LUT = new int [256]; // je cree une LUT DE 256 nivreau de gris c'est a dire de 0 a 255
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        toGray(bitmap); // je grise la copie afin de prendre le max et le min de l'image grisée
        bitmap.getPixels(pixelTab2,0,width,0,0,width,height);

        for ( int i = 0; i < pixelTab2.length ; i++) { // je recupere le max et le min des niveaux gris
            if (red(pixelTab2[i]) < min) {
                min = red(pixelTab2[i]);
            }
            if (red(pixelTab2[i]) > max) {
                max = red(pixelTab2[i]);
            }
        }
        for (int k = 0; k < LUT.length; ++k) {
            a = controle(k, min, max, cmin, cmax);
            if (a < 0) {
                LUT[k] = cmin;
            }
            if (a > 255) {
                LUT[k] = cmax;
            }
            if (a > 0 && a < 256) {
                LUT[k] = a;
            }
        }
        for ( int i = 0; i < pixelTab.length  ;++i){
            pixelTab[i] = Color.rgb(LUT[red(pixelTab[i])], LUT[green(pixelTab[i])],LUT[blue(pixelTab[i])]);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }

    public Bitmap grayLevelExtension(Bitmap bitmap){

        int width = bitmap.getWidth(), height = bitmap.getHeight(), min = 255, max = 0;
        int [] pixelTab = new int [width*height],LUT  = new int [256];
        toGray(bitmap);
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);

        for ( int i = 0; i < pixelTab.length ; i++) { // Ceci est un debut de l'extension dynamique mais que j'ai pas termininé. toute la fonction grayLevelExtension marche
            if (red(pixelTab[i]) < min) {
                min = red(pixelTab[i]);
            }
            if ( red(pixelTab[i]) > max) {
                max = red(pixelTab[i]);
            }
        }
        for ( int k = 0 ; k < LUT.length ;++k) {
            LUT[k] = controle(k,min,max,0,255);
        }
        for ( int i = 0 ; i < pixelTab.length ; ++i){
            pixelTab[i] = Color.rgb(LUT[red(pixelTab[i])], LUT[red(pixelTab[i])],LUT[red(pixelTab[i])]);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }

    public int controle(int v, int mina, int maxa , int minb, int maxb ){

        int r1 = maxb - minb;
        int r2 = maxa - mina;
        v = (((v-mina)*r1)/r2) + minb;

        return v;
    }



    public Bitmap keepHueGray(Bitmap bitmap , int min , int max ) {
        // pour garder uniquement le rouge d'une image et grayLevelExtension le reste
        int height = bitmap.getHeight(), width = bitmap.getWidth();
        int[] pixelTab = new int[height * width];// à l'aide d'un tableau
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);

        float[] hsv = new float[3];
        for (int i = 0; i < pixelTab.length; ++i) {
            int red = Color.red(pixelTab[i]);
            int blue = Color.blue(pixelTab[i]);
            int green = Color.green(pixelTab[i]);
            int alpha = Color.alpha(pixelTab[i]);
            int x = (int) (0.3 * red + 0.59 * green + 0.11 * blue);

            RGBToHSV(red, blue, green, hsv);
            if ( hsv[0] < min && hsv[0] > max ) {
                pixelTab[i] = Color.HSVToColor(alpha, hsv);
            } else {
                pixelTab[i] = Color.argb(alpha,x, x, x);
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height); // on applique les changements à l'image
      return bitmap;
    }


    public Bitmap hsv360(Bitmap bitmap, int angle) {//fait varier les teintes par la variable hsv360 qui represente l'angle associé a une couleur

        int width = bitmap.getWidth(), heigth = bitmap.getHeight(), red, green, blue, alpha;
        int [] pixelTab = new int [width*heigth];
        float [] hsv = new float[3];
        bitmap.getPixels(pixelTab,0,width,0,0,width,heigth); // je recupere tous les pixels dans un tableau

        for (int i = 0; i < pixelTab.length;++i){
            red = Color.red(pixelTab[i]);
            green = Color.green(pixelTab[i]);
            blue = Color.blue(pixelTab[i]);
            alpha = Color.alpha(pixelTab[i]);
            Color.RGBToHSV(red,green,blue,hsv);
            hsv[0] = angle;
            pixelTab[i] = Color.HSVToColor(alpha,hsv);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,heigth);
        return  bitmap;
    }

    public Bitmap luminosity(Bitmap bitmap,int pourcent){

        int width = bitmap.getWidth(), height = bitmap.getHeight(), currentPixel;
        double d = (double) (pourcent*0.01);
        int [] pixelTab = new int [width*height];
        float[] hsv = new float[3];
        Bitmap copy = bitmap.copy(bitmap.getConfig(),true);
        copy.getPixels(pixelTab,0,width,0,0,width,height);

        for ( int i = 0; i < pixelTab.length ; ++i ) {
            currentPixel = pixelTab[i];
            int red = Color.red(currentPixel);
            int blue = Color.blue(currentPixel);
            int green = Color.green(currentPixel);
            int alpha = Color.alpha(currentPixel);
            Color.RGBToHSV(red, green, blue, hsv);
            if (hsv[2] == 1 || hsv[2] == 0) {
                pixelTab[i] = currentPixel;
            } else {
                hsv[2] = (float) (hsv[2] * d) + hsv[2];
                if (hsv[2] > 1) {
                    hsv[2] = 1;
                } else if (hsv[2] < 0) {
                    hsv[2] = 0;
                }
                pixelTab[i] = Color.HSVToColor(alpha, hsv);
            }
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;

    }


    public Bitmap saturation(Bitmap bitmap, int pourcent) {

        int width = bitmap.getWidth(), height = bitmap.getHeight(), currentPixel;
        double d = (double) (pourcent*0.01);
        int [] pixelTab = new int [width*height];
        float[] hsv = new float[3];
        Bitmap copy = bitmap.copy(bitmap.getConfig(),true);
        copy.getPixels(pixelTab,0,width,0,0,width,height);


        for (int i = 0; i < pixelTab.length; ++i) {
            currentPixel = pixelTab[i];
            int red = Color.red(currentPixel);
            int blue = Color.blue(currentPixel);
            int green = Color.green(currentPixel);
            int alpha = Color.alpha(currentPixel);
            Color.RGBToHSV(red, green, blue, hsv);
            if (hsv[1] == 1 || hsv[1] == 0) {
                pixelTab[i] = currentPixel;
            } else {
                hsv[1] = (float) (hsv[1] * d) + hsv[1];
                if (hsv[1] > 1) {
                    hsv[1] = 1;
                } else if (hsv[1] < 0) {
                    hsv[1] = 0;
                }
                pixelTab[i] = Color.HSVToColor(alpha, hsv);
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }


    public Bitmap convolution(Bitmap bitmap, int [][] filtre) {

        int s = 0, t = filtre.length, width = bitmap.getWidth(),height = bitmap.getHeight(),currentPixel, indice, couleur;
        int[] pixelTab = new int[width * height];
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        int[] copyPixelTab = pixelTab.clone();


        for (int i = 0; i < t; ++i) {
            for (int j = 0; j < t; ++j) {
                s = Math.abs(s + filtre[i][j]); // on recupere la somme des coefficients du filtre;
            }
        }
        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < height - t/2; ++i) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;
                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;  //on recupere l'indice des voisin a commencer par le premier y compris le pixel pricipal lui-meme
                        currentPixel = copyPixelTab[indice];
                        sumRed += red(currentPixel) * filtre[k][l];
                        sumGreen += green(currentPixel) * filtre[k][l];
                        sumBlue += blue(currentPixel) * filtre[k][l];
                    }

                }
                     couleur = rgb(sumRed/s, sumGreen/ s, sumBlue/s);
                    pixelTab[(j - t / 2) + (i - t / 2) * width] = couleur;

            }
        }
        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }


    public Bitmap laplacian (Bitmap bitmap, int[][] filtre){

        int  t = filtre.length, width = bitmap.getWidth(),height = bitmap.getHeight(),currentPixel, indice, couleur;
        int[] pixelTab = new int[width * height];
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        int[] copyPixelTab = pixelTab.clone();
        bitmap = toGray(bitmap);
        bitmap.getPixels(copyPixelTab, 0, width, 0, 0, width, height);

       //on fait un clone du tableau pour ne pas faire la modification et repasser la dessus

        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < height - t/2; ++i) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;
                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;  //on recupere l'indice des voisin a commencer par le premier y compris le pixel pricipal lui-meme
                        currentPixel = copyPixelTab[indice];
                        sumRed += red(currentPixel) * filtre[k][l];
                        sumGreen += green(currentPixel) * filtre[k][l];
                        sumBlue += blue(currentPixel) * filtre[k][l];
                    }

                }
                    if(sumBlue < 0 || sumBlue > 255){
                        sumBlue = controle(sumBlue,-8*255,8*255,0,255);
                    }
                    if (sumGreen < 0 || sumGreen > 255){
                        sumGreen = controle(sumGreen,-8*255,8*255,0,255);
                    }
                    if (sumRed < 0 || sumRed > 255){
                        sumRed = controle(sumRed,-8*255,8*255,0,255);
                    }
                    couleur = rgb(sumRed, sumGreen, sumBlue);
                    pixelTab[(j - t / 2) + (i - t / 2) * width] = couleur;
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public Bitmap sobelPrewitt(Bitmap bitmap, int [][] filter1, int [][] filter2, int result){

        int t = filter1.length, width = bitmap.getWidth(), height = bitmap.getHeight(),indice,currentPixel;
        int[] pixelTab = new int[width * height];
        bitmap = toGray(bitmap);
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        int [] copyPixelTab = pixelTab.clone();

        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < height - t / 2; ++i) {
                int gradientRX = 0, gradientGX = 0, gradientBX = 0, gradientRY = 0, gradientGY = 0, gradientBY = 0;
                int normeR, normeG, normeB;

                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;  //on recupere l'indice des voisin a commencer par le premier y compris le pixel pricipal lui-meme
                        currentPixel = copyPixelTab[indice];
                        gradientRX = (int) (gradientRX + red(currentPixel) * (filter1[k][l]));
                        gradientGX = (int) (gradientGX + green(currentPixel) * (filter1[k][l]));
                        gradientBX = (int) (gradientBX + blue(currentPixel) * (filter1[k][l]));

                        gradientRY = (int) (gradientRY + red(currentPixel) * (filter2[k][l]));
                        gradientGY = (int) (gradientGY + green(currentPixel) * (filter2[k][l]));
                        gradientBY = (int) (gradientBY + blue(currentPixel) * (filter2[k][l]));

                    }
                    normeR = (int) Math.sqrt(gradientRX * gradientRX + gradientRY * gradientRY);
                    normeG = (int) Math.sqrt(gradientGX * gradientGX + gradientGY * gradientGY);
                    normeB = (int) Math.sqrt(gradientBX * gradientBX + gradientBY * gradientBY);
                    if (normeB > 255) {
                        normeB = controle(normeB, 0, (int) (Math.sqrt(2.00000) * result) * 255, 0, 255);
                    }

                    if (normeR > 255) {
                            normeR = controle(normeR, 0, (int) (Math.sqrt(2.00000) * result*255), 0, 255);
                    }
                    if (normeG > 255) {
                            normeG = controle(normeG, 0, (int) (Math.sqrt(2.00000) * result*255), 0, 255);
                    }

                    int couleur = Color.rgb(normeR, normeG, normeB);

                    pixelTab[(j - t / 2) + (i - t / 2) * width] = couleur;
                }
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }



    int [][] fgauss7 = gaussianFilter(3,1);
    int [][] fgauss5 = gaussianFilter(2,2/3);
    int [][] fgauss3 = gaussianFilter(1,1/3);
    int [][] fgauss11 = gaussianFilter(5,1.66);

    public int [][] gaussianFilter(int radius, double sigma){

        double [][] noyau = new double [2*radius+1][2*radius+1];
        int [][] noyau1 =  new int [2*radius+1][2*radius+1];
        float gaussKernel = 0;
        double e  = 0;

        if ( radius == 0 || sigma ==0){
            System.out.println("erreur");
        }
        for (int i = -radius; i < radius; ++i){
            for ( int j = -radius; j < radius; ++j){
                e = (double) Math.exp( - (i*i + j*j)/(2*sigma*sigma));
                gaussKernel += e;
                noyau[i+radius][j+radius] =  e;
            }
        }
        for ( int i = -radius ; i < radius; ++i ){
            for ( int j = -radius; j < radius; ++j){
                noyau1[radius+i][radius+j] =(int)  ((noyau[radius+i][radius+j])*gaussKernel);
                //gaussKernel += noyau1[radius+i][radius+j];
                System.out.println( noyau1[radius+i][radius+j]);

            }
        }

        return noyau1;
    }

    public int [][] averageFilter(int radius){

        int t = 2*radius+1;
        int [][] noyau = new int [t][t];

        for (int i = 0; i < t ; ++i ){
            for ( int j = 0; j < t; ++j){
                noyau[i][j] = 1;
            }
        }

        return noyau;
    }

    public Bitmap invert(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixelTab = new int[width*height];
        bitmap.getPixels(pixelTab, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixelTab.length; ++i) {
            int red = Color.red(pixelTab[i]);
            int green = Color.green(pixelTab[i]);
            int blue = Color.blue(pixelTab[i]);

            pixelTab[i] = Color.rgb(255 - red ,255 - green, 255 - blue);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }

    public void wallpaper(Bitmap bitmap){

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  Bitmap pencilEffect(Bitmap bitmap){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixelTab = new int[width*height];
        bitmap.getPixels(pixelTab,0,width, 0,0, width, height);

        Bitmap Copy,Invert,Result;
        Copy = bitmap;
        Copy = Blur.toGray(Copy);
        Invert = Blur.invert(Copy);
        Invert = Blur.blur(this,Invert);
        Result= Blur.ColorDodgeBlend(Invert, Copy);


        Result.getPixels(pixelTab,0,width,0,0,width,height);
        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }


    public void save(Bitmap bitmap) {

        FileOutputStream fileOutputStream = null;
        File file = getDisc();

        if (!file.exists() && !file.mkdirs()) {
            Toast.makeText(this, "Can't create directory to save image", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img" + date + ".Jpg";
        String file_name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);
        try {
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(this, "Save image success", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshGallery(new_file);
    }

    public void refreshGallery(File file) {

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    public File getDisc() {

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(file, "Sauvegarde");
    }

    public Action getIndexApiAction() {

        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

//System.currenttimeMiles()





