package com.example.mafangbom.projet;



import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

import static android.graphics.Color.RGBToHSV;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;

//test
public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    SeekBar sb;
    int progress ;
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
    static int [][] MOYENNE = {{3,3,3},{3,3,3},{3,3,3}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        setTitle("Projet Android");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        sb = (SeekBar) findViewById(R.id.seekbar_luminosite);
        sb.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.griser:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(toGrayTableau(modifiedBitmap));
                break;
            case R.id.contrasteCouleur:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(Contraste(modifiedBitmap));
                break;
            case R.id.extensiongris:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(extensionDynamiqueGris(modifiedBitmap));
                break;
            case R.id.TeinteB:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(garderTeinte("bleu", modifiedBitmap));
                break;
            case R.id.TeinteJ:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(garderTeinte("jaune", modifiedBitmap));
                break;
            case R.id.TeinteM:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(garderTeinte("magenta", modifiedBitmap));
                break;
            case R.id.TeinteR:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(garderTeinte("rouge", modifiedBitmap));
                break;
            case R.id.TeinteV:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(garderTeinte("vert", modifiedBitmap));
                break;
            case R.id.gallery:
                onPickImage();
                break;
            case R.id.luminosite:
                luminosite(modifiedBitmap);
                break;
            case  R.id.laplacien:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,LAPLACIEN8,"laplacien"));
                break;
            case R.id.sobel:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(sobelPrewitt(modifiedBitmap,SOBEL1,SOBEL2));
                break;
            case R.id.prewitt:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(sobelPrewitt(modifiedBitmap,PREWITT1,PREWITT2));
                break;
            case R.id.moyenne:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,MOYENNE,"moyenne"));
                break;
            case R.id.gaussien:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,gaussien,"gauss"));
                break;
            case R.id.action_reset:
                modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(),true);
                imageToUpload.setImageBitmap(modifiedBitmap);
                sb.setVisibility(View.INVISIBLE);
                break;
            case R.id.saturation:
                saturation(modifiedBitmap);
                break;
            case R.id.rotHsv:
                rotationHSV(modifiedBitmap);
                break;
            case R.id.negatif:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(negatif(modifiedBitmap));
                break;
            case R.id.wallpaper:
                sb.setVisibility(View.INVISIBLE);
                startWall(modifiedBitmap);
                break;
            case R.id.crayon:
                sb.setVisibility(View.INVISIBLE);
                imageToUpload.setImageBitmap(changetosketch(modifiedBitmap));

        }
        return super.onOptionsItemSelected(item);
    }


    public void onPickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE);
    }

    private void takePictureFromGallery()
    {
        startActivityForResult(
                Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image"), PICK_IMAGE);
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


    public Bitmap toGrayTableau(Bitmap bMap) {
        int[] Pixels = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(Pixels, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);
            rd = (int) (0.3 * rd + 0.59 * vt + 0.11 * bl);
            Pixels[i] = Color.rgb(rd, rd, rd);
        }
        bMap.setPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bMap;
    }


    public Bitmap Contraste (Bitmap bMap){

        int [] tableau = new int [bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(tableau,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        toGrayTableau(bMap); // je grise la copie afin de prendre le max et le min de l'image grisée
        int [] Pixels = new int [bMap.getWidth() * bMap.getHeight()];
        int min = 255;
        int max = 0;
        bMap.getPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());

        for ( int ng = 0; ng < Pixels.length;ng++) { // je recupere le max et le min des niveaux gris
            if (red(Pixels[ng]) < min) {
                min = red(Pixels[ng]);
            }
            if ( red(Pixels[ng]) > max) {
                max = red(Pixels[ng]);
            }
        }
        int LUT [] = new int [256]; // je cree une LUT DE 256 nivreau de gris c'est a dire de 0 a 255
        int dif = max - min ;
        for ( int k = 0;  k < LUT.length ;++k) {
            int a = (255 * (k - min)) / dif;
            if ( a < 0){
                LUT[k] = 0;
            }
            if ( a > 255){
                LUT[k] = 255;
            }
            if ( a > 0 && a <256){
                LUT[k] = a;
            }
        }
        for ( int i = 0; i < tableau.length  ;++i){
            tableau[i] = Color.rgb(LUT[red(tableau[i])], LUT[green(tableau[i])],LUT[blue(tableau[i])]);
        }

        bMap.setPixels(tableau,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bMap;
    }

    public Bitmap extensionDynamiqueGris(Bitmap bMap){

        toGrayTableau(bMap);
        int [] Pixels = new int [bMap.getWidth() * bMap.getHeight()];
        int min = 255;
        int max = 0;

        bMap.getPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());

        for ( int ng = 0; ng < Pixels.length;ng++) { // Ceci est un debut de l'extension dynamique mais que j'ai pas termininé. toute la fonction extensionDynamiqueGris marche
            if (red(Pixels[ng]) < min) {
                min = red(Pixels[ng]);
            }
            if ( red(Pixels[ng]) > max) {
                max = red(Pixels[ng]);
            }
        }
        int LUT [] = new int [256];
        int dif = max - min ;
        for ( int k = 0;  k < LUT.length ;++k) {
            LUT[k] = (255 * (k - min)) / dif;
        }
        for ( int i = 0; i < Pixels.length  ;++i){
            Pixels[i] = Color.rgb(LUT[red(Pixels[i])], LUT[red(Pixels[i])],LUT[red(Pixels[i])]);
        }

        bMap.setPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bMap;
    }



    public void luminosite (final Bitmap b){
        sb.setVisibility(View.VISIBLE);
        progress = 50;
        sb.setProgress(progress);
        sb.setMax(99);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (i < 50) {
                    i = i - 50;
                    Bitmap r = b.copy(Bitmap.Config.ARGB_8888, true);
                    imageToUpload.setImageBitmap(luminositeEtSaturation((i), r, "luminosite"));
                } else if (i > 50){
                    i = i % 50;
                    Bitmap r = b.copy(Bitmap.Config.ARGB_8888, true);
                    imageToUpload.setImageBitmap(luminositeEtSaturation((i), r, "luminosite"));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void saturation (final Bitmap b){
        sb.setVisibility(View.VISIBLE);
        progress = 50;
        sb.setProgress(progress);
        sb.setMax(99);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (i < 50) {
                    i = i - 50;
                    Bitmap r = b.copy(Bitmap.Config.ARGB_8888, true);
                    imageToUpload.setImageBitmap(luminositeEtSaturation((i), r, "saturation"));
                } else if (i > 50){
                    i = i % 50;
                    Bitmap r = b.copy(Bitmap.Config.ARGB_8888, true);
                    imageToUpload.setImageBitmap(luminositeEtSaturation((i), r, "saturation"));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void rotationHSV(final Bitmap b){
        sb.setVisibility(View.VISIBLE);
        progress = 0;
        sb.setProgress(progress);
        sb.setMax(360);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                imageToUpload.setImageBitmap(teinte(i,b));}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public Bitmap garderTeinte(String monchoix, Bitmap bMap) { // pour garder uniquement le rouge d'une image et extensionDynamiqueGris le reste
        int height = bMap.getHeight();
        int width = bMap.getWidth();
        int[] pixelTab = new int[height * width];// à l'aide d'un tableau
        bMap.getPixels(pixelTab,0,width,0,0,width,height);
        for (int i = 0; i < pixelTab.length; ++i) {
            int rD = Color.red(pixelTab[i]);
            int bD = Color.blue(pixelTab[i]);
            int gD = Color.green(pixelTab[i]);
            int alpha = Color.alpha(pixelTab[i]);
            int x = (int) (0.3 * rD + 0.59 * gD + 0.11 * bD);
            float[] hs = new float[3];
            RGBToHSV(rD, bD, gD, hs);
            switch (monchoix) {
                case "rouge":

                    if (hs[0] < 15 || hs[0] > 345) {
                        pixelTab[i] = Color.HSVToColor(alpha, hs);
                    } else {
                        pixelTab[i] = Color.argb(alpha,x, x, x);
                    }
                    break;
                case "vert" :
                    if (hs[0]> 90 && hs[0] < 140) {
                        pixelTab[i] = Color.HSVToColor(alpha, hs);
                    }
                    else {
                        pixelTab[i] = Color.argb(alpha, x, x, x);
                    }
                    break;
                case "magenta" :
                    if ( hs[0] > 300 && hs[0] < 320){
                        pixelTab[i] = Color.HSVToColor(alpha, hs);
                    }
                    else {
                        pixelTab[i] = Color.argb(alpha,x, x, x);
                    }break;
                case "jaune" :
                    if (hs[0] > 50 && hs[0]<60){
                        pixelTab[i] = Color.HSVToColor(alpha, hs);
                    }
                    else {
                        pixelTab[i] = Color.argb(alpha,x, x, x);
                    }break;
                case "bleu" :
                    if (hs[0] > 210 && hs[0] < 250){
                        pixelTab[i] = Color.HSVToColor(alpha, hs);
                    }
                    else {
                        pixelTab[i] = Color.argb(alpha,x, x, x);
                    }break;

            }
        }
        bMap.setPixels(pixelTab, 0, width, 0, 0, width, height); // on applique les changements à l'image
      return bMap;
    }


    public Bitmap teinte(int teinte, Bitmap b) { //fait varier les teintes par la variable teinte qui represente l'angle associé a une couleur
        int [] Pixels = new int [b.getWidth() * b.getHeight()];
        b.getPixels(Pixels,0,b.getWidth(),0,0,b.getWidth(),b.getHeight()); // je recupere tous les pixels dans un tableau
        for (int i =0; i < Pixels.length;++i){
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);
            int alpha = Color.alpha(Pixels[i]);
            float [] hsv = new float[3];
            Color.RGBToHSV(rd,vt,bl,hsv);
            if (teinte == 0){
                hsv[0] = hsv[0];
            }
            else {
                hsv[0] = teinte;
            }
            Pixels[i] = Color.HSVToColor(alpha,hsv);

        }
        b.setPixels(Pixels,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());
        return  b;
    }


    public Bitmap convolution (Bitmap b, int [][] filtre, String monchoix) {
        int s = 0, t = filtre.length;
        for (int i = 0; i < t; ++i) {
            for (int j = 0; j < t; ++j) {
                s = Math.abs(s + filtre[i][j]); // on recupere la somme des coefficients du filtre;
            }
        }
        int width = b.getWidth();
        int heigth = b.getHeight();
        int[] tableauPixel = new int[width * heigth];
        Bitmap clonage = b.copy(b.getConfig(),true);
        if (monchoix.equals("laplacien")) {
           clonage = toGrayTableau(clonage);
        }
        clonage.getPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        int[] copie = tableauPixel.clone(); //on fait un clone du tableau pour ne pas faire la modification et repasser la dessus

        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < heigth - t/2; ++i) {
                int sommer = 0, sommeg = 0, sommeb = 0;
                //int PixelPrincpl = copie[j + i * width];
                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        int indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;  //on recupere l'indice des voisin a commencer par le premier y compris le pixel pricipal lui-meme
                        int Pixel = copie[indice];
                        sommer += red(Pixel) * filtre[k][l];
                        sommeg += green(Pixel) * filtre[k][l];
                        sommeb += blue(Pixel) * filtre[k][l];
                    }

                }
                if (monchoix.equals("laplacien")) {

                    if (sommer > 255) {
                        sommer = 255;
                    }
                    if (sommeb > 255) {
                        sommeb = 255;
                    }
                    if (sommeg > 255) {
                        sommeg = 255;
                    }
                    if (sommeg < 0) {
                        sommeg = 0;
                    }
                    if (sommer < 0) {
                        sommer = 0;
                    }
                    if (sommeb < 0) {
                        sommeb = 0;
                    }
                    int couleur = rgb(sommer, sommeg, sommeb);
                    tableauPixel[(j - t / 2) + (i - t / 2) * width] = couleur;
                } else {
                    int couleur = rgb(sommer/s, sommeg/ s, sommeb/s);
                    tableauPixel[(j - t / 2) + (i - t / 2) * width] = couleur;
                }
            }
        }
        clonage.setPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        return clonage;
    }


    public Bitmap sobelPrewitt(Bitmap b, int [][] filtre1, int [][] filtre2){
        int t = filtre1.length;
        int width = b.getWidth();
        int heigth = b.getHeight();
        int[] tableauPixel = new int[width * heigth];
        Bitmap retour  = b.copy(b.getConfig(),true);
        retour = toGrayTableau(retour);
        retour.getPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        int[] copie = tableauPixel.clone(); //on fait un clone du tableau pour ne pas faire la modification et repasser la dessus
        for (int j = t / 2; j < width - 2; ++j) {
            for (int i = t / 2; i < heigth - 2; ++i) {
                int gradientRX = 0;
                int gradientGX = 0;
                int gradientBX = 0;
                int gradientRY = 0;
                int gradientGY = 0 ;
                int gradientBY = 0;
                //int PixelPrincpl = copie[j + i * width];
                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        int indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;  //on recupere l'indice des voisin a commencer par le premier y compris le pixel pricipal lui-meme
                        int Pixel = copie[indice];
                        gradientRX = (int) (gradientRX + red(Pixel) *(filtre1[k][l]));
                        gradientGX = (int) (gradientGX + green(Pixel) * (filtre1[k][l]));
                        gradientBX = (int) (gradientBX + blue(Pixel) * (filtre1[k][l]));

                        gradientRY = (int) (gradientRY + red(Pixel) * (filtre2[k][l]));
                        gradientGY = (int) (gradientGY + green(Pixel) * (filtre2[k][l]));
                        gradientBY = (int) (gradientBY + blue(Pixel) * (filtre2[k][l]));

                    }
                    int NormeR = (int) Math.sqrt(gradientRX * gradientRX + gradientRY * gradientRY);
                    int NormeV = (int) Math.sqrt(gradientGX * gradientGX + gradientGY * gradientGY);
                    int NormeB = (int) Math.sqrt(gradientBX * gradientBX + gradientBY * gradientBY);
                    if (NormeB > 255) {
                        NormeB = 255;
                    }
                    if (NormeR > 255) {
                        NormeR = 255;
                    }
                    if (NormeV > 255) {
                        NormeV = 255;
                    }

                    int couleur = Color.rgb(NormeR, NormeV, NormeB);

                    tableauPixel[(j - t / 2) + (i - t / 2) * width] = couleur;
                }
            }
        }
        retour.setPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        return retour;
    }

    public Bitmap luminositeEtSaturation(int pourcentage, Bitmap b, String monchoix) {
        int [] pixelTab = new int [b.getHeight()*b.getWidth()];
        Bitmap copie = b.copy(b.getConfig(),true);
        copie.getPixels(pixelTab,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());
        double d = (double) (pourcentage*0.01);
        float[] hsv = new float[3];

        for ( int i = 0; i < pixelTab.length ; ++i ) {
            int color = pixelTab[i];
            int rd = Color.red(color);
            int bd = Color.blue(color);
            int gd = Color.green(color);
            int alpha = Color.alpha(color);
            Color.RGBToHSV(rd, gd, bd, hsv);
            switch (monchoix) {

                case "luminosite":
                    if (hsv[2] == 1 || hsv[2] == 0) {
                        pixelTab[i] = color; //Color.HSVToColor(alpha,hsv);
                    }
                    else {
                        hsv[2] = (float) (hsv[2] * d) + hsv[2];
                        if (hsv[2] > 1) {
                            hsv[2] = 1;
                        } else if (hsv[2] < 0) {
                            hsv[2] = 0;
                        }
                        pixelTab[i] = Color.HSVToColor(alpha, hsv);
                    }
                    break;
                case "saturation":

                    if (hsv[1] == 1 || hsv[1] == 0) {
                        pixelTab[i] = color; //Color.HSVToColor(alpha,hsv);
                    }
                    hsv[1] = (float) (hsv[1] * d) + hsv[1];
                    if (hsv[1] > 1){
                        hsv[1] = 1;
                    }
                    else if(hsv[1] < 0){
                        hsv[1] = 0;
                    }
                    pixelTab[i] = Color.HSVToColor(alpha,hsv);

                    break;
            }
        }
        b.setPixels(pixelTab,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());
        return  b;
    }


    public Bitmap negatif (Bitmap bMap) {
        int[] Pixels = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(Pixels, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);

            Pixels[i] = Color.rgb(255 - rd ,255 - vt, 255 - bl);
        }
        bMap.setPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bMap;
    }

    public void startWall(Bitmap bMap){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(bMap);
            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Bitmap effetCrayon (Bitmap b, int [][] filtre1, int [][] filtre2){
        int t = filtre1.length;
        int width = b.getWidth();
        int heigth = b.getHeight();
        int[] tableauPixel = new int[width * heigth];
        Bitmap retour  = b.copy(b.getConfig(),true);
        retour = toGrayTableau(retour);
        retour.getPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        int[] copie = tableauPixel.clone();
        for (int j = t / 2; j < width - 2; ++j) {
            for (int i = t / 2; i < heigth - 2; ++i) {
                int gradientRX = 0;
                int gradientGX = 0;
                int gradientBX = 0;
                int gradientRY = 0;
                int gradientGY = 0 ;
                int gradientBY = 0;

                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        int indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;
                        int Pixel = copie[indice];
                        gradientRX = (int) (gradientRX + red(Pixel) *(filtre1[k][l]));
                        gradientGX = (int) (gradientGX + green(Pixel) * (filtre1[k][l]));
                        gradientBX = (int) (gradientBX + blue(Pixel) * (filtre1[k][l]));

                        gradientRY = (int) (gradientRY + red(Pixel) * (filtre2[k][l]));
                        gradientGY = (int) (gradientGY + green(Pixel) * (filtre2[k][l]));
                        gradientBY = (int) (gradientBY + blue(Pixel) * (filtre2[k][l]));

                    }
                    int NormeR = (int) Math.sqrt(gradientRX * gradientRX + gradientRY * gradientRY);
                    int NormeV = (int) Math.sqrt(gradientGX * gradientGX + gradientGY * gradientGY);
                    int NormeB = (int) Math.sqrt(gradientBX * gradientBX + gradientBY * gradientBY);
                    if (NormeB > 255) {
                        NormeB = 255;
                    }
                    if (NormeR > 255) {
                        NormeR = 255;
                    }
                    if (NormeV > 255) {
                        NormeV = 255;
                    }

                    int couleur = Color.rgb(NormeR, NormeV, NormeB);

                    tableauPixel[(j - t / 2) + (i - t / 2) * width] = couleur;
                }
            }
        }
        retour.setPixels(tableauPixel, 0, width, 0, 0, width, heigth);
        for(int i = 0 ; i < width ; ++i){
            for(int j = 0 ; j < heigth; ++j){
                int pixel = retour.getPixel(i,j);
                int somme = (Color.blue(pixel) + Color.red(pixel)+Color.green(pixel));
                if (somme > 255) {
                    somme = 0 ;
                }
                else {
                    somme = 255 ;
                }
                pixel = Color.rgb(somme,somme,somme);
                retour.setPixel(i,j,pixel);
            }
        }
        return retour;
    }

    public  Bitmap changetosketch(Bitmap bmp){
        Bitmap Copy,Invert,Result;
        Copy =bmp;
        Copy = Blur.toGrayTableau(Copy);
        Invert = Blur.negatif(Copy);
        Invert = Blur.blur(this,Invert);
        Result = Blur.ColorDodgeBlend(Invert, Copy);

        return Result;
    }

}


/*
    if(resultCode != RESULT_CANCELED) {
        if (requestCode == CAMERA_REQUEST) {
            if (data != null) {
                currentBitmap = (Bitmap) data.getExtras().get("data");
                imageToUpload.setImageBitmap(currentBitmap);
            }
        }
        if (requestCode == PICK_IMAGE) {

            currentBitmap = ImagePicker.getImageFromResult(this, Activity.RESULT_OK, data);
            modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
            imageToUpload.setImageBitmap(modifiedBitmap);
            // TODO use bitmap

        }
    }}
*/



