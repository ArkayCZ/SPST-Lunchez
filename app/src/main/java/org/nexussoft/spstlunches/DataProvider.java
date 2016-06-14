package org.nexussoft.spstlunches;

import android.content.ClipData;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by vesel on 14.05.2016.
 */
public class DataProvider {

    public static final String URL = "http://195.113.207.202:8080/faces/login.jsp";

    private Document mPage;

    private boolean mDownloadFailed = false;

    public DataProvider() throws Exception {
        try {
            mPage = Jsoup.connect(URL).get();
            mDownloadFailed = false;
        } catch (Exception e) {
            mDownloadFailed = true;
        }
    }

    public String[] getLatest() throws Exception {
        if (mDownloadFailed)
            throw new Exception();

        Elements nextDayDivs = mPage.select("div.jidelnicekDen").select("div > div");
        String first = nextDayDivs.get(3).text();
        String second = nextDayDivs.get(4).text();

        first = first.replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");
        second = second.replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");

        return new String[]{this.extractImportantInfo(first), this.extractImportantInfo(second), this.extractFirstPart(first)};
    }

    private String extractImportantInfo(String input) {
        String namePart = input.split(" \\-\\- ")[2];
        int lastBracketIndex = namePart.lastIndexOf("(");
        namePart = namePart.substring(0, lastBracketIndex);

        String[] excludedWords = {", nápoje", ", SALÁTY", ", OVOCE"};
        for (String s : excludedWords)
            namePart = namePart.replace(s, "");

        return namePart.substring(namePart.indexOf(",") + 2);
    }

    private String extractFirstPart(String input) {
        String namePart = input.split(" \\-\\- ")[2];
        return namePart.substring(0, namePart.indexOf(","));
    }
}