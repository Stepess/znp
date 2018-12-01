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
    public Mono<Modulus> getServerKey() {
        Mono<Modulus> modulusMono = webClient.get().uri("/serverKey")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is2xxSuccessful,
                        clientResponse -> {
                            MultiValueMap<String, ResponseCookie> cookies = clientResponse.cookies();
                            if (cookies.containsKey("JSESSIONID")) {
                                ResponseCookie cookie = cookies.getFirst("JSESSIONID");
                                this.webClient = WebClient.builder()
                                        .baseUrl("http://asymcryptwebservice.appspot.com/znp")
                                        .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                                        .defaultCookie(cookie.getName(), cookie.getValue())
                                        .build();
                            }
                            return null;
                        }
                )
                .bodyToMono(Modulus.class);
        znpService.setServerKey(modulusMono.block().getModulus());
        return modulusMono;
    }

    //just for debugging
    @RequestMapping(value = "/root/{y}", method = RequestMethod.GET)
    public Mono<Root> getRoot(@PathVariable String y) {
        Mono<ClientResponse> root = webClient.get()
                .uri("/challenge?y={y}", y)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        ClientResponse clientResponse = root.block();


        String ress = root.flatMap(res -> res.bodyToMono(String.class)).block();
        System.out.println(ress);
        int index = ress.lastIndexOf(':');
        System.out.println("root " + ress.substring(index + 2, ress.length() - 2));

        return clientResponse.bodyToMono(Root.class);

    }

    @RequestMapping("/challenge")
    public String challenge() {
        int counter = 0;
        BigInteger modulus = znpService.getServerKey();
        BigInteger answer = BigInteger.ZERO;
        BigInteger shot;
        BigInteger p;
        BigInteger q;

        while(true) {
            do {
                counter++;
                shot = NumUtil.generateRandomBigInteger(modulus);
                final BigInteger two = BigInteger.valueOf(2);
                BigInteger y = shot.modPow(two, modulus);
                Mono<Root> root = webClient.get()
                        .uri("/challenge?y={y}", y.toString(16))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Root.class);
                answer = new BigInteger(root.block().getRoot(), 16);

                System.out.println("shot = " + shot.toString(16));
                System.out.println("answer = " + answer.toString(16));

            } while (answer.equals(shot) || answer.equals(shot.subtract(modulus)));
            //added the last two conditions because sometimes cases when p=modulus, q=1 or p=1, q=modulus appears
            //this conditions || answer.equals(modulus) || answer.equals(BigInteger.ONE)
            p = shot.add(answer).gcd(modulus);
            q = modulus.divide(p);
            //the same reason
            if (!p.equals(modulus) && !p.equals(BigInteger.ONE)) {
                break;
            }

        }


        System.out.println("attempts: " + counter);
        System.out.println("modulus = " + modulus.toString(16));
        System.out.println("p = " + p.toString(16));
        System.out.println("q = " + q.toString(16));
        System.out.println("p*q = " + p.multiply(q).toString(16));

        return "counter = " + counter + '\n'
                + "p = " + p.toString(16) + '\n'
                + "q = " + q.toString(16) + '\n'
                + "p*q = " + p.multiply(q).toString(16);
    }

}
