import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is Match class wich represent Match object
 */

public class Match {

    //This pages are needed to get info about match, h2h matches and other
    private final static String FLASH_SCORE_URL = "https://www.flashscore.com/match/%s/#h2h;overall";
    private final static String FLASH_SCORE_MATCH_SUMMARY_URL = "https://www.flashscore.com/match/%s/#match-summary";
    private final static String FLASH_SCORE_DETAILS_SUMMARY_URL = "https://d.flashscore.com/x/feed/d_su_%s_en_1";
    private final static String FLASH_SCORE_DETAILS_H2H_URL = "https://d.flashscore.com/x/feed/d_hh_%s_en_1";
    private final static Integer YEARS_BACK = 5;

    private String ID;
    public String name;
    private LocalDateTime date;
    private String league;
    private String leagueType;
    //BetExplorer URL
    private String BEURL;
    //FlashScore URL
    private String FSURL;
    private String score;
    private List<Match> h2hMatchList = new ArrayList<>();
    private List<Integer> goalTimeListFirstHalf = new ArrayList<>();
    private List<Integer> goalTimeListSecondHalf = new ArrayList<>();

    //This variable represents in how many (h2h) matches conditions were fulfill
    //condition first half
    private Integer cfh = 0;
    //condition second half
    private Integer csh = 0;
    //condition first, second half
    private Integer cfsh = 0;
    //condition minimum 3 goals
    private Integer min3Goals = 0;

    //Contidion to fulfill
    boolean condGoalInFirstHalf = false;
    boolean condGoalsInSecondHalf = false;
    boolean condGoalsAllMatch = false;
    boolean condMin3Goals = false;

    /**
     * There are some getters
     */
    String getFileName() {
        return date.format(SoccerProgram.YYYYMMDDHHMM) +
                " " + name;
    }

    String getDivider() {
        return "---------------------------------------\r\n";
    }

    String getHeader() {
        return date.format(SoccerProgram.YYYYMMDDHHMM) +
                " | League: " + league +
                " | Match: " + name +
                " | URL: " + FSURL +
                "\r\n";
    }

    String getDetailsOver3Goals() {
        return String.format(" %c More than 2 goals  : (" + min3Goals + "/" + h2hMatchList.size() + ") = %d%%\r\n",
                condMin3Goals ? '*' : ' ',
                h2hMatchList.size() > 0 ? Math.round(min3Goals * 100 / h2hMatchList.size()) : 0);
    }

    String getDetails() {

        String outputString = " %c First half         : (" + cfh + "/" + h2hMatchList.size() + ") = %d%%\r\n" +
                " %c Second half        : (" + csh + "/" + h2hMatchList.size() + ") = %d%%\r\n" +
                " %c Whole match        : (" + cfsh + "/" + h2hMatchList.size() + ") = %d%%\r\n";

        outputString = String.format(outputString,
                condGoalInFirstHalf ? '*' : ' ',
                h2hMatchList.size() > 0 ? Math.round(cfh * 100 / h2hMatchList.size()) : 0,
                condGoalsInSecondHalf ? '*' : ' ',
                h2hMatchList.size() > 0 ? Math.round(csh * 100 / h2hMatchList.size()) : 0,
                condGoalsAllMatch ? '*' : ' ',
                h2hMatchList.size() > 0 ? Math.round(cfsh * 100 / h2hMatchList.size()) : 0);

        return outputString;
    }

    String getMoreDetails() {

        StringBuilder outputString = new StringBuilder();

        if (this.h2hMatchList.size() > 0) {
            for (Match m : this.h2hMatchList) {
                outputString.append("   --->      ").append(m.date.format(SoccerProgram.YYYYMMDD)).append(" | Score: ").append(m.score).append(" | URL: ").append(m.FSURL).append("\r\n");

                if (m.goalTimeListFirstHalf.size() > 0) {
                    outputString.append("   ------>         First half: ");
                    for (Integer time : m.goalTimeListFirstHalf) {
                        outputString.append(time).append(", ");
                    }
                    outputString.append("\r\n");
                }
                if (m.goalTimeListSecondHalf.size() > 0) {
                    outputString.append("   ------>         Second half: ");
                    for (Integer time : m.goalTimeListSecondHalf) {
                        outputString.append(time).append(", ");
                    }
                    outputString.append("\r\n");
                }
            }
        }
        return outputString.toString();
    }

    /**
     * This function analyze condition in h2h matches
     */
    private void processConditions() {

        //For each h2h match analyze condition
        for (Match h2hMatch : this.h2hMatchList) {

            //analyze condition 1 (goal after 35 minutes in first half)
            boolean cond_1 = smallCondition_1(h2hMatch);
            //analyze condition 2 (goal after 74 minutes in second half)
            boolean cond_2 = smallCondition_2(h2hMatch);
            //analyze condition 4 (minimum 3 goals)
            boolean cond_4 = smallCondition_4(h2hMatch);

            //Increment variables in how many matches condition were fulfilled
            if (cond_1)
                cfh++;
            if (cond_2)
                csh++;
            //This is third condition if there is goal after 35 minutes in first half OR if there is goal after 74 minutes in second half
            if (cond_1 || cond_2)
                cfsh++;
            //Increment variable if in match is minimum 3 goals
            if (cond_4)
                min3Goals++;
        }

        //Count percent for each match. If condition are fulfilled in minimum 80% show this match to user
        if (h2hMatchList.size() > 0) {
            if ((cfh * 100) / h2hMatchList.size() >= 80)
                condGoalInFirstHalf = true;
            if ((csh * 100) / h2hMatchList.size() >= 80)
                condGoalsInSecondHalf = true;
            if ((cfsh * 100) / h2hMatchList.size() >= 80)
                condGoalsAllMatch = true;
            if (min3Goals * 100 / h2hMatchList.size() >= 80)
                condMin3Goals = true;
        }
    }

    /**
     * smallCondition_1 return true if in first half there is goal after 35 minutes
     */
    private boolean smallCondition_1(Match m) {
        if (!m.goalTimeListFirstHalf.isEmpty())
            for (Integer gtl : m.goalTimeListFirstHalf)
                if (gtl >= 35)
                    return true;
        return false;
    }

    /**
     * smallCondition_2 return true if in second half there is goal after 74 minutes
     */
    private boolean smallCondition_2(Match m) {
        if (!m.goalTimeListSecondHalf.isEmpty())
            for (Integer gtl : m.goalTimeListSecondHalf)
                if (gtl >= 74)
                    return true;
        return false;
    }

    /**
     * smallCondition_4 return true if in all match there is minimum 3 gials
     */
    private boolean smallCondition_4(Match m) {
        return (m.goalTimeListFirstHalf.size() + m.goalTimeListSecondHalf.size()) >= 3;
    }

    /**
     * Convert web element (e.g. html tr) into Match.class
     */
    static Match elementToMatch(Element element) {
        //Create new object
        Match match = new Match();

        //Give them BetExplorer URL and name
        match.BEURL = element.selectFirst("a").attr("href");
        match.name = element.selectFirst("a[href]").text().replaceAll("[\\\\/:*?\"<>|]", "");

        //Give them league
        String league = element.parent().select("a[class=table-main__tournament]").attr("href");

        // e.g. /soccer/brazil/serie-b/
        //                      ^
        //        select this---|
        Matcher matcher = Pattern.compile("\\/([^\\/]+)[\\/]?$").matcher(league);
        if (matcher.find()) {
            match.leagueType = matcher.group(1);
        }

        // e.g. /soccer/brazil/serie-b/
        //                      ^
        //        remove this---|
        league = league.replaceAll("\\/([^\\/]+)[\\/]?$", "");


        // e.g. /soccer/brazil
        //               ^
        // select this---|
        matcher = Pattern.compile("\\/([^\\/]+)[\\/]?$").matcher(league);
        if (matcher.find()) {
            match.league = matcher.group(1);
        }

        // Get time
        String[] timeHS = element.selectFirst("span[class=table-main__time]").text().split(":");
        Integer hour = 0;
        Integer minutes = 0;
        try {
            hour = Integer.parseInt(timeHS[0]);
            minutes = Integer.parseInt(timeHS[1]);
        } catch (Exception e) {
            System.out.println("Cant find hour and minutes on page for match: " + match.name);
        }
        match.date = SoccerProgram.gDateToAnalyze.withHour(hour).withMinute(minutes);

        //Get id (e.g /soccer/brazil/serie-b/cuiaba-brasil-de-pelotas/S0Pw2KKk/)
        //                                                                ^
        //this------------------------------------------------------------|
        matcher = Pattern.compile("(\\w+(?=/$))").matcher(match.BEURL);
        if (matcher.find()) {
            match.ID = matcher.group(1);
        }

        return match;
    }

    /**
     * getH2Hmatches processes every match and analyzes every h2h match in it
     * it get h2h matches from e.g. https://www.flashscore.com/match/KfOprsW0/#h2h;overall
     */
    static void getH2Hmatches(Match match) {

        //Get FlashScore URL
        match.FSURL = String.format(FLASH_SCORE_URL, match.ID);

        //Get details for each match (URL page)
        Document documentFlashScore = MyWebDriver.getSoup(String.format(FLASH_SCORE_DETAILS_H2H_URL, match.ID));

        //Get all h2h matches on page
        Elements h2hElements = documentFlashScore.select("table[class=head_to_head h2h_mutual]").first().select("tr.highlight");

        //For each h2h match
        for (Element h2hElement : h2hElements) {

            //Find if there is ID
            //e.g. h2hElement.attr("onclick"):
            //  "cjs.Api.loader.get('cjs').call(function(_cjs){_cjs.fromGlobalScope.detail_open('g_0_dhuszWvD', null, false); });"
            //                                                                                      >--------<
            //                                                                                           ^
            //get this-----------------------------------------------------------------------------------|
            Matcher matcher = Pattern.compile("(?<=g_0_)(.+(?='))").matcher(h2hElement.attr("onclick"));

            //If there is id
            if (matcher.find()) {
                //Create new match (h2h match)
                Match h2hMatch = new Match();

                //Get date
                Integer tempDate = -1;
                try {
                    tempDate = Integer.parseInt(h2hElement.select("span.date").text());
                } catch (Exception e) {
                    System.out.println("Error... There is no tempDate in H2H match");
                }
                h2hMatch.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(tempDate), ZoneId.systemDefault());

                //Assign ID
                h2hMatch.ID = matcher.group(1);

                //Assign FlashScore URL
                h2hMatch.FSURL = String.format(FLASH_SCORE_MATCH_SUMMARY_URL, h2hMatch.ID);

                //Assign Score
                h2hMatch.score = h2hElement.select("span.score").text();

                //Check if that h2h match is in last 5 years
                //If so add them to the list
                //There are only 5 years back matches analyzed
                if (h2hMatch.date.getYear() >= SoccerProgram.gDateToAnalyze.getYear() - YEARS_BACK) {
                    Match.getDetails(h2hMatch);
                    match.h2hMatchList.add(h2hMatch);
                }
            }
        }
        //If in match there are all h2h matches check if match fulfill condition
        match.processConditions();
    }


    /**
     * getDetails get h2h matches details from e.g. https://www.flashscore.com/match/bgFN2VJK/#match-summary
     */
    private static void getDetails(Match match) {

        //Get summary for each match (URL page)
        Document h2hMatchSummary = MyWebDriver.getSoup(String.format(FLASH_SCORE_DETAILS_SUMMARY_URL, match.ID));

        //Select all "incidents" like: goal, fault etc.
        Elements h2hMatchSummaryDetails = h2hMatchSummary.select("div.detailMS").select("div[class^=detailMS__incident]");

        //for each "incident"
        int half = 0;
        for (Element h2hMatchSummaryDetail : h2hMatchSummaryDetails) {

            //This if analyze if we are in first, second or in additional time (penalties or somethink like that)
            //I didn't find better way to analyze that datas
            if (h2hMatchSummaryDetail.attr("class").contains("detailMS__incidentsHeader")) {
                if (h2hMatchSummaryDetail.attr("class").contains("stage-12"))
                    // Incident with header "stage-12" means that we are in first half
                    half = 1;

                else if (h2hMatchSummaryDetail.attr("class").contains("stage-13"))
                    // Incident with header "stage-13" means that we are in first half
                    half = 2;
                else
                    // Otherwise there are additional time
                    half = 3;

            } else {
                // Else on other incident...
                //class=icon soccer-ball informs that "incident" is goal
                Element goalsEventElement = h2hMatchSummaryDetail.select("div.detailMS__incidentRow:has(span[class=icon soccer-ball])").first();

                // if found goal and there is first or second half
                if (goalsEventElement != null && (half == 1 || half == 2)) {

                    //get goal time string (there are two types of goal eg. [35'] represents by time-box or [45'+1] represents by time-box-wide)
                    Element goalBox = goalsEventElement.select("div.time-box").first();
                    Element goalBoxWide = goalsEventElement.select("div.time-box-wide").first();

                    Integer goalTime = -1;

                    //convert string goal time into integer so there is easy to compare by int
                    if (goalBox != null) {
                        try {
                            goalTime = Integer.parseInt(goalBox.text().replaceAll("[^\\d]", ""));
                        } catch (Exception e) {
                            System.out.println("Error... There is no goalTime in minutes");
                        }
                    } else if (goalBoxWide != null) {
                        String[] times = goalBoxWide.text().split("\\+");
                        try {
                            Integer time1 = Integer.parseInt(times[0].replaceAll("[^\\d]", ""));
                            Integer time2 = Integer.parseInt(times[1].replaceAll("[^\\d]", ""));
                            goalTime = time1 + time2;
                        } catch (Exception e) {
                            System.out.println("Error... There is no goalTime in minutes in wideBox");
                        }
                    } else {
                        System.out.println("Error... Goalbox is null");
                    }

                    //Add that goal to goal list
                    if (half == 1) {
                        // if first half to goalTimeListFirstHalf
                        match.goalTimeListFirstHalf.add(goalTime);
                    } else {
                        // if second half to goalTimeListFirstHalf
                        match.goalTimeListSecondHalf.add(goalTime);
                    }
                }
            }
        }
    }
}
