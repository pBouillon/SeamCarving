package seamcarving;

/**
 * References for portable anymap
 *
 * @version 1.0
 */

public class PortableAnymap {

    public static final char COMMENT = '#' ;

    /**
     * Magic numbers for portable anymap formats
     */
    public static final String PBM    =  "P1" ;
    public static final String P_PGM  =  "P2" ; // see http://netpbm.sourceforge.net/doc/pgm.html#plainpgm
    public static final String P_PPM  =  "P3" ; // see http://netpbm.sourceforge.net/doc/ppm.html#plainppm
    public static final String PGM    =  "P5" ; // see http://netpbm.sourceforge.net/doc/pgm.html
    public static final String PPM    =  "P6" ; // see http://netpbm.sourceforge.net/doc/ppm.html

    /**
     * Portable grey maps constraints
     */
    public static final int PGM_MAX_LEN =   70 ;
    public static final int PGM_MAX_VAL =  255 ;

    /**
     * Portable pixel map constraints
     */
    public static final int PPM_MAX_LEN =   70 ;
    public static final int PPM_MAX_VAL =  255 ;
}