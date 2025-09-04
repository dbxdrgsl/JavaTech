package ap.lab12.scan;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Loader implements Closeable {
    private final URLClassLoader cl;
    private final List<String> classNames;

    private Loader(URLClassLoader cl, List<String> classNames){ this.cl=cl; this.classNames=classNames; }

    public static Loader fromInput(Path input) throws IOException {
        if (!Files.exists(input)) throw new IOException("not found: "+input);
        if (Files.isDirectory(input)) return fromDirectory(input);
        String name = input.getFileName().toString().toLowerCase();
        if (name.endsWith(".jar")) return fromJar(input);
        if (name.endsWith(".class")) return fromClassFile(input);
        throw new IOException("unsupported: "+input);
    }

    private static Loader fromDirectory(Path dir) throws IOException {
        List<String> names = new ArrayList<>();
        try (var walk = Files.walk(dir)) {
            walk.filter(p -> p.toString().endsWith(".class"))
                    .forEach(p -> names.add(toClassName(dir, p)));
        }
        URLClassLoader cl = new URLClassLoader(new URL[]{dir.toUri().toURL()});
        return new Loader(cl, names);
    }

    private static Loader fromJar(Path jar) throws IOException {
        List<String> names = new ArrayList<>();
        try (JarFile jf = new JarFile(jar.toFile())) {
            Enumeration<JarEntry> en = jf.entries();
            while (en.hasMoreElements()) {
                JarEntry e = en.nextElement();
                if (e.getName().endsWith(".class")) names.add(e.getName().replace('/', '.').replaceAll("\\.class$",""));
            }
        }
        URLClassLoader cl = new URLClassLoader(new URL[]{jar.toUri().toURL()});
        return new Loader(cl, names);
    }

    private static Loader fromClassFile(Path file) throws IOException {
        Path root = file.getParent();
        String cn = toClassName(root, file);
        URLClassLoader cl = new URLClassLoader(new URL[]{root.toUri().toURL()});
        return new Loader(cl, List.of(cn));
    }

    private static String toClassName(Path root, Path file) {
        Path rel = root.relativize(file);
        String s = rel.toString().replace('\\','/').replaceAll("\\.class$","");
        return s.replace('/', '.');
    }

    public List<Class<?>> loadAll(){
        List<Class<?>> out = new ArrayList<>();
        for (String cn : classNames) {
            try { out.add(Class.forName(cn, false, cl)); }
            catch (Throwable ignored) { }
        }
        return out;
    }

    @Override public void close() throws IOException { cl.close(); }
}
