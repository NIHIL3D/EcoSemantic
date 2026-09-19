package com.inrae.ecosemantic.service;

import com.inrae.ecosemantic.importer.SourceACsvImporter;
import com.inrae.ecosemantic.importer.SourceBJsonImporter;
import com.inrae.ecosemantic.model.DatasetMetadata;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetService {
    private final List<DatasetMetadata> datasets = new ArrayList<>();

    public DatasetService(SourceACsvImporter csv, SourceBJsonImporter json) throws IOException {
            datasets.addAll(csv.read(getReader("data/source-a.csv")));
            datasets.addAll(json.read(getReader("data/source-b.json")));
    }

    private Reader getReader(String filename) throws IOException {
        return new InputStreamReader(new ClassPathResource(filename).getInputStream(), StandardCharsets.UTF_8);
    }

    public List<DatasetMetadata> findAll() {
        return List.copyOf(datasets);
    }
}
