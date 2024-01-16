package com.example.demo.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

  @Bean
  public OpenAIClient getOpenAIClient(@Value("${openAI.apiKey}") String apiKey) {
    return new OpenAIClientBuilder()
        .credential(new KeyCredential(apiKey))
        .buildClient();
  }
}
