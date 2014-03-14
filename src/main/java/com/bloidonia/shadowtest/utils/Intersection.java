package com.bloidonia.shadowtest.utils;

public class Intersection {
    public static final Intersection NONE = new Intersection( new Point( 100, 100 ), Double.MAX_VALUE );

    Point i;
    double d;

    private Intersection( Point i, double d ) {
        this.i = i;
        this.d = d;
    }

    public static Intersection intersect( Segment ray, Segment segment ) {
        double r_mag = ray.magnitude();
        double s_mag = segment.magnitude();

        if( ray.getTo().getX() / r_mag == segment.getTo().getX() / s_mag &&
                ray.getTo().getY() / r_mag == segment.getTo().getY() / s_mag ) { // Directions are the same.
            return NONE;
        }

        double T2 = ( ray.getTo().getX() *
                ( segment.getFrom().getY() - ray.getFrom().getY() ) +
                ray.getTo().getY() *
                        ( ray.getFrom().getX() - segment.getFrom().getX() ) ) /
                ( segment.getTo().getX() * ray.getTo().getY() - segment.getTo().getY() * ray.getTo().getX() );
        double T1 = ( segment.getFrom().getX() + segment.getTo().getX() * T2 - ray.getFrom().getX() ) / ray.getTo().getX();

        if( T1 < 0 || ( T2 < 0 || T2 > 1 ) ) return NONE;

        return new Intersection( new Point( ray.getFrom().getX() + ray.getTo().getX() * T1,
                ray.getFrom().getY() + ray.getTo().getY() * T1 ),
                T1
        );
    }

    public Point getPoint() {
        return i;
    }

    public double getDistance() {
        return d;
    }

    @Override
    public String toString() {
        return getPoint().toString() + ":" + d ;
    }
}
