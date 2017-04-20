package com.example.mafangbom.projet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.nio.IntBuffer;

/**
 * Created by mafangbom on 17/03/17.
 *
 * Cette classe nous sert essentiellement dans l'implementation du filtre "effet crayon". Elle a
 * ete recuperee sur les sites :
 * http://stackoverflow.com/questions/6795483/create-blurry-transparent-background-effect/
 * 21052060#21052060
 * http://stackoverflow.com/questions/9826273/photo-image-to-sketch-algorithm
 * Nous l'avons modifiee afin d'utiliser nos propres methodes toGray et invert et non celles qu'on
 * peut trouver sur ces sites.
 * Les methodes qui nous sont utiles sont : blur(Context ctx, Bitmap image), ColorDodgeBlend(Bitmap
 * source, Bitmap layer) et colordodge(int in1, int in2).
 */
    public class Blur extends AppCompatActivity {

        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 25f; // cette valeur est la maximale possible.
    // En fait au-dela de cette valeur on a une image toute blanche. setRadius de android developer
    // nous donne comme description :Set the radius of the Blur. Supported range 0 < radius <= 25.


    public static Bitmap blur(View v) {

            return blur((Blur) v.getContext(), getScreenshot(v));
        }

    /**
     * Cette methode permet d'appliquer le filtre gaussien Blur sur la bitmap passee en parametre.
     * @param ctx
     * Le contexte.
     *
     * @param image
     * La bitmap initiale.
     *
     * @return
     * La bitmap a laquelle on a applique un filtre gaussien.
     */
        public static Bitmap blur(Context ctx, Bitmap image) {
            Bitmap photo = image.copy(Bitmap.Config.ARGB_8888, true);

            try {
                final RenderScript rs = RenderScript.create( ctx );
                final Allocation input = Allocation.createFromBitmap(rs, photo,
                        Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                final Allocation output = Allocation.createTyped(rs, input.getType());
                ScriptIntrinsicBlur script = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    script.setRadius( BLUR_RADIUS ); /* e.g. 3.f */
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    script.setInput( input );
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    script.forEach( output );
                }
                output.copyTo( photo );
            }catch (Exception e){
                e.printStackTrace();
            }
            return photo;
        }

        private static Bitmap getScreenshot(View v) {
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            v.draw(c);
            return bitmap;
        }

    /**
     * Cette methode permet de faire un melange de couleurs des deux bitmaps passees en parametre.
     *
     * @param source
     * Premiere bitmap.
     *
     * @param layer
     * Seconde bitmap.
     *
     * @return
     * La bitmap melangee.
     */
    public static  Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        // on utilise un buffer pour accelerer les operations sur les pixels en allouant un espace
        // necessaire
        base.copyPixelsToBuffer(buffBase); // on mets les pixels dans le buffer.
        // Par analogie on peut le comparer a la methode getPixels.
        buffBase.rewind(); // rembobine le buffer.
        // Rewinds this buffer. The position is set to zero and the mark is discarded.

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt); // nous faisons  le traitement que sur le
            // canal rouge puisque nous passons une bitmap en niveaux de gris d'ou les commentaires
            // sur tout ce qui concerne les canaux vert et bleu
            //int greenValueFilter = Color.green(filterInt);
            //int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            //int greenValueSrc = Color.green(srcInt);
            //int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc); // explication avec la
            // méthode colordodge ci-dessous
            //int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            //int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

            int pixel = Color.argb(255, redValueFinal, redValueFinal,redValueFinal);
            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }

    /**
     * Cette methode renvoie 255 si in2 la valeur du canal passee en parametre est 255, sinon elle
     * renvoie le min entre 255 et (((long)in1 << 8 ) / (255 - in2))).
     * Nous l'utilisons dans la methode Colordodge plus haut qui prend en parametre une bitmap
     * invert floutee en blur et une bitmap grise.
     * Elle met du blanc la ou le blur est moins present et du  noir lorsque le blur est plus
     * present, surout sur les contours.
     * @param in1
     * @param in2
     * @return
     */
    static int colordodge(int in1, int in2) {

        return ((int) ((in2 == 255) ? in2 : Math.min(255, (((long)in1 << 8 ) / (255 - in2))) ));

    }

    /**
     * Il s'agit de la methode toGray de la classe MainActivity a une difference pres qu'on renvoie
     * la copie de  la bitmap de depart.
     * @param bitmap
     * @return
     */
    public static Bitmap toGray(Bitmap bitmap) {
        Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[] Pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(Pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);
            rd = (int) (0.3 * rd + 0.59 * vt + 0.11 * bl);
            Pixels[i] = Color.rgb(rd, rd, rd);
        }
        copy.setPixels(Pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        return copy;
    }

    /**
     * Il s'agit de la methode invert de la classe MainActivity a une difference pres qu'on renvoie
     * la copie de la bitmap de depart.
     * @param bitmap
     * @return
     */
    public static Bitmap invert(Bitmap bitmap) {
        Bitmap copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[] Pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(Pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);

            Pixels[i] = Color.rgb(255 - rd ,255 - vt, 255 - bl);
        }

        copy.setPixels(Pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        return copy;
    }


    }

// https://openclassrooms.com/courses/apprenez-a-programmer-en-java/les-flux-d-entree-sortie
//https://openclassrooms.com/courses/apprenez-a-programmer-en-java/la-genericite-en-java
//http://stackoverflow.com/questions/6795483/create-blurry-transparent-background-effect/21052060#21052060
//http://stackoverflow.com/questions/9826273/photo-image-to-sketch-algorithm


