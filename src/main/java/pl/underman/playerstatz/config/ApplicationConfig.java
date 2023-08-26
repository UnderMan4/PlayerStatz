package pl.underman.playerstatz.config;

import pl.underman.playerstatz.util.annotations.ComponentScan;
import pl.underman.playerstatz.util.annotations.ConfigScan;
import pl.underman.playerstatz.util.annotations.Configuration;

@Configuration
@ComponentScan(
        {
                "pl.underman.playerstatz.services",
                "pl.underman.playerstatz.repositories",
                "pl.underman.playerstatz.listeners",
                "pl.underman.playerstatz.commands",
                "pl.underman.playerstatz.hibernate"
        }
)
@ConfigScan(
        {
                "pl.underman.playerstatz.pluginconfig"
        }
)
public class ApplicationConfig {
}
