import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class
 * @brief
 */
public class FsmManagerMain {

    private static final Logger logger = LoggerFactory.getLogger(FsmManagerMain.class);

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Argument Error. (&0: FsmManagerMain, &1: config_path)");
            return;
        }

        String configPath = args[1].trim();
        logger.info("| Config path: {}", configPath);

    }

}
