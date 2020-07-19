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

    private final static String FLASHSCOREURL = "https://www.flashscore.com/match/%s/#h2h;overall";

    public String ID;
    public String name;
    public LocalDateTime date;
    public String league;
    public String leagueType;
    public String BEURL;
    public String FSURL;
    public String score;
    public List<Match> h2hMatchList = new ArrayList<>();
    public List<Integer> goalTimeListFirstHalf = new ArrayList<>();
    public List<Integer> goalTimeListSecondHalf = new ArrayList<>();

    public Integer conditionalFHMatchCounter = 0;
    public Integer conditionalSHMatchCounter = 0;
    public Integer conditionalFSHMatchCounter = 0;
    private StringBuilder checkConditionString = new StringBuilder();


    public String getGlobal() {


        StringBuilder outputGlobal = new StringBuilder();

        getCondition();

        //Data Output START
        for (Match m : this.h2hMatchList) {

        }

        if (this.h2hMatchList.size() > 0) {
            fhP = Math.round(howManyGoalsInFirstHalf * 100 / this.h2hMatchList.size());
            shP = Math.round(howManyGoalsInSecondHalf * 100 / this.h2hMatchList.size());
            fshP = Math.round(fsh * 100 / this.h2hMatchList.size());
        }

        outputGlobal.append(this.date.format(yyyymmddHHmmFormatter)).append(" | Mecz: ").append(this.name).append(" | URL: ").append(this.FSURL);
        outputGlobal.append("\r\n   Pierwsza polowa: (").append(fh).append("/").append(this.h2hMatchList.size()).append(") = ").append(fhP).append("%");
        outputGlobal.append("\r\n   Druga polowa   : (").append(sh).append("/").append(this.h2hMatchList.size()).append(") = ").append(shP).append("%");
        outputGlobal.append("\r\n   Caly mecz      : (").append(fsh).append("/").append(this.h2hMatchList.size()).append(") = ").append(fshP).append("%");
        outputGlobal.append("\r\n");


        return String.valueOf(outputGlobal);

    }

    private void getCondition() {

        for (Match m : h2hMatchList) {

            checkConditionString.append("\r\n   --->      ").append(m.date.format(yyyymmddFormatter)).append(" | URL: ").append(m.FSURL).append(" | Wynik: ").append(m.score);

            if (!m.goalTimeListFirstHalf.isEmpty()) {
                checkConditionString.append("\r\n   ------>         First half: ");
                for (Integer gtl : m.goalTimeListFirstHalf) {
                    checkConditionString.append(gtl).append(", ");
                    if (gtl >= 35)
                        conditionalFHMatchCounter++;
                        conditionalFSHMatchCounter++;
                }
            }

            if (!m.goalTimeListSecondHalf.isEmpty()) {
                checkConditionString.append("\r\n   ------>         Second half: ");
                for (Integer gtl : m.goalTimeListSecondHalf) {
                    checkConditionString.append(gtl).append(", ");
                    if (gtl >= 74)
                        conditionalSHMatchCounter++;
                        conditionalFSHMatchCounter++;
                }
            }
        }
    }

    public static Match elementToMatch(Element element){
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

        match.date = dateToAnalyze.withHour(hour).withMinute(minutes);

        matcher = Pattern.compile("(\\w+(?=/$))").matcher(match.BEURL);

        if (matcher.find()) {
            match.ID = matcher.group(1);
        }

        return match;
    }

    public static void getH2Hmatches(Match match){


        match.FSURL = String.format(FLASHSCOREURL, match.ID);

        Document documentFlashScore = myWebDriver.getSoup(String.format(flashScoreDetailsHHURL, match.ID));


        Element tableH2H = documentFlashScore.select("table[class=head_to_head h2h_mutual]").first();
        Elements H2H = tableH2H.select("tr.highlight");

        for (Element elementh2hmatch : H2H) {

            Matcher matcher = Pattern.compile("(?<=g_0_)(.+(?='))").matcher(elementh2hmatch.attr("onclick"));

            if (matcher.find()) {

                Match h2hMatch = new Match();
                Integer tempDate = -1;
                try{
                    tempDate = Integer.parseInt(elementh2hmatch.select("span.date").text());
                }catch (Exception e){
                    System.out.println("Error... There is no tempDate in H2H match");
                }
                h2hMatch.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(tempDate), ZoneId.systemDefault());
                h2hMatch.ID = matcher.group(1);
                h2hMatch.FSURL = String.format(flashScoreMatchSummaryURL, h2hMatch.ID);
                h2hMatch.score = elementh2hmatch.select("span.score").text();

                if (h2hMatch.date.getYear() >= dateToAnalyze.getYear() - 5) { //Check if match is in last 5 years

                    Document h2hMatchSummary = myWebDriver.getSoup(String.format(flashScoreDetailsSUURL, h2hMatch.ID));
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
                                    }catch (Exception e){
                                        System.out.println("Error... There is no goalTime in minutes");
                                    }
                                } else if (goalBoxWide != null) {
                                    String[] times = goalBoxWide.text().split("\\+");
                                    try {
                                        Integer time1 = Integer.parseInt(times[0].replaceAll("[^\\d]", ""));
                                        Integer time2 = Integer.parseInt(times[1].replaceAll("[^\\d]", ""));
                                        goalTime = time1 + time2;
                                    }catch (Exception e){
                                        System.out.println("Error... There is no goalTime in minutes in wideBox");
                                    }
                                } else {
                                    System.out.println("Error... Goalbox is null");
                                }

                                if (half == 1) {
                                    h2hMatch.goalTimeListFirstHalf.add(goalTime);
                                } else {
                                    h2hMatch.goalTimeListSecondHalf.add(goalTime);
                                }
                            }
                        }
                    }

                    match.h2hMatchList.add(h2hMatch);
                }
            }
        }
    }

}
