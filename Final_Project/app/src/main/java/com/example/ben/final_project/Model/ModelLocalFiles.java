package com.example.ben.final_project.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.ben.final_project.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mazliachbe on 26/06/2017.
 */

/**
 * ModelLocalFiles class to handle local storage pictures save and load
 */
public class ModelLocalFiles {

    /**
     * Save an image ASYNC locally.
     * @param imageBitmap the Bitmap image to be saved.
     * @param imageFileName the name of the generated save file.
     */
    static void saveImageToFileAsynch(Bitmap imageBitmap, String imageFileName){
        AsyncTask<Object,String,Boolean> task = new AsyncTask<Object,String,Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                saveImageToFile((Bitmap)params[0], (String)params[1]);
                return true;
            }
        };
        task.execute(imageBitmap, imageFileName);
    }

    /**
     * Save an image SYNC locally.
     * @param imageBitmap the Bitmap image to be saved.
     * @param imageFileName the name of the generated save file.
     */
    private static void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback that fires when the bitmap image is returned from local storage
     */
    interface LoadImageFromFileAsynch{
        void onComplete(Bitmap bitmap);
    }

    /**
     * Get an image locally if exists in storage ASYNC.
     * @param imageFileName name of the image.
     * @param callback see {@link LoadImageFromFileAsynch}.
     */
    static void loadImageFromFileAsynch(String imageFileName,
                                        final LoadImageFromFileAsynch callback) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String,String,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                return loadImageFromFile(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callback.onComplete(bitmap);
            }
        };
        task.execute(imageFileName);
    }

    /**
     * Get an image locally if exists in storage SYNC.
     * @param imageFileName name of the image.
     */
    private static Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we won't need to manage the pictures cache
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getContext().sendBroadcast(mediaScanIntent);
    }


}
