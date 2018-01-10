package graph;

import java.util.ArrayList;
import java.io.*;

public class Graph {
   private ArrayList<Edge>[] adj ;
   private final int V ;
   int E ;

    /**
     *
     * @param N
     */
    @SuppressWarnings("unchecked")
    public Graph (int N) {
        this.V = N ;
        this.E = 0 ;

        adj = (ArrayList<Edge>[]) new ArrayList[N] ;
        for (int v= 0; v < N; v++) {
            adj[v] = new ArrayList<>() ;
        }
    }

    /**
     *
     * @return
     */
    public int vertices() {
        return V ;
    }

    /**
     *
     * @param e
     */
    public void addEdge (Edge e) {
        int v = e.from ;
        int w = e.to ;

        adj[v].add(e) ;
        adj[w].add(e) ;
    }

    /**
     *
     * @param v
     * @return
     */
    public Iterable<Edge> adj (int v) {
        return adj[v] ;
    }

    /**
     *
     * @param v
     * @return
     */
    public Iterable<Edge> next (int v) {
        ArrayList<Edge> n = new ArrayList<>() ;
        for (Edge e: adj(v)) {
            if (e.to != v) {
                n.add(e);
            }
        }
        return n;
    }

    /**
     *
     * @return
     */
    public Iterable<Edge> edges() {
        ArrayList<Edge> list = new ArrayList<>() ;
        for (int v = 0; v < V; v++) {
            for (Edge e : adj(v)) {
                if (e.to != v) {
                    list.add(e) ;
                }
            }
        }
        return list ;
    }

    /**
     *
     * @param s
     */
    public void writeFile (String s) {
        try {
            PrintWriter writer = new PrintWriter (s, "UTF-8") ;
            writer.println ("digraph G{") ;
            for (Edge e: edges()) {
                writer.println (e.from + "->" + e.to + "[label=\"" + e.cost + "\"];") ;
            }
            writer.println ("}") ;
            writer.close() ;
        }
        catch (IOException ignored) {}
    }
}
