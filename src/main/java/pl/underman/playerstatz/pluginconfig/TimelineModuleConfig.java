package pl.underman.playerstatz.pluginconfig;

import lombok.Data;
import pl.underman.playerstatz.util.annotations.YamlConfig;

@YamlConfig(name = "timelineModule")
@Data
public class TimelineModuleConfig {
    private boolean enableModule = true;

    private boolean enablePlayerActivity = true;

    private int minSessionTime = 15;

    private int afkTimeout = 300;

    private boolean enableAfkCheck = true;

    private boolean enableDeathTracking = true;

    private boolean saveDeathLocation = true;

    private boolean enableAchievementTracking = true;

    public boolean isEnablePlayerActivity() {
        return enablePlayerActivity && enableModule;
    }

    public boolean isEnableAfkCheck() {
        return enableAfkCheck && enableModule;
    }

    public boolean isEnableDeathTracking() {
        return enableDeathTracking && enableModule;
    }
    

    public boolean isEnableAchievementTracking() {
        return enableAchievementTracking && enableModule;
    }
}
