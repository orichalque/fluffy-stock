package com.alma.group8.factories;

import com.alma.group8.exceptions.FunctionalException;
import com.alma.group8.interfaces.ProductFactory;
import com.alma.group8.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Dennis on 25/11/16.
 */
public class ProductFactoryImpl implements ProductFactory<Product>{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Product deserialize(String metierAsJson) throws FunctionalException {
        try {
            return OBJECT_MAPPER.readValue(metierAsJson, Product.class);
        } catch (IOException e) {
            throw new FunctionalException(e);
        }
    }

    @Override
    public String serialize(Product object) throws FunctionalException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new FunctionalException(e);
        }
    }
}