import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.logging.Level;

public class MyWebDriver {

    private static WebDriver webDriver;
    private static String betExplorerURL = "https://www.betexplorer.com/next/soccer/?year=%d&month=%d&day=%d";

    private static String flashScoreMatchSummaryURL = "https://www.flashscore.com/match/%s/#match-summary";
    private static String flashScoreDetailsSUURL = "https://d.flashscore.com/x/feed/d_su_%s_en_1";
    private static String flashScoreDetailsHHURL = "https://d.flashscore.com/x/feed/d_hh_%s_en_1";


    MyWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--headless"); //Window is visible or hidden

        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        webDriver = new ChromeDriver(chromeOptions);
    }

    public static Document get(String URL) {
        webDriver.get(URL);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pageSource = webDriver.getPageSource();
        return Jsoup.parse(pageSource);
    }

    public static Document getSoup(String URL) {
        Document document = null;
        try {
            document = Jsoup.connect(URL).header("x-fsign", "SW9D1eZo").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
    public static void quitWebDriver(){
        webDriver.quit();
    }
}
