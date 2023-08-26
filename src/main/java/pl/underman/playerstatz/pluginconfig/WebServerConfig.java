package pl.underman.playerstatz.pluginconfig;

import lombok.Data;
import pl.underman.playerstatz.util.annotations.NotNull;

@Data
public class WebServerConfig {

    private int    port     = 3031;
    @NotNull
    private String webTitle = "PlayerStatz";
}
