package com.offsidegame.offside.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.OffsideApplication;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by KFIR on 3/13/2017.
 */

public class ImageHelper {

    public static void loadImage(Context context, final String imageUrl, final ImageView imageView, final String callerName) {
        Picasso.with(context).load(imageUrl).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                imageView.setImageDrawable(roundedImage);
            }

            @Override
            public void onError() {
                throw new RuntimeException(callerName + " - onCreate - Error loading image with url: " + imageUrl);
            }
        });
    }

    public static void loadImage(Context context, final File imagePath, final ImageView imageView, final String callerName) {
        Picasso.with(context).load(imagePath).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                imageView.setImageDrawable(roundedImage);
            }

            @Override
            public void onError() {
                throw new RuntimeException(callerName + " - onCreate - Error loading image with path: " + imagePath.getPath());
            }
        });
    }

    public static void storeImage(Bitmap image, Context context) {
        String filename = OffsideApplication.getProfileImageFileName();
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleException(ex);
        }

    }

    public static Bitmap generateInitialsBasedProfileImage(String initials, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        int colorPrimary = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorSecondary)));

        paint.setColor(colorPrimary);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(50, 50, 51, paint);


        Paint textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaint.setTextSize(40);


        textPaint.setStyle(Paint.Style.FILL);

        //paint.setTextSize(20);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));


        canvas.drawText(initials, xPos, yPos, textPaint);
        return bitmap;
    }


}
