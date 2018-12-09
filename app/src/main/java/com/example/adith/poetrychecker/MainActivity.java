package com.example.adith.poetrychecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.*;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void btnOnClick(View v) {
        final TextView txt = findViewById(R.id.poemInput);
        String poemIn = txt.getText().toString();
        final String[][] poemArray = stringArray(poemIn);
        final Spinner spin = findViewById(R.id.poemType);
        final String poemType = spin.getSelectedItem().toString();
        final TextView output = findViewById(R.id.textView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (poemType.equals("Couplet")) {
                    if (isCouplet(poemArray)) {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is a couplet");
                            }
                        });
                    } else {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is not a couplet");
                            }
                        });
                    }
                } else if (poemType.equals("Haiku")) {
                    if (isHaiku(poemArray)) {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is a haiku");
                            }
                        });
                    } else {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is not a haiku");
                            }
                        });
                    }
                } else if (poemType.equals("Quatrain")) {
                    if (isQuatrain(poemArray)) {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is a quatrain");
                            }
                        });
                    } else {
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.setText("This is not a quatrain");
                            }
                        });
                    }
                }
            }
        }).start();
    }
    private static String getData(String url) throws Exception {
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
    private static String[][] stringArray(String input) {
        String[] byLines = input.split("\\n");
        String[][] byWords = new String[byLines.length][];
        for(int i = 0; i < byWords.length; i++) {
            byWords[i] = byLines[i].split(" ");
        }
        return byWords;
    }
    private static int getNumSyllables(String word) {
        String url = "https://api.datamuse.com/words?sl=" + word;
        try {
            String data = getData(url);
            JSONArray obj = new JSONArray(data);
            JSONObject correctWord = obj.getJSONObject(0);
            return (Integer) correctWord.get("numSyllables");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    private static String[] getRhymes(String word) {
        String url = "https://api.datamuse.com/words?rel_rhy=" + word;
        String url2 = "https://api.datamuse.com/words?rel_nry=" + word;
        ArrayList<String> array = new ArrayList<>();
        try {
            String data1 = getData(url);
            JSONArray rhy = new JSONArray(data1);
            String data2 = getData(url2);
            JSONArray nry = new JSONArray(data2);
            for (int i = 0; i < rhy.length(); i++) {
                array.add((String) rhy.getJSONObject(i).get("word"));
            }
            for (int i = 0; i < nry.length(); i++) {
                array.add((String) nry.getJSONObject(i).get("word"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] s = new String[array.size()];
        return array.toArray(s);
    }
    private static int sumSyllables(String[] line) {
        int sum = 0;
        for (String s : line) {
            sum += getNumSyllables(s);
        }
        return sum;
    }
    private static boolean checkSyllables(String[][] poem) {
        int syllCount = sumSyllables(poem[0]);
        for (String[] s : poem) {
            if (sumSyllables(s) != syllCount) {
                return false;
            }
        }
        return true;
    }
    private static boolean checkLinesRhyme(String[][] poem) {
        String[] endWords = new String[poem.length];
        for (int i = 0; i < poem.length; i++) {
            endWords[i] = poem[i][poem[i].length - 1];
        }
        String[] rhymes = getRhymes(endWords[0]);
        for (int i = 1; i < endWords.length; i++) {
            if (!contains(rhymes, endWords[i])) {
                return false;
            }
        }
        return true;
    }
    private static boolean contains(String [] array, String word) {
        for (String s : array) {
            if (s.equals(word)) {
                return true;
            }
        }
        return false;
    }
    private static boolean isCouplet(String[][] poem) {
        return poem.length == 2 && checkSyllables(poem) && checkLinesRhyme(poem);
    }
    private static boolean isHaiku(String[][] poem) {
        return poem.length == 3
                && sumSyllables(poem[0]) == 5
                && sumSyllables(poem[1]) == 7
                && sumSyllables(poem[2]) == 5;
    }
    private static boolean isQuatrain(String[][] poem) {
        if (poem.length != 4) {
            return false;
        }
        String[][] AA = new String[2][];
        AA[0] = poem[0];
        AA[1] = poem[2];
        String[][] BB = new String[2][];
        BB[0] = poem[0];
        BB[1] = poem[2];
        return isCouplet(AA) && isCouplet(BB);
    }
}
