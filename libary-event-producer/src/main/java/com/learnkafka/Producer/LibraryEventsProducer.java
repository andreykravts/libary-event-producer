package com.learnkafka.Producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class LibraryEventsProducer {
    //key Integer, value String
    private final KafkaTemplate<Integer,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value(
            "${spring.kafka.topic}"
    )
    public String topic;

    //send the message asynchronously
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsyncApproach(LibraryEvent libraryEvent) throws JsonProcessingException {
        //integer
        var key=libraryEvent.libraryEventId();
        //String
        var value=objectMapper.writeValueAsString(libraryEvent);

        // first connection:
        // 1.blocking call - get metadata about kafka cluster
        //success:
        // 2. Send message happens - Returns a CompletableFuture

        //CompletableFuture<SendResult<Integer,String>>
        var completableFuture=kafkaTemplate.send(topic,key,value);
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if(throwable!=null){
                        handleFailure(key,value,throwable);
                    }else{
                        handleSuccess(key,value,sendResult);
                    }
                });
    }



    //send the message synchronously
    public SendResult<Integer, String> sendLibraryEventSyncApproach(LibraryEvent libraryEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //integer
        var key=libraryEvent.libraryEventId();
        //String
        var value=objectMapper.writeValueAsString(libraryEvent);

        // first connection:
        // 1.blocking call - get metadata about kafka cluster
        //success:
        // 2. Block and await until the message is sent to the kafka cluster

        //CompletableFuture<SendResult<Integer,String>>
        var sendResult=kafkaTemplate.send(topic,key,value)
                .get(3,TimeUnit.SECONDS);
        handleSuccess(key,value,sendResult);
        return sendResult;
    }


    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsyncApproachAddHeaderToMessage(LibraryEvent libraryEvent) throws JsonProcessingException {

        //integer
        var key=libraryEvent.libraryEventId();
        //String
        var value=objectMapper.writeValueAsString(libraryEvent);

        //build producer record and save it to var
        var producerRecord = buildProducerRecord(key, value);
        //function producer buildProducerRecord below this function\|/


        // first connection:
        // 1.blocking call - get metadata about kafka cluster
        //success:
        // 2. Send message happens - Returns a CompletableFuture

        //CompletableFuture<SendResult<Integer,String>>
        var completableFuture=kafkaTemplate.send(producerRecord);
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if(throwable!=null){
                        handleFailure(key,value,throwable);
                    }else{
                        handleSuccess(key,value,sendResult);
                    }
                });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {

        //Add header to the kafka message
        //meaning add metadata that says that message come from the scanner
        //add record header to new Producer record
        List<Header> recordHeaders= List.of(new RecordHeader("event-source", "scanner".getBytes()));
        //return a new object
        return new ProducerRecord<>(topic,null,key,value,recordHeaders);
    }






    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message was sent successfully for the key : {} , and the value {} , partition is {} ",key,value,sendResult.getRecordMetadata().partition());
    }


    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message and the exception is {} "+ex.getMessage() , ex);
    }
}
