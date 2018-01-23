import graph.Graph;

/**
 * Seam Carving launcher
 *
 * @version 1.0
 */
public class SeamCarvingLauncher {
    private final static int    ROW_REMOVED  = 50 ; /* number of rows to be removed */

    private final static int    SOURCE  = 0 ; /* position of output file in arg list for -c */
    private final static int    OUTPUT  = 1 ; /* position of output file in arg list for -c */
    final static int[]  NO_PROP = {-1, -1};   /* */

    private static void exitSeamCarving (String reason) {
        exitSeamCarving (reason, -1) ;
    }

    private static void exitSeamCarving(String reason, int errcode) {
        System.out.println (reason) ;
        System.exit (errcode) ;
    }


    public static void main(String[] args) {
        Parser launcher = new Parser() ;
        launcher.parse(args) ; // check prog args

        int[]    keep    = launcher.getKeep() ; // get cols
        int[]    delete  = launcher.getDel()  ; // get cols
        String[] file    = launcher.getFiles()   ; // get files as {source, output}
        boolean  simple  = launcher.useSimple()  ; // check requested version
        boolean  verbose = launcher.useVerbose() ; // check if verbose

        if (!simple) { // coming in v2
            System.out.println ("Warning: Simple method used by default (version < 2.0)\n") ;
        }

        String  magicNumber = SeamCarving.readFileType(file[SOURCE]) ;
        if (magicNumber == null) {
            exitSeamCarving ("Unable to read file format") ;
        }

        int[][]   imgPGM = null ;
        int[][][] imgPPM = null ;

        if (magicNumber.contains(PortableAnymap.P_PGM)) {
            if ((imgPGM  = SeamCarving.readPGM(file[SOURCE])) == null) {
                exitSeamCarving ("Unable to read the PGM file") ;
            }
        }
        else if (magicNumber.contains(PortableAnymap.P_PPM)) {
            if ((imgPPM  = SeamCarving.readPPM(file[SOURCE])) == null) {
                exitSeamCarving ("Unable to read the PPM file") ;
            }
        }
        else {
            exitSeamCarving ("Unable read format: " + magicNumber) ;
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

            switch (magicNumber) {
                case PortableAnymap.P_PGM :
                    interest = SeamCarving.interest (imgPGM, keep, delete) ;
                    imgGraph = SeamCarving.toGraph(interest) ;             // build graph from interest array
                    shortestPath = SeamCarving.getShortestPath (imgGraph) ; // evaluates shortest path from graph
                    imgPGM = SeamCarving.resize (imgPGM, shortestPath) ;    // delete one column of imgPixels
                    break ;

                case PortableAnymap.P_PPM :
                    interest = SeamCarving.interest(imgPPM, keep, delete) ;
                    imgGraph = SeamCarving.toGraph(interest) ;             // build graph from interest array
                    shortestPath = SeamCarving.getShortestPath(imgGraph) ; // evaluates shortest path from graph
                    imgPPM   = SeamCarving.resize(imgPPM, shortestPath)  ; // delete one column of imgPixels
                    break ;
            }
        }

        switch (magicNumber) {
            case PortableAnymap.P_PGM :
                SeamCarving.writepgm(imgPGM, file[OUTPUT]) ;
                break ;
            case PortableAnymap.P_PPM :
                SeamCarving.writeppm(imgPPM, file[OUTPUT]) ;
                break ;
        }

        if (verbose) {
            System.out.println(" Done !") ;
            System.out.println("Successfully saved in " + file[OUTPUT]) ;
        }
    }
}
