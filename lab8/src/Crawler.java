import java.net.*;
import java.util.*;
import java.io.*;

//Реализация веб сканера
//Класс имеет метод getAllLinks для хранения всех ссылок на данной странице
public class Crawler {

    public static void main(String[] args) {
        //глубина
        int depth = 0;

        //проверяет корректность ввода данных
        //если не равно завешает
        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL> <depth>");
            System.exit(1);
        }
        //иначе продолжает
        else {
            //проверяем является ли введенная глубина числом
            try {
                //задаем введеную глубину преобразуя из str в int
                depth = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException nfe) {
                //выводим ошибку и завершаем
                System.out.println("usage: java Crawler <URL> <depth>");
                System.exit(1);
            }
        }

        // A linked list to represent pending URLs.
        LinkedList<URLDepthPair> pendingURLs = new LinkedList<URLDepthPair>();

        // A linked list to represent processed URLs.
        LinkedList<URLDepthPair> processedURLs = new LinkedList<URLDepthPair>();

        // A URL Depth Pair to represent the website that the user inputted
        // with depth 0.
        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);

        // Add the current website from user input to pending URLs.
        pendingURLs.add(currentDepthPair);

        // An array list to represent URLs that have been seen. Add current
        // website.
        ArrayList<String> seenURLs = new ArrayList<String>();
        seenURLs.add(currentDepthPair.getURL());

        // While pendingURLs is not empty, iterate through, visit each website,
        // and get all links from each.
        while (pendingURLs.size() != 0) {

            // Get the next URL from pendingURLs, add to processed URLs, and
            // store its depth.
            URLDepthPair depthPair = pendingURLs.pop();
            processedURLs.add(depthPair);
            int myDepth = depthPair.getDepth();

            // Get all links from the site and store them in a new linked list.
            LinkedList<String> linksList = new LinkedList<String>();
            linksList = Crawler.getAllLinks(depthPair);

            // If we haven't reached the maximum depth, add links from the site
            // that haven't been seen before to pendingURLs and seenURLs.
            if (myDepth < depth) {
                // Iterate through links from site.
                for (int i=0;i<linksList.size();i++) {
                    String newURL = linksList.get(i);
                    // If we've already seen the link, continue.
                    if (seenURLs.contains(newURL)) {
                        continue;
                    }
                    // If we haven't seen the link, create a new URLDepthPair
                    // with depth one greater than current depth, and add
                    // to pendingURLs and seenURLs.
                    else {
                        URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
                        pendingURLs.add(newDepthPair);
                        seenURLs.add(newURL);
                    }
                }
            }
        }
        // Print out all processed URLs with depth.
        Iterator<URLDepthPair> iter = processedURLs.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    //Принимает URLDepthPair, находит все ссылки и добавляет в LinkedList
    private static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {

        //инициализирую список строк для храннения найденных ссылок
        LinkedList<String> URLs = new LinkedList<String>();

        //инициализурем сокет
        Socket sock;

        //пытаемся создать новый сокет на порте 80
        try {
            sock = new Socket(myDepthPair.getWebHost(), 80);
        }
        //выводим ошибку и возвращаем пустой список
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        //выводим ошибку и возвращаем пустой список
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }

        //если ответа нет 3 секунд выдаем ошибку
        try {
            sock.setSoTimeout(3000);
        }
        //выводим ошибку и возвращаем пустой список
        catch (SocketException exc) {
            System.err.println("SocketException: " + exc.getMessage());
            return URLs;
        }

        //Устанавливаем docPath и webHost
        String docPath = myDepthPair.getDocPath();
        String webHost = myDepthPair.getWebHost();

        //Инициализируем outStream
        OutputStream outStream;

        //пытаемся получить getOutputStream у сокета
        try {
            outStream = sock.getOutputStream();
        }
        //выводим ошибку и возвращаем пустой список
        catch (IOException exce) {
            System.err.println("IOException: " + exce.getMessage());
            return URLs;
        }

        //инициализируем PrintWriter
        PrintWriter myWriter = new PrintWriter(outStream, true);

        //отправляем запрос на сервер
        myWriter.println("GET " + docPath + " HTTP/1.1");
        myWriter.println("Host: " + webHost);
        myWriter.println("Connection: close");
        myWriter.println();

        //инициализируем InputStream.
        InputStream inStream;

        //пытаемся получить getInputStream у сокета
        try {
            inStream = sock.getInputStream();
        }
        //выводим ошибку и возвращаем пустой список
        catch (IOException excep){
            System.err.println("IOException: " + excep.getMessage());
            return URLs;
        }
        //Создаем новые InputStreamReader и BufferedReader для чтения строк с сервера
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);

        //пытаемся прочесть строку с BuffReader
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            //выводим ошибку и возвращаем пустой список
            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }
            //Готовый документ для чтения
            if (line == null)
                break;

            //переменные индекса
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;

            while (true) {

                //константа указывающая на ссылку
                String URL_INDICATOR = "a href=\"";

                //константа указывающая на конец веб-хоста
                String END_URL = "\"";


                // Search for our start in the current line.
                index = line.indexOf(URL_INDICATOR, index);
                if (index == -1) // No more copies of start in this line
                    break;

                // Advance the current index and set to beginIndex.
                index += URL_INDICATOR.length();
                beginIndex = index;

                // Search for our end in the current line and set to endIndex.
                endIndex = line.indexOf(END_URL, index);
                index = endIndex;

                // Set the link to the substring between the begin index
                // and end index.  Add to our URLs list.
                String newLink = line.substring(beginIndex, endIndex);
                URLs.add(newLink);
            }

        }
        //возвращаем массив URL
        return URLs;
    }
}

