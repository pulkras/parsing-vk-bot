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

    static int vkGroupId;
    static String vkToken;

    static void main(String[] args) throws ClientException, InterruptedException, ApiException {


        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vkClient = new VkApiClient(transportClient); // interface which work with vk api by requests
        Random random = new Random();

        getVkKeys();
        GroupActor actor = new GroupActor(vkGroupId, vkToken);

        Integer identification = vkClient.messages()
                .getLongPollServer(actor)
                .execute()
                .getTs();
        
    }

    public static void getVkKeys() {
        Properties property = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties")) {

            property.load(fileInputStream);

            vkGroupId = Integer.parseInt(property.getProperty("vk.key"));
            vkToken = property.getProperty("vk.token");

        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }

}
