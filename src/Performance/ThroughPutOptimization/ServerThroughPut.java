// Create an HTTP server which receives a word to be searched in a large book viz. Tolstoy's war & peace
// Application searches the word in the book and counts the no of instances it occurs in the book
// Further throughput performance is measured through Jmeter.
package Performance.ThroughPutOptimization;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerThroughPut {
    private static final String INPUT_FINAL = "./resources/war_and_peace.txt";
    // Thread pool size ie the number of threads we will have in fixed executor
    private static final int NUMBER_OF_THREADS = 8;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FINAL)));
        startServer(text);
    }
    public static void startServer(String text) throws IOException{
        // take address of localhost and the port we want to listen on
        // first parameter is port , second parameter is the backlog size,
        // which determines the size of the http queue for http server requests
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        //create a handler , which basically assigns a handle object to a particuar route.
        // new wordcounthandler will be instance of a clas
        server.createContext("/search", new WordCountHandler(text));
        // Executor which schedules a request to a pool of threads
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }
    //route handlers need to implement HttpHandler
    private static class WordCountHandler implements HttpHandler{
        private String text;
        public WordCountHandler(String text){
            this.text= text;
        }
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            // get query part of uri, where we are sending the search key in key "word"
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String value = keyValue[1];
            if(!action.equals("word")){
                httpExchange.sendResponseHeaders(400, 0);
                byte [] response = Long.toString(-1).getBytes();
                OutputStream outputStream = httpExchange.getResponseBody();
                outputStream.write(response);
                outputStream.close();
            }
            long count = countWords(value);
            // convert count into byte array so that we can serialize it and send over the wire
            byte [] response = Long.toString(count).getBytes();
            // in the HTTP header set the status code 200, and pass the response length
            httpExchange.sendResponseHeaders(200, response.length);
            //write the http body into the http response stream and close the stream which sends the response
            // to client upstream
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }
        private long countWords(String word){
            long count = 0;
            int index = 0;
            while(index >= 0){
                index = text.indexOf(word, index);
                if(index >= 0){
                    count++;
                    index++;
                }
            }
            return count;
        }
    }
}

