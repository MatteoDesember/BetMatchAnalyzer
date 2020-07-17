import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.logging.Level;

public class MyWebDriver {

    WebDriver webDriver;

    MyWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.out.println("Start Program_BMA_V1.0");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--headless"); //Window is visible or hidden

        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        webDriver = new ChromeDriver(chromeOptions);
    }

    public Document get(String URL) {
        webDriver.get(URL);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pageSource = webDriver.getPageSource();
        return Jsoup.parse(pageSource);
    }

    public Document getSoup(String URL) {
        Document document = null;
        try {
            document = Jsoup.connect(URL).header("x-fsign", "SW9D1eZo").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}
