package com.asymcrypto.zeroknowledgeprotocol.controller;

import com.asymcrypto.zeroknowledgeprotocol.model.Modulus;
import com.asymcrypto.zeroknowledgeprotocol.model.NumUtil;
import com.asymcrypto.zeroknowledgeprotocol.model.Root;
import com.asymcrypto.zeroknowledgeprotocol.model.ZNPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

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

        Mono<Modulus> mod = webClient.get()
                .uri("/serverKey")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Modulus.class);
        znpService.setServerKey(mod.block().getModulus());

        //znpService.setServerKey(modulusMono.block().getModulus());
        return response.bodyToMono(Modulus.class);

        //.bodyToMono(Modulus.class);
//        znpService.setServerKey(serverKey.block().getModulus());
//        return serverKey;
    }

    @RequestMapping(value = "/root/{y}", method = RequestMethod.GET)
    public Mono<Root> getRoot(@PathVariable String y) {
    //public void getRoot(@PathVariable String y) {


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
        int index = ress.lastIndexOf(':');
        System.out.println("root " +  ress.substring(index+2, ress.length()-2));
      /* ClientResponse response = root.block();

       // System.out.println(response);
        System.out.println(response.bodyToMono(Modulus.class));



        System.out.println(response);
//        System.out.println(root);
//        System.out.println(root.block());
        //return root;*/


        return clientResponse.bodyToMono(Root.class);

    }

    @RequestMapping("/challenge")
    public String challenge(Model model) {
        int counter = 0;
        BigInteger modulus = znpService.getServerKey();
        BigInteger answer = BigInteger.ZERO;
        BigInteger shot;

        do {

            counter++;

                //shot= NumUtil.generateRandomBigInteger(modulus);
                shot= NumUtil.generateRandomBigInteger(modulus);
                final BigInteger two = BigInteger.valueOf(2);
                BigInteger y = shot.modPow(two, modulus);
                //System.out.println(y.toString(16));
                Mono<Root> root = webClient.get()
                        .uri("/challenge?y={y}", y.toString(16))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Root.class);
                answer = new BigInteger(root.block().getRoot(), 16);



            System.out.println("shot = " + shot.toString(16));
            System.out.println("answer = " + answer.toString(16));


        } while (answer.equals(shot) || answer.equals(shot.subtract(modulus)));

        BigInteger p = shot.add(answer).gcd(modulus);
        BigInteger q = modulus.divide(p);

        System.out.println("attempts: " + counter);
        System.out.println("modulus = " + modulus.toString(16));
        System.out.println("p = " + p.toString(16));
        System.out.println("q = " + q.toString(16));
        System.out.println("p*q = " + p.multiply(q).toString(16));

//        ModelAndView modelAndView = new ModelAndView("index");
//        modelAndView.addObject("counter", counter);
//        modelAndView.addObject("p", p);
//        modelAndView.addObject("q", q);
//        return modelAndView;


        return "counter = " + counter + '\n'
                + "p = " + p.toString(16) + '\n'
                + "q = " + q.toString(16);
    }



    @RequestMapping("/greeting")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
}
