package com.inrae.ecosemantic.importer;

import com.inrae.ecosemantic.model.DatasetMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.Optional;

@Component
public class DatasetMapper {
    static final Map<String, String> NORMALIZED = Map.of(
            "soil_temp", "soil_temperature",
            "soil_temperature", "soil_temperature",
            "soil_moisture", "soil_moisture"
    );
    private static final Logger log = LoggerFactory.getLogger(DatasetMapper.class);

    public Optional<DatasetMetadata> map(String source, String sourceId, String title, String site, String rawVariable) {
        if(source == null || source.isBlank() ||
            sourceId == null || sourceId.isBlank() ||
            title == null || title.isBlank() ||
            site == null || site.isBlank() ||
            rawVariable == null || rawVariable.isBlank()
        ){
            log.warn("Rejected entry : source={}, sourceId={}, cause=Missing field",
                    source, sourceId);
            return Optional.empty();
        }
        if(!NORMALIZED.containsKey(rawVariable)){
            log.warn("Rejected entry : source={}, sourceId={}, cause=Unknown variable: {}",
                    source, sourceId, rawVariable);
            return Optional.empty();
        }
        String variable =  NORMALIZED.get(rawVariable);
        String id = source + ":" + sourceId;
        return Optional.of(new DatasetMetadata(id, sourceId, source, title, site, variable));
    }
}
