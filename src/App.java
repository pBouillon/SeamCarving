import graph.Graph;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class App extends Application {
    private static final int ROW_REMOVED = 50;
    private final String APP_TITLE = "Seam Carving Application" ;
    private final int APP_WIDTH  = 700 ;
    private final int APP_HEIGHT = 450 ;

    static String source ;

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
                    source = selectedFile.getPath() ;
                    System.out.println("Chosen file: " + selectedFile.getPath());
                }
            });
            Button run_sc = new Button("Resize") ;
            run_sc.setPrefWidth(155);
            run_sc.setOnAction(e -> {

                System.out.println("Processing...");
                resize();
                System.out.println("Done");
            });

        but_pan.getChildren().add(chose_img) ;
        but_pan.getChildren().add(run_sc) ;

        BorderPane root = new BorderPane() ;
        root.setLeft(but_pan);
        primaryStage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        primaryStage.show();
    }

    static void resize() {
        String magicNumber = SeamCarving.readFileType(source) ;
        if (magicNumber == null) {
            // TODO ERR MSG
        }

        int[][]   imgPGM = null ;
        int[][][] imgPPM = null ;

        assert magicNumber != null ;
        if (magicNumber.contains(PortableAnymap.P_PGM)) {
            if ((imgPGM  = SeamCarving.readPGM(source)) == null) {
                // TODO ERR MSG
            }
        }
        else if (magicNumber.contains(PortableAnymap.P_PPM)) {
            if ((imgPPM  = SeamCarving.readPPM(source)) == null) {
                // TODO ERR MSG
            }
        }
        else {
            // TODO ERR MSG
        }

        Graph imgGraph ;
        int[][] interest ;
        int[]   shortestPath ;

        for (int i = 0; i < ROW_REMOVED; ++i) {
            switch (magicNumber) {
                case PortableAnymap.P_PGM :
                    interest = SeamCarving.interest (
                            imgPGM,
                            SeamCarvingLauncher.NO_PROP,
                            SeamCarvingLauncher.NO_PROP
                    ) ;
                    imgGraph = SeamCarving.toGraph(interest) ;              // build graph from interest array
                    shortestPath = SeamCarving.getShortestPath (imgGraph) ; // evaluates shortest path from graph
                    imgPGM = SeamCarving.resize (imgPGM, shortestPath) ;    // delete one column of imgPixels
                    break ;

                case PortableAnymap.P_PPM :
                    interest = SeamCarving.interest(
                            imgPPM,
                            SeamCarvingLauncher.NO_PROP,
                            SeamCarvingLauncher.NO_PROP
                    ) ;
                    imgGraph = SeamCarving.toGraph(interest) ;             // build graph from interest array
                    shortestPath = SeamCarving.getShortestPath(imgGraph) ; // evaluates shortest path from graph
                    imgPPM   = SeamCarving.resize(imgPPM, shortestPath)  ; // delete one column of imgPixels
                    break ;
            }
        }

        switch (magicNumber) {
            case PortableAnymap.P_PGM :
                SeamCarving.writepgm(imgPGM, "./out_fx.pgm") ;
                break ;
            case PortableAnymap.P_PPM :
                SeamCarving.writeppm(imgPPM, "./out_fx.ppm") ;
                break ;
        }
    }

    public static void main(String[] args) {
        launch(args) ;
    }
}
