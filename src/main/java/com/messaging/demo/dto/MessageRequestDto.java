package com.messaging.demo.dto;

import com.messaging.demo.model.enums.MessageChannel;
import lombok.Data;

@Data
public class MessageRequestDto {
    private String fromNumber;
    private String toNumber;
    private String body;
    private MessageChannel channel;
    private String mediaUrl;
}
