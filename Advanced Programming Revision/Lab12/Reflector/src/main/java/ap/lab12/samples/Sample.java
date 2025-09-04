package ap.lab12.samples;

import ap.lab12.test.Test;

@Test
public class Sample {
    public Sample() {}
    @Test public static void ok(){ /* pass */ }
    @Test public void needsArgs(int x, String s){ if (x<0 || s==null) throw new RuntimeException(); }
    public void notATest() {}
}
