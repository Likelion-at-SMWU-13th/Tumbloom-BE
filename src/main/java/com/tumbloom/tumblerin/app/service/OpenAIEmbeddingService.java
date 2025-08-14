package com.tumbloom.tumblerin.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }


    /**
     * 텍스트를 받아 OpenAI 임베딩 모델로 임베딩을 생성하고,
     * float 배열로 반환
     */
    public float[] createEmbeddingArray(String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResults().get(0).getOutput();
    }


    /**
     * 텍스트를 받아 OpenAI 임베딩 모델로 임베딩을 생성하고,
     * List<Double>로 변환하여 반환
     */
    public List<Double> createEmbeddingList(String text) {
        float[] array = createEmbeddingArray(text);
        List<Double> list = new java.util.ArrayList<>(array.length);
        for (float f : array) {
            list.add((double) f);
        }
        return list;
    }

    /**
     * List<Double> 벡터를 JSON 문자열로 변환
     */
    public String vectorToJson(List<Double> vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            throw new RuntimeException("임베딩 벡터 JSON 변환 실패", e);
        }
    }

    /**
     * JSON 문자열을 List<Double> 벡터로 변환
     */
    public List<Double> jsonToVector(String json) {
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON → 벡터 변환 실패", e);
        }
    }
}
