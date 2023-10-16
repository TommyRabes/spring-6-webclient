package mg.tommy.springboot.spring6webclient.client;

import mg.tommy.springboot.spring6webclient.model.dto.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {
    @Autowired
    BeerClient client;

    @Test
    void listBeerTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.listBeer().subscribe(response -> {
            System.out.println(response);
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void listBeerMapTest() throws InterruptedException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.listBeerMap().subscribe(response -> {
            System.out.println(response);
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void listBeerJsonTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.listBeerJsonNode().subscribe(jsonNode -> {
            System.out.println(jsonNode.toPrettyString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void listBeerDtoTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.listBeerDto().subscribe(dto -> {
            System.out.println(dto);
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void getBeerByIdTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.listBeerDto()
                .next()
                .map(BeerDto::getId)
                .flatMap(client::getBeerById)
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void getBeerByStyleTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        client.getBeerByStyle("PALE_ALE")
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void saveBeerTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        BeerDto beerToSave = BeerDto.builder()
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(500)
                .upc("9643")
                .build();

        client.saveBeer(beerToSave)
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void updateBeerTest() {
        final String BEER_NAME = "New Beer Name";
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        client.listBeerDto()
                .next()
                .doOnNext(dto -> dto.setBeerName(BEER_NAME))
                .flatMap(client::updateBeer)
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void patchBeerTest() {
        final String BEER_STYLE = "PILSNER";
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        client.listBeerDto()
                .next()
                .doOnNext(dto -> dto.setBeerStyle(BEER_STYLE))
                .flatMap(client::patchBeer)
                .subscribe(dto -> {
                    System.out.println(dto);
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void deleteBeerTest() {
        AtomicReference<String> atomicId = new AtomicReference<>();

        client.listBeerDto()
                .next()
                .map(BeerDto::getId)
                .flatMap(id -> {
                    return client.deleteBeer(id).doOnSuccess(voidResponse -> {
                        atomicId.set(id);
                    });
                })
                .subscribe();


        await().until(() -> atomicId.get() != null);

        StepVerifier.create(client.getBeerById(atomicId.get()))
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(WebClientResponseException.class);
                    WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
                    assertThat(webClientResponseException.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
                })
                .verify();
    }
}