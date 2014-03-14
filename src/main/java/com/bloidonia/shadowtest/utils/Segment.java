package com.bloidonia.shadowtest.utils;

public class Segment {
    private Point from;
    private Point to;

    public Segment( Point a, Point b ) {
        this.from = a;
        this.to = new Point( b.getX() - a.getX(), b.getY() - a.getY() );
    }

    public double magnitude() {
        return Math.sqrt( to.getX() * to.getX() + to.getY() * to.getY() );
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }
}
