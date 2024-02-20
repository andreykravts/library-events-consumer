package com.learnkafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.jpa.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventsService {
    @Autowired
    private LibraryEventsRepository libraryEventsRepository;
    @Autowired
    ObjectMapper objectMapper;
    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("ObjectMapper : {} ", libraryEvent);
        switch (libraryEvent.getLibraryEventType()){
            case NEW:
                //save operation
                save(libraryEvent);
                break;
            case UPDATE:
                //UPDATE operation
                break;
            default:
                log.info("Invalid Library event type");
        }
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the Library Event {} ", libraryEvent);
    }
}
