package ap.lab12.util;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class Printer {

    public void printPrototype(Class<?> c){
        System.out.println(signatureOf(c));
        for (Field f : c.getDeclaredFields()) {
            System.out.println("  " + signatureOf(f) + ";");
        }
        for (Constructor<?> k : c.getDeclaredConstructors()) {
            System.out.println("  " + signatureOf(k) + " { ... }");
        }
        for (Method m : c.getDeclaredMethods()) {
            System.out.println("  " + signatureOf(m) + " { ... }");
        }
        System.out.println("}");
    }

    private String signatureOf(Class<?> c){
        String mods = Modifier.toString(c.getModifiers());
        String kind;
        if (c.isRecord()) kind = "record";
        else if (c.isInterface()) kind = "interface";
        else if (c.isEnum()) kind = "enum";
        else kind = "class";

        String name = c.getName();
        String sup = (c.getSuperclass()!=null && c.getSuperclass()!=Object.class)
                ? " extends " + typeName(c.getGenericSuperclass())
                : "";
        String ifs = c.getInterfaces().length>0
                ? " implements " + Arrays.stream(c.getGenericInterfaces())
                .map(this::typeName).collect(Collectors.joining(", "))
                : "";
        return (mods.isEmpty()? "" : mods+" ") + kind + " " + name + sup + ifs + " {";
    }

    private String signatureOf(Field f){
        return (Modifier.toString(f.getModifiers()) + " " + typeName(f.getGenericType()) + " " + f.getName()).trim();
    }
    private String signatureOf(Constructor<?> k){
        String mods = Modifier.toString(k.getModifiers());
        String params = Arrays.stream(k.getGenericParameterTypes()).map(this::typeName).collect(Collectors.joining(", "));
        return (mods.isEmpty()? "" : mods+" ") + k.getDeclaringClass().getName() + "(" + params + ")";
    }
    private String signatureOf(Method m){
        String mods = Modifier.toString(m.getModifiers());
        String ret = typeName(m.getGenericReturnType());
        String name = m.getName();
        String params = Arrays.stream(m.getGenericParameterTypes()).map(this::typeName).collect(Collectors.joining(", "));
        String ex = m.getExceptionTypes().length>0
                ? " throws " + Arrays.stream(m.getExceptionTypes()).map(Class::getName).collect(Collectors.joining(", "))
                : "";
        return (mods.isEmpty()? "" : mods+" ") + ret + " " + name + "(" + params + ")" + ex;
    }

    private String typeName(Type t){
        return switch (t) {
            case Class<?> cc -> cc.getName();
            case ParameterizedType pt -> typeName(pt.getRawType()) + "<" +
                    Arrays.stream(pt.getActualTypeArguments()).map(this::typeName).collect(Collectors.joining(", ")) + ">";
            case GenericArrayType gat -> typeName(gat.getGenericComponentType()) + "[]";
            case TypeVariable<?> tv -> tv.getName();
            case WildcardType wc -> "?";
            default -> t.getTypeName();
        };
    }
}
