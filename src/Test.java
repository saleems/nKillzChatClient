import com.sun.javafx.scene.layout.region.BorderStyleConverter;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Pane;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;



/**
 * Created by Nasir on 12/21/2014.
 */
public class Test extends Application {

    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new VBox();
        root.setStyle("-fx-border-color: red");


        Rectangle rect1= new Rectangle(50,50);
        rect1.setFill(Color.BLUE);
        root.getChildren().add(rect1);

        Path path1 = new Path();
        path1.getElements().add(new MoveTo(20,20));
        path1.getElements().add(new CubicCurveTo(80,80,150,150,200,80));

        PathTransition transit1 = new PathTransition();
        transit1.setDuration(Duration.seconds(3));
        transit1.setAutoReverse(true);
        transit1.setPath(path1);
        transit1.setNode(rect1);
        transit1.setCycleCount(Timeline.INDEFINITE);

        Button button1 = new Button("Start animation");
        button1.resi
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (transit1.getStatus().equals(Animation.Status.STOPPED)){
                    transit1.play();
                    button1.setText("pause");
                }else if (transit1.getStatus().equals(Animation.Status.RUNNING)){
                    transit1.pause();
                    button1.setText("play");
                }else if (transit1.getStatus().equals(Animation.Status.PAUSED)){
                    transit1.playFrom(transit1.getCurrentTime());
                    button1.setText("pause");
                }
            }
        });
        root.getChildren().add(button1);

        Scene scene = new Scene(root,500,500);

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
