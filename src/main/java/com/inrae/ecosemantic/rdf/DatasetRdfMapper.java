package com.inrae.ecosemantic.rdf;



import com.inrae.ecosemantic.model.DatasetMetadata;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Component;
import org.apache.jena.rdf.model.Model;

import java.util.List;

@Component
public class DatasetRdfMapper {
    private static final String EX = "https://example.org/ecocatalogue/";
    private static final String DCAT = "http://www.w3.org/ns/dcat#";
    private static final String DCT = "http://purl.org/dc/terms/";

    public Model toModel(List<DatasetMetadata> datasets){
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("ex", EX);
        model.setNsPrefix("dcat", DCAT);
        model.setNsPrefix("dct", DCT);

        for (DatasetMetadata dataset : datasets){
            var resource = model.createResource(EX + "dataset-" + dataset.id().replace(":", "-"));
            resource.addProperty(RDF.type, model.createResource(DCAT + "Dataset"));
            resource.addProperty(model.createProperty(DCT + "title"), model.createLiteral(dataset.title(), "fr"));
            resource.addProperty(model.createProperty(DCT + "identifier"), dataset.id());
            resource.addProperty(model.createProperty(EX + "site"), model.createLiteral(dataset.site(), "fr"));
            resource.addProperty(model.createProperty(EX + "variable"), model.createResource(EX + "variable/" + dataset.variable()));
            resource.addProperty(model.createProperty(EX + "source"), dataset.source());
            resource.addProperty(model.createProperty(EX + "sourceId"), dataset.sourceId());
        }

        return model;
    }
}
