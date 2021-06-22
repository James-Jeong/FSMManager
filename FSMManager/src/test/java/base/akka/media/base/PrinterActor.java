package base.akka.media.base;

import akka.actor.AbstractActor;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class public class PrinterActor extends AbstractActor
 * @brief PrinterActor class
 */
public class PrinterActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(PrinterActor.class);

    public static final String PRINTER_ACTOR_NAME = "PrinterActor";

    static public Props props() {
        return Props.create(PrinterActor.class, PrinterActor::new);
    }

    static public class Printing {
        public final String message;

        public Printing(String message) {
            this.message = message;
        }
    }

    public PrinterActor() {
        // Nothing
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Printing.class, printing -> {
                    logger.info(printing.message);
                })
                .build();
    }

}
