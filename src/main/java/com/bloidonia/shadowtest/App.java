package com.bloidonia.shadowtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start( Stage stage ) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("presentation/main/main.fxml"));

        Scene scene = new Scene(root );
        stage.setTitle( "JavaFX Shadow Test" );
        final String uri = getClass().getResource( "app.css" ).toExternalForm();
        scene.getStylesheets().add( uri );
        stage.setScene( scene );
        stage.setResizable( false );
        stage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    public static void main( String[] args ) {
        launch( args );
    }
}