package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import models.Candidate;
import models.VoteJournal;
import utils.TemplateRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int totalVotes = VoteJournal.getTotalVotes();
        List<Candidate> sortedCandidates = VoteJournal.getCandidatesSortedByVotesDescending();

        List<Map<String, Object>> displayList = sortedCandidates.stream().map(candidate -> {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("name", candidate.getName());
            dataModel.put("photo", candidate.getPhoto());
            double percentage = (totalVotes > 0) ? (double) candidate.getVotes() * 100 / totalVotes : 0;
            dataModel.put("percentage", String.format("%.1f", percentage));
            dataModel.put("count", candidate.getVotes());
            return dataModel;
        }).collect(Collectors.toList());

        try {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("votesList", displayList);
            String response = TemplateRenderer.processTemplate("results.ftlh", dataModel);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }
}
