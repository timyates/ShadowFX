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

        if( ray.to().x() / r_mag == segment.to().x() / s_mag &&
                ray.to().y() / r_mag == segment.to().y() / s_mag ) { // Directions are the same.
            return NONE;
        }

        double T2 = ( ray.to().x() *
                ( segment.from().y() - ray.from().y() ) +
                ray.to().y() *
                        ( ray.from().x() - segment.from().x() ) ) /
                ( segment.to().x() * ray.to().y() - segment.to().y() * ray.to().x() );
        double T1 = ( segment.from().x() + segment.to().x() * T2 - ray.from().x() ) / ray.to().x();

        if( T1 < 0 || ( T2 < 0 || T2 > 1 ) ) return NONE;

        return new Intersection( new Point( ray.from().x() + ray.to().x() * T1,
                ray.from().y() + ray.to().y() * T1 ),
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
