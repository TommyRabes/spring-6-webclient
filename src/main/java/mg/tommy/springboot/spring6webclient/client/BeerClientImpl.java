package mg.tommy.springboot.spring6webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import mg.tommy.springboot.spring6webclient.model.dto.BeerDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Service
public class BeerClientImpl implements BeerClient {
    public static final String BASE_PATH = "/api/v3/beers";
    public static final String BEER_PATH_ID = BASE_PATH + "/{beerId}";
    private final WebClient webClient;

    public BeerClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    @Override
    public Flux<String> listBeer() {
        return webClient.get().uri("/api/v3/beers")
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    public Flux<Map> listBeerMap() {
        return webClient.get().uri(BASE_PATH)
                .retrieve().bodyToFlux(Map.class);
    }

    @Override
    public Flux<JsonNode> listBeerJsonNode() {
        return webClient.get().uri(BASE_PATH)
                .retrieve().bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<BeerDto> listBeerDto() {
        return webClient.get().uri(BASE_PATH)
                .retrieve().bodyToFlux(BeerDto.class);
    }

    @Override
    public Mono<BeerDto> getBeerById(String id) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
                .retrieve().bodyToMono(BeerDto.class);
    }

    @Override
    public Flux<BeerDto> getBeerByStyle(String style) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("beerStyle", style).build())
                .retrieve().bodyToFlux(BeerDto.class);
    }

    @Override
    public Mono<BeerDto> saveBeer(BeerDto beerToSave) {
        return webClient.post()
                .uri(BASE_PATH)
                .body(Mono.just(beerToSave), BeerDto.class)
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getHeaders)
                .map(HttpHeaders::getLocation)
                .map(URI::getPath)
                .map(path -> path.split("/")[path.split("/").length - 1])
                .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDto> updateBeer(BeerDto beerDto) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(beerDto.getId()))
                .body(Mono.just(beerDto), BeerDto.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerDto.getId()));
    }

    @Override
    public Mono<BeerDto> patchBeer(BeerDto beerDto) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(beerDto.getId()))
                .body(Mono.just(beerDto), BeerDto.class)
                .retrieve()
                .toBodilessEntity()
                .flatMap(voidResponseEntity -> getBeerById(beerDto.getId()));
    }

    @Override
    public Mono<Void> deleteBeer(String id) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
