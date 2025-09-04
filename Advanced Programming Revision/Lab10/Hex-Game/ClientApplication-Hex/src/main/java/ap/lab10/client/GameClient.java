package ap.lab10.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public final class GameClient {
    public static void main(String[] args) throws Exception {
        String host = args.length>0? args[0] : "127.0.0.1";
        int    port = args.length>1? Integer.parseInt(args[1]) : 5000;
        try (Socket s = new Socket(host, port);
             var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             var out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
             var sc = new Scanner(System.in)) {
            System.out.println("Connected. Type commands. 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                if (!sc.hasNextLine()) break;
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) break;
                out.println(line);
                // read until sentinel "."
                String resp;
                while ((resp = in.readLine()) != null) {
                    if (resp.equals(".")) break;
                    System.out.println(resp);
                }
            }
        }
    }
}
