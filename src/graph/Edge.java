package graph;

public class Edge {
	int cost ;
	int from ;
    int to ;

	/**
	 * @param x
	 * @param y
	 * @param cost
	 */
	public Edge(int x, int y, int cost) {
        this.cost = cost ;
        this.from = x ;
        this.to   = y ;
    }
}
