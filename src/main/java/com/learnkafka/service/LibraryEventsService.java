package com.learnkafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.jpa.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventsService {
    @Autowired
    private LibraryEventsRepository libraryEventsRepository;
    @Autowired
    ObjectMapper objectMapper;
    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException, IllegalAccessException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("ObjectMapper : {} ", libraryEvent);
        switch (libraryEvent.getLibraryEventType()){
            case NEW:
                //save operation
                save(libraryEvent);
                break;
            case UPDATE:
                //validate libraryEvent
                validate(libraryEvent);
                //save
                save(libraryEvent);
                break;
            default:
                log.info("Invalid Library event type");
        }
    }

    private void validate(LibraryEvent libraryEvent) throws IllegalAccessException {
        //for update id shouldn't be null!
        if(libraryEvent.getLibraryEventId()==null){
            throw new IllegalAccessException("Library event id is missing");
        }
        Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if(!libraryEventOptional.isPresent()){
            throw new IllegalAccessException("Not a valid library event");
        }
        log.info("Validation if successful for the library event : {} ",libraryEventOptional.get());
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the Library Event {} ", libraryEvent);
    }
}
