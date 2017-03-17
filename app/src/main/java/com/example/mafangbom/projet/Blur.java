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
        private static final float BLUR_RADIUS = 25f;

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
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
            return b;
        }


    public static  Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

            if( greenValueFinal > redValueFinal && greenValueFinal>blueValueFinal){
                greenValueFinal = redValueFinal;
                blueValueFinal =  redValueFinal;
            }
            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);

            /*float[] hsv = new float[3];
            Color.colorToHSV(pixel, hsv);
            hsv[1] = 0.0f;
            float top = VALUE_TOP; //Between 0.0f .. 1.0f I use 0.87f
            if (hsv[2] <= top) {
                hsv[2] = 0.0f;
            } else {
                hsv[2] = 1.0f;
            }
            pixel = Color.HSVToColor(hsv);
*/

            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return base;
    }


    static int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }


// une modification


    public static Bitmap toGrayTableau(Bitmap bMap) {
        Bitmap bitmap = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(),
                Bitmap.Config.ARGB_8888);
        int[] Pixels = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(Pixels, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);
            rd = (int) (0.3 * rd + 0.59 * vt + 0.11 * bl);
            Pixels[i] = Color.rgb(rd, rd, rd);
        }
        bitmap.setPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bitmap;
    }


    public static Bitmap negatif (Bitmap bMap) {
        Bitmap bitmap = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(),
                Bitmap.Config.ARGB_8888);
        int[] Pixels = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(Pixels, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        for (int i = 0; i < Pixels.length; ++i) {
            int rd = Color.red(Pixels[i]);
            int vt = Color.green(Pixels[i]);
            int bl = Color.blue(Pixels[i]);

            Pixels[i] = Color.rgb(255 - rd ,255 - vt, 255 - bl);
        }
        bitmap.setPixels(Pixels,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return bitmap;
    }


    }


