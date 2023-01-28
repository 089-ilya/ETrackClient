package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.example.exception.ExecutionException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.util.jar.Attributes.Name.CONTENT_TYPE;

public class RequestExecutor {

    public static <T> HttpResponse<String> executePost(String url, T body) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String requestBody = objectMapper
                .writeValueAsString(body);

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE.toString(), "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return   client.send(request,
                HttpResponse.BodyHandlers.ofString());

    }

    public static HttpResponse<String> executeGet(String url) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        return client.send(request,
                HttpResponse.BodyHandlers.ofString());

    }

    @SneakyThrows
    public static HttpResponse<String> executeWithTimeout(Callable<HttpResponse<String>> callable, int executionTimes, int timoutSeconds) {
        for (int i = 0; i < executionTimes; i++) {
            try {
                HttpResponse<String> call = callable.call();
                if (call.statusCode() != 200) {
                    throw new ExecutionException(call.body());
                }
                return call;
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(timoutSeconds);
                if (timoutSeconds == i) {
                    throw new ExecutionException(e);
                }
            }
        }
        throw new ExecutionException();
    }
}
