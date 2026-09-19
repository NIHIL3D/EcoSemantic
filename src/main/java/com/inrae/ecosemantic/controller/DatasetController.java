package com.inrae.ecosemantic.controller;

import java.io.IOException;
import java.util.List;

import com.inrae.ecosemantic.service.DatasetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class DatasetController {
    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping("/datasets")
    public List<String> listDatasets(@RequestParam(required = false) String variable) throws IOException, InterruptedException {
//        return datasetService.findAll();
        return datasetService.getIds(variable);
    }

    @PostMapping("/datasets/rdf")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void uploadRdf() throws IOException, InterruptedException {
        datasetService.uploadToBlazegraph();
    }
}
