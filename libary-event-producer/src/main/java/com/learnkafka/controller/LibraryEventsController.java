package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.Producer.LibraryEventsProducer;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    //endpoint post
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(
        @RequestBody @Valid LibraryEvent libraryEvent
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

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //new endpoint put
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ",libraryEvent);

        // add validation for id because it can be null if we have do update date for some book

        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;


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

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }


    //function of an upper statement
    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if(libraryEvent.libraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass Library Event Id");
        }

        if(!libraryEvent.libraryEventType().equals(LibraryEventType.UPDATE)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        return null;
    }


}
