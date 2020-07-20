import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.logging.Level;

class MyWebDriver {

    static Document get(String URL) {

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--headless"); //Window is visible or hidden

        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);


        WebDriver webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(URL);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pageSource = webDriver.getPageSource();
        webDriver.quit();
        return Jsoup.parse(pageSource);
    }

    static Document getSoup(String URL) {
        Document document = null;
        try {
            document = Jsoup.connect(URL).header("x-fsign", "SW9D1eZo").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}
