package org.nexussoft.spstlunches;

import android.content.ClipData;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vesel on 14.05.2016.
 */
public class DataProvider {

    public static final String URL = "http://195.113.207.202:8080/faces/login.jsp";

    private Document mPage;

    public DataProvider() throws Exception {
        mPage = Jsoup.connect(URL).get();
    }

    public String[] getLatest() {
        ItemHolder[] result = new ItemHolder[2];

        Elements nextDayDivs = mPage.select("div.jidelnicekDen").select("div > div");
        String first = nextDayDivs.get(3).text();
        String second = nextDayDivs.get(4).text();

        return new String[]{this.extractImportantInfo(first), this.extractImportantInfo(second), this.extractFirstPart(first), this.extractFirstPart(second)};
    }

    private String extractImportantInfo(String input) {
        String[] splitInput = input.split(" \\-\\- ");
        String[] nameParts = splitInput[2].split("\\(")[0].split("\\, ");

        StringBuilder result = new StringBuilder();
        for (int i = 1; i < (nameParts.length - 1); i++) {
            result.append(nameParts[i] + ", ");
        }

        return result.toString().substring(0, result.toString().length() - 2);
    }

    private String extractFirstPart(String input) {
        String[] splitInput = input.split(" \\-\\- ");
        String[] nameParts = splitInput[2].split("\\(")[0].split("\\, ");
        return nameParts[0];
    }
}