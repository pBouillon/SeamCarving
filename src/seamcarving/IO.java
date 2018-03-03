package seamcarving;

import java.io.*;
import java.util.Scanner;

import static seamcarving.SeamCarving.RGB;

/**
 * Seam Carving methods
 *
 * @version 2.0
 */
public class IO {
    private static final String BASE_PATH      = "./" ; // default path
    private static final String SEPARATOR      = " "  ; // pixel anymap separator
    private static final String LONG_SEPARATOR = "  " ; // pixel anymap long separator

    /**
     */
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
     *
     */
    static String readFileType(String filename) {
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
     * Read values from a File
     *
     * @param filename : file name
     * @return int[][] pgm values for each pixel
     */
    static int[][] readPGM(String filename) {
        int[][] img = null ;

        BufferedReader br = null;
        Scanner scan ;

        File source = new File(filename) ;
        if (!source.exists()) {
            return null ;
        }

        try {
            br = new BufferedReader(new FileReader(source)) ;

            br.readLine() ;  // magic number (http://netpbm.sourceforge.net/doc/pgm.html)
            String line  = br.readLine() ;
            while (line.charAt(0) == PortableAnyMap.COMMENT) {  // ignoring comments
                line = br.readLine() ;
            }

            scan = new Scanner(line) ;      // getting dimensions
            int width  = scan.nextInt() ;
            int height = scan.nextInt() ;

            line = br.readLine() ;
            scan = new Scanner(line) ;
            int maxVal = scan.nextInt() ;
            if (maxVal > PortableAnyMap.PGM_MAX_VAL) {
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

            br.readLine() ;
            String line  = br.readLine() ;
            while (line.charAt(0) == PortableAnyMap.COMMENT) {  // ignoring comments
                line = br.readLine() ;
            }

            scan = new Scanner(line) ;      // getting dimensions
            int width  = scan.nextInt() ;
            int height = scan.nextInt() ;

            line = br.readLine() ;
            scan = new Scanner(line) ;

            int maxVal = scan.nextInt() ;
            if (maxVal > PortableAnyMap.PPM_MAX_VAL) {
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
     * Save greyscale values into a .pgm file
     *
     * @param image    greyscale per pixels
     * @param filename destination
     */
    public static void writepgm(int[][] image, String filename) {
        PrintWriter pw = null ;
        try {
            pw = new PrintWriter(getDestination(filename)) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace() ;
        }

        assert pw != null;
        pw.write("") ;
        pw.flush() ;

        // Write plain pgm header
        pw.println(PortableAnyMap.P_PGM + SEPARATOR) ;
        pw.println(image[0].length + SEPARATOR + image.length + SEPARATOR) ;
        pw.println(PortableAnyMap.PGM_MAX_VAL) ;

        // write the value for each table cell
        for (int[] row : image) {
            int line_len = 0 ;

            for (int val : row) {
                String written = val + SEPARATOR ;

                if (line_len + written.length() > PortableAnyMap.PGM_MAX_LEN) {
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
     * Save pixel values into a .ppm file
     *
     * @param image    greyscale per pixels
     * @param filename destination
     */
    public static void writeppm(int[][][] image, String filename) {
        PrintWriter pw = null ;
        try {
            pw = new PrintWriter(getDestination(filename)) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace() ;
        }

        // clean the file
        assert pw != null;
        pw.write("") ;
        pw.flush() ;

        // Write plain ppm header
        pw.println(PortableAnyMap.P_PPM + SEPARATOR) ;
        pw.println(image[0].length + SEPARATOR + image.length + SEPARATOR) ;
        pw.println(PortableAnyMap.PPM_MAX_VAL) ;

        // write the value for each table cell
        int line_len = 0 ;
        StringBuilder buff ;
        for (int row[][] : image) {
            for (int px[] : row) {
                // clear buff
                buff = new StringBuilder() ;

                // add rgb vals to line
                for (int rgb : px) {
                    buff.append(rgb).append(SEPARATOR) ;
                }

                // separate rgb vals
                buff.append(LONG_SEPARATOR) ;

                // ensure ppm line limit
                if (buff.length() + line_len > PortableAnyMap.PPM_MAX_LEN) {
                    line_len = 0 ;
                    pw.write("\n") ;
                }
                line_len += buff.length() ;
                pw.write(buff.toString()) ;
            }
        }
        pw.close() ;
    }
}
