/**
 * References for portable pixmaps
 */

class PortablePixmap {

    /**
     * Magic numbers for portable pixmaps formats
     */
    public static final String PBM    =  "P1" ;
    public static final String P_PGM  =  "P2" ; // see http://netpbm.sourceforge.net/doc/pgm.html#plainpgm
    public static final String PPM    =  "P3" ;
    public static final String PGM    =  "P5" ;

    /**
     * Portable pixmaps constraints
     */
    public static final int PGM_MAX_PGM_LEN  =   70 ;
    public static final int PGM_MAX_VAL      =  255 ;
}