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

public class Match {

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
    private String BEURL;
    private String FSURL;
    private String score;
    private List<Match> h2hMatchList = new ArrayList<>();
    private List<Integer> goalTimeListFirstHalf = new ArrayList<>();
    private List<Integer> goalTimeListSecondHalf = new ArrayList<>();

    private Integer cfh = 0;
    private Integer csh = 0;
    private Integer cfsh = 0;
    private Integer over3Goals = 0;

    boolean condGoalInFirstHalf = false;
    boolean condGoalsInSecondHalf = false;
    boolean condGoalsAllMatch = false;
    boolean condOver3Goals = false;

    String getFileName() {
        return date.format(Program.YYYYMMDDHHMM) +
                " " + name;
    }

    String getDivider() {
        return "---------------------------------------\r\n";
    }

    String getHeader() {
        return date.format(Program.YYYYMMDDHHMM) +
                " | Liga: " + league +
                " | Mecz: " + name +
                " | URL: " + FSURL +
                "\r\n";
    }

    String getDetailsOver3Goals() {
        return String.format(" %c Ponad 3 bramki  : (" + over3Goals + "/" + h2hMatchList.size() + ") = %d%%\r\n",
                condOver3Goals ? '*' : ' ',
                h2hMatchList.size() > 0 ? Math.round(over3Goals * 100 / h2hMatchList.size()) : 0);
    }

    String getDetails() {

        String outputString = " %c Pierwsza polowa : (" + cfh + "/" + h2hMatchList.size() + ") = %d%%\r\n" +
                " %c Druga polowa    : (" + csh + "/" + h2hMatchList.size() + ") = %d%%\r\n" +
                " %c Caly mecz       : (" + cfsh + "/" + h2hMatchList.size() + ") = %d%%\r\n";

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
                outputString.append("   --->      ").append(m.date.format(Program.YYYYMMDD)).append(" | Wynik: ").append(m.score).append(" | URL: ").append(m.FSURL).append("\r\n");

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


    private void processConditions() {

        for (Match h2hMatch : this.h2hMatchList) {

            boolean cond_1 = smallCondition_1(h2hMatch);
            boolean cond_2 = smallCondition_2(h2hMatch);
            boolean cond_4 = smallCondition_4(h2hMatch);

            if (cond_1)
                cfh++;
            if (cond_2)
                csh++;
            if (cond_1 || cond_2)
                cfsh++;
            if (cond_4)
                over3Goals++;
        }

        if (h2hMatchList.size() > 0) {
            if ((cfh * 100) / h2hMatchList.size() >= 80)
                condGoalInFirstHalf = true;
            if ((csh * 100) / h2hMatchList.size() >= 80)
                condGoalsInSecondHalf = true;
            if ((cfsh * 100) / h2hMatchList.size() >= 80)
                condGoalsAllMatch = true;
            if (over3Goals * 100 / h2hMatchList.size() >= 80)
                condOver3Goals = true;
        }
    }

    private boolean smallCondition_1(Match m) {
        if (!m.goalTimeListFirstHalf.isEmpty())
            for (Integer gtl : m.goalTimeListFirstHalf)
                if (gtl >= 35)
                    return true;
        return false;
    }

    private boolean smallCondition_2(Match m) {
        if (!m.goalTimeListSecondHalf.isEmpty())
            for (Integer gtl : m.goalTimeListSecondHalf)
                if (gtl >= 74)
                    return true;
        return false;
    }

    private boolean smallCondition_4(Match m) {
        return (m.goalTimeListFirstHalf.size() + m.goalTimeListSecondHalf.size()) > 3;
    }

    static Match elementToMatch(Element element) {
        Match match = new Match();

        match.BEURL = element.selectFirst("a").attr("href");
        match.name = element.selectFirst("a[href]").text().replaceAll("[\\\\/:*?\"<>|]", "");

        String league = element.parent().select("a[class=table-main__tournament]").attr("href");

        Matcher matcher = Pattern.compile("\\/([^\\/]+)[\\/]?$").matcher(league);
        if (matcher.find()) {
            match.leagueType = matcher.group(1);
        }

        league = league.replaceAll("\\/([^\\/]+)[\\/]?$", "");

        matcher = Pattern.compile("\\/([^\\/]+)[\\/]?$").matcher(league);
        if (matcher.find()) {
            match.league = matcher.group(1);
        }

        String[] timeHS = element.selectFirst("span[class=table-main__time]").text().split(":");
        Integer hour = 0;
        Integer minutes = 0;
        try {
            hour = Integer.parseInt(timeHS[0]);
            minutes = Integer.parseInt(timeHS[1]);
        } catch (Exception e) {
            System.out.println("Cant find hour and minutes on page for match: " + match.name);
        }

        match.date = Program.gDateToAnalyze.withHour(hour).withMinute(minutes);

        matcher = Pattern.compile("(\\w+(?=/$))").matcher(match.BEURL);

        if (matcher.find()) {
            match.ID = matcher.group(1);
        }

        return match;
    }

    static void getH2Hmatches(Match match) { //get h2h matches from e.g. https://www.flashscore.com/match/KfOprsW0/#h2h;overall

        match.FSURL = String.format(FLASH_SCORE_URL, match.ID);

        Document documentFlashScore = MyWebDriver.getSoup(String.format(FLASH_SCORE_DETAILS_H2H_URL, match.ID));

        Elements h2hElements = documentFlashScore.select("table[class=head_to_head h2h_mutual]").first().select("tr.highlight");

        for (Element h2hElement : h2hElements) {

            Matcher matcher = Pattern.compile("(?<=g_0_)(.+(?='))").matcher(h2hElement.attr("onclick"));

            if (matcher.find()) {
                Match h2hMatch = new Match();
                Integer tempDate = -1;
                try {
                    tempDate = Integer.parseInt(h2hElement.select("span.date").text());
                } catch (Exception e) {
                    System.out.println("Error... There is no tempDate in H2H match");
                }
                h2hMatch.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(tempDate), ZoneId.systemDefault());
                h2hMatch.ID = matcher.group(1);
                h2hMatch.FSURL = String.format(FLASH_SCORE_MATCH_SUMMARY_URL, h2hMatch.ID);
                h2hMatch.score = h2hElement.select("span.score").text();

                if (h2hMatch.date.getYear() >= Program.gDateToAnalyze.getYear() - YEARS_BACK) { //Check if match is in last 5 years
                    Match.getDetails(h2hMatch);
                    match.h2hMatchList.add(h2hMatch);
                }
            }
        }
        match.processConditions();
    }

    private static void getDetails(Match match) {//get h2h matches details from e.g. https://www.flashscore.com/match/bgFN2VJK/#match-summary

        Document h2hMatchSummary = MyWebDriver.getSoup(String.format(FLASH_SCORE_DETAILS_SUMMARY_URL, match.ID));
        Elements h2hMatchSummaryDetails = h2hMatchSummary.select("div.detailMS").select("div[class^=detailMS__incident]");

        int half = 0;
        for (Element h2hMatchSummaryDetail : h2hMatchSummaryDetails) {

            if (h2hMatchSummaryDetail.attr("class").contains("detailMS__incidentsHeader")) {
                if (h2hMatchSummaryDetail.attr("class").contains("stage-12"))
                    half = 1;//First half
                else if (h2hMatchSummaryDetail.attr("class").contains("stage-13"))
                    half = 2;
                else
                    half = 3;
            } else {

                Element goalsEventElement = h2hMatchSummaryDetail.select("div.detailMS__incidentRow:has(span[class=icon soccer-ball])").first();
                if (goalsEventElement != null && (half == 1 || half == 2)) { // if found goal

                    Element goalBox = goalsEventElement.select("div.time-box").first();
                    Element goalBoxWide = goalsEventElement.select("div.time-box-wide").first();

                    Integer goalTime = -1;

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

                    if (half == 1) {
                        match.goalTimeListFirstHalf.add(goalTime);
                    } else {
                        match.goalTimeListSecondHalf.add(goalTime);
                    }
                }
            }
        }
    }
}
