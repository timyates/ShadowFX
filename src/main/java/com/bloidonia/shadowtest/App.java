package com.bloidonia.shadowtest;

import com.airhacks.afterburner.injection.InjectionProvider;
import com.bloidonia.shadowtest.presentation.main.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start( Stage stage ) throws Exception {
        MainView appView = new MainView();
        Scene scene = new Scene( appView.getView() );
        stage.setTitle( "JavaFX Shadow Test" );
        final String uri = getClass().getResource( "app.css" ).toExternalForm();
        scene.getStylesheets().add( uri );
        stage.setScene( scene );
        stage.setResizable( false );
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        InjectionProvider.forgetAll();
    }

    public static void main( String[] args ) {
        launch( args );
    }
}