package ru.home.security_bot.service;

import org.springframework.stereotype.Service;
import ru.home.security_bot.model.RecordData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
public class SecurityAdminBotClient {
    private static final String REST_URI = "https://mihnevskayasecurityadminbot.herokuapp.com/send-record";
    private Client client = ClientBuilder.newClient();

    public Response sendRecordToAdminBot(RecordData recordData) {
        return client
                .target(REST_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(recordData, MediaType.APPLICATION_JSON));
    }
}
