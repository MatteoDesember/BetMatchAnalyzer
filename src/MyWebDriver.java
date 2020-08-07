import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * MyWebDriver is a webDriver wchich handle web connections
 */
class MyWebDriver {
    /**
     * getSoup connects to given URL and returns html page
     */
    static Document getSoup(String URL) {
        Document document = null;
        do {
            try {
                // This header allows to download specyfic page.
                document = Jsoup.connect(URL).header("x-fsign", "SW9D1eZo").get();
            } catch (IOException e) {
                // If there is a problem with the internet connection show info and wait
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
