package ibb.api.goannotationservice;

import java.util.Map;

import io.smallrye.config.ConfigMapping;

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
        boolean deleteIndexOnStart();
        String indexPrefix();
    }

    Map<String, SpeciesConfig> species();
    TargetConfig target();
    ESConfig elasticsearch();
}
