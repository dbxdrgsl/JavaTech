package ap.lab12.util;

import ap.lab12.test.Test;

import java.lang.reflect.*;
import java.util.*;

public final class Runner {
    public static final class Info {
        public boolean isPublicAndAnnotated=false;
        public int totalMethods=0, testMethods=0, passed=0, failed=0;
    }

    public Info runTests(Class<?> c){
        Info inf = new Info();
        inf.isPublicAndAnnotated = Modifier.isPublic(c.getModifiers()) && c.isAnnotationPresent(Test.class);

        for (Method m : c.getDeclaredMethods()) {
            inf.totalMethods++;
            if (!m.isAnnotationPresent(Test.class)) continue;
            inf.testMethods++;
            try {
                m.setAccessible(true);
                Object target = Modifier.isStatic(m.getModifiers()) ? null : newInstance(c);
                Object[] args = buildArgs(m.getParameterTypes());
                m.invoke(target, args);
                inf.passed++;
                System.out.println("[PASS] " + c.getName()+"#"+sig(m));
            } catch (Throwable ex) {
                inf.failed++;
                System.out.println("[FAIL] " + c.getName()+"#"+sig(m)+" -> "+ root(ex).toString());
            }
        }
        return inf;
    }

    private Object newInstance(Class<?> c){
        try {
            Constructor<?> ctor = Arrays.stream(c.getDeclaredConstructors())
                    .min(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow();
            ctor.setAccessible(true);
            return ctor.newInstance(buildArgs(ctor.getParameterTypes()));
        } catch (Exception e) {
            throw new RuntimeException("cannot instantiate "+c.getName(), e);
        }
    }

    private Object[] buildArgs(Class<?>[] ts){
        Object[] a = new Object[ts.length];
        for (int i=0;i<ts.length;i++) a[i] = mock(ts[i]);
        return a;
    }

    private Object mock(Class<?> t){
        if (!t.isPrimitive()) {
            if (t==String.class) return "mock";
            if (t.isArray()) return java.lang.reflect.Array.newInstance(t.getComponentType(), 0);
            try { return t.getConstructor().newInstance(); }
            catch (Throwable ignored) { return null; }
        }
        if (t==int.class) return 42;
        if (t==long.class) return 42L;
        if (t==double.class) return 3.14;
        if (t==float.class) return 2.71f;
        if (t==boolean.class) return true;
        if (t==byte.class) return (byte)1;
        if (t==short.class) return (short)2;
        if (t==char.class) return 'x';
        return null;
    }

    private Throwable root(Throwable e){
        Throwable x=e instanceof InvocationTargetException ite? ite.getTargetException():e;
        while (x.getCause()!=null) x=x.getCause(); return x;
    }
    private String sig(Method m){
        StringBuilder sb=new StringBuilder(m.getName()).append("(");
        for (int i=0;i<m.getParameterCount();i++){
            if(i>0) sb.append(","); sb.append(m.getParameterTypes()[i].getSimpleName());
        }
        return sb.append(")").toString();
    }
}
