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
    /**
     * La toolbar contenant le menu et les fonctions Selectionner image, Sauvegarder et Supprimer
     * les effets.
     */
    private Toolbar toolbar;

    /**
     * La bitmap qui va etre affichee a l'ecran.
     */
    public ImageView imageToUpload;

    /**
     * La bitmap initiale chargee depuis la galerie ou la camera.
     */
    public Bitmap currentBitmap;

    /**
     * La bitmap en cours de modification.
     */
    public  Bitmap modifiedBitmap;

    /**
     * requestCode du onActivityResult pour activer la selection d'image depuis la galerie.
     */
    private static final int PICK_IMAGE = 100;

    // Filtres à appliquer dans les effets de convolution
    static int [][] LAPLACIEN4 = {{0,1,0},{1,-4,1},{0,1,0}};
    static int [][] LAPLACIEN8 = {{1,1,1},{1,-8,1},{1,1,1}};
    static int [][] SOBEL1 = {{-1,0,1},{-2 ,0, 2},{-1,0,1}};
    static int [][] SOBEL2 = {{-1,-2,-1},{0 ,0, 0},{1,2,1}};
    static int [][] PREWITT1 = {{-1,0,1},{-1,0,1},{-1,0,1}};
    static int [][] PREWITT2 = {{-1,-1,-1},{0,0,0},{1,1,1}};

    //Filtres moyenne et gaussien generes par les methodes qu'on a crees
    int [][] moyenne5 = averageFilter(2);
    int [][] moyenne7 = averageFilter(3);
    int [][] moyenne11 =averageFilter(5);
    int [][] gauss5 = gaussianFilter(2,5);
    int [][] gauss7 = gaussianFilter(3,5);
    int [][] gauss11 = gaussianFilter(5,5);

    //Coefficients utilises pour garder une teinte avec la methode keepHueGray et keepRedGray
    static int MIN_YELLOW = 50;
    static int MAX_YELLOW = 60;
    static int MIN_BLUE = 220;
    static int MAX_BLUE = 245;
    static int MIN_RED = 345;
    static int MAX_RED = 15;
    static int MIN_MAGENTA = 300;
    static int MAX_MAGENTA = 344;
    static int MIN_GREEN = 90;
    static int MAX_GREEN = 140;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mise en place du layout
        setContentView(R.layout.first_activity);

        //Affichage du nom de l'application
        setTitle("Projet Android");

        //Affichage de la toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Image affichee dans la vue
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Utilise pour le zoom
        PhotoViewAttacher photoView = new PhotoViewAttacher(imageToUpload);
        photoView.update();
    }

    /**
     * Initialise le contenu du menu d'options dans l'activite.
     *
     * @param menu
     * Le menu d'options dans lequel on place les items.
     *
     * @return
     * Retourne true pour que le menu soit affiche.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * Methode appelee quand un item du menu est selectionne, pour appliquer la methode
     * correspondante. Chaque item a un identifiant auquel correspond une methode et des parametres
     * differents.
     * On charge l'image modifiee (modifiedBitmap) dans la vue (imageToUpload) avec la methode
     * setImageBitmap.
     *
     * @param item
     * L'item qui a ete selectionne par l'utilisateur.
     */
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
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,MIN_BLUE, MAX_BLUE));
                break;
            case R.id.yellowGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,MIN_YELLOW,MAX_YELLOW));
                break;
            case R.id.magentaGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,MIN_MAGENTA,MAX_MAGENTA));
                break;
            case R.id.redGray:
                imageToUpload.setImageBitmap(keepRedGray(modifiedBitmap,MIN_RED,MAX_RED));
                break;
            case R.id.greenGray:
                imageToUpload.setImageBitmap(keepHueGray(modifiedBitmap,MIN_GREEN,MAX_GREEN));
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
            case R.id.filter5m:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap, moyenne5));
                break;
            case R.id.filter7m:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,moyenne7));
                break;
            case R.id.filter11m:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap,moyenne11));
                break;
            case R.id.filter5g:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap, gauss5));
                break;
            case R.id.filter7g:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap, gauss7));
                break;
            case R.id.filter11g:
                imageToUpload.setImageBitmap(convolution(modifiedBitmap, gauss11));
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

    /**
     * Cette méthode va appeler des méthodes de la classe ImagePicker pour choisir une image depuis
     * la galerie ou en capturer une avec la camera.
     */
    public void onPickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {

            if (requestCode == PICK_IMAGE) {
                currentBitmap = ImagePicker.getImageFromResult(this, Activity.RESULT_OK, data);
                modifiedBitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
                imageToUpload.setImageBitmap(modifiedBitmap);

            }
        }}

    /**
     * Cette methode grise une bitmap en multipliant les valeurs de pixels par les coefficients
     * 0.3 ; 0.59 ; 0.11
     * @param bitmap
     * La bitmap initiale a modifier
     * @return
     * La bitmap grisee.
     */
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

    /**
     * Cette methode permet d'appliquer un contraste a une bitmap, il y a 3 niveaux de contraste :
     * minimum ; moyen et maximum, au choix de l'utilisateur dans le menu.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param cmin
     * La valeur du contraste minimum.
     *
     * @param cmax
     * La valeur du contraste maximum.
     *
     * @return
     * La bitmap avec l'effet de contraste choisi.
     */
    public Bitmap contrast(Bitmap bitmap, int cmin, int cmax){

        int width = bitmap.getWidth(),height = bitmap.getHeight(),min = 255, max = 0, a = 0;
        int [] pixelTab = new int [width*height],pixelTab2 = new int [width*height];
        // je cree une LUT DE 256 niveaux de gris c'est-a-dire de 0 a 255
        int [] LUT = new int [256];
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        toGray(bitmap); // je grise la copie afin de prendre le max et le min de l'image grisee
        bitmap.getPixels(pixelTab2,0,width,0,0,width,height);

        for ( int i = 0; i < pixelTab2.length ; i++) { //Recuperation du max et min des niveaux gris
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
            pixelTab[i] = Color.rgb(LUT[red(pixelTab[i])], LUT[green(pixelTab[i])],
                    LUT[blue(pixelTab[i])]);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }

    /**
     * Cette methode utilise l'extension dynamique pour griser une image.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @return
     * La bitmap grisee dynamiquement.
     */
    public Bitmap grayLevelExtension(Bitmap bitmap){
        int width = bitmap.getWidth(), height = bitmap.getHeight(), min = 255, max = 0;
        int [] pixelTab = new int [width*height],LUT  = new int [256];
        toGray(bitmap);
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);

        for ( int i = 0; i < pixelTab.length ; i++) {
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
            pixelTab[i] = Color.rgb(LUT[red(pixelTab[i])], LUT[red(pixelTab[i])],
                    LUT[red(pixelTab[i])]);
        }

        bitmap.setPixels(pixelTab,0,width,0,0,width,height);
        return bitmap;
    }

    /**
     * Cette methode va controler une valeur afin qu'elle ne fasse pas partie des valeurs
     * "impossibles" pour une méthode particuliere.
     *
     * @param v
     * La valeur a controler
     *
     * @param mina
     * Minimum de a.
     *
     * @param maxa
     * Maximum de a.
     *
     * @param minb
     * Minimum de b.
     *
     * @param maxb
     * Maximum de b.
     *
     * @return
     * La valeur correcte controlee, grace a laquelle on pourra appliquer un effet sans erreur.
     */
    public int controle(int v, int mina, int maxa , int minb, int maxb ){

        int r1 = maxb - minb;
        int r2 = maxa - mina;
        v = (((v-mina)*r1)/r2) + minb;

        return v;
    }

    /**
     * Cette methode garde la teinte rouge d'une image et grise le reste de l'image avec une
     * extension dynamique.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param min
     * L'angle minimum du rouge.
     *
     * @param max
     * L'angle maximum du rouge.
     *
     * @return
     * La bitmap avec juste les teintes de rouge.
     */
    public Bitmap keepRedGray(Bitmap bitmap , int min , int max) {

        int height = bitmap.getHeight(), width = bitmap.getWidth();
        int[] pixelTab = new int[height * width];
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);

        float[] hsv = new float[3];
        for (int i = 0; i < pixelTab.length; ++i) {
            int red = Color.red(pixelTab[i]);
            int blue = Color.blue(pixelTab[i]);
            int green = Color.green(pixelTab[i]);
            int alpha = Color.alpha(pixelTab[i]);
            int x = (int) (0.3 * red + 0.59 * green + 0.11 * blue);

            RGBToHSV(red, blue, green, hsv);
            if ( hsv[0] < max || hsv[0] > min ) {
                pixelTab[i] = Color.HSVToColor(alpha, hsv);
            } else {
                pixelTab[i] = Color.argb(alpha,x, x, x);
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Cette methode garde une teinte choisie d'une image et grise le reste de l'image avec une
     * extension dynamique. L'utilisateur a le choix entre Jaune, Vert, Bleu ou Magenta.
     * On choisit ces valeurs par rapport a leurs angles hsv.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param min
     * La valeur minimum de l'angle.
     *
     * @param max
     * La valeur maximum de l'angle.
     *
     * @return
     * La bitmap avec juste la teinte choisie.
     */
    public Bitmap keepHueGray(Bitmap bitmap , int min , int max ) {

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
            if ( hsv[0] < max && hsv[0] > min ) {
                pixelTab[i] = Color.HSVToColor(alpha, hsv);
            } else {
                pixelTab[i] = Color.argb(alpha,x, x, x);
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Cette methode permet d'appliquer une teinte a l'ensemble d'une image. L'utilisateur a le
     * choix entre Sépia, Rouge, Bleu, Magenta, Vert, Cyan et Jaune. On choisit ces valeurs par
     * rapport a leur angle hsv.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param angle
     * L'angle hsv correspondant a la teinte voulue.
     *
     * @return
     * La bitmap a laquelle on applique une teinte.
     */
    public Bitmap hsv360(Bitmap bitmap, int angle) {

        int width = bitmap.getWidth(), heigth = bitmap.getHeight(), red, green, blue, alpha;
        int [] pixelTab = new int [width*heigth];
        float [] hsv = new float[3];
        bitmap.getPixels(pixelTab,0,width,0,0,width,heigth);

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

    /**
     * Cette methode permet de faire varier la luminosite d'une image. L'utilisateur a le choix
     * entre les differents niveaux de luminosite suivants : -100%; -75%; -50%; -25%; +25%; +50%;
     * +75%; +100%.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param pourcent
     * Le pourcentage de luminosite qu'on veut appliquer.
     *
     * @return
     * La bitmap avec le niveau de luminosite qui a varie.
     */
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

    /**
     * Cette methode permet de faire varier la saturation d'une image. L'utilisateur a le choix
     * entre les differents niveaux de saturation suivants : -100%; -75%; -50%; -25%; +25%; +50%;
     * +75%; +100%.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param pourcent
     * Le pourcentage de saturation qu'on veut appliquer.
     *
     * @return
     * La bitmap avec le niveau de saturation qui a varie.
     */
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

    /**
     * Cette methode va appliquer un filtre de convolution a une image. L'utilisateur a le choix
     * entre un filtre moyenne (de taille 5*5, 7*7 ou 11*11) ou un filtre gaussien (meme tailles).
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param filtre
     * Le filtre de convolution a appliquer a la bitmap.
     *
     * @return
     * La bitmap a laquelle on a applique le filtre choisi.
     */
    public Bitmap convolution(Bitmap bitmap, int [][] filtre) {

        int s = 0, t = filtre.length, width = bitmap.getWidth(),height = bitmap.getHeight(),
                currentPixel, indice, couleur;
        int[] pixelTab = new int[width * height];
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        int[] copyPixelTab = pixelTab.clone();

        for (int i = 0; i < t; ++i) {
            for (int j = 0; j < t; ++j) {
                s = Math.abs(s + filtre[i][j]);
            }
        }
        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < height - t/2; ++i) {
                int sumRed = 0, sumGreen = 0, sumBlue = 0;
                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        //on recupere l'indice des voisins a commencer par le premier y compris le
                        // pixel pricipal lui-meme dans la variable indice
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;
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

    /**
     * Cette methode applique un filtre de convolution laplacien a une image.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param filter
     * Le filtre laplacien, de coefficient 4 ou 8.
     *
     * @return
     * La bitmap convoluee avec le filtre laplacien choisi.
     */
    public Bitmap laplacian (Bitmap bitmap, int[][] filter){

        int  t = filter.length, width = bitmap.getWidth(),height = bitmap.getHeight(),currentPixel,
                indice, couleur;
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
                        //on recupere l'indice des voisins a commencer par le premier y compris le
                        // pixel pricipal lui-meme dans la variable indice.
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;
                        currentPixel = copyPixelTab[indice];
                        sumRed += red(currentPixel) * filter[k][l];
                        sumGreen += green(currentPixel) * filter[k][l];
                        sumBlue += blue(currentPixel) * filter[k][l];
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

    /**
     * Cette methode applique un filtre de convolution de Sobel ou Prewitt a une bitmap. Il y a
     * deux filtres chacun pour Sobel et pour Prewitt.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @param filter1
     * Premier filtre de Sobel ou Prewitt.
     *
     * @param filter2
     * Second filtre de Sobel ou Prewitt.
     *
     * @param result
     * Cet entier va nous aider dans le controle des valeurs en fonction du type de filtre passe en
     * parametre
     *
     * @return
     * La bitmap convoluee selon le filtre de Sobel ou Prewitt choisi.
     */
    public Bitmap sobelPrewitt(Bitmap bitmap, int [][] filter1, int [][] filter2, int result){

        int t = filter1.length, width = bitmap.getWidth(), height = bitmap.getHeight(),indice,
                currentPixel;
        int[] pixelTab = new int[width * height];
        bitmap = toGray(bitmap);
        bitmap.getPixels(pixelTab,0,width,0,0,width,height);
        int [] copyPixelTab = pixelTab.clone();

        for (int j = t / 2; j < width - t/2; ++j) {
            for (int i = t / 2; i < height - t / 2; ++i) {
                int gradientRX = 0, gradientGX = 0, gradientBX = 0, gradientRY = 0, gradientGY = 0,
                        gradientBY = 0;
                int normeR, normeG, normeB;

                for (int k = 0; k < t; ++k) {
                    for (int l = 0; l < t; ++l) {
                        //on recupere l'indice des voisins a commencer par le premier y compris le
                        // pixel pricipal lui-meme dans la variable indice
                        indice = ((j - t / 2) + (i - t / 2) * width) + k * width + l;
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
                        normeB = controle(normeB, 0, (int) (Math.sqrt(2.00000) * result * 255),
                                0, 255);
                    }
                    if (normeR > 255) {
                            normeR = controle(normeR, 0, (int) (Math.sqrt(2.00000) * result * 255),
                                    0, 255);
                    }
                    if (normeG > 255) {
                            normeG = controle(normeG, 0, (int) (Math.sqrt(2.00000) * result * 255),
                                    0, 255);
                    }

                    int couleur = Color.rgb(normeR, normeG, normeB);

                    pixelTab[(j - t / 2) + (i - t / 2) * width] = couleur;
                }
            }
        }

        bitmap.setPixels(pixelTab, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Cette methode permet de generer des filtres gaussiens de taille (2*radius+1)*(2*radius+1) en
     * choisissant le rayon et le coefficient sigma.
     *
     * @param radius
     * Rayon du filtre.
     *
     * @param sigma
     * Plus sigma > 1 est grand plus la bitmap sera floue, si sigma < 1 on réduit le bruit.
     *
     * @return
     * Un filtre gaussien definit comme on l'a choisi.
     */
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
                noyau1[radius+i][radius+j] = (int) ((noyau[radius+i][radius+j])*gaussKernel);
            }
        }

        return noyau1;
    }

    /**
     * Cette methode permet de generer des filtres moyenne de taille (2*radius+1)*(2*radius+1) en
     * choisissant le rayon.
     *
     * @param radius
     * Rayon du filtre.
     *
     * @return
     * Un filtre moyenne definit comme on l'a choisi.
     */
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

    /**
     * Cette methode passe la bitmap en negatif, on inverse les couleurs en calculant 255-pixel pour
     * chaque pixel de la bitmap.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @return
     * La bitmap en effet negatif.
     */
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

    /**
     * Cette methode permet d'appliquer en fond d'ecran de notre telephone la bitmap que nous avons
     * creee apres modifications par des effets.
     *
      * @param bitmap
     * La bitmap initiale.
     */
    public void wallpaper(Bitmap bitmap){

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Cette methode permet d'applique un effet crayon a notre bitmap.
     *
     * @param bitmap
     * La bitmap initiale.
     *
     * @return
     * La bitmap modifiee avec un effet crayon.
     */
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

    /**
     * Cette methode permet de sauvegarder la bitmap qu'on vient de modifier dans la memoire de
     * botre telephone.
     *
     * @param bitmap
     * La bitmap a laquelle on a applique des effets.
     */
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

    /**
     * Cette methode permet de reactualiser la galerie du telephone pour faire apparaitre le fichier
     * que l'on a cree et qu'on a nomme "Sauvegarde".
     *
     * @param file
     * Le fichier a creer dans le telephone.
     */
    public void refreshGallery(File file) {

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    /**
     * Cette methode cree le fichier ou on va sauvegarder les images modifiees, ce fichier est
     * nomme "Sauvegarde"/
     *
     * @return
     * Le fichier cree dans le telephone.
     */
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

}





