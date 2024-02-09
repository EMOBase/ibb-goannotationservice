package ibb.api.goannotationservice;

import java.util.Map;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "goannotationservice")
public interface Config {
    interface SpeciesConfig {
        String localGaf();
    }

    interface TargetConfig {
        String species();
        String taxon();
    }

    interface ESConfig {
        @WithDefault("false")
        boolean deleteIndexOnStart();

        String indexPrefix();
    }

    Map<String, SpeciesConfig> species();
    TargetConfig target();
    ESConfig elasticsearch();
}
