package org.balinhui.stayAwakeSpwmod;

import com.xuncorp.spw.workshop.api.PluginContext;
import com.xuncorp.spw.workshop.api.SpwPlugin;
import com.xuncorp.spw.workshop.api.WorkshopApi;
import org.balinhui.stayAwakeSpwmod.services.ServicesManager;
import org.balinhui.stayAwakeSpwmod.tools.Toast;
import org.jspecify.annotations.NonNull;

public class MainPlugin extends SpwPlugin {

    public static boolean isWindows;

    public MainPlugin(@NonNull PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        isWindows = System.getProperty("os.name").contains("Windows");
        if (isWindows)
            Toast.succeed("已为 Windows 系统启用");
        else Toast.warned("非 Windows 系统，将不启用功能");

        if (!isWindows) return;
        ServicesManager.getManager().initAll();
        WorkshopApi.playback().pause();
    }

    @Override
    public void stop() {
        if (!isWindows) return;
        ServicesManager.shutdown();
        Toast.succeed("已关闭功能");
    }
}
