package com.murshid.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {

    public static void main(String[] args){
        List<String> values = IntStream.iterate(0, i -> i + 1).limit(3)
                .boxed().map(i-> Integer.toString(i))
                .collect(Collectors.toList());

        System.out.println(values);
    }
}
