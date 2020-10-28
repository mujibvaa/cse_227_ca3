package com.techiesatelier.ca3cse227;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ConnectivityManager connectivityManager;
    ImageView imageView;
    TextView textView;
    boolean flag = false;
    MediaPlayer player;
    String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.iv);
        textView = findViewById(R.id.tv);


        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        player = new MediaPlayer();
        checkWritePermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    private void checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            flag = true;
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkWritePermission();
            } else
                Toast.makeText(this, "Write permission denied by user", Toast.LENGTH_SHORT).show();
        }
    }

    public void checknetworkStatus(View view) {

      //  /  String imageUrl = "https://wallpapersite.com/images/pages/pic_w/6408.jpg";

    //   / String textUrl = "https://drive.google.com/uc?export=download&id=1ivvH3bgEqR0JCFBNRyHNcas0dLIXgtO3";

        String musicUrl = "https://drive.google.com/uc?export=download&id=1JYTNenaODX3lOCkqUBY5s-WOJ54O7OSX";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities
                    (connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Toast.makeText(this, "Connected with Mobile network", Toast.LENGTH_SHORT).show();
                    //new MyImageTask().execute(imageUrl);
                    new MyMusicTask().execute(musicUrl);
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Toast.makeText(this, "Connected with Wi-fi network", Toast.LENGTH_SHORT).show();
                    //  new MyImageTask().execute(imageUrl);
                    new MyMusicTask().execute(musicUrl);
                    //    new MyTextTask().execute(textUrl);

                }
            } else
                Toast.makeText(this, "no active network is present", Toast.LENGTH_SHORT).show();
        } else {
//https://drive.google.com/uc?export=download&id=YourFile'sID

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {

                    Toast.makeText(this, "Connected with Wi-fi network", Toast.LENGTH_SHORT).show();
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                    Toast.makeText(this, "Connected with Mobile network", Toast.LENGTH_SHORT).show();
                    //new MyImageTask().execute(imageUrl);
// new MyTextTask().execute(textUrl);
                    new MyMusicTask().execute(musicUrl);
                }
            } else
                Toast.makeText(this, "no active network is present", Toast.LENGTH_SHORT).show();
        }
    }


    class MyMusicTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return downloadMusic(strings[0]);
        }

        String downloadMusic(String path) {
            String s = null;
            try {
                URL myurl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream stream = httpURLConnection.getInputStream();
                    if (stream != null) {
                        Log.d("Tag", "Stream is not null");
                        if (flag) {
                            String state = Environment.getExternalStorageState();
                            if (state.equals(Environment.MEDIA_MOUNTED)) {
                                File root = Environment.getExternalStorageDirectory();

                                File f1 = new File(root, "abc.mp3");

                                FileOutputStream fos = new FileOutputStream(f1);
                                int i = 0;
                                while ((i = stream.read()) != -1) {
                                    fos.write(i);
                                }
                                Log.d("Tag", "Music file downloaded");
                                s = "done";
                                filePath = f1.getAbsolutePath();
                                fos.close();
                            }

                        }


                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {

            Toast.makeText(MainActivity.this, "Done.....", Toast.LENGTH_SHORT).show();
            if (s != null) {
                try {
                    player.setDataSource(filePath);
                    player.prepare();
                    player.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /*

    class MyTextTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return downloadText(strings[0]);
        }

        String downloadText(String path) {
            String s = null;

            try {
                Log.d("Tag", "Inside try");
                URL myurl = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
//httpURLConnection.setReadTimeout(5000);
//httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream stream = httpURLConnection.getInputStream();
                    if (stream != null) {

                        Log.d("Tag", "Stream is not null");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        String line = "";
                        String text = "";
                        while ((line = reader.readLine()) != null) {
                            text = text + line + "\n";
                        }

                        Log.d("Tag", "Reading completed");
                        s = text;
                        if (flag) {
                            String state = Environment.getExternalStorageState();
                            if (state.equals(Environment.MEDIA_MOUNTED)) {
                                File root = Environment.getExternalStorageDirectory();
                                File f = new File(root, "myfile.txt");

                                FileOutputStream fos = new FileOutputStream(f);
                                byte[] b = s.getBytes();
                                fos.write(b);
                                Log.d("Tag", "Writing completed");
                                Log.d("Tag", "Path is " + f.getAbsolutePath());
                                fos.close();

                            }

                        }
                    }
                }
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Tag", "Exceptioan is" + e.getMessage());
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null)
                textView.setText(s);

        }
    }

    class MyImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {

            return downloadImage(strings[0]);
        }

        Bitmap downloadImage(String s) {
            Bitmap bitmap = null;
            try {
                URL myurl = new URL(s);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

                httpURLConnection.connect();

                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream stream = httpURLConnection.getInputStream();

                    if (stream != null) {
                        bitmap = BitmapFactory.decodeStream(stream);
                    }
                }
            } catch (MalformedURLException e) {

                Log.d("Hello", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Hello", e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                Toast.makeText(MainActivity.this, "Bitmap is null", Toast.LENGTH_SHORT).show();

        }
    }

     */



}