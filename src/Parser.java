public class Parser {

    private final static String PROG_NAME    = "SeamCarving" ;  /* prog name for -h */

    private final static char   OPT_SYMBOL   = '-' ; /* */
    private final static char   OPT_COMPRESS = 'c' ; /* compression option */
    private final static char   OPT_DELETE   = 'd' ; /* property "delete" on pixels */
    private final static char   OPT_HELP     = 'h' ; /* displays help */
    private final static char   OPT_KEEP     = 'k' ;  /* property "keep" on pixels */
    private final static char   OPT_SIMPLE   = 's' ; /* chose method */
    private final static char   OPT_VERBOSE  = 'v' ; /* displays progress */

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
    Parser() {
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
    void displayHelp(String msg, int ret) {
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
    void parse(String[] args) {
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
    String[] getFiles() {
        return files ;
    }

    int[] getKeep() {
        return col_k;
    }

    int[] getDel() {
        return col_d ;
    }

    /**
     * Get the chosed method
     *
     * @return simple
     */
    boolean useSimple() {
        return simple ;
    }

    /**
     * Get if the user wants progression
     *
     * @return verbose
     */
    boolean useVerbose() {
        return verbose ;
    }
}
