import graph.Graph;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.IOException;

/**
 * Entrypoint for SeamCarving
 *
 * @version 1.0
 */
public class SeamCarvingLauncher {
    private final static String PROG_NAME    = "SeamCarving" ;
    private final static int    ROW_REMOVED  = 50 ;

    private final static char OPT_COMPRESS = 'c' ;
    private final static char OPT_HELP     = 'h' ;
    private final static char OPT_SIMPLE   = 's'  ;
    private final static char OPT_VERBOSE  = 'v' ;

    private final static int SOURCE = 0 ;
    private final static int OUTPUT = 1 ;

    private boolean  simple  ; /* if using the simple algorithm instead of the double one */
    private boolean  verbose ;
    private String[] files   ; /* will contain String{source, dest} */

    /**
     * Default constructor
     *
     * initialize everything with default values:
     *  - files are empty
     *  - boolean are false
     */
    private SeamCarvingLauncher() {
        files   = new String[]{"", ""};
        simple  = false ;
        verbose = false ;
    }

    public void displayHelp (String msg, int ret) {
        String helper_msg = "" +
                PROG_NAME +" : " + msg + "\n" +
                "   -" + OPT_COMPRESS  + " <img> <out.pgm> ... compress an image to a pgm file\n" +
                "   -" + OPT_HELP      + " ................... displays help\n" +
                "   -" + OPT_SIMPLE    + " ................... use simple method instead of double (v2.0)\n" +
                "   -" + OPT_VERBOSE   + " ................... enable verbose mode" ;
        System.out.println(helper_msg) ;
        System.exit(ret) ;
    }

    /**
     * Parse all arguments
     *
     * Displays help if required argument is missing
     * Update booleans if set
     * Fill files
     *
     * @param args : program arguments
     */
    private void parse(String[] args) {
        int file_args = -1 ;
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                    case OPT_COMPRESS :
                        if (file_args < 0) {
                            file_args++ ;
                        }
                        else {
                            displayHelp("Duplicated option", -1) ;
                        }
                        break ;
                    case OPT_HELP :
                        displayHelp("Available options", 0) ;
                        break ;
                    case OPT_SIMPLE :
                        this.simple = true ;
                        break ;
                    case OPT_VERBOSE :
                        this.verbose = true ;
                        break ;
                    default:
                        displayHelp("Missing parameters", -1) ;
                }
            }
            else if (file_args >= 0){
                if (file_args > 1) {
                    displayHelp("Too many arguments", -1) ;
                }
                this.files[file_args] = arg ;
                file_args++ ;
            }
            else {
                displayHelp("Missing arguments", -1) ;
            }
        }

        if (file_args < 2) {
            displayHelp("Missing files arguments", -1) ;
        }
    }

    /**
     * @return String{source, dest}
     */
    private String[] getFiles() {
        return files ;
    }

    /**
     * @return simple
     */
    private boolean useSimple() {
        return simple ;
    }

    /**
     * @return verbose
     */
    private boolean useVerbose() {
        return verbose ;
    }


    /**
     * @param tab
     */
    public static void showTab(int[][] tab) {
        for (int[] x : tab) {
            for (int y : x) {
                System.out.print(y +" ") ;
            }
            System.out.println() ;
        }
        System.out.println() ;
    }

    public static void main(String[] args) {
        SeamCarvingLauncher launcher = new SeamCarvingLauncher() ;
        launcher.parse(args) ; // check prog args

        String[] file    = launcher.getFiles()   ; // get files as {source, output}
        boolean  simple  = launcher.useSimple()  ; // check requested version
        boolean  verbose = launcher.useVerbose() ; // check if verbose

        if (!simple) {
            System.out.println ("Simple method used by default (version < 2.0)\n") ;
        }

        int[][] imgPx = SeamCarving.readpgmv2(file[SOURCE]) ;
        if (imgPx == null) {
            System.out.println("Unable to read the source") ;
            System.exit(-1) ;
        }
        if (verbose){
            System.out.println("PGM values acquired") ;
//            showTab(imgPx) ;
        }

        Graph imgGraph ;
        int[][] interest ;
        int[]   shortestPath ;

        for (int i = 0; i < ROW_REMOVED; ++i) {
            interest = SeamCarving.interest(imgPx) ; // evaluates interest of each pixel from imgPx
            if (verbose){
                System.out.println("Interest tab evaluated") ;
//                showTab(interest) ;
            }

            imgGraph = SeamCarving.tograph(interest) ;  // build graph from interest array
            imgGraph.writeFile("test_sc_alg.dot");

            shortestPath = SeamCarving.getShortestPath(imgGraph) ; // evaluates shortest path from graph
            if (verbose){
                System.out.println("Shortest path found") ;
//                for (int vertice : shortestPath) {
//                    System.out.print(vertice+" ") ;
//                }
//                System.out.println("\n") ;
            }

            imgPx = SeamCarving.run(imgPx, shortestPath) ;
            if (verbose) {
                System.out.println("New PGM values calculated") ;
//                showTab(imgPx) ;
            }
        }

        SeamCarving.writepgm(imgPx, file[OUTPUT]) ; // write the new image
        if (verbose) {
            System.out.println("Successfully saved in " + file[OUTPUT]) ;
        }
    }
}
