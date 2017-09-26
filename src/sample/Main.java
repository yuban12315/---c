package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public void init(Stage primaryStage){
        ImageView imageView=new ImageView();
        HBox mHbox = new HBox(10);
        mHbox.getChildren().add(imageView);


        Task<Void> progressTask = new Task<Void>(){

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Succeeded");
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Failed");
            }

            @Override
            protected Void call() throws Exception {
                for(int i = 0; i <100;){
                    Thread.sleep(20);
                    BufferedImage image=getScreenshot();
                    WritableImage image1=SwingFXUtils.toFXImage(image,null);

                    imageView.setFitWidth(640);
                    imageView.setFitHeight(320);
                    imageView.setPreserveRatio(true);
                    imageView.setImage(image1);
                }
                updateMessage("Finish");
                return null;
            }
        };

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView);
        Scene scene = new Scene(root);

        primaryStage.setTitle("The lesson of Task");
        primaryStage.setScene(scene);

        new Thread(progressTask).start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client client = new Client();
        client.start();
        primaryStage.setTitle("Monitor-Client");
        //primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    private BufferedImage getScreenshot() throws AWTException, IOException {
        final Robot robot = new Robot();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        BufferedImage screenshot = robot.createScreenCapture(new Rectangle(0, 0, width, height));
        //File outputfile = new File("saved.png");
        //ImageIO.write(screenshot, "png", outputfile);
        return screenshot;
    }


}
