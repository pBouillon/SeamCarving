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

    private static void progressPercentage(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 20;
        int remainProcent = ((200 * remain) / total) / maxBareSize;
        char defaultChar = ' ';
        String icon = "==";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent * 2, bare.length());
        System.out.print("\r\t" + bareDone + bareRemain  + " " + remainProcent * 10 + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Parser launcher = new Parser() ;
        launcher.parse(args) ; // check prog args

        long     begin   = System.currentTimeMillis() ;

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

        assert magicNumber != null ;
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

        if (verbose) System.out.println("Progress:") ;

        switch (magicNumber) {
            case PortableAnymap.P_PGM:
                System.out.println("\tPGM values acquired") ;
                break ;
            case PortableAnymap.P_PPM:
                System.out.println("\tPPM values acquired") ;
                break ;
        }

        Graph   imgGraph ;
        int[][] interest ;
        int[]   shortestPath ;

        for (int i = 0; i < ROW_REMOVED; ++i) {
            if (verbose) {
                progressPercentage(i, ROW_REMOVED - 1) ;
            }

            switch (magicNumber) {
                case PortableAnymap.P_PGM :
                    interest = SeamCarving.interest (imgPGM, keep, delete) ;
                    imgGraph = SeamCarving.toGraph(interest) ;              // build graph from interest array
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
            System.out.println("\t| Compression successful in " + (System.currentTimeMillis() - begin) + " ms") ;
            System.out.println("\t| New image saved in: " + file[OUTPUT]) ;
        }
    }
}
