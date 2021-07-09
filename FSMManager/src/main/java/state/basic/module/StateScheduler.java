package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.module.base.AbstractStateTaskUnit;
import state.basic.module.base.EventCondition;
import state.basic.unit.StateUnit;

import java.util.List;
import java.util.Map;

/**
 * @class public class StateScheduler
 * @brief StateScheduler class
 */
public class StateScheduler extends AbstractStateTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateScheduler.class);

    private final StateHandler stateHandler;
    private final String handlerName;

    ////////////////////////////////////////////////////////////////////////////////

    protected StateScheduler(StateHandler stateHandler, int delay) {
        super(delay);

        this.stateHandler = stateHandler;
        this.handlerName = stateHandler.getName();
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {
        try {
            StateManager stateManager = StateManager.getInstance();

            // 1) 개발자가 등록한 이벤트 리스트를 가져온다.
            List<EventCondition> eventConditionList = stateHandler.getEventConditionList();
            if (eventConditionList.isEmpty()) {
                return;
            }

            // 2) 현재 StateManager 에 등록된 StateUnit Map 을 가져온다.
            Map<String, StateUnit> stateUnitMap = stateManager.getStateUnitMap();
            if (stateUnitMap.isEmpty()) {
                return;
            }

            // 3) 등록한 각각의 이벤트의 From state 가 개별적인 StateUnit 의 From state 와 같으면, 등록한 이벤트를 실행한다.
            for (EventCondition eventCondition : eventConditionList) {
                if (eventCondition == null) { continue; }

                StateEvent stateEvent = eventCondition.getStateEvent();
                if (stateEvent == null) { continue; }

                String fromState = stateEvent.getFromState();
                for (StateUnit stateUnit : stateUnitMap.values()) {
                    if (stateUnit == null) { continue; }

                    // StateUnit 의 StateHandler 이름과 다르면 다른 StateUnit 검색
                    if (!stateUnit.getHandlerName().equals(handlerName)) { continue; }

                    eventCondition.setCurStateUnit(stateUnit);
                    if (stateUnit.getCurState().equals(fromState) && eventCondition.checkCondition()) {
                        logger.info("({}) Event is triggered by scheduler. (event={}, stateUnit={})",
                                handlerName, stateEvent, stateUnit
                        );

                        stateHandler.fire(
                                stateEvent.getName(),
                                stateUnit
                        );
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("({}) StateScheduler.run.Exception", handlerName, e);
        }
    }

}
