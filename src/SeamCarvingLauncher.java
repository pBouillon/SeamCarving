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
    final static int[]          NO_PROP = {-1, -1};   /* */

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
        int remainPercent = ((200 * remain) / total) / maxBareSize;
        char defaultChar = ' ';
        String icon = "==";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainPercent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainPercent * 2, bare.length());
        System.out.print("\r\t" + bareDone + bareRemain  + " " + remainPercent * 10 + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Parser launcher = new Parser() ;
        launcher.parse(args) ;

        long     begin   = System.currentTimeMillis() ;

        int[]    keep    = launcher.getKeep() ;   // get cols
        int[]    delete  = launcher.getDel()  ;   // get cols

        String[] file    = launcher.getFiles()  ; // get files as {source, output}

        boolean  simple  = launcher.isSimple()  ; // check requested version
        boolean  toggle  = launcher.isToggle()  ; // toggle grey values
        boolean  verbose = launcher.isVerbose() ; // check if verbose

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

        if (verbose) {
            System.out.println("Progress:") ;

            switch (magicNumber) {
                case PortableAnymap.P_PGM:
                    System.out.println("\tPGM values acquired") ;
                    break ;
                case PortableAnymap.P_PPM:
                    System.out.println("\tPPM values acquired") ;
                    break ;
            }
        }

        Graph   imgGraph ;
        int[][] interest ;
        int[]   shortestPath ;

        for (int i = 0; i < ROW_REMOVED; ++i) {
            if (verbose) progressPercentage(i, ROW_REMOVED - 1) ;

            switch (magicNumber) {
                case PortableAnymap.P_PGM :
                    interest = SeamCarving.interest (imgPGM, keep, delete) ;
                    imgGraph = SeamCarving.toGraph(interest) ;
                    if (!simple) {
                        shortestPath = SeamCarving.getDoublePath(interest) ;
                    } else {
                        shortestPath = SeamCarving.getShortestPath (imgGraph) ;
                    }
                    imgPGM = SeamCarving.resize (imgPGM, shortestPath) ;
                    break ;

                case PortableAnymap.P_PPM :
                    interest = SeamCarving.interest(imgPPM, keep, delete) ;
                    if (!simple) {
                        shortestPath = SeamCarving.getDoublePath(interest) ;
                    } else {
                        imgGraph = SeamCarving.toGraph(interest) ;
                        shortestPath = SeamCarving.getShortestPath(imgGraph) ;
                    }
                    imgPPM   = SeamCarving.resize(imgPPM, shortestPath)  ;
                    break ;
            }
        }
        if (verbose && !simple) System.out.println( "\t| Using Double Dijkstra") ;

        switch (magicNumber) {
            case PortableAnymap.P_PGM :
                if (toggle) {
                    imgPGM = SeamCarving.toggleppm(imgPGM) ;
                    System.out.println( "\t| Values correctly inverted") ;
                }
                SeamCarving.writepgm(imgPGM, file[OUTPUT]) ;
                break ;
            case PortableAnymap.P_PPM :
                SeamCarving.writeppm(imgPPM, file[OUTPUT]) ;
                break ;
        }

        if (verbose) {
            System.out.println(
                    "\t| Successfully removed " +
                            ROW_REMOVED + " columuns in " +
                            (System.currentTimeMillis() - begin) + " ms"
            ) ;
            System.out.println("\t| New image saved in: " + file[OUTPUT]) ;
        }
    }
}
