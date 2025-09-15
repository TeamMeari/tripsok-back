package com.tripsok_back.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmbeddingUtil implements AutoCloseable {

	private ZooModel<String, float[]> model;
	private Predictor<String, float[]> predictor;

	@PostConstruct
	public void init() {
		try {
			Criteria<String, float[]> criteria = Criteria.builder()
				.setTypes(String.class, float[].class)
				.optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
				.optEngine("PyTorch")
				.optTranslatorFactory(new TextEmbeddingTranslatorFactory())
				.build();

			this.model = criteria.loadModel();
			this.predictor = model.newPredictor();

			float[] embedding = predictor.predict("테스트 문장");
			log.info("임베딩 모델 초기화 완료. 벡터 길이: {}", embedding.length);

		} catch (Exception e) {
			log.error("임베딩 모델 초기화 실패", e);
			throw new RuntimeException("임베딩 모델 초기화 실패", e);
		}
	}

	public List<Float> embed(String text) {
		try {
			float[] vector = predictor.predict(text);
			List<Float> result = new ArrayList<>(vector.length);
			for (float v : vector) {
				result.add(v);
			}
			return result;
		} catch (Exception e) {
			log.error("임베딩 생성 실패. text={}", text, e);
			throw new RuntimeException("임베딩 생성 실패", e);
		}
	}

	@PreDestroy
	@Override
	public void close() {
		if (predictor != null)
			predictor.close();
		if (model != null)
			model.close();
	}
}