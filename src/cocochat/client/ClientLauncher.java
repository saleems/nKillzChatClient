package cocochat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sun.tools.jar.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Sadruddin on 12/15/2014.
 */
public class ClientLauncher extends Application {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(ClientLauncher.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Pane page = (Pane) FXMLLoader.load(ClientLauncher.class.getResource("gui.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Chat Client");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop(){
        System.exit(0);
    }
}
