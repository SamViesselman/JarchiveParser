/**
 * Created by Sam on 11/3/2015.
 */


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


class JeopardyData {
    public String date;
    public String[] headers = new String[12];
    public String[][] jeopardyClues = new String[6][5];
    public String[][] dJeopardyClues = new String[6][5];
    public String[][] jeopardyAnswers = new String[6][5];
    public String[][] dJeopardyAnswers = new String[6][5];

    JeopardyData(){};

}


public class Parser {

    private static final String FILE_HEADER = "Date,Season,Episode,Category,C1,A1,C2,A2," +
            "C3,A3,C4,A4,C5,A5,";

    public static HashMap<String, String> makeDateToEpisodeMap() throws IOException{

        HashMap<String, String> dateToEpisode = new HashMap<>();

        for(int seasonIndex = 1; seasonIndex <= 32; seasonIndex++) {



            String pageURL = String.format("http://j-archive.com/showseason.php?season=%d", seasonIndex);


            Document doc = Jsoup.connect(pageURL).timeout(100*1000).get();

            Elements leftText = doc.select("td");


            int gameId = 1;
            for(Element test : leftText) {


                if (!test.text().isEmpty() && test.text().length() > 4) {

                    if (test.text().substring(0, 1).equals("#")) {

                        int firstComma = test.text().indexOf(",");
                        gameId = Integer.parseInt(test.text().substring(1,firstComma).trim());

                    }
                }
            }

            for(Element test : leftText) {

                if (!test.text().isEmpty() && test.text().length() > 4) {

                    if (test.text().substring(0, 1).equals("#")) {

                        String dateToSave;
                        String episodeIdToSave;

                        int firstComma = test.text().indexOf(",");
                        int currentId = Integer.parseInt(test.text().substring(1,firstComma).trim());
                        dateToSave = test.text().substring(test.text().length() - 10,test.text().length());

                        dateToSave =  dateToSave.substring(5,10) + "-" + dateToSave.substring(0,4);

                        episodeIdToSave = seasonIndex + "-" + (currentId - gameId + 1);


                        System.out.println(dateToSave + " " + episodeIdToSave);

                        dateToEpisode.put(dateToSave, episodeIdToSave);

                    }
                }
            }








        }




        return dateToEpisode;

    }

    public static String getDate(Document webPage){

        Element links = webPage.select("title").first();

        String titleText = links.text();

        int a = titleText.indexOf("aired");

        String dateOnly = titleText.substring(a + 6, titleText.length());

        return dateOnly.substring(5,dateOnly.length()) + "-" + dateOnly.substring(0,4);

    }

    public static String getClue(Document webPage, String searchTag){

        Element clueElement;

            clueElement = webPage.select(searchTag).first();

        if(clueElement == null){
            return null;
        }

        return '"' + clueElement.text().replace("\"","") + '"';

    }

    public static String getAnswer(Document webPage, String searchTag){

        Element links = webPage.select(searchTag).first();

        if(links == null){
            return null;
        }

        String tester = links.attr("onmouseover");

        int a = tester.indexOf("response") + 10;
        int b = tester.indexOf("</em><br");




        return '"' + tester.substring(a,b).replace("\"","") + '"';




    }

    public static String[] getCategory(Document webPage) {

        String[] arrayOfCategories = new String[13];
        Elements categories = webPage.select("td.category_name");

        int counter = 0;
        for(Element s : categories) {
            if(s != null && counter <= 12) {
                arrayOfCategories[counter] = s.text();
            }
            counter++;
        }

        String[] outputArray = new String[12];
        for(int i = 0; i <= 11; i++){
            if (arrayOfCategories[i]==null){
                outputArray[i] = null;
            }
            else {
                outputArray[i] = '"' + arrayOfCategories[i].replace("\"", "") + '"';
            }
        }

        return outputArray;

    }

    public static JeopardyData getOneGameData(String pageURL) throws IOException{

        Document doc = Jsoup.connect(pageURL).timeout(100*1000).get();


        JeopardyData thisGameData = new JeopardyData();

        String jeopardyClueID;
        String dJeopardyClueID;

        String jeopardyAnswerID;
        String dJeopardyAnswerID;

        for(int i = 1; i <= 6; i++ ) {
            for (int j = 1; j <= 5; j++) {

                jeopardyClueID = String.format("td#clue_J_%d_%d",i,j);
                dJeopardyClueID = String.format("td#clue_DJ_%d_%d",i,j);

                thisGameData.jeopardyClues[i-1][j-1] = getClue(doc,jeopardyClueID);
                thisGameData.dJeopardyClues[i-1][j-1] = getClue(doc,dJeopardyClueID);

                jeopardyAnswerID = String.format("[onmouseover*=clue_J_%d_%d]",i,j);
                dJeopardyAnswerID = String.format("[onmouseover*=clue_DJ_%d_%d]",i,j);

                thisGameData.jeopardyAnswers[i-1][j-1] = getAnswer(doc, jeopardyAnswerID);
                thisGameData.dJeopardyAnswers[i-1][j-1] = getAnswer(doc, dJeopardyAnswerID);

            }

        }
        thisGameData.headers = getCategory(doc);

        thisGameData.date = getDate(doc);



        return thisGameData;
    }

    public static void writeDataToFile(JeopardyData oneGame, HashMap<String, String> theMap, FileWriter writer) throws IOException{

        for(int i = 0; i <=5; i++){

            writer.append(oneGame.date);
            writer.append(",");
            if (theMap.get(oneGame.date) == null){}
            else {
                String[] seasonEpisode = theMap.get(oneGame.date).split("-");
                writer.append(seasonEpisode[0]);
                writer.append(",");
                writer.append(seasonEpisode[1]);
                writer.append(",");
            }
            writer.append(oneGame.headers[i]);
            writer.append(",");

            for(int j = 0; j <=4; j++){

                writer.append(oneGame.jeopardyClues[i][j]);
                writer.append(",");
                writer.append(oneGame.jeopardyAnswers[i][j]);
                writer.append(",");
            }

            writer.append("\n");


        }

        for(int i = 0; i <=5; i++){

            writer.append(oneGame.date);
            writer.append(",");
            if (theMap.get(oneGame.date) == null){}
            else {
                String[] seasonEpisode = theMap.get(oneGame.date).split("-");
                writer.append(seasonEpisode[0]);
                writer.append(",");
                writer.append(seasonEpisode[1]);
                writer.append(",");
            }
            writer.append(oneGame.headers[i+6]);
            writer.append(",");

            for(int j = 0; j <=4; j++){

                writer.append(oneGame.dJeopardyClues[i][j]);
                writer.append(",");
                writer.append(oneGame.dJeopardyAnswers[i][j]);
                writer.append(",");
            }

            writer.append("\n");


        }


    }

    public static void main(String[] arguments)throws IOException{


        HashMap<String, String> episodeMap = makeDateToEpisodeMap();


        //FileWriter theWriter = new FileWriter("C:\\test.csv", true);

        FileWriter theWriter = new FileWriter("C:\\test2.csv");

        theWriter.append(FILE_HEADER);
        theWriter.append("\n");


        for(int i = 801; i <= 1600; i++) {

            if (!(i==1347 || i==1933 || i==1348 || i==948 || i==940 || i==1936 || i==1349 || i==1940
                    || i==1970 || i==1982 || i==1985 || i==1022)) {
                String currentURL = String.format("http://www.j-archive.com/showgame.php?game_id=%d", i);
                JeopardyData thisGameData = getOneGameData(currentURL);

                writeDataToFile(thisGameData, episodeMap, theWriter);
                System.out.println("Just Finished Game ID:" + i);
            }

        }

        theWriter.flush();
        theWriter.close();

    }

};

