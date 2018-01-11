import graph.Graph;
import org.apache.commons.cli.*;

/**
 * Entrypoint for SeamCarving
 */
public class SeamCarvingLauncher {

    private final String DEFAULT_OUT  = "out.pgm" ;
    private final String PROG_NAME    = "SeamCarvingLauncher" ;

    private final String OPT_COMPRESS = "compress" ;
    private final String OPT_HELP     = "help" ;
    private final String OPT_SIMPLE   = "simple"  ;
    private final String OPT_VERBOSE  = "verbose" ;


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

        Option compress = Option.builder(OPT_COMPRESS.charAt(0) + "")
            .longOpt(OPT_COMPRESS)
            .desc("Compress image source into dest.")
            .hasArgs()
            .valueSeparator(' ')
            .argName("source> <dest")
            .build() ;
        scOptions.addOption(compress) ;

        Option help = Option.builder(OPT_HELP.charAt(0) + "")
                .longOpt(OPT_HELP)
                .desc("Displays help")
                .build() ;
        scOptions.addOption(help) ;

        Option simple = Option.builder(OPT_SIMPLE.charAt(0) + "")
                .longOpt(OPT_SIMPLE)
                .desc("Uses the 'simple' method.")
                .build() ;
        scOptions.addOption(simple) ;

        Option verbose = Option.builder(OPT_VERBOSE.charAt(0) + "")
            .longOpt(OPT_VERBOSE)
            .desc("Shows program's progression")
            .build() ;
        scOptions.addOption(verbose) ;

        return scOptions ;
    }

    private void displayHelp (String msg, Options opts, int ret) {
        HelpFormatter formatter  = new HelpFormatter() ;
        System.out.println(msg) ;
        formatter.printHelp(PROG_NAME, opts) ;
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
        CommandLineParser parser = new DefaultParser() ;
        Options scOptions = buildOptions() ;

        try {
            CommandLine cmd = parser.parse(scOptions, args) ;

            simple  = cmd.hasOption(OPT_SIMPLE) ;
            verbose = cmd.hasOption(OPT_VERBOSE) ;

            if (cmd.getArgs().length == 0 || cmd.hasOption(OPT_HELP)) {
                displayHelp (
                        "-- HELPER --",
                        scOptions,
                        0
                ) ;
            }

            if (cmd.hasOption(OPT_COMPRESS)) {
                switch (cmd.getOptionValues(OPT_COMPRESS).length) {
                    case 1 :
                        files = new String[] {
                                cmd.getOptionValues(OPT_COMPRESS)[0],
                                DEFAULT_OUT
                        };
                        break ;
                    case 2:
                        files = cmd.getOptionValues(OPT_COMPRESS) ;
                        break ;
                    default:
                        displayHelp (
                                "Incorrect args for -c, --compress",
                                scOptions,
                                1
                        ) ;
                }
            } else {
                displayHelp (
                        "Missing required argument: -c, -compress",
                        scOptions,
                        1
                ) ;
            }

        } catch (ParseException e) {
            displayHelp (
                    e.getMessage(),
                    scOptions,
                    -1
            ) ;
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
