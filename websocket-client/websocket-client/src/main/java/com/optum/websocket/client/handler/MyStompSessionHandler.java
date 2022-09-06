package com.optum.websocket.client.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.websocket.client.model.Greeting;
import com.optum.websocket.client.model.Word;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;


public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/greetings", this);
        System.out.println("Connected...");
        List<Word> list = getSampleMessage();
        try {
            for (Word word : list) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(word);
                System.out.println("Sending Payload "+word.getContent());
                session.send("/app/hello", word.getContent());
                Thread.sleep(1000);
            }
            System.out.println("=======================================");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("Received Response :" + ((Greeting) payload).getContent());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Greeting.class;
    }

    private List<Word> getSampleMessage() {
        String fileName = "D:\\Pawan_Patil\\optum\\checker.txt";
        List<Word> wordList = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<String> linelist  = stream.collect(Collectors.toList());
            for (String line : linelist) {
                Word word = new Word();
                word.setContent(line);
                wordList.add(word);
                //System.out.println("Word content is "+ line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }
}
