package models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VoteJournal {
    private static final Map<String, Candidate> candidates = new ConcurrentHashMap<>();
    private static final Path JSON_PATH = Paths.get("src/data/candidates.json");

    static {
        loadCandidatesFromJson();
    }

    private static void loadCandidatesFromJson() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(JSON_PATH.toFile())) {
            Type candidateListType = new TypeToken<List<Map<String, String>>>(){}.getType();
            List<Map<String, String>> rawCandidates = gson.fromJson(reader, candidateListType);

            for (int i = 0; i < rawCandidates.size(); i++) {
                Map<String, String> raw = rawCandidates.get(i);
                String id = String.valueOf(i + 1);
                Candidate candidate = new Candidate(id, raw.get("name"), raw.get("photo"));
                candidates.put(id, candidate);
            }
        } catch (IOException e) {
            System.err.println("Error loading candidates from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Candidate> getAllCandidates() {
        return List.copyOf(candidates.values());
    }

    public static Candidate getCandidateById(String id) {
        return candidates.get(id);
    }

    public static void addVote(String id) {
        Candidate candidate = candidates.get(id);
        if (candidate != null) {
            candidate.addVote();
        }
    }

    public static int getTotalVotes() {
        return candidates.values().stream().mapToInt(Candidate::getVotes).sum();
    }

    public static List<Candidate> getCandidatesSortedByVotesDescending() {
        return candidates.values().stream()
                .sorted(Comparator.comparingInt(Candidate::getVotes).reversed())
                .collect(Collectors.toList());
    }
}