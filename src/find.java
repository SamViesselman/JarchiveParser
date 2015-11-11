/**
 * Created by Sam on 11/4/2015.
 */


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import java.io.IOException;
import java.util.Date;

public class find {

    public static void main(String[] arguments) throws IOException{

        Document doc = Jsoup.connect("http://www.j-archive.com/showgame.php?game_id=2729").get();


        Element links = doc.select("title").first();

        String titleText = links.text();

        int a = titleText.indexOf("aired");

        String dateOnly = titleText.substring(a + 6, titleText.length());
        System.out.println(dateOnly);

        String finalDate = dateOnly.substring(6,dateOnly.length()) + "-" + dateOnly.substring(0,4);

        System.out.println(finalDate);

    }

}
