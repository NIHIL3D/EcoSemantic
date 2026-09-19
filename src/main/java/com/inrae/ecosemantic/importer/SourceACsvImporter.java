package com.inrae.ecosemantic.importer;

import com.inrae.ecosemantic.model.DatasetMetadata;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SourceACsvImporter {
    private final DatasetMapper datasetMapper;

    public SourceACsvImporter(DatasetMapper mapper) {
        this.datasetMapper = mapper;
    }

    public List<DatasetMetadata> read(Reader reader) throws IOException {
        List<DatasetMetadata> datasetMetadata = new ArrayList<>();

        var format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        try (CSVParser parser = format.parse(reader)) {
            for (CSVRecord record : parser) {
                datasetMapper.map(
                        "a",
                        record.get("id"),
                        record.get("label"),
                        record.get("station"),
                        record.get("parameter")
                ).ifPresent(datasetMetadata::add);
            }
        }
        return datasetMetadata;
    }
}
