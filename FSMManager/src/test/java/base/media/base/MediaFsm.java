package base.media.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.base.AbstractFsm;
import state.base.TransitionContext;

/**
 * @class public class MediaFsm extends AbstractFsm
 * @brief MediaFsm class
 */
public class MediaFsm extends AbstractFsm {

    private static final Logger logger = LoggerFactory.getLogger(MediaFsm.class);

    public MediaFsm() {
        // Nothing
    }

    public void mediaStart (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Media is started! ({} > {}) (event={})", from, to, event);
    }

    public void mediaStop (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Media is stopped! ({} > {}) (event={})", from, to, event);
    }

    public void mediaCreateSuccess (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Success to create media! ({} > {}) (event={})", from, to, event);
    }

    public void mediaCreateFail (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Fail to create media! ({} > {}) (event={})", from, to, event);
    }

    public void mediaDeleteSuccess (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Success to delete media! ({} > {}) (event={})", from, to, event);
    }

    public void mediaDeleteFail (Object from, Object to, Object event, Object transitionContext) {
        logger.info("@ Fail to delete media! ({} > {}) (event={})", from, to, event);
    }

}
