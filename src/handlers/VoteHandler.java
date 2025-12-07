package handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.VoteJournal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

public class VoteHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String candidateId = null;

        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            candidateId = parseFormData(formData).get("candidateId");
        } else if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            candidateId = parseFormData(query).get("candidateId");
        }

        if (candidateId != null && VoteJournal.getCandidateById(candidateId) != null) {
            VoteJournal.addVote(candidateId);

            String cookie = String.format("votedForId=%s; Path=/; HttpOnly", candidateId);
            exchange.getResponseHeaders().add("Set-Cookie", cookie);

            exchange.getResponseHeaders().set("Location", "/thankyou");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
        } else {

            String response = "404 Candidate Not Found";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, response.length());
            try (var os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private Map<String, String> parseFormData(String formData) {
        return Arrays.stream(formData.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(
                        a -> decode(a[0]),
                        a -> decode(a.length > 1 ? a[1] : ""))
                );
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
