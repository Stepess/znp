package com.asymcrypto.zeroknowledgeprotocol.controller;

import com.asymcrypto.zeroknowledgeprotocol.model.Modulus;
import com.asymcrypto.zeroknowledgeprotocol.model.Root;
import com.asymcrypto.zeroknowledgeprotocol.model.ZNPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
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
    //public void getServerKey() {
    public Mono<Modulus> getServerKey() {
        Mono<ClientResponse> serverKey = webClient.get()
                .uri("/serverKey")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        ClientResponse response = serverKey.block();
        MultiValueMap<String, ResponseCookie> cookies = response.cookies();
        if (cookies.containsKey("JSESSIONID")) {
            ResponseCookie cookie = cookies.getFirst("JSESSIONID");
            this.webClient = WebClient.builder()
                    .baseUrl("http://asymcryptwebservice.appspot.com/znp")
                    .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                    .defaultCookie(cookie.getName(), cookie.getValue())
                    .build();
        }
        String ress = serverKey.flatMap(res -> res.bodyToMono(String.class)).block();
        System.out.println(ress);
        int index = ress.lastIndexOf(':');
        System.out.println(ress.substring(index+2, ress.length()-2));
        Mono<Modulus> modulusMono = response.bodyToMono(Modulus.class);
        //znpService.setServerKey(modulusMono.block().getModulus());
        return modulusMono;

        //.bodyToMono(Modulus.class);
//        znpService.setServerKey(serverKey.block().getModulus());
//        return serverKey;
    }

    @RequestMapping(value = "/root/{y}", method = RequestMethod.GET)
    public Mono<Root> getRoot(@PathVariable String y) {
    //public void getRoot(@PathVariable String y) {
        System.out.println(y);

        Mono<ClientResponse> root = webClient.get()
                .uri("/challenge?y={y}", y)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
//                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus),
//                        response -> response.bodyToMono(String.class).map(body -> new Exception()))
                //.bodyToMono(Modulus.class);
//        Mono<Modulus> root = webClient.get()
//                .uri("/challenge?y=" + y)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Modulus.class);

        ClientResponse clientResponse = root.block();

       String ress = root.flatMap(res -> res.bodyToMono(String.class)).block();
        System.out.println(ress);
      /* ClientResponse response = root.block();

       // System.out.println(response);
        System.out.println(response.bodyToMono(Modulus.class));



        System.out.println(response);
//        System.out.println(root);
//        System.out.println(root.block());
        //return root;*/


        return clientResponse.bodyToMono(Root.class);

    }



    @RequestMapping("/greeting")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
