package io.github.jonesun.standaloneserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jone.sun
 * @date 2021/1/29 16:48
 */
public class CompletablePizza {

    static final int QUANTITY = 5;

    public static CompletableFuture<Pizza> makeCF(Pizza za){
        return CompletableFuture
                .completedFuture(za)
                .thenApplyAsync(Pizza::roll)
                .thenApplyAsync(Pizza::sauce)
                .thenApplyAsync(Pizza::cheese)
                .thenApplyAsync(Pizza::toppings)
                .thenApplyAsync(Pizza::bake)
                .thenApplyAsync(Pizza::slice)
                .thenApplyAsync(Pizza::box);
    }

    public static void show(CompletableFuture<Pizza> cf){
        try{
            System.out.println(cf.get());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        List<CompletableFuture<Pizza>> pizzas =
                IntStream.range(0, QUANTITY)
                        .mapToObj(Pizza::new)
                        .map(CompletablePizza::makeCF)
                        .collect(Collectors.toList());

        pizzas.forEach(CompletablePizza::show);
    }

}
