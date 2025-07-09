package com.example.assessment_employee.repository;

import com.example.assessment_employee.dto.request.SentimentRequest;
import com.example.assessment_employee.dto.response.SentimentResponse;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sentiment-analysis", url = "${service.sentiment-analysis-url}")
public interface SentimentAnalysisClient {

    @PostMapping(value = "/sentiment-analysis", consumes = "application/json")
    SentimentResponse analyzeSentiment(@RequestBody SentimentRequest request);


}
