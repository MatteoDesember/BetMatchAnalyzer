import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

class SoccerProgram {
    //Queue of founded matches
    private static List<Match> matchQueue = new ArrayList<>();

    //There are some date formatter. Program needs special format for txt file names
    final static DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    final static DateTimeFormatter YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm");

    //Date to analyze
    public static LocalDateTime gDateToAnalyze;

    //On this page we can find all soccer matches in given date
    private final static String BETEXPLORER_URL = "https://www.betexplorer.com/next/soccer/?year=%d&month=%d&day=%d";

    //There are some bufferWriters to write a files
    private static BufferedWriter matchesAllStatOnlyWriter = null;
    private static BufferedWriter matchesWithConditionWriter = null;
    private static BufferedWriter matchesAllWriter = null;
    private static BufferedWriter matchesWithConditionMin3GoalsWriter = null;


    /**
     * This program searches matches wich with given criteria
     */
    SoccerProgram() throws IOException {
        //Start SoccerProgram
        System.out.println("Start Program_BMA_V2.3");
    }

    /**
     * Start program
     */

    void start(LocalDateTime dateToAnalyze) throws IOException {

        //if dateToAnalyze is given by user, use it
        //otherwise process tommorow
        Date dt = new Date();
        gDateToAnalyze = LocalDateTime.from(dt.toInstant().atZone(ZoneId.systemDefault())).plusDays(1);
        if (dateToAnalyze != null)
            gDateToAnalyze = dateToAnalyze;

        //Create few file pointers
        File matchesAll = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/_" + gDateToAnalyze.format(YYYYMMDD) + ".txt");
        File matchesAllStatOnly = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/_" + gDateToAnalyze.format(YYYYMMDD) + "_stat.txt");
        File matchesWithCondition = new File("data/" + gDateToAnalyze.format(YYYYMMDD) + "_gole.txt");
        File matchesWithConditionMin3Goals = new File("data/" + gDateToAnalyze.format(YYYYMMDD) + "_ponad_2_gole.txt");

        //Make dirs for files if there are no exists
        matchesAll.getParentFile().mkdirs();
        matchesAllStatOnly.getParentFile().mkdirs();
        matchesWithCondition.getParentFile().mkdirs();
        matchesWithConditionMin3Goals.getParentFile().mkdirs();

        //Attach files to bufferWriters
        matchesAllWriter = new BufferedWriter(new FileWriter(matchesAll, false));
        matchesAllStatOnlyWriter = new BufferedWriter(new FileWriter(matchesAllStatOnly, false));
        matchesWithConditionWriter = new BufferedWriter(new FileWriter(matchesWithCondition, false));
        matchesWithConditionMin3GoalsWriter = new BufferedWriter(new FileWriter(matchesWithConditionMin3Goals, false));

        //Get all matches which are tommorow
        //Create special url to get data
        Document documentBetExplorer = MyWebDriver.getSoup(
                String.format(BETEXPLORER_URL,
                        gDateToAnalyze.getYear(),
                        gDateToAnalyze.getMonthValue(),
                        gDateToAnalyze.getDayOfMonth()));

        //Get all matches without cancelled ones
        Elements matcheElements = documentBetExplorer.select("tr[data-def=1]:not(:has(span[title=Canceled]))");


        //On the betexplorer page find all tommorow matches id
        for (Element matchElement : matcheElements) {
            //convert matchElement to match (Class Match) and add it to list
            matchQueue.add(Match.elementToMatch(matchElement));
        }

        int i = 1;
        for (Match match : matchQueue) {
            System.out.println(i++ + "/" + matchQueue.size() + " Processing " + match.name + "...");

            //For each match get h2h matches
            Match.getH2Hmatches(match);

            String fileName = match.getFileName();
            String header = match.getHeader();
            String details = match.getDetails();
            String moreDetails = match.getMoreDetails();
            String detailsOver3Goals = match.getDetailsOver3Goals();
            String divider = match.getDivider();

            //Create file with single match details
            File matchSingleDetails = new File("data/details/" + gDateToAnalyze.format(YYYYMMDD) + "/" + fileName + ".txt");
            matchSingleDetails.getParentFile().mkdirs();
            BufferedWriter matchSingleDetailsWriter = new BufferedWriter(new FileWriter(matchSingleDetails, false));

            //Writes to files details
            writeLineToFile(matchSingleDetailsWriter, header + details + detailsOver3Goals + moreDetails + divider);
            writeLineToFile(matchesAllWriter, header + details + detailsOver3Goals + moreDetails + divider);
            writeLineToFile(matchesAllStatOnlyWriter, header + details + detailsOver3Goals + divider);

            //If match meets the criteria write to main file details
            if (match.condGoalInFirstHalf || match.condGoalsInSecondHalf || match.condGoalsAllMatch)
                writeLineToFile(matchesWithConditionWriter, header + details + divider);

            //If match meets the criteria write to main file details
            if (match.condMin3Goals)
                writeLineToFile(matchesWithConditionMin3GoalsWriter, header + detailsOver3Goals + divider);

            //Close file with single match details
            matchSingleDetailsWriter.close();

            System.out.println("   ... " + match.name + " --- OK!");
        }

        //Close all fileWriters
        matchesWithConditionWriter.close();
        matchesAllWriter.close();
        matchesAllStatOnlyWriter.close();
        matchesWithConditionMin3GoalsWriter.close();
        System.out.println("Finished!\r\n");
    }

    /**
     * writeLineToFile writes single line given in string variable into file given in bufferedWriter parameter
     */
    private void writeLineToFile(BufferedWriter bufferedWriter, String string) {
        try {
            bufferedWriter.write(string);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}