package org.dlrg.lette.telegrambot.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SenderChatRepository extends MongoRepository<SenderChat, Long> {
}
