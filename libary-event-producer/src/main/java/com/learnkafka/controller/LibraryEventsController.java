package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.Producer.LibraryEventsProducer;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventsController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(
        @RequestBody LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ",libraryEvent);
        //invoke the kafka producer


        //send the message asynchronously
        //throws exception
        //libraryEventsProducer.sendLibraryEventAsyncApproach(libraryEvent);

        //send the message synchronously
        //throws exception
        //libraryEventsProducer.sendLibraryEventSyncApproach(libraryEvent);


        //send the message asynchronously
        //throws exception
        //send a message like producer record
        libraryEventsProducer.sendLibraryEventAsyncApproachAddHeaderToMessage(libraryEvent);

        log.info("After sending library event ");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
