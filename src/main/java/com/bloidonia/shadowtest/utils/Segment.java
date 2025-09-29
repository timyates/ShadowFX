package com.bloidonia.shadowtest.utils;

public record Segment(Point from, Point to) {

    public static Segment build(Point a, Point b) {
        return new Segment(a, new Point(b.x() - a.x(), b.y() - a.y()));
    }

    public double magnitude() {
        return Math.sqrt(to.x() * to.x() + to.y() * to.y());
    }
}