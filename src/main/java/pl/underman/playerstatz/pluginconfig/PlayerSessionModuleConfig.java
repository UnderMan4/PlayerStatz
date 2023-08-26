package pl.underman.playerstatz.pluginconfig;

import lombok.Data;
import pl.underman.playerstatz.util.annotations.YamlConfig;

@YamlConfig(name = "playerSessionModule")
@Data
public class PlayerSessionModuleConfig {
    private boolean enableModule   = true;
    private int     afkTimeout     = 300;
    private int     minSessionTime = 15;
}
