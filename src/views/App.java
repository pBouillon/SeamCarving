package views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {
    private final String APP_TITLE = "Seam Carving Application" ;
    private final int APP_WIDTH  = 700 ;
    private final int APP_HEIGHT = 450 ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(APP_TITLE) ;
        primaryStage.setResizable(false) ;

        VBox but_pan = new VBox(15) ;
        but_pan.setPadding(new Insets(150, 10, 0, 10));
        but_pan.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-insets: 5;" +
                "-fx-border-color: grey;");
        but_pan.setPrefWidth(175);
            Button chose_img = new Button("Pick image") ;
            chose_img.setPrefWidth(155);
            chose_img.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PGM image", "*.pgm"),
                        new FileChooser.ExtensionFilter("PPM image", "*.ppm")) ;
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    System.out.println("Chosen file: " + selectedFile.getPath());
                }
            });
            Button run_sc = new Button("Resize") ;
            run_sc.setPrefWidth(155);

        but_pan.getChildren().add(chose_img) ;
        but_pan.getChildren().add(run_sc) ;

        BorderPane root = new BorderPane() ;
        root.setLeft(but_pan);
        primaryStage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args) ;
    }
}
