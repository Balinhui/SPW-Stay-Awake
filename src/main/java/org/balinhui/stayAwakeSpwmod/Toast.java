package org.balinhui.stayAwakeSpwmod;

import com.xuncorp.spw.workshop.api.WorkshopApi;

public class Toast {
    private Toast() {}

    public static void succeed(String info) {
        String fullInfo = "Stay Awake: " + info;
        WorkshopApi.ui().toast(fullInfo, WorkshopApi.Ui.ToastType.Success);
    }

    public static void error(String info) {
        String fullInfo = "Stay Awake: " + info;
        WorkshopApi.ui().toast(fullInfo, WorkshopApi.Ui.ToastType.Error);
    }

    public static void warned(String info) {
        String fullInfo = "Stay Awake: " + info;
        WorkshopApi.ui().toast(fullInfo, WorkshopApi.Ui.ToastType.Warning);
    }
}
