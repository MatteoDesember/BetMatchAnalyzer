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
        System.out.println("Start Program_BMA_V1.1");
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


        String formattedBetExplorerURL = String.format(betExplorerURL, dateToAnalyze.getYear(), dateToAnalyze.getMonthValue(), dateToAnalyze.getDayOfMonth());
        Document documentBetExplorer = MyWebDriver.get(formattedBetExplorerURL);
        MyWebDriver.quitWebDriver();

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
            matchQueue.add(Match.elementToMatch(element));
        }

        int i = 1;

        for (Match match : matchQueue) { //for each match get h2h matches

            if (i > 5 && !isLicense) {
                System.out.println("Do pelnej wersji programu potrzebny jest plik licencji.");
                break;
            }

            System.out.println(i++ + "/" + matchQueue.size() + " Processing " + match.name + "...");

            Match.getH2Hmatches(match);

            printOutput(match);

            System.out.println("   ... " + match.name + " --- OK!");
        }
        matchSortedWriter.close();
        matchDetailsAllWriter.close();
        matchAllWriter.close();
        System.out.println("Finished!\r\n");
    }

    private void printOutput(Match match) throws IOException {


        File matchDetails = new File("data/details/" + match.date.format(yyyymmddFormatter) + "/" + match.date.format(yyyymmddHHmmFormatter) + " " + match.name + ".txt"); //Detail of each match
        matchDetails.getParentFile().mkdirs();
        BufferedWriter matchDetailsWriter = new BufferedWriter(new FileWriter(matchDetails, false));



//        matchAllWriter.write(header);
//        matchAllWriter.write(outputGlobal.toString());
//        matchAllWriter.write("\r\n");
//        matchAllWriter.flush();
//
//        matchDetailsWriter.write(header);
//        matchDetailsWriter.write(outputGlobal.toString());
//        matchDetailsWriter.write(outputDetails.toString());
//        matchDetailsWriter.close();
//
//        matchDetailsAllWriter.write(header);
//        matchDetailsAllWriter.write(outputGlobal.toString());
//        matchDetailsAllWriter.write(outputDetails.toString());
//        matchDetailsAllWriter.write("\r\n-------------------------------------------------------------------------------\r\n\r\n");
//        matchDetailsAllWriter.flush();
//
//        if (fhP >= 80 || shP >= 80 || fshP >= 80) {
//
//            matchSortedWriter.write(header);
//            matchSortedWriter.write(outputGlobal.toString());
//            matchSortedWriter.write("\r\n-------------------------------------------------------------------------------\r\n\r\n");
//            matchSortedWriter.flush();
//        }
    }


}