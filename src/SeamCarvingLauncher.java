import seamcarving.SeamCarving;

/**
 * Seam Carving launcher
 *
 * @version 2.0
 */
public class SeamCarvingLauncher {

    public static void main(String[] args) {
        Parser launcher = new Parser() ;
        launcher.parse(args) ;

        SeamCarving.resizeImage (
                launcher.getKeep().clone(),
                launcher.getDel().clone(),
                launcher.getFiles().clone(),
                launcher.isLong_meth(),
                launcher.isToggle(),
                launcher.isLines(),
                launcher.isGrey(),
                launcher.isIncrease(),
                launcher.isVerbose()
        ) ;
    }
}
