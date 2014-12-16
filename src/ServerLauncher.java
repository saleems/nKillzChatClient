import javafx.application.Application;
import javafx.application.Platform;
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
public class ServerLauncher extends Application {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(ServerLauncher.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Pane page = (Pane) FXMLLoader.load(ServerLauncher.class.getResource("serverGui.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Chat Server");
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
