package com.asymcrypto.zeroknowledgeprotocol.controller;

import com.asymcrypto.zeroknowledgeprotocol.model.ServerKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController("/")
public class Controller {
    private WebClient webClient;

    public Controller() {
        this.webClient = WebClient.builder()
                .baseUrl("http://asymcryptwebservice.appspot.com/znp")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
    }

    @RequestMapping(value = "/serverkey", method = RequestMethod.GET)
    public Mono<ServerKey> getServerKey() {
    //public ResponseEntity<ServerKey> getKey() {
        Mono<ServerKey> serverKey = webClient.get()
                .uri("/serverKey")
                .retrieve()
                .bodyToMono(ServerKey.class);
        System.out.println(serverKey);
        //System.out.println(serverKey.block().toString());
        //return new ResponseEntity<>(serverKey.block(), HttpStatus.OK);
        return serverKey;
    }

    @RequestMapping("/greeting")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
