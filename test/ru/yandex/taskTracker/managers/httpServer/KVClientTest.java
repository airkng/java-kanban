package ru.yandex.taskTracker.managers.httpServer;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

public class KVClientTest {
    //нее, нафиг надо, тут тесты намного понятнее, сразу отметается вариант плохого взаимодействия клиента и KVServer
    static KVServer server;
    static {
        try {
            server = new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static KVTaskClient client;


    @BeforeAll
    public static void startServerAndClient(){
        server.start();
        client = new KVTaskClient("http://localhost:8078");
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void put_shouldEqualsDataInfo_usingPutClientMethod() {
        String putValue1 = "Hello";
        String putKey1 = "1234";

        String putValue2 = "Java";
        String putKey2 = "9876";

        client.put(putKey1, putValue1);
        client.put(putKey2, putValue2);
        Map<String, String> dataFromServer = server.getData();

        Assertions.assertEquals(putValue1, dataFromServer.get(putKey1));
        Assertions.assertEquals(putValue2, dataFromServer.get(putKey2));
    }
    @Test
    public void put_shouldUpdateDataInfo_usingPutClientMethod() {
        String putValue1 = "Hello";
        String putKey1 = "1234";

        String putValue2 = "Java";
        String putKey2 = "9876";

        String replaceValue = "Python";
        String replaceKey = "9876";

        client.put(putKey1, putValue1);
        client.put(putKey2, putValue2);
        client.put(replaceKey, replaceValue);
        Map<String, String> dataFromServer = server.getData();

        Assertions.assertEquals(replaceValue, dataFromServer.get(putKey2));
    }
    @Test
    public void load_shouldReturnNull_WrongKey() {
        String wrongKey = "wrongKey";
        Assertions.assertNull(client.load(wrongKey));
    }
    @Test
    public void load_shouldReturnNull_EmptyKey() {
        Assertions.assertNull(client.load(""));
    }
    @Test
    public void load_shouldReturnRightValue_UsingExistKey() {
        String putValue1 = "Hello";
        String putKey1 = "1234";

        String putValue2 = "Java";
        String putKey2 = "9876";

        client.put(putKey1, putValue1);
        client.put(putKey2, putValue2);

        Assertions.assertEquals(putValue1, client.load(putKey1));
        Assertions.assertEquals(putValue2, client.load(putKey2));
    }
    @Test
    public void registerTest_shouldEqualsApiTokens_afterRegistration() {
        Assertions.assertEquals(client.getApiToken(), server.getApiToken());
    }
}
