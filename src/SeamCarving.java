import graph.Edge;
import graph.Graph;
import graph.Heap;

import java.io.*;
import java.util.*;

/**
 * Seam Carving methods
 *
 * @version 1.0
 */
public class SeamCarving {

    private static final String BASE_PATH      = "./" ; // default path
    private static final String SEPARATOR      = " " ;  // pixel anymap separator
    private static final String LONG_SEPARATOR = "  " ; // pixel anymap long separator

    private static final int x   = 0 ; // axis for .pgm writing
    private static final int y   = 1 ; // axis for .pgm writing
    private static final int RGB = 3 ;

    private static final int TO_REMOVE = -1 ; // designate values to be erased

    private static final int PX_DEL_VAL =  0 ;
    private static final int PX_KEEP_VAL = PortableAnymap.PGM_MAX_VAL ;

    /**
     *
     */
    private static int getFileLines (String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            return count;
        }
    }

    /**
     * Give the coordinates of an axis in the pixel tab
     *
     * @param verticeID id of the vertice
     * @param width     table width
     *
     * @return {x, y} - pixel coord in the image
     */
    private static int[] edge2coord(int verticeID, int width) {
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
        return prev ;
    }

    private static File getDestination(String filename) {
        File destination = new File(BASE_PATH + filename) ;
        try {
            boolean ret = true ;

            if (!destination.exists()) {
                ret = destination.createNewFile();
            }
            if (!ret) {
                System.out.println("Failed to create file: " + BASE_PATH + filename) ;
                System.exit(-1) ;
            }

        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(-1) ;
        }

        return destination ;
    }

    /**
     * @param g
     * @return
     */
    public static int[] getShortestPath (Graph g) {
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

    /**
     * Build a double array with interest for each pixel
     * for an array :[x] [y] [z]
     * interest = y - avg(x, z)
     *
     * @param img     : pixels of a .pgm files
     * @return interest : average val of adjacent pixels
     */
    static int[][] interest(int[][] img) {
        int height = img.length ;
        int width  = img[0].length ;

        int[][] interest_grid = new int[height][width] ;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++){
                int px_c = img[i][j] ;   // current pixel

                if (j - 1 < 0) { // no right neighbor
                    interest_grid[i][j] = Math.abs(px_c - img[i][j + 1]) ;
                    continue ;
                }

                if (j + 1 == width) { // no left neighbor
                    interest_grid[i][j] = Math.abs(px_c - img[i][j - 1]) ;
                    continue ;
                }

                // both neighbors
                int px_l = img[i][j - 1] ; // left  pixel
                int px_r = img[i][j + 1] ; // right pixel
                int neighbors_avg = (px_l + px_r) / 2 ;

                interest_grid[i][j] = Math.abs(px_c - neighbors_avg) ;
            }
        }
        return interest_grid ;
    }

    /**
     *
     */
    private static int[][] colorInterest(int[][][] ppmImg) {
        int height = ppmImg.length ;
        int width  = ppmImg[0].length ;

        int[][] interest_grid = new int[height][width] ;

        int[][] analyzed_img = new int[height][width] ;
        for (int color_id = 0; color_id < RGB; ++color_id) { // RGB

            // build map for one color
            for (int x = 0; x < height; ++x) {
                for (int y = 0; y < width; ++y) {
                    analyzed_img[x][y] += ppmImg[x][y][color_id] ;
                }
            }

            // getting interest
            analyzed_img = interest(analyzed_img) ;

            // cumulate interests
            for (int x = 0; x < height; ++x) {
                for (int y = 0; y < width; ++y) {
                    interest_grid[x][y] += analyzed_img[x][y] ;
                }
            }
        }

        return interest_grid ;
    }

    /**
     * Build a double array with interest for each pixel
     * for an array :[x] [y] [z]
     * interest = y - avg(x, z)
     *
     * @param pgmImage     : pixels of a .pgm files
     * @return interest : average val of adjacent pixels
     */
    public static int[][] interest (int[][] pgmImage, int[] keep, int[] delete) {
        return applyChanges (
                interest(pgmImage),
                keep,
                delete
        ) ;
    }

    /**
     *
     */
    public static int[][] interest(int[][][] ppmImage, int[] keep, int[] delete) {
        return applyChanges (
                colorInterest(ppmImage),
                keep,
                delete
        ) ;
    }

    /**
     *
     */
    private static int[][] applyChanges (int[][] interest_grid, int[] keep, int[] delete) {
        if (keep != SeamCarvingLauncher.NO_PROP) {
            if (keep[0] < 0 || keep[1] > interest_grid[0].length) {
                System.out.println("Error: columns to keep are not matching the current image") ;
                System.exit(-1) ;
            }
            interest_grid = alterInterest (interest_grid, keep, PX_KEEP_VAL) ;
        }

        if (delete != SeamCarvingLauncher.NO_PROP) {
            if (delete[0] < 0 || delete[1] > interest_grid[0].length) {
                System.out.println("Error: columns to delete are not matching the current image") ;
                System.exit(-1) ;
            }
            interest_grid = alterInterest (interest_grid, delete, PX_DEL_VAL) ;
        }

        return interest_grid ;
    }

    /**
     *
     */
    private static int[][] alterInterest(int[][] interest, int[] toAlter, int change) {
        for (int x = 0; x < interest.length; ++x) {
            for (int y = 0; y < interest[x].length; ++y) {
                if (y > toAlter[0] && y < toAlter[1]) {
                    interest[x][y] = change ;
                }
            }
        }
        return interest ;
    }

    /**
     *
     */
    public static String readFileType(String filename) {
        BufferedReader br = null ;
        String magic = "" ;

        File source = new File(filename) ;
        if (!source.exists()) {
            return null ;
        }

        try {
            br = new BufferedReader(new FileReader(source)) ;
            magic = br.readLine() ;

        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(-1) ;
        }
        finally {
            try {
                if (br != null)
                    br.close() ;
            } catch (IOException ex) {
                ex.printStackTrace() ;
            }
        }

        return magic ;
    }

    /**
     * Method provided by the subject
     *
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
     * Read values from a File
     *
     * @param filename : file name
     * @return int[][] pgm values for each pixel
     */
    public static int[][] readPGM(String filename) {
        int[][] img = null ;

        BufferedReader br = null;
        Scanner scan ;

        File source = new File(filename) ;
        if (!source.exists()) {
            return null ;
        }

        try {
            br = new BufferedReader(new FileReader(source)) ;

            String magic = br.readLine() ;  // getting magic number (http://netpbm.sourceforge.net/doc/pgm.html)
            String line  = br.readLine() ;
            while (line.charAt(0) == PortableAnymap.COMMENT) {  // ignoring comments
                line = br.readLine() ;
            }

            scan = new Scanner(line) ;      // getting dimensions
            int width  = scan.nextInt() ;
            int height = scan.nextInt() ;

            line = br.readLine() ;
            scan = new Scanner(line) ;
            int maxVal = scan.nextInt() ;
            if (maxVal > PortableAnymap.PGM_MAX_VAL) {
                return null ;
            }


            img = new int[height][width] ;
            scan = new Scanner(br) ;
            for (int count = 0; count < height * width; ++count) {   // getting image values
                img[count / width][count % width] = scan.nextInt() ;
            }
        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(-1) ;
        }
        finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return img ;
    }

    /**
     *
     */
    public static int[][][] readPPM(String filename) {
        int[][][] img = null ;

        BufferedReader br = null;
        Scanner scan ;

        File source = new File(filename) ;
        if (!source.exists()) {
            return null ;
        }

        try {
            br = new BufferedReader(new FileReader(source)) ;

            String magic = br.readLine() ;
            String line  = br.readLine() ;
            while (line.charAt(0) == PortableAnymap.COMMENT) {  // ignoring comments
                line = br.readLine() ;
            }

            scan = new Scanner(line) ;      // getting dimensions
            int width  = scan.nextInt() ;
            int height = scan.nextInt() ;

            line = br.readLine() ;
            scan = new Scanner(line) ;

            int maxVal = scan.nextInt() ;
            if (maxVal > PortableAnymap.PPM_MAX_VAL) {
                return null ;
            }

            img = new int[height][width][RGB] ;
            scan = new Scanner(br) ;

            for (int count = 0; count < height * width; ++count) {   // getting image values
                int i = 0 ;
                img[count / width][count % width][i++] = scan.nextInt() ; // red
                img[count / width][count % width][i++] = scan.nextInt() ; // green
                img[count / width][count % width][i]   = scan.nextInt() ; // blue
            }
        } catch (IOException e) {
            e.printStackTrace() ;
            System.exit(-1) ;
        }
        finally {
            try {
                if (br != null)
                    br.close() ;
            } catch (IOException ex) {
                ex.printStackTrace() ;
            }
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
        PrintWriter pw = null ;
        try {
            pw = new PrintWriter(getDestination(filename)) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace() ;
        }

        pw.write("") ;
        pw.flush() ;

        // Write plain pgm header
        pw.println(PortableAnymap.P_PGM + SEPARATOR) ;
        pw.println(image[0].length + SEPARATOR + image.length + SEPARATOR) ;
        pw.println(PortableAnymap.PGM_MAX_VAL) ;

        // write the value for each table cell
        for (int[] row : image) {
            int line_len = 0 ;

            for (int val : row) {
                String written = val + SEPARATOR ;

                if (line_len + written.length() > PortableAnymap.PGM_MAX_PGM_LEN) {
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
     * Save greyscale values into a .pgm file
     *
     * @param image    greyscale per pixels
     * @param filename destination
     */
    public static void writeppm (int[][][] image, String filename) {
        PrintWriter pw = null ;
        try {
            pw = new PrintWriter(getDestination(filename)) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace() ;
        }

        // clean the file
        pw.write("") ;
        pw.flush() ;

        // Write plain ppm header
        pw.println(PortableAnymap.P_PPM + SEPARATOR) ;
        pw.println(image[0].length + SEPARATOR + image.length + SEPARATOR) ;
        pw.println(PortableAnymap.PPM_MAX_VAL) ;

        // write the value for each table cell
        for (int[][] row : image) {
            for (int[] pixel : row) {
                for (int val : pixel) {
                    pw.print(val + SEPARATOR) ;
                }
                pw.print(LONG_SEPARATOR) ;
            }
            pw.print("\n") ;
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

        // set each pixel associated to a vertice to -1
        for (int vertice : pixels) {
            int coord[] = edge2coord(vertice, img[0].length) ;
            img[coord[x]][coord[y]] = TO_REMOVE ;
        }

        int[][] newImg = new int[img.length][img[0].length - 1] ; // new image has one column less

        ArrayList<Integer> newRow = new ArrayList<>() ; // list of all accepted values
        for (int x = 0; x < img.length; ++x) {
            for (int val : img[x]) {
                if (val != TO_REMOVE) {
                    newRow.add(val) ; // create new row with remaining values
                }
            }

            // add the row to the new image and cleaning list
            newImg[x] = newRow.stream()
                    .mapToInt(Integer::valueOf)
                    .toArray() ;
            newRow.clear() ;
        }
        return newImg ;
    }

    /**
     * @param img
     * @param vertices
     * @return
     *
     * TODO
     */
    public static int[][][] resize (int[][][] img, int[] vertices) {
        int[] pixels = new int[vertices.length - 2] ; // removing first and last vertice
        System.arraycopy (vertices, 1, pixels, 0, vertices.length - 1 - 1) ;

        // set each pixel associated to a vertice to -1
        for (int vertice : pixels) {
            int coord[] = edge2coord(vertice, img[0].length) ;
            for (int i = 0; i < RGB; ++i) {
                img[coord[x]][coord[y]][i] = TO_REMOVE ;
            }
        }

        // one column less and RGB
        int[][][] newImg = new int[img.length][img[0].length - 1][RGB] ;

        // list of all accepted values
        ArrayList<Integer> newPx  = new ArrayList<>() ;
        ArrayList<int[]>   newRow = new ArrayList<>() ;

        //
        for (int row = 0; row < img.length; ++row) {
            for (int[] col : img[row]) {
                for (int val : col) {
                    // add R | G | B val if expected
                    if (val != TO_REMOVE) {
                        newPx.add(val) ;
                    }
                }
                // if RGB values found - add it
                if (newPx.size() > 0) {
                    newRow.add(newPx.stream()
                            .mapToInt(Integer::valueOf)
                            .toArray()) ;
                    newPx.clear() ;
                }
            }

            // add the row to the new image and cleaning list
            img[row] = (int[][]) newRow.toArray() ;
            newRow.clear() ;
        }
        return newImg ;
    }
}
