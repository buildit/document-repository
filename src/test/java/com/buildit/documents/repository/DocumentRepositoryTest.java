package com.buildit.documents.repository;

import com.buildit.documents.model.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.allegro.tech.embeddedelasticsearch.PopularProperties.TRANSPORT_TCP_PORT;

public class DocumentRepositoryTest {

    private static final String ELASTIC_VERSION = "6.0.1";
    private static final Integer TRANSPORT_TCP_PORT_VALUE = 9930;


    private EmbeddedElastic embeddedElastic;
    private DocumentRepository documentRepository;

    @Before
    public void setUp() throws Exception {
        embeddedElastic = EmbeddedElastic.builder()
                .withElasticVersion(ELASTIC_VERSION)
                .withSetting(TRANSPORT_TCP_PORT, TRANSPORT_TCP_PORT_VALUE)
                .withEsJavaOpts("-Xms128m -Xmx512m")
                .build()
                .start();

        documentRepository = DocumentRepository.builder()
                .withServerUrl("http://localhost:" + embeddedElastic.getHttpPort())
                .build();

        /*documentRepository = DocumentRepository.builder()
                .withServerUrl("https://search-search-bp-documents-dev-nucubutwqzfkl4zibdzec7cygq.us-east-1.es.amazonaws.com")
                .withAccessKey("AKIAJX7LDE3M5AINNN3Q")
                .withSecretKey("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")
                .build();*/
    }

    @Test
    public void testInsertAndFindById() throws Exception {
        Document document = new Document();
        document.setContent("Stately, plump Buck Mulligan came from the stairhead, bearing a bowl of lather on which a mirror and a razor lay crossed.");
        document.setTitle("Ulysses");

        String id = documentRepository.insert(document);

        Document result = documentRepository.findById(id);

        assertThat(result).isEqualTo(document);
    }

    @Test
    public void testInsertAndFindAll() throws Exception {
        Document firstDocument = new Document();
        firstDocument.setContent("I am by birth a Genevese, and my family is one of the most distinguished of that republic.");
        firstDocument.setTitle("Frankenstein");
        documentRepository.insert(firstDocument);

        Document secondDocument = new Document();
        secondDocument.setContent("My father had a small estate in Nottinghamshire: I was the third of five sons.");
        secondDocument.setTitle("Gulliver's Travels");
        documentRepository.insert(secondDocument);

        embeddedElastic.refreshIndices();

        List<Document> result = documentRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @After
    public void tearDown() throws Exception {
        embeddedElastic.stop();
    }
}
