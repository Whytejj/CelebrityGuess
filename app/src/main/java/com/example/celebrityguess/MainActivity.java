package com.example.celebrityguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celeburls = new ArrayList<>();
    ArrayList<String> celebnames = new ArrayList<>();
    ArrayList<Integer> usedcelebs = new ArrayList<>();
    ImageView imageView;
    Button button1;
    Button button2 ;
    Button button3 ;
    Button button4;
    TextView textView;
    int chosenceleb = 0;
    int anslocation = 0;
    //int[] usedcelebs = new int[celeburls.size()];
    String[] choices = new String[4];
    int total = 0;
    int correct = 0;

    public  void tapped(View view) {
        if (view.getTag().toString().equals(Integer.toString(anslocation))){
            Toast.makeText(getApplicationContext(), "correct!", Toast.LENGTH_LONG).show();
            correct++;
        }else{
            Toast.makeText(getApplicationContext(), "wrong!", Toast.LENGTH_LONG).show();
        }
        total++;
        textView.setText(correct+"/"+total);
        progress();
    }
    public void progress() {

        Random random = new Random();
        chosenceleb = random.nextInt(celeburls.size());
        for (int j = 0; j < usedcelebs.size(); j++){
            while (chosenceleb == usedcelebs.get(j)){
                chosenceleb = random.nextInt(celeburls.size());
                j = 0;
            }
        }
        ImageDownloader image = new ImageDownloader();
        Bitmap celebimage = null;
        try {
            celebimage = image.execute(celeburls.get(chosenceleb)).get();

            imageView.setImageBitmap(celebimage);
            anslocation = random.nextInt(4);
            for (int i = 0; i < 4; i++){
                if(i == anslocation){
                    choices[i] = celebnames.get(chosenceleb);

                }else{
                    int wrongname = random.nextInt(celeburls.size());
                    while (wrongname==chosenceleb){
                        wrongname = random.nextInt(celeburls.size());
                    }
                    choices[i] = celebnames.get(wrongname);
                }
            }
            button1.setText(choices[0]);
            button2.setText(choices[1]);
            button3.setText(choices[2]);
            button4.setText(choices[3]);
            usedcelebs.add(chosenceleb);
            if(total == 23){
                while (true) {
                    Toast.makeText(getApplicationContext(), "do!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        textView = findViewById(R.id.textView);


        DownloadTask task = new DownloadTask();
        try {
            String htmlstring = task.execute("http://www.posh24.se/kandisar").get();
            Log.i("urlhtml", htmlstring);

            String[] splithtml = htmlstring.split("sidebarContainer");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splithtml[0]);

            while (m.find()){
                celeburls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splithtml[0]);

            while (m.find()){
                celebnames.add(m.group(1));
            }
           progress();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   public class DownloadTask extends AsyncTask<String, Void, String>{

       @Override
       protected String doInBackground(String... urls) {
           String htmlstring = "";
           try {
               URL url = new URL(urls[0]);
               HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
               InputStream inputStream = urlconnection.getInputStream();
               InputStreamReader reader = new InputStreamReader(inputStream);
               int data = reader.read();
               while (data !=-1){
                   char current = (char) data;
                   htmlstring+=current;

                   data = reader.read();
               }

               return htmlstring;
           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

           return null;
       }
   }
   public static class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

       @Override
       protected Bitmap doInBackground(String... urls) {
           try {
               URL url = new URL(urls[0]);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.connect();
               InputStream inputStream = urlConnection.getInputStream();
               Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
               return bitmap;
           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
       }
   }
}
