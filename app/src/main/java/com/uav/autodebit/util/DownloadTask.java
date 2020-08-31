package com.uav.autodebit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.uav.autodebit.Activity.User_Registration;
import com.uav.autodebit.R;
import com.uav.autodebit.vo.BitmapVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private FileDownloadInterface fileDownloadInterface;
    private BitmapInterface bitmapInterface;
    private String fileURLAddress, downloadFileName = "";
    private ProgressDialog progressDialog;
    List<BitmapVO> bitmapVOs;

    public DownloadTask(FileDownloadInterface fileDownloadInterface,Context context, String fileURLAddress) {
        this.context = context;
        this.fileDownloadInterface=fileDownloadInterface;

        this.fileURLAddress = fileURLAddress;

        downloadFileName = fileURLAddress.substring(fileURLAddress.lastIndexOf('/'), fileURLAddress.length());//Create file name by picking download file name from URL
        Log.w(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    public DownloadTask(BitmapInterface bitmapInterface, Context context, List<BitmapVO> bitmapVOs) {
        this.context = context;
        this.bitmapInterface=bitmapInterface;
        this.bitmapVOs = bitmapVOs;
        new BitmapImageViewTask().execute();
    }


    /*Set Banner Images*/
    public  class BitmapImageViewTask extends AsyncTask<String, Void, Void> {
        ImageView bmImage;

        public BitmapImageViewTask() {

        }

        protected Void doInBackground(String... urls) {

            Bitmap mIcon = null;
            try {

                new DiskLruImageCache(context, bitmapVOs.get(0).getLocalCacheFolder(),bitmapVOs.get(0).getLocalCacheFolderSize(), bitmapVOs.get(0).getImageFormat() ,bitmapVOs.get(0).getImageQuality());

                for(BitmapVO bitmapVO: bitmapVOs){
                    URL newurl = new URL(bitmapVO.getURL());
                    mIcon=  BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    bitmapVO.setBitmap(mIcon);
                    bitmapVOs.set( bitmapVOs.indexOf(bitmapVO) ,bitmapVO);
                    DiskLruImageCache.put(bitmapVO.getFileName(),mIcon);
                }


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            bitmapInterface.downloadComplete(bitmapVOs);
        }
    }




    private class DownloadingTask extends AsyncTask<String, Integer, File> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(File result) {
            try {
                if (result != null) {
                    Utility.dismissDialog(context, progressDialog);


                    fileDownloadInterface.downloadComplete(result);
                    //Toast.makeText(context, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressDialog.setProgress(values[0]);
        }

        protected File doInBackground(String... arg0) {
            try {
                URL url = new URL(fileURLAddress);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }
                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {
                    apkStorage = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + context.getString(R.string.app_name));
                } else{
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                }
                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }
                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection

                int fileLength = c.getContentLength();


                byte[] buffer = new byte[1024];//Set buffer type


                int len1 = 0;//init length
                long total = 0;

                while ((len1 = is.read(buffer)) != -1) {

                    total += len1;

                    publishProgress((int) (total * 100 / fileLength));
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                Utility.dismissDialog(context, progressDialog);

                fileDownloadInterface.error(Utility.getStackTrace(e));
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " +Utility.getStackTrace(e));


            }

            return outputFile;
        }
    }

}
