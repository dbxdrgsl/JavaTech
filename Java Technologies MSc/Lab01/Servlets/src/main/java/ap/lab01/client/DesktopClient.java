package ap.lab01.client;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class DesktopClient {
    public static void main(String[] args) throws Exception {
        String base = (args.length>0)? args[0] : "http://localhost:8080";
        int n = (args.length>1)? Integer.parseInt(args[1]) : 5;
        int m = (args.length>2)? Integer.parseInt(args[2]) : 6;

        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(
                        URI.create(base + "/graph?numVertices=" + n + "&numEdges=" + m + "&format=txt"))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "text/plain")
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + resp.statusCode());
        System.out.println(resp.body());
    }
}
