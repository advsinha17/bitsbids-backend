package com.bitsbids.bitsbids.AnonymousUser;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Component
public class AnonUsernameGenerator {

    private List<String> adjectives;
    private List<String> nouns;
    private Random random = new Random();

    public AnonUsernameGenerator() throws IOException {
        Resource adjectivesResource = new ClassPathResource("adjectives.txt");
        Resource nounsResource = new ClassPathResource("nouns.txt");
        adjectives = Files.readAllLines(Paths.get(adjectivesResource.getURI()));
        nouns = Files.readAllLines(Paths.get(nounsResource.getURI()));
    }

    public String generateUsername() {
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String noun = nouns.get(random.nextInt(nouns.size()));
        int number = 100 + random.nextInt(900);
        return adjective + noun + number;
    }
}
