import graph.Graph;

/**
 * Entrypoint for SeamCarving
 */
public class SeamCarvingLauncher {
    private final static String PROG_NAME    = "SeamCarving" ;
    private final static int    ROW_REMOVED  = 50 ;

    private final static char OPT_COMPRESS = 'c' ;
    private final static char OPT_HELP     = 'h' ;
    private final static char OPT_SIMPLE   = 's'  ;
    private final static char OPT_VERBOSE  = 'v' ;

    private final static int ENTRY  = 0 ;
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

    private void displayHelp (String msg, int ret) {
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

    public static void main(String[] args) {
        SeamCarvingLauncher launcher = new SeamCarvingLauncher() ;
        launcher.parse(args) ;

        String[] file    = launcher.getFiles()   ;
        boolean  simple  = launcher.useSimple()  ;
        boolean  verbose = launcher.useVerbose() ;

        int[][] maping   = SeamCarving.readpgm(file[ENTRY]) ;

        assert maping != null ;
        int[][] interest = SeamCarving.interest(maping) ;

        Graph imgGraph = SeamCarving.tograph(interest) ;
        int[] shortestPath = SeamCarving.getShortestPath(imgGraph) ;

        int img[][] = null ;
        for (int i = 0; i < ROW_REMOVED; ++i) {
            img = SeamCarving.run(maping, shortestPath) ;
        }
        SeamCarving.writepgm(img, file[OUTPUT]) ;
    }
}
