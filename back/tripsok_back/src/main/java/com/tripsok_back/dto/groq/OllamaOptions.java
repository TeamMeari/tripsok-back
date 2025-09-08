package com.tripsok_back.dto.groq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaOptions {
	private Double temperature;
	private Integer num_ctx;
}
