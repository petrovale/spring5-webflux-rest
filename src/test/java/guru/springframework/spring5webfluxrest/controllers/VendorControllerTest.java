package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class VendorControllerTest {

    VendorRepository vendorRepository;
    VendorController vendorController;
    WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void getVendors() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder()
                        .id("FredId")
                        .firstName("Fred")
                        .build(),
                        Vendor.builder().id("JonId")
                                .firstName("Jon")
                                .build()));

        webTestClient.get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void get() {
        BDDMockito.given(vendorRepository.findById("JonId"))
                .willReturn(Mono.just(Vendor.builder()
                        .firstName("Jon")
                        .id("JonId").build()));

        webTestClient.get().uri("/api/v1/vendors/JonId")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void create() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().id("JonId").firstName("Jon").build()));

        Mono<Vendor> createToSaveMono = Mono.just(Vendor.builder().id("TomId").lastName("Tom").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(createToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}