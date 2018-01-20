import graph.Graph;

/**
 * Seam Carving launcher
 *
 * @version 1.0
 */
public class SeamCarvingLauncher {
    private final static String PROG_NAME    = "SeamCarving" ;  /* prog name for -h */
    private final static int    ROW_REMOVED  = 50 ;             /* number of rows to be removed */

    private final static char   OPT_SYMBOL   = '-' ; /* */
    private final static char   OPT_COMPRESS = 'c' ; /* compression option */
    private final static char   OPT_DELETE   = 'd' ; /* property "delete" on pixels */
    private final static char   OPT_HELP     = 'h' ; /* displays help */
    private final static char   OPT_KEEP     = 'k' ;  /* property "keep" on pixels */
    private final static char   OPT_SIMPLE   = 's' ; /* chose method */
    private final static char   OPT_VERBOSE  = 'v' ; /* displays progress */

    private final static int    SOURCE  = 0 ; /* position of output file in arg list for -c */
    private final static int    OUTPUT  = 1 ; /* position of output file in arg list for -c */
    public  final static int[]  NO_PROP = {-1, -1}; /* */

    private boolean  simple  ; /* if using the simple algorithm instead of the double one */
    private boolean  verbose ; /* enable verbose mode */
    private String[] files   ; /* will contain String{source, dest} */
    private int[] col_k ; /* source for pixel to keep   */
    private int[] col_d ; /* source for pixel to delete */

    /**
     * Default constructor
     *
     * initialize everything with default values:
     *  - files are empty
     *  - boolean are false
     */
    private SeamCarvingLauncher() {
        col_k = new int[]{0, 0};
        col_d = new int[]{0, 0};
        files = new String[]{"", ""} ;
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
                "   " + OPT_SYMBOL + OPT_COMPRESS  + " <img> <out.pgm> ... compress an image to a pgm file\n" +
                "   " + OPT_SYMBOL + OPT_HELP      + " ................... displays help\n" +
                "   " + OPT_SYMBOL + OPT_DELETE    + " <begin> <end>...... delete pixel between those columns\n" +
                "   " + OPT_SYMBOL + OPT_KEEP      + " <begin> <end>...... keep pixel between those columns\n" +
                "   " + OPT_SYMBOL + OPT_SIMPLE    + " ................... use simple method instead of double (v2.0)\n" +
                "   " + OPT_SYMBOL + OPT_VERBOSE   + " ................... enable verbose mode" ;
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
        int file  = -1 ;
        int dim_d = -1 ;
        int dim_k = -1 ;

        for (String arg : args) {

            if (file > -1 && file < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("File expected", -1);
                }
                files[file++] = arg ;
            }

            else if (dim_k > -1 && dim_k < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("Column expected", -1) ;
                }
                try {
                    col_k[dim_k++] = Integer.parseInt(arg) ;
                } catch (NumberFormatException e) {
                    displayHelp("Column id should be a number", -1) ;
                }
            }

            else if (dim_d > -1 && dim_d < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("Column expected", -1) ;
                }
                try {
                    col_d[dim_d++] = Integer.parseInt(arg) ;
                } catch (NumberFormatException e) {
                    displayHelp("Column id should be a number", -1) ;
                }
            }

            else if (arg.charAt(0) == OPT_SYMBOL) {
                switch (arg.charAt(1)) {
                    case OPT_COMPRESS:
                        if (file < 0) { // if -c is not set
                            ++file ;
                        } else {
                            displayHelp("Duplicated option", -1) ;
                        }
                        break;
                    case OPT_DELETE:
                        if (dim_d < 0) { // if -k is not set
                            ++dim_d;
                        } else {
                            displayHelp("Duplicated option", -1) ;
                        }
                        break ;
                    case OPT_HELP:
                        displayHelp("Available options", 0) ;
                        break;
                    case OPT_KEEP:
                        if (dim_k < 0) { // if -k is not set
                            ++dim_k;
                        } else {
                            displayHelp("Duplicated option", -1) ;
                        }
                        break ;
                    case OPT_SIMPLE:
                        simple = true ;
                        break;
                    case OPT_VERBOSE:
                        verbose = true ;
                        break ;
                    default:
                        displayHelp("Missing parameters", -1) ;
                }
            }
        }

        if (dim_k > 0 && dim_k < 2 ||
                file > 0 && file < 2 ) {
            displayHelp("Missing arguments", -1) ;
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

    private int[] getKeep() {
        return col_k;
    }

    private int[] getDel() {
        return col_d ;
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

        int[]    keep    = launcher.getKeep() ; // get cols
        int[]    delete  = launcher.getDel()  ; // get cols
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
            System.out.print("Progression:\n\t0% ") ;
        }

        int state = 0 ;
        for (int i = 0; i < ROW_REMOVED; ++i) {
            if (verbose) {
                int progression = 100 * i / ROW_REMOVED ;
                if (progression > 75 && state < 3) {
                    System.out.print(" 75% ") ;
                    ++state ;
                }
                else if (progression > 50 && state < 2) {
                    System.out.print(" 50% ") ;
                    ++state ;
                }
                else if (progression > 25 && state < 1) {
                    System.out.print(" 25% ") ;
                    ++state ;
                }
                else if (progression % 4 == 0) {
                    System.out.print(".") ;
                }
            }

            interest = SeamCarving.interest(imgPixels, keep, delete) ;

            imgGraph = SeamCarving.tograph(interest)   ;                 // build graph from interest array
            shortestPath = SeamCarving.getShortestPath(imgGraph) ;       // evaluates shortest path from graph
            imgPixels    = SeamCarving.resize(imgPixels, shortestPath) ; // delete one column of imgPixels
        }

        SeamCarving.writepgm(imgPixels, file[OUTPUT]) ; // write the new image

        if (verbose) {
            System.out.println(" Done !") ;
            System.out.println("Successfully saved in " + file[OUTPUT]) ;
        }
    }
}
