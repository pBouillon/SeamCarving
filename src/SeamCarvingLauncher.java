import org.apache.commons.cli.*;

/**
 * Entrypoint for SeamCarving
 */
public class SeamCarvingLauncher {

    private final String COMPRESS_OPT = "compress" ;
    private final String HELP_OPT     = "help" ;
    private final String SIMPLE_OPT   = "simple"  ;
    private final String VERBOSE_OPT  = "verbose" ;

    private boolean  simple  ;
    private boolean  verbose ; /* if using the simple algorithm instead of the double one */
    private String[] files   ; /* will contain String{source, dest} */

    /**
     * Default constructor
     *
     * initialize everything with default values:
     *  - files are empty
     *  - boolean are false
     */
    private SeamCarvingLauncher() {
        files   = null  ;
        simple  = false ;
        verbose = false ;
    }

    /**
     * Create all SeamCarving options
     *
     * Default options are:
     *  - compress
     *  - help
     *  - simple
     *  - verbose
     *
     * @return Options : an Option collection
     */
    private Options buildOptions() {
        Options scOptions = new Options() ;

        Option compress = Option.builder(COMPRESS_OPT.charAt(0) + "")
            .longOpt(COMPRESS_OPT)
            .desc("Compress image source into dest")
            .numberOfArgs(2)
            .valueSeparator(' ')
            .argName("source> <dest")
            .required(true)
            .build() ;
        scOptions.addOption(compress) ;

        Option help = Option.builder(HELP_OPT.charAt(0) + "")
                .longOpt(HELP_OPT)
                .desc("Displays help")
                .build() ;
        scOptions.addOption(help) ;

        Option simple = Option.builder(SIMPLE_OPT.charAt(0) + "")
                .longOpt(SIMPLE_OPT)
                .desc("Uses the 'simple' method instead of the double")
                .build() ;
        scOptions.addOption(simple) ;

        Option verbose = Option.builder(VERBOSE_OPT.charAt(0) + "")
            .longOpt(VERBOSE_OPT)
            .desc("Shows program's progression")
            .build() ;
        scOptions.addOption(verbose) ;

        return scOptions ;
    }

    /**
     * @TODO: handle more than 2 arg for dest; -h should not return 1
     *
     * Parse all arguments
     *
     * Displays help if required argument is missing
     * Update booleans if set
     * Fill files
     *
     * @param args : program arguments
     */
    private void parse(String[] args) {
        CommandLineParser parser = new DefaultParser() ;
        HelpFormatter formatter  = new HelpFormatter() ;

        Options scOptions = buildOptions() ;
        CommandLine cmd ;

        try {
            cmd = parser.parse(scOptions, args) ;

            if (cmd.hasOption(HELP_OPT)) {
                formatter.printHelp("SeamCarvingLauncher", scOptions) ;
                System.exit(0) ;
            }

            if (cmd.hasOption(VERBOSE_OPT)) {
                verbose = true ;
            }

            if (cmd.hasOption(SIMPLE_OPT)) {
                simple = true ;
            }

            if (cmd.hasOption(COMPRESS_OPT)) {
                files = cmd.getOptionValues(COMPRESS_OPT) ;
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage()) ;
            formatter.printHelp("SeamCarvingLauncher", scOptions) ;
            System.exit(1) ;
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
        if (verbose) {
            System.out.println("Arg parsing done") ;
            System.out.println("Converting source to pixel array") ;
        }
        int[][] maping   = SeamCarving.readpgm(file[0]) ;

        if (verbose) {
            System.out.println("Calculating pixel interest array") ;
        }
        int[][] interest = SeamCarving.interest(maping) ;

        if (verbose) {
            System.out.println("Converting interest array to graph") ;
        }
        Graph imgGraph = SeamCarving.tograph(interest) ;
    }
}
