package seamcarving;

import graph.Edge;
import graph.Heap;

import java.util.ArrayList;

/**
 * Seam Carving graph handling methods
 *
 * @version 1.0
 */
public class Graph {
    private static int[] vertices ;

    /**
     * Give the coordinates of an axis in the pixel tab
     *
     * @param verticeID id of the vertice
     * @param width     table width
     *
     * @return {x, y} - pixel coord in the image
     */
    static int[] edge2coord(int verticeID, int width) {
        return new int[] {
                verticeID / width,
                verticeID % width
        } ;
    }

    /**
     * Dijkstra algorithm implementation
     *
     * @param g manipulated graph
     * @param s start vertex
     * @param t end vertex
     *
     * @return sequence of edges for the shortest path
     */
    @SuppressWarnings("unused")
    private static int[] Dijkstra(graph.Graph g, int s, int t) {
        Heap pq = new Heap(g.vertices()) ;   // priority queue to ensure that every vertice is reached

        int[] dist = new int[g.vertices()] ; // array with shortest path to each vertex
        int[] prev = new int[g.vertices()] ; // array with previous node

        for (int i = 0 ; i < dist.length ; i++) {
            dist[i] = Integer.MAX_VALUE ;
        }
        dist[s] = 0 ; // no cost because origin
        pq.decreaseKey(s, 0) ; // origin = no cost to reach another vertice

        do { // while we don't reach the final vertice
            int shortest_v = pq.pop() ;

            for (Edge e : g.next(shortest_v)) {
                int newDist = dist[shortest_v] + e.getCost() ; // evaluate the new cost
                if( dist[e.getTo()] > newDist ) {              // if new cost is less than previously
                    dist[e.getTo()] = newDist ;
                    prev[e.getTo()] = shortest_v ;       // update predecessor
                    pq.decreaseKey(e.getTo(), newDist) ; // update the priority of the element
                }
            }
        } while (!pq.isEmpty()) ; // evaluate each paths to validate solution
        vertices = dist;
        return prev ;
    }

    /**
     */
    private static int[] doubleDjikstra(graph.Graph g, int s, int t){
        return null;
    }

    /**
     * Method that return all Vertices contained in two shortest path
     * @param interest
     * @return
     */
    static int[] getDoublePath(int[][] interest){
        graph.Graph newGraph  = toDoubleGraph(interest);

        // find shortest path
        int[] shortestPath = getShortestPath(newGraph);
        int[] vertices = getVertices();

        // update new cost :
        for(Edge e : newGraph.edges()){
            // for each edge u v add difference between shortest path to get to u with shortest path to get to v
            e.setCost(e.getCost() + (vertices[e.getFrom()] - vertices[e.getTo()]) );
        }
        // revert edges :
        for(int i = 1; i< shortestPath.length   ; i++){
            newGraph.revertEdge(shortestPath[i],shortestPath[i-1]);
        }
        // find shortest path : OK good path as shown on example
        int[] secondShortestPath = getShortestPath(newGraph);

        ArrayList<Integer> allVertice = new ArrayList<>() ;
        for(int i : shortestPath){
            if( i != newGraph.getV()-2 && i != newGraph.getV()-1){
                allVertice.add(i);
            }

        }
        for(int i : secondShortestPath){
            if(!allVertice.contains(i) && i != newGraph.getV()-2 && i != newGraph.getV()-1 ){
                allVertice.add(i);
            }
        }

        int[] res = new int[allVertice.size()] ;
        int cpt = 0 ;
        for(int i : allVertice){
            res[cpt++]= i ;
        }
        return res;
    }

    /**
     * Convert the image to a graph
     *
     * @param  itr   interest per pixel (see seamcarving.SeamCarving.interest(...))
     *
     * @return the translated graph
     */
    public static graph.Graph toGraph(int[][] itr) {
        int i, j ;

        int height = itr.length ;
        int width  = itr[0].length ;

        graph.Graph graph = new graph.Graph(height * width + 2) ; // height * width + first node + last node
        // graph's core
        for (i = 0; i < height - 1; i++) {
            int  c_val ; // current value
            for (j = 0; j < width ; j++) {
                c_val = itr[i][j] ;

                graph.addEdge( new Edge (
                        width * i + j ,
                        width * (i + 1) + j,
                        c_val
                )) ;
                if (!(j - 1 < 0)) {
                    graph.addEdge( new Edge (
                            width * i + j  ,
                            width * (i + 1) + j - 1,
                            c_val
                    )) ;
                }
                if (j + 1 < width) {
                    graph.addEdge( new Edge (
                            width * i + j,
                            width * (i + 1) + j + 1,
                            c_val
                    )) ;
                }
            }
        }
        // edge to final vertex
        for (j = 0; j < width ; j++) {
            graph.addEdge (
                    new Edge (
                            width * (height - 1) + j,
                            height * width,
                            itr[i][j]
                    )
            ) ;
        }
        // edge from origin vertex
        for (j = 0; j < width; j++) {
            graph.addEdge (
                    new Edge (width * height + 1, j, 0)
            ) ;
        }
        return graph ;
    }

    /**
     * Convert the image to a graph doubled
     *
     * @param  itr   interest per pixel (see seamcarving.SeamCarving.interest(...))
     *
     * @return the translated graph
     */
    public static graph.Graph toDoubleGraph(int[][] itr) {
        int i, j;
        int cpt = 0;

        int height = itr.length ;
        int width  = itr[0].length ;

        int nbTotal = height * width +(height-2) * width + 2; // height * width + first node + last node + double every inside line

        graph.Graph graph = new graph.Graph(nbTotal) ;
        // graph's core
        for (i = 0; i < height + (height-2) - 1; i++) {
            int  c_val ; // current value

            if(i%2 != 0){
                cpt++; // count how many 0 line we crossed
            }

            for (j = 0; j < width ; j++) {

                c_val = itr[i-cpt][j] ; // keep the right interest -> ignore 0 line

                if( i %2 == 0){ // 1 line on 2 is a 0 line
                    graph.addEdge( new Edge (
                            width * i + j ,
                            width * (i + 1) + j,
                            c_val
                    )) ;
                    // check right - left boundary
                    if (!(j - 1 < 0)) {
                        graph.addEdge( new Edge (
                                width * i + j  ,
                                width * (i + 1) + j - 1,
                                c_val
                        )) ;
                    }
                    if (j + 1 < width) {
                        graph.addEdge( new Edge (
                                width * i + j,
                                width * (i + 1) + j + 1,
                                c_val
                        )) ;
                    }
                }else{
                    // we are in 0 line so only edge with 0
                    graph.addEdge( new Edge (
                            width * i + j ,
                            width * (i + 1) + j,
                            0
                    )) ;
                }

            }
        }

        // edge from origin vertex
        for (j = 0; j < width; j++) {
            graph.addEdge (
                    new Edge (nbTotal-1, j, 0) // from the initial vertex to first line
            ) ;
        }

        for (j = 0; j < width ; j++) {
            graph.addEdge (
                    new Edge (
                            nbTotal - width - 2 + j, // from the last line of the graph
                            nbTotal-2, // to the final vertex
                            itr[i-cpt][j]
                    )
            ) ;
        }

        return graph ;
    }

    /**
     */
    public static int[] getVertices() {
        return vertices;
    }

    /**
     *
     */
    public static int[] getShortestPath(graph.Graph g) {
        int[] path = Dijkstra(g, g.vertices() - 1, g.vertices() - 2 ) ;

        boolean parsed = false ;
        int i = g.vertices() - 2 ; // final vertice to reach

        ArrayList<Integer> res = new ArrayList<>() ;
        res.add(i) ; // from the bottom vertice

        while (!parsed) {
            if (path[i] == 0) {
                parsed = true ;
            } else {
                res.add(path[i]) ; // adding each vertice of the best path
                i = path[i] ;
            }
        }

        return res.stream()
                .mapToInt(Integer::valueOf)
                .toArray() ;
    }
}
