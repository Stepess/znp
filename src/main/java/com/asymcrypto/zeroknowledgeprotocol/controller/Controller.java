package com.asymcrypto.zeroknowledgeprotocol.controller;

import com.asymcrypto.zeroknowledgeprotocol.model.NumberFromServer;
import com.asymcrypto.zeroknowledgeprotocol.model.ZNPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController("/")
public class Controller {
    @Autowired
    private ZNPService znpService;

    private WebClient webClient;

    public Controller() {
        this.webClient = WebClient.builder()
                .baseUrl("http://asymcryptwebservice.appspot.com/znp")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
    }

    @RequestMapping(value = "/serverkey", method = RequestMethod.GET)
    public Mono<NumberFromServer> getServerKey() {
        Mono<NumberFromServer> serverKey = webClient.get()
                .uri("/serverKey")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NumberFromServer.class);
        znpService.setServerKey(serverKey.block().getModulus());
        return serverKey;
    }

    @RequestMapping(value = "/root/{y}", method = RequestMethod.GET)
    //public Mono<NumberFromServer> getRoot(@PathVariable String y) {
    public void getRoot(@PathVariable String y) {
        System.out.println(y);

        Mono<ClientResponse> root = webClient.get()
                .uri("/challenge?y={y}", y)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
//                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus),
//                        response -> response.bodyToMono(String.class).map(body -> new Exception()))
                //.bodyToMono(NumberFromServer.class);
//        Mono<NumberFromServer> root = webClient.get()
//                .uri("/challenge?y=" + y)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(NumberFromServer.class);

        String ress = root.flatMap(res -> res.bodyToMono(String.class)).block();
        System.out.println(ress);
//        System.out.println(root);
//        System.out.println(root.block());
        //return root;

    }



    @RequestMapping("/greeting")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
