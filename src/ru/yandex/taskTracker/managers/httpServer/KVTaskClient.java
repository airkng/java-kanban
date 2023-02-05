package ru.yandex.taskTracker.managers.httpServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private String API_TOKEN;
    private String serverUrl;

    public KVTaskClient(String serverUrl) {
        //registration url and API_TOKEN
        try {
            this.serverUrl = serverUrl;
            URI registration = URI.create(serverUrl + "/register");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(registration)
                    .header("Content-Type", "*/*")
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            API_TOKEN = response.body();
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный Url");
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            System.out.println("произошла ошибка при запросе registration на сервер");
            e.printStackTrace();
        }
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void put(String key, String json) {
        //request post save/key?Api_token
        String urlStrRequest = serverUrl + "/save/" + key + "?API_TOKEN=" + API_TOKEN;
        URI url = URI.create(urlStrRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "*/*")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Значение успешно обновилось!");
            } else {
                System.out.println("Возникла ошибка при сохранении в KV_Server");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка в работе KV server либо при отправке from KV_TaskClient put");
            e.printStackTrace();
        }
    }

    public String load(String key) {
        String urlStrRequest = serverUrl + "/load/" + key + "?API_TOKEN=" + API_TOKEN;
        URI url = URI.create(urlStrRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "Application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body(); //идет в httpTaskManager in json-format
            } else {
                System.out.println("Возникла ошибка при запросе из KV_Client");
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
