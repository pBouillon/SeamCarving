package seamcarving;

import static seamcarving.Graph.*;
import static seamcarving.IO.*;
import static seamcarving.Resize.resize;

/**
 * Seam Carving methods
 *
 * @version 1.0
 */
public class SeamCarving {

    static final int RGB = 3 ;

    private final static int    ROW_REMOVED  = 50 ; /* number of rows to be removed */
    private final static int    SOURCE  = 0 ; /* position of output file in arg list for -c */
    private final static int    OUTPUT  = 1 ; /* position of output file in arg list for -c */

    private static int[]    keep    ;   // get cols
    private static int[]    delete  ;   // get cols

    private static boolean long_meth ; // check requested version

    private static graph.Graph imgGraph ;
    private static int[][] interest ;
    private static int[]   shortestPath ;

    private static int[][]   imgPGM ;
    private static int[][][] imgPPM ;

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

    public static void resizeImage (
            int[] _keep,
            int[] _del,
            String[] _files,
            boolean _long_meth,
            boolean _toggle,
            boolean _lines,
            boolean _verb ) {
        keep    = _keep   ;
        delete  = _del    ;
        long_meth = _long_meth  ;

        long begin = System.currentTimeMillis() ;

        String  magicNumber = readFileType(_files[SOURCE]) ;
        if (magicNumber == null) exitSeamCarving ("Unable to read file format") ;

        assert magicNumber != null ;
        if (magicNumber.contains(PortableAnymap.P_PGM)) {
            imgPGM  = readPGM(_files[SOURCE]) ;
            if (imgPGM == null) exitSeamCarving ("Unable to read the PGM file") ;
        }
        else if (magicNumber.contains(PortableAnymap.P_PPM)) {
            imgPPM  = readPPM(_files[SOURCE]) ;
            if (imgPPM == null) exitSeamCarving ("Unable to read the PPM file") ;
        }
        else {
            exitSeamCarving ("Unable read format: " + magicNumber) ;
        }

        if (_verb) {
            String msg = "Progress " ;
            if (long_meth)  msg += "(Using double Dijkstra)" ;
            System.out.println (msg + ": ") ;
        }

        // swapping img to cut lines
        if (_lines) swapAll() ;

        for (int i = 0; i < ROW_REMOVED; ++i) {
            if (_verb) progressPercentage(i, ROW_REMOVED - 1) ;

            assert magicNumber != null;
            switch (magicNumber) {
                case PortableAnymap.P_PGM :
                    imgPGM = resizePGM (imgPGM) ;
                    break ;
                case PortableAnymap.P_PPM :
                    imgPPM = resizePPM(imgPPM) ;
                    break ;
            }
        }

        if (_verb && long_meth) System.out.println( "\t| Using Double Dijkstra") ;

        if (_verb && _toggle && imgPGM != null) {
            imgPGM = toggleppm (imgPGM) ;
            System.out.println( "\t| Values correctly inverted") ;
        }

        if (_verb && _lines) {
            System.out.println( "\t| Lines used") ;
        }

        if (_verb) {
            System.out.println(
                    "\t| Successfully removed " +
                            ROW_REMOVED + " columuns in " +
                            (System.currentTimeMillis() - begin) + " ms"
            ) ;
            System.out.println("\t| New image saved in: " + _files[OUTPUT]) ;
        }

        // swapping back img to cut lines
        if (_lines) swapAll() ;

        assert magicNumber != null;
        switch (magicNumber) {
            case PortableAnymap.P_PGM :
                writepgm(imgPGM, _files[OUTPUT]) ;
                break ;
            case PortableAnymap.P_PPM :
                writeppm(imgPPM, _files[OUTPUT]);
                break ;
        }
    }

    private static int[][][] resizePPM(int[][][] imgPPM) {
        interest = Interest.ppmInterest(imgPPM, keep, delete) ;
        if (long_meth) {
            shortestPath = getDoublePath(interest) ;
        } else {
            imgGraph = toGraph(interest) ;
            shortestPath = getShortestPath(imgGraph) ;
        }
        return resize (imgPPM, shortestPath)  ;
    }

    private static int[][] resizePGM(int[][] imgPGM) {
        interest = Interest.pgmInterest (imgPGM, keep, delete) ;
        if (long_meth) {
            shortestPath = getDoublePath(interest) ;
        }
        else {
            imgGraph = toGraph(interest) ;
            shortestPath = getShortestPath (imgGraph) ;
        }
        return resize (imgPGM, shortestPath) ;
    }

    private static void swapAll(){
        if (imgPGM != null) imgPGM = swapTable(imgPGM) ;
        if (imgPPM != null) imgPPM = swapTable(imgPPM) ;
    }

    private static int[][] swapTable(int[][] toSwap) {
        int x = toSwap.length ;
        int y = toSwap[0].length ;
        int[][] toReturn = new int[y][x] ;

        for (int _x = 0; _x < x; ++_x) {
            for (int _y = 0; _y < y; ++_y) {
                toReturn[_y][_x] = toSwap[_x][_y] ;
            }
        }
        return toReturn ;
    }

    private static int[][][] swapTable(int[][][] toSwap) {
        int x = toSwap.length ;
        int y = toSwap[0].length ;
        int[][][] toReturn = new int[y][x][RGB] ;

        for (int _x = 0; _x < x; ++_x) {
            for (int _y = 0; _y < y; ++_y) {
                toReturn[_y][_x] = toSwap[_x][_y] ;
            }
        }
        return toReturn ;
    }

    /**
     * Switch all ppm pixels to their invert
     */
    private static int[][] toggleppm (int[][] img) {
        for (int x = 0; x < img.length; ++x) {
            for (int y = 0; y < img[0].length; ++y) {
                img[x][y] = PortableAnymap.PGM_MAX_VAL - img[x][y] ;
            }
        }
        return img ;
    }
}
