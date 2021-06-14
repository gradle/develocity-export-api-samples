package com.gradle.enterprise.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;

public final class ExportApiJavaExample {

    private static final HttpUrl GRADLE_ENTERPRISE_SERVER_URL = HttpUrl.parse("https://gradle.my-company.com");
    private static final String EXPORT_API_USERNAME = "username";
    private static final String EXPORT_API_PASSWORD = "password";
    private static final Map<String, String> BUILD_AGENT_EVENT_BY_BUILD_TOOL = new ImmutableMap.Builder<String, String>()
            .put("gradle", "BuildAgent")
            .put("maven", "MvnBuildAgent")
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_BUILD_SCANS_STREAMED_CONCURRENTLY = 30;

    public static void main(String[] args) throws Exception {
        Instant since1Day = now().minus(Duration.ofHours(24));

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ZERO)
                .readTimeout(Duration.ZERO)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(MAX_BUILD_SCANS_STREAMED_CONCURRENTLY, 30, TimeUnit.SECONDS))
                .authenticator(Authenticators.basic(EXPORT_API_USERNAME, EXPORT_API_PASSWORD))
                .build();
        httpClient.dispatcher().setMaxRequests(MAX_BUILD_SCANS_STREAMED_CONCURRENTLY);
        httpClient.dispatcher().setMaxRequestsPerHost(MAX_BUILD_SCANS_STREAMED_CONCURRENTLY);

        EventSource.Factory eventSourceFactory = EventSources.createFactory(httpClient);
        ExtractUsernamesFromBuilds listener = new ExtractUsernamesFromBuilds(eventSourceFactory);

        eventSourceFactory.newEventSource(requestBuilds(since1Day), listener);
        List<String> usernames = listener.getUsernames().get();

        List<String> results = usernames.stream()
                .collect(Collectors.groupingBy(username -> username, Collectors.counting()))
                .entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .sorted()
                .collect(Collectors.toList());
        System.out.println("Results: " + results);

        // Cleanly shuts down the HTTP client, which speeds up process termination
        shutdown(httpClient);
    }

    @NotNull
    private static Request requestBuilds(Instant since1Day) {
        return new Request.Builder()
                .url(GRADLE_ENTERPRISE_SERVER_URL.resolve("/build-export/v2/builds/since/" + since1Day.toEpochMilli()))
                .build();
    }

    @NotNull
    private static Request requestBuildScan(String buildId, String buildTool) {
        return new Request.Builder()
                .url(GRADLE_ENTERPRISE_SERVER_URL.resolve("/build-export/v2/build/" + buildId + "/events?eventTypes=" + Joiner.on(",").join(ImmutableSet.of(BUILD_AGENT_EVENT_BY_BUILD_TOOL.get(buildTool)))))
                .build();
    }

    private static class ExtractUsernamesFromBuilds extends PrintFailuresEventSourceListener {
        private final List<CompletableFuture<String>> candiateUsernames = new ArrayList<>();
        private final CompletableFuture<List<String>> usernames = new CompletableFuture<>();
        private final EventSource.Factory eventSourceFactory;

        private ExtractUsernamesFromBuilds(EventSource.Factory eventSourceFactory) {
            this.eventSourceFactory = eventSourceFactory;
        }

        public CompletableFuture<List<String>> getUsernames() {
            return usernames;
        }

        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            System.out.println("Streaming builds...");
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
            JsonNode json = parse(data);
            JsonNode buildToolJson = json.get("toolType");
            if (buildToolJson == null || buildToolJson.asText().equals("gradle") || buildToolJson.asText().equals("maven")) {
                final String buildId = json.get("buildId").asText();
                final String buildTool = buildToolJson != null ? buildToolJson.asText() : "gradle";

                Request request = requestBuildScan(buildId, buildTool);

                ExtractUsernameFromBuildScan listener = new ExtractUsernameFromBuildScan(buildId);
                eventSourceFactory.newEventSource(request, listener);
                candiateUsernames.add(listener.getUsername());
            }
        }

        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            usernames.complete(candiateUsernames.stream()
                    .map(u -> {
                        try {
                            return u.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return "Unknown";
                        }
                    })
                    .collect(Collectors.toList())
            );
        }
    }

    private static class ExtractUsernameFromBuildScan extends PrintFailuresEventSourceListener {
        private final String buildId;
        private final CompletableFuture<String> username = new CompletableFuture<>();

        private ExtractUsernameFromBuildScan(String buildId) {
            this.buildId = buildId;
        }

        public CompletableFuture<String> getUsername() {
            return username;
        }

        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            System.out.println("Streaming events for : " + buildId);
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
            if (type.equals("BuildEvent")) {
                JsonNode eventJson = parse(data);
                username.complete(eventJson.get("data").get("username").asText());
            }
        }

        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            // Complete only sets the value if it hasn't already been set
            username.complete("Unknown");
        }
    }

    private static class PrintFailuresEventSourceListener extends EventSourceListener {
        @Override
        public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
            if (t != null) {
                System.err.println("FAILED: " + t.getMessage());
                t.printStackTrace();
            }
            if (response != null) {
                System.err.println("Bad response: " + response);
                System.err.println("Response body: " + getResponseBody(response));
            }
            eventSource.cancel();
            this.onClosed(eventSource);
        }

        private String getResponseBody(Response response) {
            try {
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static JsonNode parse(String data) {
        try {
            return MAPPER.readTree(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void shutdown(OkHttpClient httpClient) {
        httpClient.dispatcher().cancelAll();
        MoreExecutors.shutdownAndAwaitTermination(httpClient.dispatcher().executorService(), Duration.ofSeconds(10));
    }
}
