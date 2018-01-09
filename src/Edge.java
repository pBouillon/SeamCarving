class Edge {
	int cost ;
	int from ;
    int to ;

	/**
	 * @param x
	 * @param y
	 * @param cost
	 */
    Edge(int x, int y, int cost) {
        this.cost = cost ;
        this.from = x ;
        this.to   = y ;
    }
}
