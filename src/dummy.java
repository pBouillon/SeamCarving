import graph.Edge;
import graph.Graph;

import java.util.ArrayList;

import static seamcarving.Graph.*;
import static seamcarving.IO.*;
import static seamcarving.Interest.interest;

/**
 * @deprecated Only for immediate testing purpose
 * @version 1.0
 */
public class dummy {
    public static void main(String[] args) {
        //writepgm_try() ;
        //interest_try() ;
        //tograpg_test();
        //to_graph_double();
        //shortestPathTest() ;
        shortestPathDouble();
        //readppm_try() ;
        //writeppm_try() ;
        //purged() ;
    }

    private static void interest_try() {
        int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

        int[][] ret = interest(image) ;

        toGraph(ret) ;

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
        int[][][] img = readPPM("PortablePixmaps/ppm/dummy.ppm") ;

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
        int[][] itr = interest(image) ;
        Graph g = toGraph(itr);
        g.writeFile("test_graph.dot");
    }
    private static Graph to_graph_double(){
        int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;
       /** int[][] image = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {20, 6, 2, 0},
                {200, 60, 25, 0}
        } ;**/
        int[][] itr = interest(image) ;
        Graph g = toDoubleGraph(itr);
        g.writeFile("test_graph_double.dot");
        return g;
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
        int[][] itr = interest(image) ;
        Graph g = toGraph(itr);

        int [] path = getShortestPath(g) ;
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

    private static void shortestPathDouble(){
        Graph doubledGraph  = to_graph_double() ;
        // find shortest path
        int[] shortest_1 = getShortestPath(doubledGraph) ;
        int[] vertices = getVertices() ;

        // update new cost : OK costs are ok
        int diff ;
        for (Edge e : doubledGraph.edges()) {
            // for each edge u v add difference between shortest path to get to u with shortest path to get to v
            diff = vertices[e.getFrom()] - vertices[e.getTo()] ;
            e.setCost (e.getCost() + diff) ;
        }

        // revert edges : OK but render is ... not fine for debug
        for (int i = 1; i< shortest_1.length; ++i) {
            doubledGraph.revertEdge (
                    shortest_1[i],
                    shortest_1[i - 1]
            ) ;
        }

        // reverse array
        int pathPart ;
        for (int i = 0; i < shortest_1.length / 2; ++i) {
            pathPart = shortest_1[i] ;
            shortest_1[i] = shortest_1[shortest_1.length - i - 1] ;
            shortest_1[shortest_1.length - i - 1] = pathPart ;
        }

        diff = 0 ;
        int cpt = 0 ;
        // result
        ArrayList<Integer> allVertices = new ArrayList<>() ;
        for(int i = 0 ; i < shortest_1.length - 1; ++i) {
            if (shortest_1[i] != doubledGraph.getV() - 2
                    && shortest_1[i] != doubledGraph.getV() - 1) {
                if (diff++ % 2 == 0
                        || i == shortest_1.length - 2 ) {
                    allVertices.add (shortest_1[i] - (cpt * 4)) ;
                }
                else ++cpt ;
            }
        }

        // reverse array
        // find shortest path : OK good path as shown on example
        int temp ;
        int[] shortestClone = shortest_1.clone() ;
        for (int i = 0; i < shortestClone.length / 2; ++i) {
            temp = shortestClone[i] ;
            shortestClone[i] = shortestClone[shortestClone.length - i - 1] ;
            shortestClone[shortestClone.length - i - 1] = temp ;
        }

        ArrayList<Integer> shortest_2 = new ArrayList<>() ;
        for(int i : shortestClone) shortest_2.add(i) ;
        for (int vertice : shortest_1) {
            if (shortest_2.contains(vertice)
                    && vertice != doubledGraph.getV() - 2
                    && vertice != doubledGraph.getV() - 1) {
                shortest_2.remove(vertice) ;
            }
        }

        cpt  = 0 ;
        diff = 0 ;
        for (int i = 0 ; i < shortest_2.size() - 1; ++i){
            if (shortest_2.get(i) != doubledGraph.getV() - 2
                    && shortest_2.get(i) != doubledGraph.getV() - 1) {
                if (diff++ % 2 == 0
                        || i == shortest_2.size() - 2) {
                    allVertices.add (shortest_2.get(i) - (cpt * 4)) ;
                }
                else ++cpt ;
            }
        }

        for (int i : allVertices) System.out.println(i) ;
        doubledGraph.writeFile("graph_djikstra.dot") ;
    }

    private static void writepgm_try() {
        int[][] p_pgm = {
                {3,   11, 24, 39},
                {8,   21, 29, 39},
                {200, 60, 25, 0}
        } ;

        String dest = "dummy.pgm" ;

        writepgm(p_pgm, dest) ;
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
        writeppm(test, "PortablePixmaps/ppm/test.ppm") ;
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
