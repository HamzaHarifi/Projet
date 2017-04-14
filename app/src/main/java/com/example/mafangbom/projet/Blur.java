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
 */

    public class Blur extends AppCompatActivity {

        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 25f; // cette valeur est la maximale possible. en fait au dela de cette valeur on a une image toute blanche. setRadius android developper nous donne comme description
    //Set the radius of the Blur. Supported range 0 < radius <= 25


    public static Bitmap blur(View v) {

            return blur((Blur) v.getContext(), getScreenshot(v));
        }

        public static Bitmap blur(Context ctx, Bitmap image) {
            Bitmap photo = image.copy(Bitmap.Config.ARGB_8888, true);

            try {
                final RenderScript rs = RenderScript.create( ctx );
                final Allocation input = Allocation.createFromBitmap(rs, photo, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
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
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            v.draw(c);
            return bitmap;
        }


    public static  Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight()); // on utilise un buffer pour accelerer les operations sur les pixels en allouant un espace necessaire
        base.copyPixelsToBuffer(buffBase); // on mets les pixels dans le buffer. Par abnalogie on peut le comparer àla méthode getPixels
        buffBase.rewind(); // elle rembobine le buffer. Rewinds this buffer. The position is set to zero and the mark is discarded.

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            //int greenValueFilter = Color.green(filterInt);
            //int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
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


    static int colordodge(int in1, int in2) {
        //float image = (float)in2;
        //float mask = (float)in1;
        //return ((int) ((image == 255) ? image : Math.min(255, (((long)mask << 8 ) / (255 - image))) ));
        return ((int) ((in2 == 255) ? in2 : Math.min(255, (((long)in1 << 8 ) / (255 - in2))) ));

    }


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


