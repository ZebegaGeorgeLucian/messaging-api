package com.messaging.demo.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.demo.dto.MessageRequestDto;
import com.messaging.demo.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SqsService {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    public void queueMessage(MessageResponseDto message) {
        try{
            String messageBody = objectMapper.writeValueAsString(message);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .messageAttributes(Map.of(
                            "channel", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(message.getChannel().toString())
                                    .build()
                    ))
                    .build();

            SendMessageResponse respone = sqsClient.sendMessage(request);
            log.info("Message queued in SQS with messageID: {}",  respone.messageId());

        } catch (Exception e){
            log.error("Failed to queue message in SQS: {}", e.getMessage());
            throw new RuntimeException("Failed to queue message in SQS", e);
        }
    }

    public List<Message> pollMessages() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .messageAttributeNames("All")
                .build();

        return sqsClient.receiveMessage(request).messages();
    }

    public void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build());
        log.info("Message deleted from SQS queue");
    }
}
