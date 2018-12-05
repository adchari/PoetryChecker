package com.example.adith.poetrychecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void btnOnClick(View v) {
        final TextView txt = findViewById(R.id.poemInput);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String toSet = getData("https://api.datamuse.com/words?sl=periwinkle");
                    txt.post(new Runnable() {
                        @Override
                        public void run() {
                            txt.append(toSet);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static String getData(String url) throws Exception {
        URL api = new URL(url);
        HttpURLConnection link = (HttpURLConnection) api.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
