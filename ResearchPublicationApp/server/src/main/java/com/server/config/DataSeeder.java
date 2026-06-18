package com.server.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.server.models.PublicationModel;
import com.server.repos.PublicationRepo;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PublicationRepo publicationRepo;

    public DataSeeder(PublicationRepo publicationRepo) {
        this.publicationRepo = publicationRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (publicationRepo.count() > 0) {
            return; // already seeded
        }

        PublicationModel p1 = new PublicationModel();
        p1.setTitle("Deep Learning for Image Recognition");
        p1.setDescription("A survey of CNN architectures and training techniques for image classification.");
        p1.setBannerImg("");
        p1.setAuthor("Alice Doe");
        p1.setAuthorId("u1");
        p1.setDomain("AI");
        p1.setKeywords(Arrays.asList("deep learning", "cnn", "vision"));
        p1.setPublishedDate("2024-05-01");
        p1.setPdfFileName(null);
        p1.setStatus("accepted");

        PublicationModel p2 = new PublicationModel();
        p2.setTitle("Efficient Data Structures for Search");
        p2.setDescription("Comparative study of tries, suffix arrays, and inverted indices for search engines.");
        p2.setBannerImg("");
        p2.setAuthor("Bob Smith");
        p2.setAuthorId("u2");
        p2.setDomain("Systems");
        p2.setKeywords(Arrays.asList("data structures", "search", "indexing"));
        p2.setPublishedDate("2023-11-12");
        p2.setPdfFileName(null);
        p2.setStatus("accepted");

        PublicationModel p3 = new PublicationModel();
        p3.setTitle("Natural Language Processing with Transformers");
        p3.setDescription("Applications of Transformer models to translation, QA, and summarization.");
        p3.setBannerImg("");
        p3.setAuthor("Carol Nguyen");
        p3.setAuthorId("u3");
        p3.setDomain("AI");
        p3.setKeywords(Arrays.asList("nlp", "transformers", "bert"));
        p3.setPublishedDate("2024-02-20");
        p3.setPdfFileName(null);
        p3.setStatus("accepted");

        publicationRepo.saveAll(List.of(p1, p2, p3));
    }
}
