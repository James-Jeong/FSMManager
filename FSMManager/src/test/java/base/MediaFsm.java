package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.base.AbstractFsm;

/**
 * @class
 * @brief
 */
public class MediaFsm extends AbstractFsm {

    private static final Logger logger = LoggerFactory.getLogger(MediaFsm.class);

    public MediaFsm() {
        // Nothing
    }

    public void mediaStart () {
        logger.debug("@ Media is started!");
    }

    public void mediaStop () {
        logger.debug("@ Media is stopped!");
    }

    public void mediaCreateSuccess () {
        logger.debug("@ Success to create media!");
    }

    public void mediaCreateFail () {
        logger.debug("@ Fail to create media!");
    }

    public void mediaDeleteSuccess () {
        logger.debug("@ Success to delete media!");
    }

    public void mediaDeleteFail () {
        logger.debug("@ Fail to delete media!");
    }

}
