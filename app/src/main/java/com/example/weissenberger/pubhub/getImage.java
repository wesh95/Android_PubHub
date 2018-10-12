package com.example.weissenberger.pubhub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Travis on 12/15/2017.
 */

class GetImage extends AsyncTask<String, Void, Bitmap> {
    ImageView dealImage;
    Deal deal;

    public GetImage(ImageView dealImage, Deal deal) {
        this.dealImage = dealImage;
        this.deal=deal;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub

        try {
            URL url = new URL(params[0]);
            InputStream is = url.openConnection().getInputStream();
            Bitmap bitMap = BitmapFactory.decodeStream(is);
            return bitMap;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(Bitmap result) {
        deal.setImageData(result);
        dealImage.setImageBitmap(result);
    }

}

