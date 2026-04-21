package com.messaging.demo.service;

import com.messaging.demo.aws.SqsService;
import com.messaging.demo.dto.MessageRequestDto;
import com.messaging.demo.dto.MessageResponseDto;
import com.messaging.demo.exception.MessageNotFoundException;
import com.messaging.demo.model.Message;
import com.messaging.demo.model.enums.MessageStatus;
import com.messaging.demo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final SqsService sqsService;

    public MessageResponseDto sendMessage(MessageRequestDto request){
        log.info("Queueing meessage from {} to {} via {}",
                request.getFromNumber(), request.getToNumber(), request.getChannel());

        Message message = Message.builder()
                .fromNumber(request.getFromNumber())
                .toNumber(request.getToNumber())
                .body(request.getBody())
                .channel(request.getChannel())
                .status(MessageStatus.QUEUED)
                .mediaUrl(request.getMediaUrl())
                .build();

        Message saved = messageRepository.save(message);
        log.info("Message saved with id: {}", saved.getId());
        sqsService.queueMessage(toResponseDto(saved));

        return toResponseDto(saved);
    }

    public MessageResponseDto getMessageById(String id){
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id:"+id));
        return toResponseDto(message);
    }

    public List<MessageResponseDto> getMessagesByRecipient(String toNumber){
        return messageRepository.findByToNumber(toNumber)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<MessageResponseDto> getMessagesByStatus(MessageStatus status) {
        return messageRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public MessageResponseDto updateMessageStatus(String id, MessageStatus newStatus){
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id:"+id));
        message.setStatus(newStatus);
        Message updated = messageRepository.save(message);
        return toResponseDto(updated);
    }

    private MessageResponseDto toResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .fromNumber(message.getFromNumber())
                .toNumber(message.getToNumber())
                .body(message.getBody())
                .channel(message.getChannel())
                .status(message.getStatus())
                .mediaUrl(message.getMediaUrl())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
