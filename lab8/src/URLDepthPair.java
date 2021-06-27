import java.net.*;

//класс для представляения пар для Crawler
public class URLDepthPair {

    //поля для URL и глубины
    private int currentDepth;
    private String currentURL;

    //метод, задающий URL и глубину
    public URLDepthPair(String URL, int depth) {
        currentDepth = depth;
        currentURL = URL;
    }

    //метод возвращающтй текущий URL
    public String getURL() {
        return currentURL;
    }

    //метод возвращающий текущую глубину
    public int getDepth() {
        return currentDepth;
    }

    //метод возвращающий текущую URL и глубину в формате строки
    public String toString() {
        String stringDepth = Integer.toString(currentDepth);
        return stringDepth + '\t' + currentURL;
    }

    //метод возвращающий DocPatch текущего URL
    public String getDocPath() {
        try {
            URL url = new URL(currentURL);
            return url.getPath();
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    //Метод возвращающий webHost текущего URL
    public String getWebHost() {
        try {
            URL url = new URL(currentURL);
            return url.getHost();
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }
}


