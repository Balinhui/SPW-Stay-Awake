package org.balinhui.stayAwakeSpwmod;

import com.xuncorp.spw.workshop.api.PlaybackExtensionPoint;
import org.pf4j.Extension;

@Extension
public class PlaybackExtension implements PlaybackExtensionPoint {
    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (!MainPlugin.isWindows) return;
        StayAwakeStateManager.setCondition("isPlaying", isPlaying);
    }
}
