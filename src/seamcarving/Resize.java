package seamcarving;

import static seamcarving.Graph.edge2coord;
import static seamcarving.SeamCarving.RGB;
import static seamcarving.SeamCarving.increase;

/**
 /**
 * Seam Carving graph handling methods
 *
 * @version 1.0
 */
class Resize {
    private static final int x   = 0 ; // axis for .pgm writing
    private static final int y   = 1 ; // axis for .pgm writing
    private static final int TO_REMOVE = -1 ; // designate values to be erased

    /**
     * Resize image with one less column
     *
     * @param img      pixels tab
     * @param vertices vertices to remove in the graph (see seamcarving.SeamCarving.toGraph(...))
     *
     * @return the shortened image
     */
    static int[][] resize(int[][] img, int[] vertices) {
        // marking each nodes as to remove except for the first and the last ones
        int[] coord ;

        boolean[][] toDel = new boolean[img.length][img[0].length] ;
        for (int x = 0; x < img.length * img[0].length; ++x) {
            toDel[x / img[0].length][x % img.length] = false ;
        }

        for (int i = 1; i < vertices.length - 1; ++i) {
            coord = edge2coord(vertices[i], img[0].length) ;
            toDel[coord[x]][coord[y]] = true ;
        }

        int[][] newImg ;
        // new image has one column less
        newImg = new int[img.length][img[0].length - 1] ;
        // new image has one more column
        if (increase) newImg = new int[img.length][img[0].length + 1] ;

        int _x, _y ;
        _x = 0 ;
        for (int x = 0; x < img.length; ++x) {
            _y = 0 ;
            for (int y = 0; y < img[0].length; ++y) {
                if (toDel[x][y]) {
                    // ignoring the line to reduce size
                    if (!increase) continue ;
                    // duplicate the pixel to increase size
                    newImg[_x][_y++] = img[x][y] ;
                }
                newImg[_x][_y++] = img[x][y] ;
            }
            ++_x ;
        }

        return newImg ;
    }

    /**
     * Resize a ppm image with one less column
     *
     * @param  img      image pixels
     * @param  vertices vertices composing the shortest path
     * @return the shortened image
     */
    static int[][][] resize (int[][][] img, int[] vertices) {
        int k ;
        int[] coord ;
        for (int i = 1; i < vertices.length - 1; ++i) {
            coord = edge2coord(vertices[i], img[0].length) ;
            img[coord[x]][coord[y]] = new int[]{TO_REMOVE, TO_REMOVE, TO_REMOVE} ;
        }

        // one column less and RGB
        int[][][] newImg = new int[img.length][img[0].length - 1][RGB] ;

        int _x = 0 ;
        int _y = 0 ;
        for (int[][] x : img) {
            for (int[] y : x) {
                if (y[0] == TO_REMOVE) {
                    continue ;
                }
                newImg[_x][_y] = y ;
                ++_y ;
            }
            _y = 0 ;
            ++_x   ;
        }

        return newImg ;
    }
}
