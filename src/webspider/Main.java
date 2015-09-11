package webspider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Parisi Germán
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("https://www.google.com");
        System.out.println(url.toString());
        System.out.println(url.getHost());
    }
    
}
