package com.messaging.demo.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.demo.dto.MessageResponseDto;
import com.messaging.demo.model.enums.MessageStatus;
import com.messaging.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWorker {
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final SqsService sqsService;

    @Scheduled(fixedDelay = 5000)
    public void processMessages(){
        log.info("Worker polling SQS for messages...");
        List<Message> sqsMessages = sqsService.pollMessages();

        for (Message sqsMessage : sqsMessages) {
            try {
                log.info("Processing SQS message: {}", sqsMessage.messageId());

                MessageResponseDto dto = objectMapper.readValue(
                        sqsMessage.body(),
                        MessageResponseDto.class);

                messageService.updateMessageStatus(dto.getId(), MessageStatus.SENT);

                sqsService.deleteMessage(sqsMessage.receiptHandle());

                log.info("Message {} processed and marked as SENT", dto.getId());

            } catch (Exception e) {
                log.error("Failed to process SQS message: {}", e.getMessage());
            }
        }
    }
}
