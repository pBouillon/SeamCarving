package seamcarving;

import static seamcarving.SeamCarving.RGB;
import static seamcarving.SeamCarving.increase;

/**
 * Seam Carving interest handling methods
 *
 * @version 2.0
 */
public class Interest {
    private static final int PX_DEL_VAL =  0 ;
    private static final int PX_KEEP_VAL = PortableAnymap.PGM_MAX_VAL ;

    /**
     * Build a double array with interest for each pixel
     * for an array :[x] [y] [z]
     * interest = y - avg(x, z)
     *
     * @param img     : pixels of a .pgm files
     * @return interest : average val of adjacent pixels
     */
    public static int[][] interest(int[][] img) {
        int height = img.length ;
        int width  = img[0].length ;

        if (increase) {
            // making uninteresting edge interesting for shortest path
            for (int x = 0; x < img.length; ++x) {
                for (int y = 0; y < img[0].length; ++y) {
                    if (img[x][y] == 0) continue ;
                    img[x][y] = -img[x][y] ;
                }
            }
        }

        int[][] interest_grid = new int[height][width] ;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++){
                int px_c = img[i][j] ;   // current pixel

                if (j - 1 < 0) { // no left neighbor
                    interest_grid[i][j] = Math.abs(px_c - img[i][j + 1]) ;
                    continue ;
                }

                if (j + 1 == width) { // no right neighbor
                    interest_grid[i][j] = Math.abs(px_c - img[i][j - 1]) ;
                    continue ;
                }

                // both neighbors
                int px_l = img[i][j - 1] ; // left  pixel
                int px_r = img[i][j + 1] ; // right pixel
                int neighbors_avg = (px_l + px_r) / 2 ;

                interest_grid[i][j] = Math.abs(px_c - neighbors_avg) ;
            }
        }

        return interest_grid ;
    }

    /**
     *
     */
    private static int[][] alterInterest(int[][] interest, int[] toAlter, int change) {
        for (int x = 0; x < interest.length; ++x) {
            for (int y = 0; y < interest[x].length; ++y) {
                if (y > toAlter[0] && y < toAlter[1]) {
                    interest[x][y] = change ;
                }
            }
        }
        return interest ;
    }

    /**
     *
     */
    private static int[][] colorInterest(int[][][] ppmImg) {
        int height = ppmImg.length ;
        int width  = ppmImg[0].length ;

        int[][] interest_grid = new int[height][width] ;
        int[][] analyzed_img  = new int[height][width] ;

        for (int color_id = 0; color_id < RGB; ++color_id) { // for each color
            // build map
            for (int x = 0; x < height; ++x) {
                for (int y = 0; y < width; ++y) {
                    analyzed_img[x][y] += ppmImg[x][y][color_id] ;
                }
            }

            // getting interest
            analyzed_img = interest(analyzed_img) ;

            // cumulate interest
            for (int x = 0; x < height; ++x) {
                for (int y = 0; y < width; ++y) {
                    interest_grid[x][y] += analyzed_img[x][y] ;
                }
            }
        }
        return interest_grid ;
    }

    /**
     * Build a double array with interest for each pixel
     * for an array :[x] [y] [z]
     * interest = y - avg(x, z)
     *
     * @param pgmImage     : pixels of a .pgm files
     * @return interest : average val of adjacent pixels
     */
    public static int[][] pgmInterest(int[][] pgmImage, int[] keep, int[] delete) {
        return applyChanges (
                interest(pgmImage),
                keep,
                delete
        ) ;
    }

    /**
     *
     */
    public static int[][] ppmInterest(int[][][] ppmImage, int[] keep, int[] delete) {
        return applyChanges (
                colorInterest(ppmImage),
                keep,
                delete
        ) ;
    }

    /**
     *
     */
    private static int[][] applyChanges (int[][] interest_grid, int[] keep, int[] delete) {
        if (isSet(keep)) {
            if (keep[0] < 0 || keep[1] > interest_grid[0].length) {
                System.out.println("Error: columns to keep are not matching the current image") ;
                System.exit(-1) ;
            }
            interest_grid = alterInterest (interest_grid, keep, PX_KEEP_VAL) ;
        }

        if (isSet(delete)) {
            if (delete[0] < 0 || delete[1] > interest_grid[0].length) {
                System.out.println("Error: columns to delete are not matching the current image") ;
                System.exit(-1) ;
            }
            interest_grid = alterInterest (interest_grid, delete, PX_DEL_VAL) ;
        }

        return interest_grid ;
    }

    /**
     * If any value is unset, return false
     */
    private static boolean isSet(int[] property) {
        for (int v : property) {
            if (v < 0) return false ;
        }
        return true ;
    }
}
