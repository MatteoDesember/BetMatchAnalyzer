import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Program {

    private static List<Match> matchQueue = new ArrayList<>();

    final static DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    final static DateTimeFormatter YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm");
    public static LocalDateTime gDateToAnalyze;
    
    private final static String BETEXPLORER_URL = "https://www.betexplorer.com/next/soccer/?year=%d&month=%d&day=%d";

    private static BufferedWriter matchesAllStatOnlyWriter = null;
    private static BufferedWriter matchesWithConditionWriter = null;
    private static BufferedWriter matchesAllWriter = null;



    Program() throws IOException {
        System.out.println("Start Program_BMA_V2.0");
    }

    void start(LocalDateTime dateToAnalyze) throws IOException {

        Date dt = new Date();
        gDateToAnalyze = LocalDateTime.from(dt.toInstant().atZone(ZoneId.systemDefault())).plusDays(1);
        if (dateToAnalyze != null)
            gDateToAnalyze = dateToAnalyze;

        File matchesAll = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/_" + gDateToAnalyze.format(YYYYMMDD) + ".txt");
        File matchesAllStatOnly = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/_" + gDateToAnalyze.format(YYYYMMDD) + "_stat.txt");
        File matchesWithCondition = new File("data/" + gDateToAnalyze.format(YYYYMMDD) + ".txt");

        matchesAll.getParentFile().mkdirs();
        matchesAllStatOnly.getParentFile().mkdirs();
        matchesWithCondition.getParentFile().mkdirs();

        matchesAllWriter = new BufferedWriter(new FileWriter(matchesAll, false));
        matchesAllStatOnlyWriter = new BufferedWriter(new FileWriter(matchesAllStatOnly, false));
        matchesWithConditionWriter = new BufferedWriter(new FileWriter(matchesWithCondition, false));

        Document documentBetExplorer = MyWebDriver.get(
                String.format(BETEXPLORER_URL,
                        gDateToAnalyze.getYear(),
                        gDateToAnalyze.getMonthValue(),
                        gDateToAnalyze.getDayOfMonth()));


        Elements elements = documentBetExplorer.select("tr[data-def=1]:not(:has(span[title=Canceled]))");


        for (Element element : elements) { //On the betexplorer page find all tommorow id's
            matchQueue.add(Match.elementToMatch(element)); //convert element to match and add to list
        }

        int i = 1;
        for (Match match : matchQueue) { //for each match get h2h matches

            System.out.println(i++ + "/" + matchQueue.size() + " Processing " + match.name + "...");

            Match.getH2Hmatches(match);

            String fileName = match.getFileName();
            String header = match.getHeader();
            String details = match.getDetails();
            String moreDetails = match.getMoreDetails();
            String divider = match.getDivider();

            File matchSingleDetails = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/" + fileName + ".txt");
            matchSingleDetails.getParentFile().mkdirs();
            BufferedWriter matchSingleDetailsWriter = new BufferedWriter(new FileWriter(matchSingleDetails, false));

            writeLineToFile(matchSingleDetailsWriter, header + details + moreDetails + divider);
            writeLineToFile(matchesAllWriter, header + details + moreDetails + divider);
            writeLineToFile(matchesAllStatOnlyWriter, header + details + divider);
            if (match.isConditionOK())
                writeLineToFile(matchesWithConditionWriter, header + details + divider);

            matchSingleDetailsWriter.close();

            System.out.println("   ... " + match.name + " --- OK!");
        }
        matchesWithConditionWriter.close();
        matchesAllWriter.close();
        matchesAllStatOnlyWriter.close();
        System.out.println("Finished!\r\n");
    }

    private void writeLineToFile(BufferedWriter bufferedWriter, String string) {
        try {
            bufferedWriter.write(string);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}