/****************************************************************************
**
** Copyright (C) 2012 Coode Software
** Contact: http://www.coodesoft.com/
**
** This file is part of Notee.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
**
****************************************************************************/

package com.coodesoft.notee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.Html.ImageGetter;
import android.text.style.ImageSpan;
import android.util.Base64;


public class MyImageGetter implements ImageGetter {

	@Override
	public Drawable getDrawable(String source) {
        Drawable d = null;
        
        String strSrcLeft5 = source.substring(0, 5);
        
        // data
        if(strSrcLeft5.equalsIgnoreCase("data:")) {
            InputStream is = new ByteArrayInputStream(source.getBytes());
            
            //d = Drawable.createFromStream(is, null);
            //d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            
            //Bitmap dBitmap = BitmapFactory.decodeByteArray(data, 0, length);
            int nPosComma = source.indexOf(',');
            if(nPosComma > 0)
            {
            	byte[] arrBuffer = Base64.decode(source.substring(nPosComma + 1), Base64.DEFAULT);
            	//byte[] arrBuffer = Base64Coder.decode(source.substring(nPosComma + 1));
                Bitmap dBitmap = BitmapFactory.decodeByteArray(arrBuffer, 0, arrBuffer.length);
                d = new BitmapDrawable(dBitmap);
                d.setBounds(0, 0, dBitmap.getWidth(), dBitmap.getHeight());
            }

        } else {
        // url

            try {

                InputStream is = (InputStream) new URL(source).getContent();
                Bitmap dBitmap = BitmapFactory.decodeStream(is);
                if(dBitmap == null) {
            		d = Resources.getSystem().getDrawable(android.R.drawable.picture_frame);
                } else {
                    d = new BitmapDrawable(dBitmap);
                    d.setBounds(0, 0, dBitmap.getWidth(), dBitmap.getHeight());
                }
                
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

             } catch (MalformedURLException e) {

                e.printStackTrace();

             } catch (IOException e) {

                e.printStackTrace();

             }


        	/*
            URLDrawable urlDrawable = new URLDrawable();

            // get the actual source
            ImageGetterAsyncTask asyncTask = 
                new ImageGetterAsyncTask( urlDrawable);

            asyncTask.execute(source);

            // return reference to URLDrawable where I will change with actual image from
            // the src tag
            return urlDrawable;
            */
        	
        }

        return d;
	}
	
	
	
    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
        	if(result == null) {
        		result = Resources.getSystem().getDrawable(android.R.drawable.picture_frame);
        		result.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());        		
        	} else {
                urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 
                        + result.getIntrinsicHeight()); 
        	}     	

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;
            if(urlDrawable.drawable != null)
            	urlDrawable.drawable.invalidateSelf();

            // redraw the image by invalidating the container
 //           URLImageParser.this.container.invalidate();
        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
                        + drawable.getIntrinsicHeight()); 
                return drawable;
            } catch (Exception e) {
                return null;
            } 
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }

}
