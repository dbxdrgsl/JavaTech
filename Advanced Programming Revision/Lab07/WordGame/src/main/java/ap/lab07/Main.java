package ap.lab07;

public class Main {
    public static void main(String[] args) throws Exception {
        // optional: args[0] = dictionary path
        Dictionary dict = (args.length>0) ? new Dictionary(java.nio.file.Path.of(args[0])) : new Dictionary();
        int players = 3;
//        long limitMs = 15_000; // 15s
        long limitMs = 30_000; // in Main

        Game game = new Game(dict, players, limitMs);
        var p1 = new Player("Alice", game, 0);
        var p2 = new Player("Bob",   game, 1);
        var p3 = new Player("Cara",  game, 2);

        game.start(java.util.List.of(p1,p2,p3));

        System.out.println("\nFinal scores:");
        game.board.scores().forEach((n,s)-> System.out.println(n+": "+s));
        var winner = game.board.scores().entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue());
        System.out.println("Winner: " + (winner.isPresent()? winner.get().getKey() : "(none)"));
    }
}
