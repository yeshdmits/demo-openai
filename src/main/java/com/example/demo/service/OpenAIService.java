package com.example.demo.service;

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
import com.example.demo.domain.dto.DocumentDto;
import com.example.demo.domain.model.Document;
import com.example.demo.domain.model.DocumentTypeEnum;
import com.example.demo.domain.repository.DocumentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

  private final DocumentRepository documentRepository;
  private final OpenAIClient client;

  @SneakyThrows
  public String parseDocument(byte[] content) {
    try (var documentPdf = Loader.loadPDF(content)) {
      var stripper = new PDFTextStripper();
      return stripper.getText(documentPdf);
    }
  }

  public String getPrompt(UUID documentId, String request) {
    var document = documentRepository.findById(documentId).orElseThrow();
    return "You have following text data and you have to take important information from it:\n" +
        document.getPromptText() + "\n\n\nHere is information what I want to get as json response from you:"
        + request + "\nMaximum symbols for each field = 100";
  }

  @Transactional
  public DocumentDto requestOpenAIBot(UUID documentId, String request) {
    var prompt = this.getPrompt(documentId, request);
    var response = this.requestOpenAIBot(prompt);
    var uuid = this.saveRequest(documentId, request, response);
    return DocumentDto.builder()
        .uuid(uuid)
        .text(prompt)
        .response(response)
        .build();
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
  public DocumentDto saveRequest(MultipartFile multipartFile) {
    String originalFilename = multipartFile.getOriginalFilename();
    byte[] bytes = multipartFile.getBytes();
    String text;
    var documentBuilder = Document.builder()
        .name(originalFilename)
        .content(bytes);
    try {
      text = this.transformSpeechToText(bytes, UUID.randomUUID() + ".wav");
      documentBuilder.promptText(text);
      documentBuilder.documentType(DocumentTypeEnum.AUDIO);
    } catch (Exception e) {
      log.info("Speech to text failed");
      text = this.parseDocument(bytes);
      documentBuilder.documentType(DocumentTypeEnum.PDF);
      documentBuilder.promptText(text);
    }


    Document save = documentRepository.save(documentBuilder.build());
    return DocumentDto.builder()
        .uuid(save.getDocumentId())
        .text(text)
        .build();
  }

  public UUID saveRequest(UUID documentId, String request, String response) {
    Document document = documentRepository.findById(documentId).orElseThrow();
    document.setResponse(response);
    document.setRequest(request);

    return documentRepository.save(document).getDocumentId();
  }


}
