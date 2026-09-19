package com.inrae.ecosemantic.rdf;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;

@Component
public class BlazegraphClient {
    private final String endpoint;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public BlazegraphClient(@Value("${blazegraph.endpoint}") String endpoint) {
        this.endpoint = endpoint;
    }
    public void upload(Model model) throws IOException, InterruptedException {
        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, model, Lang.TURTLE);

        String graph = URLEncoder.encode("https://example.org/ecocatalogue/graph/catalogue", StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "?context-uri=" + graph))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "text/turtle; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(stringWriter.toString(), StandardCharsets.UTF_8))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300)  {
            throw new IOException("Blazegraph HTTP Error " + response.statusCode() + " : " + response.body());
        }
    }

    public List<String> queryByVariable(String variable) throws IOException, InterruptedException {
        String sparql = buildVariableQuery(variable);
        String encodedQuery = URLEncoder.encode(sparql, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "?query=" + encodedQuery))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/sparql-results+json")
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300)  {
            throw new IOException("Blazegraph HTTP Error " + response.statusCode() + " : " + response.body());
        }

        List<String> ids = new ArrayList<>();

        try (var input = new ByteArrayInputStream(
                response.body().getBytes(StandardCharsets.UTF_8))) {

            var results = ResultSetFactory.fromJSON(input);

            while (results.hasNext()) {
                var row = results.nextSolution();
                ids.add(row.getLiteral("id").getString());
            }
        }

        return ids;
    }

    private String buildVariableQuery(String variable){
        if(!"soil_temperature".equals(variable) && !"soil_moisture".equals(variable)){
            throw new IllegalArgumentException("Unknown variable: " + variable);
        }

        var query = new ParameterizedSparqlString("""
                PREFIX ex: <https://example.org/ecocatalogue/>
                PREFIX dct: <http://purl.org/dc/terms/>
                SELECT ?id
                WHERE{
                    GRAPH <https://example.org/ecocatalogue/graph/catalogue> {
                        ?dataset ex:variable ?requestedVariable;
                            dct:identifier ?id .
                    }
                }
                """);
        query.setIri("requestedVariable", "https://example.org/ecocatalogue/variable/" + variable);
        return query.toString();
    }


}
