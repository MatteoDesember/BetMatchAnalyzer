import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

class MyWebDriver {

//    static Document get(String URL) {
//
//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--disable-notifications");
//        chromeOptions.addArguments("--incognito");
//        chromeOptions.addArguments("--headless"); //Window is visible or hidden
//
//        System.setProperty("webdriver.chrome.silentOutput", "true");
//        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
//
//        WebDriver webDriver = new ChromeDriver(chromeOptions);
//
//        webDriver.get(URL);
//
//        Document document = Jsoup.parse(webDriver.getPageSource());
//
//        if (document.title().isEmpty())
//            System.out.println("----------ERROR----------> There is problem with the internet connection");
//
//        webDriver.quit();
//
//        return document;
//    }

    static Document getSoup(String URL) {
        Document document = null;
        do {
            try {
                document = Jsoup.connect(URL).header("x-fsign", "SW9D1eZo").get();
            } catch (IOException e) {
                System.out.println("----------ERROR----------> There is problem with the internet connection. Reconnecting in 15 seconds...");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (document == null);
        return document;
    }
}
