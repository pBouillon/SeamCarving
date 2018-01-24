import graph.Graph;

/**
 * @deprecated Only for immediate testing purpose
 * @version 1.0
 */
public class dummy {
    public static void main(String[] args) {
        //writepgm_try() ;
        //interest_try() ;
        //tograpg_test();
        //shortestPathTest() ;

        //readppm_try() ;
        //writeppm_try() ;
        purged() ;
    }

    private static void interest_try() {
        int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

        int[][] ret = SeamCarving.interest(image) ;

        SeamCarving.toGraph(ret) ;

        for (int[] row : ret) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }

        System.out.println("\n------\nexpected\n------\n");
        System.out.println("8 2 1 15\n13 3 1 10\n140 52 5 25");
    }

    private static void readppm_try() {
        int[][][] img = SeamCarving.readPPM("PortablePixmaps/ppm/dummy.ppm") ;

        for (int x[][] : img) {
            for (int y[] : x) {
                for(int v : y) {
                    System.out.print(v + " ") ;
                }
                System.out.print("   ") ;
            }
            System.out.println() ;
        }
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
        Graph g = SeamCarving.toGraph(itr);
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
        Graph g = SeamCarving.toGraph(itr);

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

    private static void writeppm_try() {
        int[][][] test =
                {
                    {
                        {0,0,0},
                        {0,0,0},
                        {0,0,0},
                        {15,0,15}
                    },
                    {
                        {0,0,0},
                        {0,15,7},
                        {0,0,0},
                        {0,0,0}
                    },
                    {
                        {0,0,0},
                        {0,0,0},
                        {0,15,7},
                        {0,0,0}
                    },
                    {
                        {15,0,15},
                        {0,0,0},
                        {0,0,0},
                        {0,0,0}
                    }
                } ;
        SeamCarving.writeppm(test, "PortablePixmaps/ppm/test.ppm") ;
    }

    private static void purged() {
        int[][][] test =
                {
                    {
                        {-1, -1, -1},
                        {0, 0, 0},
                        {0, 0, 0},
                        {15, 0, 15}
                    },
                    {
                        {-1, -1, -1},
                        {0, 15, 7},
                        {0, 0, 0},
                        {0, 0, 0}
                    },
                    {
                        {0, 0, 0},
                        {-1, -1, -1},
                        {0, 15, 7},
                        {0, 0, 0}
                    },
                    {
                        {15, 0, 15},
                        {0, 0, 0},
                        {-1, -1, -1},
                        {0, 0, 0}
                    }
                } ;

        int[][][] cleared = new int[test.length][test[0].length - 1][3] ;

        int _x = 0 ;
        int _y = 0 ;
        for (int[][] x : test) {
            for (int[] y : x) {
                if (y[0] == -1) {
                    continue ;
                }
                cleared[_x][_y++] = y ;
            }
            _y = 0 ;
            ++_x   ;
        }

        for (int[][] x : cleared) {
            for (int[] y : x) {
                for (int v : y) {
                    System.out.print(v + " ") ;
                }
                System.out.print("   ") ;
            }
            System.out.println("") ;
        }
    }
}
