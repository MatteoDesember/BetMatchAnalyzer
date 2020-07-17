import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    private static String betExplorerURL = "https://www.betexplorer.com/next/soccer/?year=%d&month=%d&day=%d";
    private static String flashScoreURL = "https://www.flashscore.com/match/%s/#h2h;overall";
    private static String flashScoreMatchSummaryURL = "https://www.flashscore.com/match/%s/#match-summary";

    private static String flashScoreDetailsSUURL = "https://d.flashscore.com/x/feed/d_su_%s_en_1";
    private static String flashScoreDetailsHHURL = "https://d.flashscore.com/x/feed/d_hh_%s_en_1";


    private static Pattern pattern = Pattern.compile("(\\w+(?=/$))");
    private static Pattern patternDetail = Pattern.compile("(?<=g_0_)(.+(?='))");
    private static Queue<Match> matchQueue = new ArrayDeque<>();

    private static DateTimeFormatter yyyymmddFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static DateTimeFormatter yyyymmddHHmmFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm");

    private boolean isLicense = false;

    private static final String LICENSE = "%DRn}/P=7=U.<dm+Wf2ENC2dBe+'CUM?{@uh)RzDX~$/j_g=bmE*QWZ=Cb(T&Tj&!VG-MZbW%jf!fvkCQ/Z!`v4mCTUC`wXHg{H(uTUMS6%:<,vSD4V&T!er'_*8e#?=p`a;[Y's;7uc/Af#.43mR+D#Q~)5Z}N<T&M)e}!{)p?(.7Xe\"aGh`qw?:Y#9dWvjg(}d;5~st-`W>?ME?sqc4JP,q/x>7NU7!G!*k7[tP\\\"+Jq6XR~pq_L._'Hy8LQc9'Cnn\\K`!cJ-`bkuuEE%6]L!*An;%WXn}t(B+M\\d;ju-.~Zp\"pY]9rhU!'+N\\]')MELX\\@D8XChx'4P.\"jCH_*?4mf)~^PYHB',a`%<+TykP\"C^\\y*)M%-kGt6g?&!#LW>M.9a6]=d+hg-yE?ZY9{}AD3{)Se8T9!)g4RB\\APV=wF^&?UAY\\2?nAWx+}xp.T$`\"$Aq7&-n9=7mA.!4U+Jdq6_ucPz<LTNy3/MM{x,6e(env2^K6%X6!UQ@6w}9>QwEv(D^{zH@<jm]>ND4q6v}j?LP{v?L!bEsp6\\(fX3y^EUm,2Fm3a/+MT#(HT!C+P8&`}gP*Gg:%)`qBa'>A8n_v&v6A#pQ=TTLb}zZ\"Rk+:K'4jC@U;Kx>NGUtu\"B:[V@SMUH/.+YzC5cG[$W5]H$'9qRy;YNUa-!+HLp[)Q:Nj&kGdYf7;3`/C2=mDK:nU?3p]!WSUgsw`bKwCeq>h3Mwvz!x@L,S(<g7jvu?(d?H967A]>L{<s\\AHFfh8L.jhhWtksvh/8n%,TsGh\\c;}A(KtjxVah$Z$/\\h;yxN!7prhHz(8zxZJRLh_;$.Utr=fvN!jC$[NYKw)WAZX>d5Q}a6Hs}N7XZzK^D3vNtdHf2<a3`h-D}?:=(!d*t/af@uk.ZyC{^w\"p4k<kV9^aXJ}wH%}}%v!cEG@u,qn(W\"A!?mCw4{(&]7:V-\\Hg5SGGWLJBHVA{Z~:x[*Se$FEkG_)2F!5*LBG*#wAm@v^~U#R9=:M%2X^DxKhS%CTu`pZ]w8kGZB-[@Vggh^7wWr:wK3!![*j)m,]Zx5K9BdKS!~hb*u8M!Tz\\^cpBg>P)T3XW/{B-)uG-[GhWHX7kv4K)F]Xyt>y2pM*\"s%HnwmWLE>$p4=<FAqRC\">^;,J>vyJJp}Q6n;5DaezefML`wf~{.@!?Ec^PU+vT*!{hJ9uS}:<#\\T?9djz,^C7@p(ux+N]W-zCPd;B&Sc5yb}?*gFJGUjddUnv&xD.?7C{d6pF>\\wnD\"=rsRDePsf`(CSM?3C3R5P\"Bj27<{G~ER=n$Fq'9{qH@8mv@3uzr;{sq;]M{?>{%+S6u^6#C'C-HkM`fk7q5kq_8/S8>K'^+XccZ*Na)mtVXvqr&[Y?/R>L+hXeX79YLzS-6AcqW&upE5AP7.u#;y7Xm5hQ:p/\\^J7A\"sZH~&yg`[J*ugA8Gfs`E>Sz('vzhC4b5s)>t3&;TR*'*;]Zpd;7KVm>j^[d@*(g_tYk5eT}Ek3@BZEG6WX}-\\b*#g!UKFJ`zB_hF`-h9>Y\\-p:x_\\*n\\;hmKqJ~'`m6@bCh?^T.5_E`*(7j[S{(X^x[jsYU-;cCN~{;,X.F>+nwk[jEDQmkMTb`#[fYjv!~<3HsJRk/FpUSrvhBK5Jm~AMV)$`JL$j=*m!c>Kk<eCVTPe`)2=/y<)LrmWx`x\\8/4UbD)e!jnUG+\"@5%u4^?({W<FW}VM#(ceKg=3MvL!/4aBD?9~HEn\\pa=8[s#~(n~uT`!Y'hz4$>g.\\,'xWT^F'Sh)pgc57U#>(&(z.y&#KC<GDu]a:'TV<]$QhrfyRLE_Ep&'qaKU-QsMR7F7fXNefH]g$+pHT)WbE)bFXRR7ym4v,~,aMP,Aj>9$J\\wW~:6+@_Vd!.D@5ytv&,mW%4U4mF[NM</5+;)7gvQVKh`};[5vFtZkmjcwdsVk-7`\\#}CG\"Q?DPL52</}BZ7(.GT_~^vcSE#&<7t'nVFrdpa[D?b]pAt2<fnQ(k$+5\\cgRFkE($+f`B4e:68f;!<]+\"4fj!^R,Nu*;*GyBX!&,SH{'\"QuT/?<Tm+hw:p";

    private static File matchDetailsAll = null;
    private static File matchAll = null;
    private static File matchSorted = null;

    private static BufferedWriter matchAllWriter = null;
    private static BufferedWriter matchSortedWriter = null;
    private static BufferedWriter matchDetailsAllWriter = null;
        

    Program() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader("license"));
            String userLicense = brTest.readLine();
            if (userLicense.equals(LICENSE))
                isLicense = true;
            else
                System.out.println("Invalid license");
            
        } catch (IOException e) {
            System.out.println("Can not find license file");
        }
    }

    void start(LocalDateTime dateToAnalyze) throws IOException {

        if (dateToAnalyze == null) {
            Date dt = new Date();
            dateToAnalyze = LocalDateTime.from(dt.toInstant().atZone(ZoneId.systemDefault())).plusDays(1);
        }

        MyWebDriver myWebDriver = new MyWebDriver();
        String formattedBetExplorerURL = String.format(betExplorerURL, dateToAnalyze.getYear(), dateToAnalyze.getMonthValue(), dateToAnalyze.getDayOfMonth());
        Document documentBetExplorer = myWebDriver.get(formattedBetExplorerURL);
        myWebDriver.webDriver.quit();

        matchDetailsAll = new File("data/details/" + dateToAnalyze.format(yyyymmddFormatter) + "/_" + dateToAnalyze.format(yyyymmddFormatter) + "_details.txt"); //Combine detail of each match
        matchAll = new File("data/details/" + dateToAnalyze.format(yyyymmddFormatter) + "/_" + dateToAnalyze.format(yyyymmddFormatter) + ".txt"); //Global of the day
        matchSorted = new File("data/" + dateToAnalyze.format(yyyymmddFormatter) + ".txt"); //Global sorted of the day

        matchDetailsAll.getParentFile().mkdirs();
        matchAll.getParentFile().mkdirs();
        matchSorted.getParentFile().mkdirs();

        matchAllWriter = new BufferedWriter(new FileWriter(matchAll, false));
        matchSortedWriter = new BufferedWriter(new FileWriter(matchSorted, false));
        matchDetailsAllWriter = new BufferedWriter(new FileWriter(matchDetailsAll, false));



        Elements elements = documentBetExplorer.select("tr[data-def=1]:not(:has(span[title=Canceled]))");

        for (Element element : elements) { //On the betexplorer page find all tommorow id's

            Match match = new Match();
            match.BEURL = element.selectFirst("a").attr("href");
            match.name = element.selectFirst("a[href]").text().replaceAll("[\\\\/:*?\"<>|]", "");

            String[] timeHS = element.selectFirst("span[class=table-main__time]").text().split(":");
            Integer hour = 0;
            Integer minutes = 0;
            try {
                hour = Integer.parseInt(timeHS[0]);
                minutes = Integer.parseInt(timeHS[1]);
            }catch (Exception e){
                System.out.println("Cant find hour and minutes on page for match: " + match.name);
            }

            LocalDateTime matchLDT = dateToAnalyze.withHour(hour).withMinute(minutes);
            match.date = matchLDT;
            Matcher matcher = pattern.matcher(match.BEURL);

            if (matcher.find()) {
                match.ID = matcher.group(1);
            }
            matchQueue.add(match);
        }


        int i = 1;
        for (Match match : matchQueue) { //for each match get h2h matches

            if (i > 5 && !isLicense) {
                System.out.println("Do pelnej wersji programu potrzebny jest plik licencji.");
                break;
            }

            System.out.println(i++ + "/" + matchQueue.size() + " Processing " + match.name + "...");

            match.FSURL = String.format(flashScoreURL, match.ID);
            Document documentFlashScore = myWebDriver.getSoup(String.format(flashScoreDetailsHHURL, match.ID));
            Element tableH2H = documentFlashScore.select("table[class=head_to_head h2h_mutual]").first();
            Elements H2H = tableH2H.select("tr.highlight");

            for (Element elementh2hmatch : H2H) {

                Matcher matcher = patternDetail.matcher(elementh2hmatch.attr("onclick"));

                if (matcher.find()) {

                    Match h2hMatch = new Match();
                    h2hMatch.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Integer.parseInt(elementh2hmatch.select("span.date").text())), ZoneId.systemDefault());
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

                                    Integer goalTime = null;

                                    if (goalBox != null) {
                                        goalTime = Integer.parseInt(goalBox.text().replaceAll("[^\\d]", ""));
                                    } else if (goalBoxWide != null) {
                                        String[] times = goalBoxWide.text().split("\\+");
                                        Integer time1 = Integer.parseInt(times[0].replaceAll("[^\\d]", ""));
                                        Integer time2 = Integer.parseInt(times[1].replaceAll("[^\\d]", ""));
                                        goalTime = time1 + time2;
                                    } else {
                                        System.out.println("1. Error");
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

            printOutput(match);
            System.out.println("   ... " + match.name + " --- OK!");
        }
        matchSortedWriter.close();
        matchDetailsAllWriter.close();
        matchAllWriter.close();
        System.out.println("Finished!\r\n");
    }

    private void printOutput(Match match) throws IOException {
        StringBuilder outputDetails = new StringBuilder();
        StringBuilder outputGlobal = new StringBuilder();

        File matchDetails = new File("data/details/" + match.date.format(yyyymmddFormatter) + "/" + match.date.format(yyyymmddHHmmFormatter) + " " + match.name + ".txt"); //Detail of each match
        matchDetails.getParentFile().mkdirs();
        BufferedWriter matchDetailsWriter = new BufferedWriter(new FileWriter(matchDetails, false));

        int fh = 0; // how many goals in first half
        int sh = 0; // how many goals in second half
        int fsh = 0; // how many goals in second half

        //Data Output START
        for (Match m : match.h2hMatchList) {
            outputDetails.append("\r\n   --->      ").append(m.date.format(yyyymmddFormatter)).append(" | URL: ").append(m.FSURL).append(" | Wynik: ").append(m.score);

            if (!m.goalTimeListFirstHalf.isEmpty())
                outputDetails.append("\r\n   ------>         First half: ");

            for (Integer gtl : m.goalTimeListFirstHalf) {
                outputDetails.append(gtl).append(", ");
            }

            if (!m.goalTimeListSecondHalf.isEmpty())
                outputDetails.append("\r\n   ------>         Second half: ");

            for (Integer gtl : m.goalTimeListSecondHalf) {
                outputDetails.append(gtl).append(", ");
            }

            ////
            boolean cond_1 = conditionFirstHalf(m);
            boolean cond_2 = conditionSecondHalf(m);
            if (cond_1)
                fh++;
            if (cond_2)
                sh++;
            if (cond_1 || cond_2)
                fsh++;
        }

        Integer fhP = 0;
        Integer shP = 0;
        Integer fshP = 0;
        if (match.h2hMatchList.size() > 0) {
            fhP = Math.round(fh * 100 / match.h2hMatchList.size());
            shP = Math.round(sh * 100 / match.h2hMatchList.size());
            fshP = Math.round(fsh * 100 / match.h2hMatchList.size());
        }


        String header = match.date.format(yyyymmddHHmmFormatter) + " | Mecz: " + match.name + " | URL: " + match.FSURL;
        outputGlobal.append("\r\n   Pierwsza polowa: (").append(fh).append("/").append(match.h2hMatchList.size()).append(") = ").append(fhP).append("%");
        outputGlobal.append("\r\n   Druga polowa   : (").append(sh).append("/").append(match.h2hMatchList.size()).append(") = ").append(shP).append("%");
        outputGlobal.append("\r\n   Caly mecz      : (").append(fsh).append("/").append(match.h2hMatchList.size()).append(") = ").append(fshP).append("%");
        outputGlobal.append("\r\n");


        matchAllWriter.write(header);
        matchAllWriter.write(outputGlobal.toString());
        matchAllWriter.write("\r\n");
        matchAllWriter.flush();

        matchDetailsWriter.write(header);
        matchDetailsWriter.write(outputGlobal.toString());
        matchDetailsWriter.write(outputDetails.toString());
        matchDetailsWriter.close();

        matchDetailsAllWriter.write(header);
        matchDetailsAllWriter.write(outputGlobal.toString());
        matchDetailsAllWriter.write(outputDetails.toString());
        matchDetailsAllWriter.write("\r\n-------------------------------------------------------------------------------\r\n\r\n");
        matchDetailsAllWriter.flush();

        if (fhP >= 80 || shP >= 80 || fshP >= 80) {

            matchSortedWriter.write(header);
            matchSortedWriter.write(outputGlobal.toString());
            matchSortedWriter.write("\r\n-------------------------------------------------------------------------------\r\n\r\n");
            matchSortedWriter.flush();
        }
    }

    private boolean conditionFirstHalf(Match m) {

        if (!m.goalTimeListFirstHalf.isEmpty()) {
            for (Integer gtl : m.goalTimeListFirstHalf) {
                if (gtl >= 35) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean conditionSecondHalf(Match m) {

        if (!m.goalTimeListSecondHalf.isEmpty()) {
            for (Integer gtl : m.goalTimeListSecondHalf) {
                if (gtl >= 74) {
                    return true;
                }
            }
        }
        return false;
    }
}
