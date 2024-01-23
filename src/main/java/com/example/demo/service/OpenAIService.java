package com.example.demo.service;

import static java.util.Optional.ofNullable;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.AudioTranscriptionFormat;
import com.azure.ai.openai.models.AudioTranscriptionOptions;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsJsonResponseFormat;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.example.demo.domain.model.Document;
import com.example.demo.domain.model.ProfileTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

  private static final Pattern PATTERN = Pattern.compile("(?<=<p><strong>About the job</strong></p><p>)(.*)(?=</em></p>)");

  private final OpenAIClient client;
  private final DocumentService documentService;

  @Transactional
  public Document requestOpenAIBot(UUID documentId, String request) {
    var response = this.requestOpenAIBot(documentService.getPrompt(documentId, request));
    return documentService.updateDocumentResponse(documentId, response);
  }

  public String requestOpenAIBot(String prompt) {
    List<ChatRequestMessage> chatMessages = new ArrayList<>();
    chatMessages.add(new ChatRequestUserMessage(prompt));
    ChatCompletions chatCompletions = client.getChatCompletions("gpt-4-1106-preview",
        new ChatCompletionsOptions(chatMessages)
            .setResponseFormat(new ChatCompletionsJsonResponseFormat()));


    log.info("Model ID={} is created at {}", chatCompletions.getId(), chatCompletions.getCreatedAt());
    return chatCompletions.getChoices().stream()
        .map(ChatChoice::getMessage)
        .map(ChatResponseMessage::getContent)
        .findAny()
        .orElseThrow();
  }

  @SneakyThrows
  public String transformSpeechToText(byte[] fileData, String filename) {
    var transcriptionOptions = new AudioTranscriptionOptions(fileData)
        .setPrompt("Extract sound into text")
        .setResponseFormat(AudioTranscriptionFormat.JSON);

    log.info("Request audio transcription options: {}", transcriptionOptions.getPrompt());


    var transcription = client
        .getAudioTranscription("whisper-1", filename, transcriptionOptions);
    var text = transcription.getText();
    log.info("Transcription: {}", text);

    return text;
  }

  @SneakyThrows
  public String transformSpeechToText(MultipartFile file) {
    return this.transformSpeechToText(file.getBytes(), file.getOriginalFilename());
  }

  @SneakyThrows
  public Document uploadFile(MultipartFile multipartFile) {
    var filename = ofNullable(multipartFile.getOriginalFilename())
        .orElse(multipartFile.getName());
    byte[] bytes = multipartFile.getBytes();

    String text;
    ProfileTypeEnum documentType;
    try {
      text = documentService.parseDocument(bytes);
      documentType = ProfileTypeEnum.TEXT;
    } catch (Exception e) {
      log.info("Cannot parse the document content, trying to recognize speech");
      text = this.transformSpeechToText(bytes, UUID.randomUUID() + getFileExtension(filename));
      documentType = ProfileTypeEnum.SPEECH;
    }

    return documentService.save(Document.builder()
        .name(filename)
        .content(bytes)
        .promptText(text)
        .documentType(documentType)
        .build());
  }

  public String getDecisionForDocument(UUID documentId, String url) {
    String linkedinJobDescription = getLinkedinJobDescription(url);
    return makeDecision(linkedinJobDescription, documentId);
  }

  public String makeDecision(String jobDescription, UUID documentId) {
    var document = documentService.findById(documentId);
    String prompt = documentService.getPrompt(jobDescription, document.getPromptText());

    return this.requestOpenAIBot(prompt);
  }

  public String getLinkedinJobDescription(String url) {
    var htmlResponse = new RestTemplate().getForObject(url, String.class);
    if (StringUtils.hasText(htmlResponse)) {
      Matcher m = PATTERN.matcher(htmlResponse);
      if (m.find()) {
        return m.group();
      }
    }
    throw new IllegalArgumentException("Could not parse selected url");
  }

  private static String getFileExtension(String fileName) {
    String extension = "";

    if (fileName.lastIndexOf('.') != -1 && fileName.lastIndexOf('.') != 0) {
      extension = fileName.substring(fileName.lastIndexOf('.'));
    }

    return extension;
  }

}
