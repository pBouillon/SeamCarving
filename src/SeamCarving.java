import graph.Edge;
import graph.Graph;
import graph.Heap;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Seam Carving methods
 *
 * @version 1.0
 */
public class SeamCarving {

	private static final String BASE_PATH = "./" ;
	private static final String SEPARATOR = "  " ;

	private static final int x = 0 ;
	private static final int y = 1 ;

    /**
     * @param verticeID id of the vertice
     * @param width     table width
     *
     * @return {x, y} - pixel coord in the image
     */
	public static int[] edge2coord (int verticeID, int width) {
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
	private static int[] Dijkstra (Graph g, int s, int t) {
        
        Heap pq   = new Heap(g.vertices()) ; // priority queue to ensure that every vertice is reached
        int[] dist = new int[g.vertices()]; // array with shortest path to each vertex
        int [] prev = new int[g.vertices()]; // array with previous node
        
        for ( int i = 0 ; i < dist.length ; i++) {
        	dist[i] = Integer.MAX_VALUE ;
        }
        dist[s] = 0 ; // no cost because origin
        pq.decreaseKey(s, 0) ; // origin

        do { // while we don't end our path at the goal
            int shortest_v = pq.pop() ;
            // System.out.println("lowest sommet  : " + shortest_v);
             
            for (Edge e : g.next(shortest_v)) { // for each edge
                int newDist = dist[shortest_v] + e.getCost() ;    // evaluate the new cost
                if( dist[e.getTo()] > newDist ) { // if new cost is less than previously
                	dist[e.getTo()] = newDist ;
                	// System.out.println(" decrease  : " + e.getTo() + " with : " + newDist);
                	prev[e.getTo()] = shortest_v ; // update predecessor 
                    pq.decreaseKey(e.getTo(), newDist) ; // update the priority of the element
                }
            }
        } while (!pq.isEmpty()) ; // we try each node to be sure that there is no better path
        return prev ;
    }

	/**
	 * @param g
	 * @return
	 */
	public static int[] getShortestPath (Graph g) {
		int[] path = Dijkstra(g, g.vertices() - 1, g.vertices() - 2 ) ;

		ArrayList<Integer> res = new ArrayList<>() ;
		int i = g.vertices() - 2 ; // final vertice to reach

		// System.out.println(i);
		res.add(i) ;
		boolean parsed = false ;
		while (!parsed) {
			if (path[i] == 0) {
				parsed = true ;
			} else {
				// System.out.print(path[i]+ " ") ;
				res.add(path[i]) ;
				i = path[i] ;
			}
		}

		return res.stream()
				.mapToInt(Integer::valueOf)
				.toArray() ;
	}

	/**
	 * Build a double array with interest for each pixel
	 * for an array :[x] [y] [z]
	 * interest = y - avg(x, z)
	 *
	 * @param image
	 * @return interest: average val of adjascent pixels
	 */
	public static int[][] interest (int[][] image) {
		int height = image.length ;
		int width  = image[0].length ;

		int[][] interest_grid = new int[height][width] ;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++){
				// current pixel
				int px_c = image[i][j] ;

				// current pixel has no right neighbor
				if (j - 1 < 0) {
					interest_grid[i][j] = Math.abs(px_c - image[i][j + 1]) ;
					continue ;
				}

				// current pixel has no left neighbor
				if (j + 1 == width) {
					interest_grid[i][j] = Math.abs(px_c - image[i][j - 1]) ;
					continue ;
				}

				// current pixel has both neighbors
				int px_l = image[i][j - 1] ; // left  pixel
				int px_r = image[i][j + 1] ; // right pixel
				int neighbors_avg = (px_l + px_r) / 2 ;

				interest_grid[i][j] = Math.abs(px_c - neighbors_avg) ;
			}
		}
		return interest_grid ;
	}

	/**
	 * @param fn
	 * @return
	 */
	public static int[][] readpgm (String fn) {
		try {
			InputStream f = ClassLoader.getSystemClassLoader()
					.getResourceAsStream(fn) ;

			BufferedReader d = new BufferedReader (new InputStreamReader(f)) ;

			String magic = d.readLine() ;
			String line  = d.readLine() ;
			while (line.startsWith("#")) {
				line = d.readLine() ;
			}

			Scanner s  = new Scanner(line) ;
			int width  = s.nextInt() ;
			int height = s.nextInt() ;

			line = d.readLine() ;
			s    = new Scanner(line) ;

			int maxVal = s.nextInt() ;
			int[][] im = new int[height][width] ;
			s = new Scanner(d) ;
			int count = 0 ;
			while (count < height*width) {
				im[count / width][count % width] = s.nextInt() ;
				count++ ;
			}
			return im ;
		}
		catch (Throwable t) {
			t.printStackTrace (System.err) ;
			return null ;
		}
	}

	/**
	 * @param fn
	 * @return
	 */
	public static int[][] readpgmv2 (String fn) {
		int[][] img = null ;

		BufferedReader br ;
		Scanner scan ;

		File source = new File(fn) ;
		if (!source.exists()) {
			return null ;
		}

		try {
			br = new BufferedReader(new FileReader(source)) ;

			String magic = br.readLine() ;
			String line  = br.readLine() ;
			while (line.startsWith("#")) {
				line = br.readLine() ;
			}

			scan = new Scanner(line) ;
			int width  = scan.nextInt() ;
			int height = scan.nextInt() ;

			line = br.readLine() ;
			scan = new Scanner(line) ;
			int maxVal = scan.nextInt() ;

			img = new int[height][width] ;

			scan = new Scanner(br) ;
			for (int count = 0; count < height * width; ++count) {
				img[count / width][count % width] = scan.nextInt() ;
			}
		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(-1) ;
		}

		return img ;
	}

	/**
	 * Convert the image to a graph
     *
	 * @param  itr   interest per pixel (see SeamCarving.interest(...))
     *
	 * @return the translated graph
	 */
    public static Graph tograph (int[][] itr) {
        int i, j ;

        int height = itr.length ;
        int width  = itr[0].length ;

        Graph graph = new Graph (height * width + 2) ; // height * width + first node + last node 
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
	 * Save greyscale values into a .pgm file
	 *
	 * @param image    greyscale per pixels
	 * @param filename destination
	 */
	public static void writepgm (int[][] image, String filename) {
		File destination ;
		PrintWriter pw    = null ;

		// Create file if not exists
		try {
			boolean ret = true ;
			destination = new File(BASE_PATH + filename) ;

			if (!destination.exists()) {
				ret = destination.createNewFile();
			}
			if (!ret) {
				System.out.println("Failed to create file: " + BASE_PATH + filename) ;
				System.exit(-1) ;
			}
			pw = new PrintWriter(destination) ;
		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(-1) ;
		}

		pw.write("");
		pw.flush() ;

		// Write plain pgm header
		pw.println(PortablePixmap.P_PGM + SEPARATOR) ;
		pw.println(image[0].length + SEPARATOR + image.length + SEPARATOR) ;
		pw.println(PortablePixmap.PGM_MAX_VAL) ;

		// write the value for each table cell
		for (int[] row : image) {
			int line_len = 0 ;

			for (int val : row) {
				String written = val + SEPARATOR ;

				if (line_len + written.length() > PortablePixmap.PGM_MAX_PGM_LEN) {
					pw.print("\n") ;
					line_len = 0 ;
				}
				line_len += written.length() ;
				pw.print(written) ;
			}
			pw.print("\n");
		}
		pw.close() ;
	}

    /**
     * @param img
     * @param vertices
     * @return
     */
	public static int[][] resize(int[][] img, int[] vertices) {
		int[] pixels = new int[vertices.length - 2] ; // removing first and last vertice
		System.arraycopy (vertices, 1, pixels, 0, vertices.length - 1 - 1) ;

		// set each designated pixels to -1
	    for (int vertice : pixels) {
	        int coord[] = edge2coord(vertice, img[0].length) ;
	        img[coord[x]][coord[y]] = -1 ;
        }

		int[][] newImg = new int[img.length][img[0].length - 1] ; // new image has one column less

		ArrayList<Integer> newRow = new ArrayList<>() ; // row with vals != -1

		for (int x = 0; x < img.length; ++x) {
			// parsing the row:
			for (int val : img[x]) {
				if (val != -1) {
					newRow.add(val) ;
				}
			}
			// adding row to the new image and cleaning list
			newImg[x] = newRow.stream()
							.mapToInt(Integer::valueOf)
							.toArray() ;
			newRow.clear() ;
		}
		return newImg ;
    }
}
