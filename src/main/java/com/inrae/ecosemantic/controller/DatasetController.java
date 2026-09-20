package com.inrae.ecosemantic.controller;

import java.io.IOException;
import java.util.List;

import com.inrae.ecosemantic.model.DatasetMetadata;
import com.inrae.ecosemantic.service.DatasetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api")
public class DatasetController {
    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping("/datasets")
    public List<DatasetMetadata> listDatasets(@RequestParam(required = false) String variable) throws IOException, InterruptedException {
        try {
            if (variable == null) {
                return datasetService.findAll();
            }
            return datasetService.findByVariable(variable);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/datasets/rdf")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadRdf() throws IOException, InterruptedException {
        datasetService.uploadToBlazegraph();
    }
}
