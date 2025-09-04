package ap.lab05;

import ap.lab05.shell.Shell;
import java.util.Scanner;

/** Entry point: interactive shell. */
public class App {
    public static void main(String[] args) {
        System.out.println("ImageRepo shell. Type 'help'.");
        new Shell().loop(new Scanner(System.in));
    }
}
