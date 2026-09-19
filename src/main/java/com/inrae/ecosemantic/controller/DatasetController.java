package com.inrae.ecosemantic.controller;

import java.util.List;

import com.inrae.ecosemantic.service.DatasetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inrae.ecosemantic.model.DatasetMetadata;


@RestController
@RequestMapping("/api")
public class DatasetController {
    DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping("/datasets")
    public List<DatasetMetadata> listDatasets(){
        return datasetService.findAll();
    }
}
