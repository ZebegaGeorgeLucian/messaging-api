package com.messaging.demo.dto;
import com.messaging.demo.model.enums.MessageChannel;
import com.messaging.demo.model.enums.MessageStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponseDto {
    private String id;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MessageChannel channel;
    private String mediaUrl;
    private String body;
    private String fromNumber;
    private String toNumber;
}
