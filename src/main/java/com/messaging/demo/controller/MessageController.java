package com.messaging.demo.controller;

import com.messaging.demo.dto.MessageRequestDto;
import com.messaging.demo.dto.MessageResponseDto;
import com.messaging.demo.model.enums.MessageStatus;
import com.messaging.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponseDto> sendMessage(@RequestBody MessageRequestDto request) {
        log.info("Received send request for channel: {}", request.getChannel());
        MessageResponseDto response = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDto> getMessageById(@PathVariable String id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getMessages(
            @RequestParam(required = false) String toNumber,
            @RequestParam(required = false) MessageStatus status) {

        if (toNumber != null) {
            String decoded = toNumber.replace(" ", "+");
            return ResponseEntity.ok(messageService.getMessagesByRecipient(toNumber));
        }
        if (status != null) {
            return ResponseEntity.ok(messageService.getMessagesByStatus(status));
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MessageResponseDto> updateStatus(
            @PathVariable String id,
            @RequestParam MessageStatus status) {
        return ResponseEntity.ok(messageService.updateMessageStatus(id, status));
    }
}
