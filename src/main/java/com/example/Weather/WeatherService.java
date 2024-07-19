package com.example.Weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherResponse getWeather(String city) {
        String url = String.format("%s?key=%s&q=%s", apiUrl, apiKey, city);
        logger.info("Request URL: {}", url);

        try {
            String rawResponse = restTemplate.getForObject(url, String.class);
            logger.info("Raw Response: {}", rawResponse);

            ObjectMapper mapper = new ObjectMapper();
            WeatherResponse response = mapper.readValue(rawResponse, WeatherResponse.class);

            logger.info("Deserialized Response: {}", response);
            return response;
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("Error getting weather data: {}", responseBody);
            throw new RuntimeException("API request failed with error: " + responseBody, e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting weather data", e);
            throw new RuntimeException("An unexpected error occurred while getting weather data", e);
        }
    }
}
