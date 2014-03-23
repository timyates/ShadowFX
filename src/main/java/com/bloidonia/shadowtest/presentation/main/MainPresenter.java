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
import javafx.scene.effect.BoxBlur;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainPresenter implements Initializable {
    @FXML
    StackPane mainPane;

    @FXML
    AnchorPane walls ;

    @FXML
    AnchorPane shadow ;

    @FXML
    AnchorPane light ;

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
        walls.getChildren().addAll(
                Arrays.asList( segments ).stream().skip( 1 ).map( ( p ) -> {
                    Polygon shape = new Polygon();
                    for( Point pt : p ) {
                        shape.getPoints().addAll( pt.getX(), pt.getY() );
                    }
                    shape.setFill( Color.BLACK );
                    return shape;
                } ).collect( Collectors.toList() )
        );
        shadow.getChildren().add( new Rectangle( 0, 0, 640, 360 ) {{
            setFill( Color.valueOf( "#000000AA" ) );
            setBlendMode( BlendMode.DARKEN );
        }} ) ;
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

    private Polygon renderRay( Point mp ) {
        Polygon lightPoly = new Polygon();
        List<Point> beams = points.stream()
                .map( p -> Math.atan2( p.getY() - mp.getY(), p.getX() - mp.getX() ) )
                .flatMap( a -> Stream.of( a - 0.0001, a, a + 0.0001 ) )
                .sorted()
                .map( a -> {
                    Segment s = new Segment( mp, new Point( mp.getX() + Math.cos( a ), mp.getY() + Math.sin( a ) ) );
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
        lightPoly.getPoints()
                .addAll( beams.stream()
                        .flatMap( p -> Stream.of( p.getX(), p.getY() ) )
                        .collect( Collectors.toList() ) );
        lightPoly.setFill( new RadialGradient( 0, .5, mp.getX(), mp.getY(),
                400, false, CycleMethod.NO_CYCLE, new Stop( 0, Color.valueOf( "#FFFFFF55" ) ),
                new Stop( 1, Color.valueOf( "#33333300" ) ) ) );
        lightPoly.setBlendMode( BlendMode.SOFT_LIGHT );
        return lightPoly ;
    }
    
    private void renderRays() {
        light.getChildren().removeAll( rays );
        rays.clear();
        double mx = mouseX.get();
        double my = mouseY.get();

        for( int i = 0 ; i < 8 ; i++ ) {
            rays.add( renderRay( new Point( mx + Math.cos( ( i / 8.0 ) * ( Math.PI * 2.0 ) ) * 7.0,
                                            my + Math.sin( ( i / 8.0 ) * ( Math.PI * 2.0 ) ) * 7.0  ) ) ) ;
        }

        light.getChildren().addAll( rays );
        debug = false;
    }

}