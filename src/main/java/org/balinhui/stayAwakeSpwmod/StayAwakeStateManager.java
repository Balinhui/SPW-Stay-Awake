package org.balinhui.stayAwakeSpwmod;

import org.balinhui.stayAwakeSpwmod.services.ServicesManager;
import org.balinhui.stayAwakeSpwmod.services.StayAwakeService;

import java.util.HashMap;
import java.util.Map;

public class StayAwakeStateManager {
    private static final Map<String, Boolean> conditions = new HashMap<>(Map.of(
            "isPlaying", false//每次启用插件会暂停播放，因此其初始值为false
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

        //StayAwakeHelper.stayAwake(true);
        ServicesManager.getManager().getService(StayAwakeService.class).stayAwake(true);

        awake = true;
    }

    public static void cancelScreenOn() {
        if (!awake) return;

        //StayAwakeHelper.stayAwake(false);
        ServicesManager.getManager().getService(StayAwakeService.class).stayAwake(false);

        awake = false;
    }
}
