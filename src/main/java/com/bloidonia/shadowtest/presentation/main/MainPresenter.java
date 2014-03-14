package com.bloidonia.shadowtest.presentation.main;

import com.bloidonia.shadowtest.utils.Intersection;
import com.bloidonia.shadowtest.utils.Point;
import com.bloidonia.shadowtest.utils.Segment;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    AnchorPane mainPane;

    DoubleProperty mouseX = new SimpleDoubleProperty();
    DoubleProperty mouseY = new SimpleDoubleProperty();

    List<Shape> rays = new ArrayList<>();

    Point[][] segments = {
            {new Point( 0.0, 0.0 ), new Point( 640.0, 0.0 ), new Point( 640.0, 360.0 ), new Point( 0.0, 360.0 )},
            {new Point( 100.0, 150.0 ), new Point( 120.0, 50.0 ), new Point( 200.0, 80.0 ), new Point( 140.0, 210.0 )},
            {new Point( 100.0, 200.0 ), new Point( 120.0, 250.0 ), new Point( 60.0, 300.0 )},
            {new Point( 200.0, 260.0 ), new Point( 220.0, 150.0 ), new Point( 300.0, 200.0 ), new Point( 250.0, 320.0 )},
            {new Point( 340.0, 60.0 ), new Point( 360.0, 40.0 ), new Point( 370.0, 70.0 )},
            {new Point( 450.0, 190.0 ), new Point( 560.0, 170.0 ), new Point( 540.0, 270.0 ), new Point( 430.0, 290.0 )},
            {new Point( 400.0, 95.0 ), new Point( 580.0, 50.0 ), new Point( 480.0, 150.0 )},
    };

    Set<Point> points;
    Set<Segment> segmentSet;
    private boolean debug = false;

    @Override
    public void initialize( URL url, ResourceBundle resourceBundle ) {
        mainPane.getChildren().addAll(
                Arrays.asList( segments ).stream().skip( 1 ).map( ( p ) -> {
                    Polygon shape = new Polygon();
                    for( Point pt : p ) {
                        shape.getPoints().addAll( pt.getX(), pt.getY() );
                    }
                    shape.setFill( Color.BLACK );
                    return shape;
                } ).collect( Collectors.toList() )
        );
        points = Arrays.asList( segments ).stream()
                .flatMap( ( pts ) -> Arrays.asList( pts ).stream() )
                .collect( Collectors.toSet() );
        segmentSet = new HashSet<>();
        for( Point[] sh : segments ) {
            for( int i = 0; i < sh.length - 1; i++ ) {
                segmentSet.add( new Segment( sh[ i ], sh[ i + 1 ] ) );
            }
            segmentSet.add( new Segment( sh[ sh.length - 1 ], sh[ 0 ] ) );
        }
        mainPane.setOnMouseMoved( ( MouseEvent mouseEvent ) -> {
            mouseX.set( mouseEvent.getSceneX() );
            mouseY.set( mouseEvent.getSceneY() );
        } );
        mainPane.setOnMouseClicked( ( MouseEvent mouseEvent ) -> {
            debug = true;
        } );
        new AnimationTimer() {
            @Override
            public void handle( long l ) {
                renderRays();
            }
        }.start();
    }

    private void renderRays() {
        mainPane.getChildren().removeAll( rays );
        rays.clear();
        double mx = mouseX.get();
        double my = mouseY.get();
        Point mp = new Point( mx, my );
        Polygon light = new Polygon();
        List<Point> beams = points.stream()
                .map( p -> Math.atan2( p.getY() - my, p.getX() - mx ) )
                .flatMap( a -> Stream.of( a - 0.0001, a, a + 0.0001 ) )
                .sorted()
                .map( a -> {
                    Segment s = new Segment( mp, new Point( mx + Math.cos( a ), my + Math.sin( a ) ) );
                    Intersection i = segmentSet.stream()
                            .map( ss -> Intersection.intersect( s, ss ) )
                            .min( ( o1, o2 ) -> Double.compare( o1.getDistance(), o2.getDistance() ) ).get();
                    if( debug ) {
                        System.out.println( i );
                    }
                    return i;
                } )
                .filter( i -> i != Intersection.NONE )
                .map( Intersection::getPoint )
                .collect( Collectors.toList() );
        light.getPoints()
                .addAll( beams.stream()
                        .flatMap( p -> Stream.of( p.getX(), p.getY() ) )
                        .collect( Collectors.toList() ) );
        Shape shadow = Shape.subtract( new Rectangle( 0, 0, 640, 360 ), light );
        shadow.setFill( Color.valueOf( "#333333EE" ) );
        rays.add( shadow );
        light.setFill( new RadialGradient( 0,
                .1,
                mx,
                my,
                400,
                false,
                CycleMethod.NO_CYCLE,
                new Stop( 0, Color.WHITE ),
                new Stop( 1, Color.valueOf( "#33333399" ) ) ) );
        rays.add( light );
        mainPane.getChildren().addAll( rays );
        debug = false;
    }

}