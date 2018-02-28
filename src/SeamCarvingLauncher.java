import graph.Graph;
import seamcarving.PortableAnymap;
import seamcarving.SeamCarving;

/**
 * Seam Carving launcher
 *
 * @version 1.0
 */
public class SeamCarvingLauncher {

    public static void main(String[] args) {
        Parser launcher = new Parser() ;
        launcher.parse(args) ;

        SeamCarving.resizeImage (
                launcher.getKeep(),
                launcher.getDel(),
                launcher.getFiles(),
                launcher.isSimple(),
                launcher.isToggle(),
                launcher.isVerbose()
        ) ;
    }
}
