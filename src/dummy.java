import graph.Graph;

import java.util.ArrayList;

/**
 * @deprecated Only for immediate testing purpose
 * @version 1.0
 */
public class dummy {
    public static void main(String[] args) {
        //writepgm_try() ;
        //interest_try() ;
        tograpg_test();
        shortestPathTest() ;
    }

    private static void interest_try() {
        int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

        int[][] ret = SeamCarving.interest(image) ;

        SeamCarving.tograph(ret) ;

        for (int[] row : ret) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }

        System.out.println("\n------\nexpected\n------\n");
        System.out.println("8 2 1 15\n13 3 1 10\n140 52 5 25");
    }

    private static void tograpg_test() {
    	int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;
//    	int[][] image = {
//                {3,   11, 24},
//                {8,   5, 29},
//        } ;
        int[][] itr = SeamCarving.interest(image) ;
        Graph g = SeamCarving.tograph(itr);
        g.writeFile("test_graph.dot");
    }

    private static void shortestPathTest() {
        int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

//    	int[][] image = {
//                {3,   11, 24},
//                {8,   5, 29},
//        } ;
        int[][] itr = SeamCarving.interest(image) ;
        Graph g = SeamCarving.tograph(itr);

        int [] path = SeamCarving.getShortestPath(g) ;
        boolean parsed = false;
        
        int i = g.vertices()-2; // final vertice to reach
        System.out.println(i);
        while(!parsed) {
        	if (path[i] == 0) {
        		parsed = true ;
        	} else {
        		System.out.println(path[i]) ;
        		i = path[i] ;
        	}
        }

    }

    private static void writepgm_try() {
        // same values as src/greymaps/test.pgm to check differences
        int[][] p_pgm = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

        String dest = "dummy.pgm" ;

        SeamCarving.writepgm(p_pgm, dest) ;
    }
}
