import java.io.*;
import java.util.*;

public class SeamCarving {
    private static final String BASE_PATH = "src/greymaps/" ;
    private static final String SEPARATOR = "  " ;

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
     * Save greyscale values into a .pgm file
     *
     * @param image    greyscale per pixels
     * @param filename destination
     */
    public static void writepgm (int[][] image, String filename) {
        PrintWriter pw    = null ;
        File destination  = null ;

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
}
