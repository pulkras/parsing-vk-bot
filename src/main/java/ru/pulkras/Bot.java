package ru.pulkras;


import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Properties;

import java.io.FileInputStream;

public class Bot {

    static VkApiClient vkClient;

    static GroupActor actor;
    static int vkGroupId;
    static String vkToken;

    static Random random = new Random();

    public static void main(String[] args) throws ClientException, InterruptedException, ApiException {


        TransportClient transportClient = new HttpTransportClient();
        vkClient = new VkApiClient(transportClient); // interface which work with vk api by requests


        getVkKeys();
        actor = new GroupActor(vkGroupId, vkToken);

        botAnswers();

    }

    public static void getVkKeys() {
        Properties property = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties")) {

            property.load(fileInputStream);

            vkGroupId = Integer.parseInt(property.getProperty("vk.groupId"));
            vkToken = property.getProperty("vk.token");

        } catch (IOException e) {
            System.err.println("Error. Such file doesn't exist!");
        }
    }

    public static void botAnswers() throws ClientException, ApiException, InterruptedException {
        Integer identification = vkClient.messages()
                .getLongPollServer(actor)
                .execute()
                .getTs();
        while(true) {
            MessagesGetLongPollHistoryQuery historyQuery = vkClient.messages()
                    .getLongPollHistory(actor)
                    .ts(identification);
            List<Message> messages = historyQuery
                    .execute()
                    .getMessages()
                    .getItems();
            if (!messages.isEmpty()) {
                commands(messages);
            }
            identification = vkClient.messages()
                    .getLongPollServer(actor)
                    .execute()
                    .getTs();
            Thread.sleep(500);
        }
    }

    public static void commands(List<Message> messages) {
        messages.forEach(message -> {
            System.out.println(message.toString());
            String userMessage = message.getText();
            try {
                if(userMessage.equals("Sap") || userMessage.equals("Hello") || userMessage.equals("Hi")) {
                    vkClient.messages()
                            .send(actor)
                            .message("Hello, my friend:)")
                            .userId(message.getFromId())
                            .randomId(random.nextInt(10000))
                            .execute();
                }
                else if(userMessage.equals("how are you") || userMessage.equals("how are you doing") || userMessage.equals("whats up")) {
                    vkClient.messages()
                            .send(actor)
                            .message("I'm alright!")
                            .userId(message.getFromId())
                            .randomId(random.nextInt(10000))
                            .execute();
                }
                else {
                    vkClient.messages()
                            .send(actor)
                            .message("I don't understand you now but my developers extend my horizons")
                            .userId(message.getFromId())
                            .randomId(random.nextInt(10000))
                            .execute();
                }
            } catch (ApiException | ClientException e) {
                e.printStackTrace();
            }
        });
    }

}
