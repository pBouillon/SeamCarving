import graph.Graph;

/**
 * Seam Carving launcher
 *
 * @version 1.0
 */
public class SeamCarvingLauncher {
    private final static String PROG_NAME    = "SeamCarving" ;  /* prog name for -h */
    private final static int    ROW_REMOVED  = 50 ;             /* number of rows to be removed */

    private final static char OPT_COMPRESS = 'c' ; /* compression option */
    private final static char OPT_HELP     = 'h' ; /* displays help */
    private final static char OPT_PROP     = 'p' ; /* apply pixel special properties */
    private final static char OPT_SIMPLE   = 's' ; /* chose method */
    private final static char OPT_VERBOSE  = 'v' ; /* displays progress */

    private final static int SOURCE = 0 ; /* position of output file in arg list for -c */
    private final static int OUTPUT = 1 ; /* position of output file in arg list for -c */

    private boolean  simple  ; /* if using the simple algorithm instead of the double one */
    private boolean  verbose ; /* enable verbose mode */
    private String   prop    ; /* source for pixel special properties*/
    private String[] files   ; /* will contain String{source, dest} */

    /**
     * Default constructor
     *
     * initialize everything with default values:
     *  - files are empty
     *  - boolean are false
     */
    private SeamCarvingLauncher() {
        files   = new String[]{"", ""} ;
        prop    = null ;
        simple  = false ;
        verbose = false ;
    }

    /**
     * Prints helper with a specific message
     *
     * @param msg message to include
     * @param ret returned code
     */
    private void displayHelp(String msg, int ret) {
        String helper_msg = "" +
                PROG_NAME +" : " + msg + "\n" +
                "   -" + OPT_COMPRESS  + " <img> <out.pgm> ... compress an image to a pgm file\n" +
                "   -" + OPT_HELP      + " ................... displays help\n" +
                "   -" + OPT_PROP      + " <filename>......... apply properties on pixel\n" +
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
        int file_args = -1 ; // count files
        boolean waiting_prop = false ;

        for (String arg : args) {
            if (waiting_prop) {
                if (arg.charAt(0) == '-') {
                    displayHelp ("Bad usage of property", -1) ;
                }
                prop = arg ;
                waiting_prop = false ;
            }
            if (arg.charAt(0) == '-') {
                switch (arg.charAt(1)) {
                    case OPT_COMPRESS :
                        if (file_args < 0) { // if -c is not set
                            file_args++ ;
                        }
                        else {
                            displayHelp("Duplicated option", -1) ;
                        }
                        break ;
                    case OPT_HELP :
                        displayHelp("Available options", 0) ;
                        break ;
                    case OPT_PROP :
                        waiting_prop = true ;
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
            else if (file_args >= 0){ // if -c is set
                if (file_args > 1) {  // if the user provided more than 2 files
                    displayHelp("Too many arguments", -1) ;
                }
                this.files[file_args] = arg ; // add file to args
                file_args++ ; // increment known files
            }
            else {
                displayHelp("Missing arguments", -1) ;
            }
        }

        if (file_args < 2) { // if at the end we have less than 2 files known
            displayHelp("Missing files arguments", -1) ;
        }
    }

    /**
     * Get the files the user provided
     *
     * @return String{source, dest}
     */
    private String[] getFiles() {
        return files ;
    }

    private String getPropFile() {
        return prop ;
    }

    /**
     * Get the chosed method
     *
     * @return simple
     */
    private boolean useSimple() {
        return simple ;
    }

    /**
     * Get if the user wants progression
     *
     * @return verbose
     */
    private boolean useVerbose() {
        return verbose ;
    }

    public static void main(String[] args) {
        SeamCarvingLauncher launcher = new SeamCarvingLauncher() ;
        launcher.parse(args) ; // check prog args

        String   prop    = launcher.getPropFile() ;
        String[] file    = launcher.getFiles()   ; // get files as {source, output}
        boolean  simple  = launcher.useSimple()  ; // check requested version
        boolean  verbose = launcher.useVerbose() ; // check if verbose

        if (!simple) { // coming in v2
            System.out.println ("Warning: Simple method used by default (version < 2.0)\n") ;
        }

        int[][] imgPixels ;
        if ((imgPixels  = SeamCarving.readpgmv2(file[SOURCE])) == null) { // check if the file is readable
            System.out.println("Unable to read the source") ;
            System.exit(-1) ;
        }

        if (verbose) {
            System.out.println("PGM values acquired") ;
        }

        Graph   imgGraph ;
        int[][] interest ;
        int[]   shortestPath ;

        if (verbose) {
            System.out.println("Beginning of the resize") ;
            System.out.print("Progression:\n\t0% ... ") ;
        }

        int[][] restrictions = null ;
        if (prop != null) {
            restrictions = SeamCarving.readvalues(prop) ;
        }

        int state = 0 ;
        for (int i = 0; i < ROW_REMOVED; ++i) {
            if (verbose) {
                int progression = 100 * i / ROW_REMOVED ;
                if (progression > 75 && state < 3) {
                    System.out.print(" 75% ... ") ;
                    ++state ;
                }
                else if (progression > 50 && state < 2) {
                    System.out.print("50% ... ") ;
                    ++state ;
                }
                else if (progression > 25 && state < 1) {
                    System.out.print("25% ... ") ;
                    ++state ;
                }
            }

            interest = (restrictions == null) ? SeamCarving.interest(imgPixels)
                                              : SeamCarving.interest(imgPixels, restrictions);

            imgGraph = SeamCarving.tograph(interest)   ;                 // build graph from interest array
            shortestPath = SeamCarving.getShortestPath(imgGraph) ;       // evaluates shortest path from graph
            imgPixels    = SeamCarving.resize(imgPixels, shortestPath) ; // delete one column of imgPixels
        }

        SeamCarving.writepgm(imgPixels, file[OUTPUT]) ; // write the new image

        if (verbose) {
            System.out.println("Done !") ;
            System.out.println("Successfully saved in " + file[OUTPUT]) ;
        }
    }
}
