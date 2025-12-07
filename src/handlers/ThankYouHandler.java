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
import java.util.Arrays;


public class ThankYouHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String votedForId = getVotedForIdFromCookie(exchange);
        Candidate votedFor = VoteJournal.getCandidateById(votedForId);
        int totalVotes = VoteJournal.getTotalVotes();

        if (votedFor == null || totalVotes == 0) {
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
            return;
        }

        try {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("candidate", votedFor);
            double percentage = (double) votedFor.getVotes() * 100 / totalVotes;
            dataModel.put("percentage", String.format("%.1f", percentage));

            String response = TemplateRenderer.processTemplate("thankyou.ftlh", dataModel);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    private String getVotedForIdFromCookie(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            return Arrays.stream(cookieHeader.split(";"))
                    .map(String::trim)
                    .filter(c -> c.startsWith("votedForId="))
                    .findFirst()
                    .map(c -> c.substring("votedForId=".length()))
                    .orElse(null);
        }
        return null;
    }
}
