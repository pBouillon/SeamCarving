package seamcarving;

import static seamcarving.Graph.edge2coord;
import static seamcarving.SeamCarving.RGB;

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
        int[] coord;
        for (int i = 1; i < vertices.length - 1; ++i) {
            coord = edge2coord(vertices[i], img[0].length);
            img[coord[x]][coord[y]] = TO_REMOVE;
        }

        int[][] newImg = new int[img.length][img[0].length - 1]; // new image has one column less

        int _x = 0;
        int _y = 0;
        for (int[] x : img) {
            for (int y : x) {
                if (y == TO_REMOVE) {
                    continue;
                }
                newImg[_x][_y++] = y;
            }
            _y = 0;
            ++_x;
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
