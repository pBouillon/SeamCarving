class Parser {

    private final static String PROG_NAME = "SeamCarving" ;  /* prog name for -h */

    private final int EXIT_FAILURE = -1 ; /* */
    private final int EXIT_SUCCESS =  0 ; /* */
    private final static int[]  NO_PROP = {-1, -1} ; /* */

    private final static char   OPT_SYMBOL   = '-' ; /* */
    private final static char   OPT_COMPRESS = 'c' ; /* compression option */
    private final static char   OPT_DELETE   = 'd' ; /* property "delete" on pixels */
    private final static char   OPT_GREY     = 'g' ; /* save ppm as pgm */
    private final static char   OPT_HELP     = 'h' ; /* displays help */
    private final static char   OPT_INCR     = 'i' ; /* increase lines */
    private final static char   OPT_KEEP     = 'k' ; /* property "keep" on pixels */
    private final static char   OPT_LONG     = 'l' ; /* chose method */
    private final static char   OPT_TOGGLE   = 't' ; /* toggle grey vals */
    private final static char   OPT_VERBOSE  = 'v' ; /* displays progress */
    private final static String OPT_LINES    = "lines" ; /* alter lines instead of meth */

    private boolean  grey ;      /* save ppm as pgm */
    private boolean  increase  ; /* increase img */
    private boolean  long_meth ; /* if using the long_meth algorithm instead of the double one */
    private boolean  lines   ;   /* alter lines instead of meth */
    private boolean  toggle  ; /* allow to toggle grey values */
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
        col_k = NO_PROP ;
        col_d = NO_PROP ;
        files = new String[]{"", ""} ;
        long_meth = false ;
        toggle  = false ;
        verbose = false ;
        grey = false ;
        lines   = false ;
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
                "   " + OPT_SYMBOL + OPT_COMPRESS  + " <img> <out> .... compress an image to a pgm file\n" +
                "   " + OPT_SYMBOL + OPT_GREY      + " ................ get greyscale for ppm and save as pgm\n" +
                "   " + OPT_SYMBOL + OPT_HELP      + " ................ displays help\n" +
                "   " + OPT_SYMBOL + OPT_DELETE    + " <begin> <end> .. delete pixel between those columns\n" +
                "   " + OPT_SYMBOL + OPT_INCR      + " ................ increase lines/rows instead of cropping\n" +
                "   " + OPT_SYMBOL + OPT_KEEP      + " <begin> <end> .. keep pixel between those columns\n" +
                "   " + OPT_SYMBOL + OPT_LONG      + " ................ use long method instead of simple\n" +
                "   " + OPT_SYMBOL + OPT_LINES     +     " ............ crop lines instead of columns\n" +
                "   " + OPT_SYMBOL + OPT_TOGGLE    + " ................ toggle values (color inversion)\n" +
                "   " + OPT_SYMBOL + OPT_VERBOSE   + " ................ enable verbose mode" ;
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

        if (args.length == 0) {
            displayHelp("Args required, see -h", EXIT_SUCCESS) ;
        }

        for (String arg : args) {
            if (arg.equals(OPT_SYMBOL + OPT_LINES)) {
                lines = true ;
                continue ;
            }

            if (file > -1 && file < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("File expected", EXIT_FAILURE) ;
                }
                files[file++] = arg ;
            }

            else if (dim_k > -1 && dim_k < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("Column expected", EXIT_FAILURE) ;
                }
                try {
                    col_k[dim_k++] = Integer.parseInt(arg) ;
                } catch (NumberFormatException e) {
                    displayHelp("Column id should be a number", EXIT_FAILURE) ;
                }
            }

            else if (dim_d > -1 && dim_d < 2) {
                if (arg.charAt(0) == OPT_SYMBOL) {
                    displayHelp("Column expected", EXIT_FAILURE) ;
                }
                try {
                    col_d[dim_d++] = Integer.parseInt(arg) ;
                } catch (NumberFormatException e) {
                    displayHelp("Column id should be a number", EXIT_FAILURE) ;
                }
            }

            else if (arg.charAt(0) == OPT_SYMBOL) {
                switch (arg.charAt(1)) {
                    case OPT_COMPRESS:
                        if (file < 0) { // if -c is not set
                            ++file ;
                        } else {
                            displayHelp("Duplicated option", EXIT_FAILURE) ;
                        }
                        break;

                    case OPT_DELETE:
                        if (dim_d < 0) { // if -k is not set
                            ++dim_d ;
                        } else {
                            displayHelp("Duplicated option", EXIT_FAILURE) ;
                        }
                        break ;

                    case OPT_HELP:
                        displayHelp("Available options", EXIT_SUCCESS) ;
                        break;

                    case OPT_KEEP:
                        if (dim_k < 0) { // if -k is not set
                            ++dim_k ;
                        } else {
                            displayHelp("Duplicated option", EXIT_FAILURE) ;
                        }
                        break ;

                    case OPT_LONG:
                        long_meth = true ;
                        break;

                    case OPT_TOGGLE:
                        toggle = true ;
                        break;

                    case OPT_GREY:
                        grey = true ;
                        break;

                    case OPT_INCR:
                        increase = true ;
                        break;

                    case OPT_VERBOSE:
                        verbose = true ;
                        break ;

                    default:
                        displayHelp("Missing parameters", EXIT_FAILURE) ;
                }
            }
        }

        if (dim_k > 0 && dim_k < 2 ||
                file > 0 && file < 2 ) {
            displayHelp("Missing arguments", EXIT_FAILURE) ;
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
     * @return long_meth
     */
    boolean isLong_meth() {
        return long_meth;
    }

    /**
     *
     */
    boolean isToggle() {return toggle ;}

    /**
     *
     */
    boolean isLines() {return lines ;}

    /**
     * Get if the user wants progression
     *
     * @return verbose
     */
    boolean isVerbose() {
        return verbose ;
    }

    /**
     *
     */
    public boolean isGrey() {
        return grey;
    }

    /**
     *
     */
    public boolean isIncrease() {
        return increase ;
    }
}
