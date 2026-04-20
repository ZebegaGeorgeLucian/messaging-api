package com.messaging.demo.repository;
import com.messaging.demo.model.Message;
import com.messaging.demo.model.enums.MessageChannel;
import com.messaging.demo.model.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findByToNumber(String toNumber);
    List<Message> findByFromNumber(String fromNumber);
    List<Message> findByStatus(MessageStatus status);
    List<Message> findByChannel(MessageChannel channel);

}
