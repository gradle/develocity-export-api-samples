package com.gradle.enterprise.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.Instant.now;

public final class BuildCountByUser {

    private static final SocketAddress GRADLE_ENTERPRISE_SERVER = new InetSocketAddress("gradle.my-company.com", 443);

    private static final HttpClient<ByteBuf, ByteBuf> HTTP_CLIENT = HttpClient.newClient(GRADLE_ENTERPRISE_SERVER).unsafeSecure();
    private static final int THROTTLE = 30;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Instant since1Day = now().minus(Duration.ofHours(24));

        buildStream(since1Day)
            .doOnSubscribe(() -> System.out.println("Streaming builds..."))
            .map(BuildCountByUser::parse)
            .map(json -> json.get("buildId").asText())
            .flatMap(buildId -> buildEventStream(buildId, ImmutableSet.of("BuildAgent"))
                .doOnSubscribe(() -> System.out.println("Streaming events for : " + buildId))
                .filter(serverSentEvent -> serverSentEvent.getEventTypeAsString().equals("BuildEvent"))
                .map(BuildCountByUser::parse)
                .single()
                .map(json -> json.get("data").get("username").asText()),
                THROTTLE
            )
            .groupBy(username -> username)
            .flatMap(g -> g.count().map(i -> g.getKey() + ": " + i))
            .toList()
            .map(strings -> "\nResults: " + strings)
            .toBlocking()
            .subscribe(System.out::println);
    }

    private static Observable<ServerSentEvent> buildStream(Instant since) {
        return resume("/build-export/v1/builds/since/" + String.valueOf(since.toEpochMilli()), null);
    }


    private static Observable<ServerSentEvent> buildEventStream(String buildId, Set<String> eventTypes) {
        return resume("/build-export/v1/build/" + buildId + "/events?eventTypes=" + Joiner.on(",").join(eventTypes), null);
    }

    private static Observable<ServerSentEvent> resume(String url, String lastEventId) {
        AtomicReference<String> eventId = new AtomicReference<>();

        HttpClientRequest<ByteBuf, ByteBuf> request = HTTP_CLIENT
            .createGet(url);

        if (lastEventId != null) {
            request = request.addHeader("Last-Event-ID", lastEventId);
        }

        return request
            .flatMap(HttpClientResponse::getContentAsServerSentEvents)
            .doOnNext(serverSentEvent -> eventId.set(serverSentEvent.getEventIdAsString()))
            .onErrorResumeNext(t -> {

                if (eventId.get() != null) {
                    System.out.println("Error streaming " + url + ", resuming from " + eventId.get() + "...");
                    return resume(url, eventId.get());
                } else {
                    System.out.println("Error streaming " + url + ", resuming from null ...");
                    return resume(url, null);
                }

            });
    }

    private static JsonNode parse(ServerSentEvent serverSentEvent) {
        try {
            return MAPPER.readTree(serverSentEvent.contentAsString());
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        } finally {
            boolean deallocated = serverSentEvent.release();
            assert deallocated;
        }
    }

}
