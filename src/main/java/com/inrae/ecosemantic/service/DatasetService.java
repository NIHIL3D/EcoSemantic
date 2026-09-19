package com.inrae.ecosemantic.service;

import com.inrae.ecosemantic.importer.SourceACsvImporter;
import com.inrae.ecosemantic.importer.SourceBJsonImporter;
import com.inrae.ecosemantic.model.DatasetMetadata;
import com.inrae.ecosemantic.rdf.BlazegraphClient;
import com.inrae.ecosemantic.rdf.DatasetRdfMapper;
import org.apache.jena.rdf.model.Model;
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
    private final DatasetRdfMapper datasetMapper;
    private final BlazegraphClient  blazegraphClient;

    public DatasetService(SourceACsvImporter csv, SourceBJsonImporter json, DatasetRdfMapper datasetٌRdfMapper, BlazegraphClient blazegraphClient) throws IOException {
        this.datasetMapper = datasetٌRdfMapper;
        this.blazegraphClient = blazegraphClient;
        datasets.addAll(csv.read(getReader("data/source-a.csv")));
        datasets.addAll(json.read(getReader("data/source-b.json")));
    }

    private Reader getReader(String filename) throws IOException {
        return new InputStreamReader(new ClassPathResource(filename).getInputStream(), StandardCharsets.UTF_8);
    }

    public List<DatasetMetadata> findAll(){
        return List.copyOf(datasets);
    }

    public void uploadToBlazegraph() throws IOException, InterruptedException {
        Model model = datasetMapper.toModel(datasets);
        try {
            blazegraphClient.upload(model);
        } finally {
            model.close();
        }
    }

    public List<String> getIds(String variable) throws IOException, InterruptedException {
        return blazegraphClient.queryByVariable(variable);

    }
}
