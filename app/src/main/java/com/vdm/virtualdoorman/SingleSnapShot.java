package com.vdm.virtualdoorman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.net.URL;

public class SingleSnapShot extends AppCompatActivity {

    String Url;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_snap_shot);
        Intent intent = getIntent();
        Url = intent.getStringExtra("Url");
        ImageView snapshotimg = (ImageView) findViewById(R.id.imageView2);
        new LoadImage(snapshotimg).execute();
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        public LoadImage(ImageView imv) {
            this.imv = imv;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SingleSnapShot.this);
            pDialog.setMessage("Loading....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bmp = null;
            // String sResponse = null;
            try {

                URL url = new URL(Url);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return bmp;

        }
        @Override
        protected void onPostExecute(Bitmap result) {
            imv.setVisibility(View.VISIBLE);
            imv.setImageBitmap(result);
            pDialog.dismiss();
        }
    }



}
