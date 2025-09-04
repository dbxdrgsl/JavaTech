package ap.lab09.util;
public final class Haversine {
    private static final double R=6371.0088;
    public static double km(double a1,double b1,double a2,double b2){
        double d1=Math.toRadians(a2-a1), d2=Math.toRadians(b2-b1);
        double s=Math.sin(d1/2), t=Math.sin(d2/2);
        double h=s*s+Math.cos(Math.toRadians(a1))*Math.cos(Math.toRadians(a2))*t*t;
        return 2*R*Math.atan2(Math.sqrt(h), Math.sqrt(1-h));
    }
}
