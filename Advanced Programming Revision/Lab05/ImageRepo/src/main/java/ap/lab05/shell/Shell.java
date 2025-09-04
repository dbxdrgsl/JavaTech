package ap.lab05.shell;

import ap.lab05.core.*;
import ap.lab05.io.JsonPersister;
import ap.lab05.io.Persister;
import ap.lab05.report.ReportGenerator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class Shell {
    public final ImageRepo repo = new ImageRepo();
    public final Persister persister = new JsonPersister();
    public final ReportGenerator reporter = new ReportGenerator();
    private final Map<String, Command> cmds = new LinkedHashMap<>();
    private final List<String> tagPool = List.of("work","family","travel","art","school","nature","party","pets");

    public Shell() {
        reg(new CmdAdd()); reg(new CmdRemove()); reg(new CmdUpdate());
        reg(new CmdList()); reg(new CmdDisplay()); reg(new CmdLoad()); reg(new CmdSave());
        reg(new CmdReport()); reg(new CmdAddAll()); reg(new CmdGroups()); reg(new CmdHelp()); reg(new CmdExit());
    }
    private void reg(Command c){ cmds.put(c.name(), c); }
    public void loop(Scanner sc){
        while (true) {
            System.out.print("> ");
            String line = sc.hasNextLine()? sc.nextLine().trim() : null;
            if (line==null) break; if (line.isEmpty()) continue;
            String[] tok = line.split("\\s+");
            Command c = cmds.get(tok[0].toLowerCase());
            try {
                if (c==null) throw new RepoException("unknown command: "+tok[0]);
                c.run(this, Arrays.copyOfRange(tok,1,tok.length));
            } catch (RepoException e) {
                System.err.println("error: " + e.getMessage());
            }
        }
    }

    /* ===== Commands ===== */

    final class CmdAdd implements Command {
        public String name(){ return "add"; }
        public String help(){ return "add <name> <yyyy-mm-dd> <path> [tag1 tag2 ...]"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length < 3) throw new RepoException(help());
            String name=a[0]; LocalDate date=LocalDate.parse(a[1]); Path p=Path.of(a[2]);
            List<String> tags = Arrays.asList(Arrays.copyOfRange(a,3,a.length));
            repo.add(new ImageItem(name,date,tags,p));
            System.out.println("added: " + name);
        }
    }

    final class CmdRemove implements Command {
        public String name(){ return "remove"; }
        public String help(){ return "remove <name>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            repo.remove(a[0]); System.out.println("removed: " + a[0]);
        }
    }

    final class CmdUpdate implements Command {
        public String name(){ return "update"; }
        public String help(){ return "update <name> name=.. date=yyyy-mm-dd path=.. tags=tag1,tag2"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length<2) throw new RepoException(help());
            String name=a[0];
            Optional<String> nn=Optional.empty(); Optional<LocalDate> nd=Optional.empty();
            Optional<List<String>> nt=Optional.empty(); Optional<java.nio.file.Path> np=Optional.empty();
            for (int i=1;i<a.length;i++) {
                String[] kv=a[i].split("=",2);
                if (kv.length!=2) throw new RepoException("bad arg: "+a[i]);
                switch (kv[0]) {
                    case "name" -> nn=Optional.of(kv[1]);
                    case "date" -> nd=Optional.of(LocalDate.parse(kv[1]));
                    case "path" -> np=Optional.of(Path.of(kv[1]));
                    case "tags" -> nt=Optional.of(Arrays.stream(kv[1].split(",")).filter(s2->!s2.isBlank()).collect(Collectors.toList()));
                    default -> throw new RepoException("unknown field: "+kv[0]);
                }
            }
            repo.update(name, nn, nd, nt, np);
            System.out.println("updated: " + (nn.orElse(name)));
        }
    }

    final class CmdList implements Command {
        public String name(){ return "list"; }
        public String help(){ return "list"; }
        public void run(Shell s, String[] a) {
            repo.all().forEach(System.out::println);
        }
    }

    final class CmdDisplay implements Command {
        public String name(){ return "display"; }
        public String help(){ return "display <name>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            repo.display(a[0]);
        }
    }

    final class CmdLoad implements Command {
        public String name(){ return "load"; }
        public String help(){ return "load <file.json>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            var items = persister.load(Path.of(a[0]));
            for (ImageItem it : items) {
                if (!repo.all().stream().anyMatch(x->x.name().equals(it.name()))) repo.add(it);
            }
            System.out.println("loaded: " + items.size());
        }
    }

    final class CmdSave implements Command {
        public String name(){ return "save"; }
        public String help(){ return "save <file.json>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            persister.save(Path.of(a[0]), repo.all());
            System.out.println("saved: " + a[0]);
        }
    }

    final class CmdReport implements Command {
        public String name(){ return "report"; }
        public String help(){ return "report <out.html>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            var list = new ArrayList<>(repo.all());
            reporter.generate(Path.of(a[0]), list);
            System.out.println("report: " + a[0]);
            try {
                if (java.awt.Desktop.isDesktopSupported())
                    java.awt.Desktop.getDesktop().browse(Path.of(a[0]).toUri());
            } catch (Exception ignore) { }
        }
    }

    final class CmdAddAll implements Command {
        public String name(){ return "addAll"; }
        public String help(){ return "addAll <directory>"; }
        public void run(Shell s, String[] a) throws RepoException {
            if (a.length!=1) throw new RepoException(help());
            java.nio.file.Path root = java.nio.file.Path.of(a[0]);
            if (!java.nio.file.Files.isDirectory(root)) throw new RepoException("not a directory: "+root);
            final String[] exts = {".png",".jpg",".jpeg",".gif",".bmp"};
            final var rng = new Random(7);
            try (var walk = java.nio.file.Files.walk(root)) {
                int[] cnt = {0};
                walk.filter(p->!java.nio.file.Files.isDirectory(p))
                        .filter(p->{
                            String f = p.getFileName().toString().toLowerCase();
                            for (String e: exts) if (f.endsWith(e)) return true;
                            return false;
                        })
                        .forEach(p->{
                            String base = p.getFileName().toString();
                            String name = base.replaceAll("\\.[^.]+$","");
                            List<String> tags = new ArrayList<>();
                            int k = 1 + rng.nextInt(3);
                            for (int i=0;i<k;i++) tags.add(tagPool.get(rng.nextInt(tagPool.size())));
                            try {
                                repo.add(new ImageItem(name, java.time.LocalDate.now(), tags, p));
                                cnt[0]++;
                            } catch (RepoException ignored) {}
                        });
                System.out.println("addAll added: " + cnt[0]);
            } catch (java.io.IOException e) {
                throw new RepoException("scan failed: "+root, e);
            }
        }
    }

    final class CmdGroups implements Command {
        public String name(){ return "groups"; }
        public String help(){ return "groups  # maximal groups where any two share ≥1 tag"; }
        public void run(Shell s, String[] a) {
            var items = new ArrayList<>(repo.all());
            int n = items.size();
            // Build adjacency by shared tag
            boolean[][] adj = new boolean[n][n];
            for (int i=0;i<n;i++) for (int j=i+1;j<n;j++) {
                boolean share = !Collections.disjoint(items.get(i).tags(), items.get(j).tags());
                adj[i][j]=adj[j][i]=share;
            }
            // Bron–Kerbosch (simple)
            List<List<Integer>> cliques = new ArrayList<>();
            bronKerbosch(new ArrayList<>(), range(n), new ArrayList<>(), adj, cliques);
            // Print maximal groups
            cliques.sort((a1,a2)->Integer.compare(a2.size(), a1.size()));
            for (var C : cliques) {
                var names = C.stream().map(i->items.get(i).name()).toList();
                System.out.println("group(" + C.size() + "): " + names);
            }
        }
        private List<Integer> range(int n){ List<Integer> r=new ArrayList<>(n); for (int i=0;i<n;i++) r.add(i); return r; }
        private List<Integer> neigh(int v, boolean[][] adj){
            List<Integer> r=new ArrayList<>(); for(int i=0;i<adj.length;i++) if(adj[v][i]) r.add(i); return r;
        }
        private void bronKerbosch(List<Integer> R, List<Integer> P, List<Integer> X, boolean[][] A, List<List<Integer>> out){
            if (P.isEmpty() && X.isEmpty()) { out.add(new ArrayList<>(R)); return; }
            List<Integer> Pcopy = new ArrayList<>(P);
            for (int v : Pcopy) {
                R.add(v);
                bronKerbosch(R, intersect(P, neigh(v,A)), intersect(X, neigh(v,A)), A, out);
                R.remove(R.size()-1);
                P.remove((Integer)v); X.add(v);
            }
        }
        private List<Integer> intersect(List<Integer> a, List<Integer> b){
            var set = new HashSet<>(b); var r=new ArrayList<Integer>();
            for (int x: a) if (set.contains(x)) r.add(x); return r;
        }
    }

    final class CmdHelp implements Command {
        public String name(){ return "help"; }
        public String help(){ return "help"; }
        public void run(Shell s, String[] a) {
            System.out.println("Commands: add, remove, update, list, display, load, save, report, addAll, groups, help, exit");
        }
    }

    final class CmdExit implements Command {
        public String name(){ return "exit"; }
        public String help(){ return "exit"; }
        public void run(Shell s, String[] a) { System.exit(0); }
    }
}
