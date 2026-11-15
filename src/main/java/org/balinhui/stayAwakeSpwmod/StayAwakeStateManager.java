package org.balinhui.stayAwakeSpwmod;

import java.util.HashMap;
import java.util.Map;

public class StayAwakeStateManager {
    private static final Map<String, Boolean> conditions = new HashMap<>(Map.of(
            "isPlaying", false
    ));
    private static boolean awake = false;
    private StayAwakeStateManager() {}

    public static void setCondition(String condition, boolean value) {
        conditions.replace(condition, value);
        notifyToChange();
    }

    private static void notifyToChange() {
        if (judgeConditions()) {
            keepScreenOn();
        } else {
            cancelScreenOn();
        }
    }

    private static boolean judgeConditions() {
        return conditions.get("isPlaying");
    }

    public static void keepScreenOn() {
        if (awake) return;

        StayAwakeHelper.stayAwake(true);

        awake = true;
    }

    public static void cancelScreenOn() {
        if (!awake) return;

        StayAwakeHelper.stayAwake(false);

        awake = false;
    }
}
