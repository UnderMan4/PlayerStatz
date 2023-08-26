package pl.underman.playerstatz.pluginconfig;

import lombok.Data;
import pl.underman.playerstatz.util.annotations.YamlConfig;

@YamlConfig(name = "config")
@Data
public class MainConfig {
    private boolean debugMode = false;
    private WebServerConfig webServer = new WebServerConfig();
}
