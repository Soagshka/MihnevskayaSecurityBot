package ru.home.security_bot.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.home.security_bot.model.RecordData;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SecurityAdminBotClient {

    public ResponseEntity<String> sendRecordToAdminBot(RecordData recordData) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = "https://mihnevskayasecurityadminbot.herokuapp.com/send-record";
            URI uri = new URI(baseUrl);
            return restTemplate.postForEntity(uri, recordData, String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
