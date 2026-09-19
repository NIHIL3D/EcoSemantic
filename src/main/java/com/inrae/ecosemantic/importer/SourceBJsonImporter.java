package com.inrae.ecosemantic.importer;

import com.inrae.ecosemantic.model.DatasetMetadata;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SourceBJsonImporter {
    private final DatasetMapper datasetMapper;
    public SourceBJsonImporter(DatasetMapper datasetMapper) {
        this.datasetMapper = datasetMapper;
    }
    private record SourceBEntry(String identifier, String title, String siteName, String observedVariable) {}
    private final ObjectMapper objectMapper =  new ObjectMapper();

    public List<DatasetMetadata> read(Reader reader) {
        List<DatasetMetadata> datasetMetadata = new ArrayList<>();

        SourceBEntry[] entries = objectMapper.readValue(reader, SourceBEntry[].class);

        for (SourceBEntry entry : entries) {
            datasetMapper.map(
                    "b",
                    entry.identifier(),
                    entry.title(),
                    entry.siteName(),
                    entry.observedVariable()
            ).ifPresent(datasetMetadata::add);
        }
        return datasetMetadata;
    }
}