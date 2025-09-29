package com.bloidonia.shadowtest.presentation.main;

import com.bloidonia.shadowtest.utils.Intersection;
import com.bloidonia.shadowtest.utils.Point;
import com.bloidonia.shadowtest.utils.Segment;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainPresenter implements Initializable {
    @FXML
    StackPane mainPane;

    @FXML
    AnchorPane walls;

    @FXML
    AnchorPane sprites;

    @FXML
    AnchorPane shadow;

    @FXML
    AnchorPane light;

    DoubleProperty mouseX = new SimpleDoubleProperty();
    DoubleProperty mouseY = new SimpleDoubleProperty();

    List<Shape> rays = new ArrayList<>();

    Point[][] segments = {
            {new Point(0.0, 0.0), new Point(640.0, 0.0), new Point(640.0, 360.0), new Point(0.0, 360.0)},
            {new Point(100.0, 150.0), new Point(120.0, 50.0), new Point(200.0, 80.0), new Point(140.0, 210.0)},
            {new Point(100.0, 200.0), new Point(120.0, 250.0), new Point(60.0, 300.0)},
            {new Point(200.0, 260.0), new Point(220.0, 150.0), new Point(300.0, 200.0), new Point(250.0, 320.0)},
            {new Point(340.0, 60.0), new Point(360.0, 40.0), new Point(370.0, 70.0)},
            {new Point(450.0, 190.0), new Point(560.0, 170.0), new Point(540.0, 270.0), new Point(430.0, 290.0)},
            {new Point(400.0, 95.0), new Point(580.0, 50.0), new Point(480.0, 150.0)},
    };

    Set<Point> points;
    Set<Point> spritePoints;
    Set<Segment> segmentSet;
    Set<Segment> spriteSegments;
    private boolean debug = false;
    ImageView chest;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        walls.getChildren().addAll(
                Arrays.stream(segments).skip(1).map((p) -> {
                    Polygon shape = new Polygon();
                    for (Point pt : p) {
                        shape.getPoints().addAll(pt.x(), pt.y());
                    }
                    shape.setFill(Color.BLACK);
                    return shape;
                }).toList()
        );

        shadow.getChildren().add(new Rectangle(0, 0, 640, 360) {{
            setFill(Color.valueOf("#000000BB"));
            setBlendMode(BlendMode.DARKEN);
        }});

        points = Arrays.stream(segments)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        segmentSet = new HashSet<>();
        for (Point[] sh : segments) {
            for (int i = 0; i < sh.length - 1; i++) {
                segmentSet.add(Segment.build(sh[i], sh[i + 1]));
            }
            segmentSet.add(Segment.build(sh[sh.length - 1], sh[0]));
        }

        chest = new ImageView(new Image("/com/bloidonia/shadowtest/presentation/main/chest.gif"));
        chest.setX(320);
        chest.setY(180);
        sprites.getChildren().add(chest);
        spritePoints = new HashSet<>();
        spriteSegments = new HashSet<>();
        spritePoints.add(new Point(320, 188));
        spritePoints.add(new Point(352, 188));
        spritePoints.add(new Point(352, 212));
        spritePoints.add(new Point(320, 212));
        spriteSegments.add(Segment.build(new Point(320, 188), new Point(352, 188)));
        spriteSegments.add(Segment.build(new Point(352, 188), new Point(352, 212)));
        spriteSegments.add(Segment.build(new Point(352, 212), new Point(320, 212)));
        spriteSegments.add(Segment.build(new Point(320, 212), new Point(320, 188)));

        mainPane.setOnMouseMoved((MouseEvent mouseEvent) -> {
            mouseX.set(mouseEvent.getSceneX());
            mouseY.set(mouseEvent.getSceneY());
        });
        mainPane.setOnMouseClicked((MouseEvent mouseEvent) -> {
            debug = true;
        });

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                renderRays();
            }
        }.start();
    }

    private Polygon renderRay(Point mp, Set<Point> points, Set<Segment> segmentSet) {
        Polygon lightPoly = new Polygon();
        List<Point> beams = points.stream()
                .map(p -> Math.atan2(p.y() - mp.y(), p.x() - mp.x()))
                .flatMap(a -> Stream.of(a - 0.0001, a, a + 0.0001))
                .sorted()
                .map(a -> {
                    Segment s = Segment.build(mp, new Point(mp.x() + Math.cos(a), mp.y() + Math.sin(a)));
                    Intersection i = segmentSet.stream()
                            .map(ss -> Intersection.intersect(s, ss))
                            .min(Comparator.comparingDouble(Intersection::getDistance)).get();
                    if (debug) {
                        System.out.println(i);
                    }
                    return i;
                })
                .filter(i -> i != Intersection.NONE)
                .map(Intersection::getPoint)
                .toList();
        lightPoly.getPoints()
                .addAll(beams.stream()
                        .flatMap(p -> Stream.of(p.x(), p.y()))
                        .toList());
        lightPoly.setFill(new RadialGradient(0, .5, mp.x(), mp.y(),
                400, false, CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#FFFFFF55")),
                new Stop(1, Color.valueOf("#33333300"))));
        lightPoly.setBlendMode(BlendMode.SOFT_LIGHT);
        return lightPoly;
    }

    private void renderRays() {
        light.getChildren().removeAll(rays);
        rays.clear();
        double mx = mouseX.get();
        double my = mouseY.get();

        for (int i = 0; i < 8; i++) {
            Polygon beam = renderRay(new Point(mx + Math.cos((i / 8.0) * (Math.PI * 2.0)) * 7.0,
                    my + Math.sin((i / 8.0) * (Math.PI * 2.0)) * 7.0), points, segmentSet);
            rays.add(beam);
        }

        chest.setClip(rays.stream().reduce(new Rectangle(0, 0, 0, 0), Shape::union));

        rays.clear();

        Set<Point> combinedPoints = new HashSet<>(points);
        combinedPoints.addAll(spritePoints);
        Set<Segment> combinedSegments = new HashSet<>(segmentSet);
        combinedSegments.addAll(spriteSegments);
        for (int i = 0; i < 8; i++) {
            Polygon beam = renderRay(new Point(mx + Math.cos((i / 8.0) * (Math.PI * 2.0)) * 7.0,
                    my + Math.sin((i / 8.0) * (Math.PI * 2.0)) * 7.0), combinedPoints, combinedSegments);
            rays.add(beam);
        }

        light.getChildren().addAll(rays);

        debug = false;
    }

}