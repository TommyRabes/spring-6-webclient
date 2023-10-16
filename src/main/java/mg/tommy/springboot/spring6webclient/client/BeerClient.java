package mg.tommy.springboot.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import mg.tommy.springboot.spring6webclient.model.dto.BeerDto;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerClient {
    Flux<String> listBeer();
    Flux<Map> listBeerMap();

    Flux<JsonNode> listBeerJsonNode();

    Flux<BeerDto> listBeerDto();

    Mono<BeerDto> getBeerById(String id);

    Flux<BeerDto> getBeerByStyle(String style);

    Mono<BeerDto> saveBeer(BeerDto beerToSave);

    Mono<BeerDto> updateBeer(BeerDto beerDto);

    Mono<BeerDto> patchBeer(BeerDto beerDto);

    Mono<Void> deleteBeer(String id);
}
