// src/main/java/ap/lab01/HelloOps.java
package ap.lab01;

/** Author: Dragos-Andrei Bobu */
public class HelloOps {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        String[] languages = {"C","C++","C#","Python","Go","Rust","JavaScript","PHP","Swift","Java"};
        int n = (int)(Math.random() * 1_000_000);

        n *= 3;
        n += 0b10101;    // 21
        n += 0xFF;       // 255
        n *= 6;

        int s = digitRoot(n);
        System.out.println("Willy-nilly, this semester I will learn " + languages[s]);
    }

    private static int digitRoot(int x) {
        x = Math.abs(x);
        while (x >= 10) {
            int t = 0; while (x > 0) { t += x % 10; x /= 10; } x = t;
        }
        return x; // 0..9
    }
}
