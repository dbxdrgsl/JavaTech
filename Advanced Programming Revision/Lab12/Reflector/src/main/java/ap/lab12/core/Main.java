package ap.lab12.core;

import ap.lab12.scan.Loader;
import ap.lab12.util.Printer;
import ap.lab12.util.Runner;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length==0) {
            System.out.println("Usage: java Main <path-to-.class | dir | jar>");
            return;
        }
        Path in = Path.of(args[0]);
        try (Loader ld = Loader.fromInput(in)) {
            List<Class<?>> classes = ld.loadAll();
            var pr = new Printer();
            var rn = new Runner();

            int cls=0, pubTestCls=0, meth=0, testMeth=0, pass=0, fail=0;

            for (Class<?> c : classes) {
                cls++;
                pr.printPrototype(c);
                var info = rn.runTests(c);
                pubTestCls += info.isPublicAndAnnotated ? 1 : 0;
                meth += info.totalMethods;
                testMeth += info.testMethods;
                pass += info.passed;
                fail += info.failed;
            }

            System.out.printf("%n--- Stats ---\nclasses=%d, @Test public classes=%d\nmethods=%d, @Test methods=%d\npassed=%d, failed=%d%n",
                    cls, pubTestCls, meth, testMeth, pass, fail);
        }
    }
}
