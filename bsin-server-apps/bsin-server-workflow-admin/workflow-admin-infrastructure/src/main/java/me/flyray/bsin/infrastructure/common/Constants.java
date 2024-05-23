package me.flyray.bsin.infrastructure.common;

public class Constants {

    public static final String OPERATION_FINISH = "完成";

    public static final String OPERATION_RESOLVE = "解决";

    public static final String OPERATION_DELEGATE = "委派";

    public static final String OPERATION_TRANSFER = "转办";

    public static final String OPERATION_CLAIM = "认领";

    public static final String OPERATION_REJECT = "驳回";

    public static final String OPERATION_SPECIAL = "特事特办";

    public static final String OPERATION_AUTH_AGENT = "授权代办";

    public static final String SEPERATION_COMMA = ",";

    public static final String SEPERATION_EQUAL_SIGN = "=";

    public static final String SEPERATION_VERTICAL_LINE = "\\|";

    public static final String MARK_LEFT_BRACE = "{";

    public static final String MARK_RIGHT_BRACE = "}";

    public static final String KEY_FOR_VARIABLE_ONLY_VISIBLE_IN_TASK = "varOnlyVisibleInTask";

    public static final String REMARK_COUNTERSIGN = "会签任务";

    public static final String REMARK_COUNTERSIGN_DENY = "会签被否决，流程终止";

    public static final String KEY_DENY_LIST = "denyList";

    public static final String KEY_HISTORY_RECORD = "historicRecord";

    public static final String KEY_NUMBER_OF_INSTANCES = "nrOfInstances";

    public static final String KEY_NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    public static final String KEY_NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

    public static final String KEY_LOOP_COUNTER = "loopCounter";

    public static final String[] INNER_KEYS = new String[]{
            Constants.KEY_HISTORY_RECORD,
            Constants.KEY_DENY_LIST,
            Constants.KEY_FOR_VARIABLE_ONLY_VISIBLE_IN_TASK,
            Constants.KEY_NUMBER_OF_INSTANCES,
            Constants.KEY_NUMBER_OF_ACTIVE_INSTANCES,
            Constants.KEY_NUMBER_OF_COMPLETED_INSTANCES,
            Constants.KEY_LOOP_COUNTER
        };

}
