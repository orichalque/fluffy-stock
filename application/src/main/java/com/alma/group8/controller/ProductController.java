package com.alma.group8.controller;

import com.alma.group8.interfaces.ProductService;
import com.alma.group8.model.exceptions.FunctionalException;
import com.alma.group8.model.interfaces.ProductsRepository;
import com.alma.group8.util.CommonVariables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Define the rest controller defining methods that can be called by clients
 */
@CrossOrigin
@RestController
@RequestMapping(value = CommonVariables.ROOT_URL, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProductController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductsRepository productsRepository;

    @Autowired
    ProductService<FunctionalException> productService;

    /**
     * Get the product with a paginated result
     * @param page the page to see
     * @param size the size of the page
     * @return a json containing the products from the page
     */
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    @ResponseBody public String getProducts(@RequestParam(value = "page", required = false) Integer page,
                                            @RequestParam(value = "size", required = false) Integer size) throws FunctionalException {
        String jsonArrayOfProducts = null;
        Collection<String> products;

        if(page == null || size == null) {
            products = productsRepository.findAll();
        } else {
            products = productsRepository.findPage(page, size);
        }


        try {
            jsonArrayOfProducts = OBJECT_MAPPER.writeValueAsString(products).replace("\\", "");
            jsonArrayOfProducts = jsonArrayOfProducts.replace("\"{", "{");
            jsonArrayOfProducts = jsonArrayOfProducts.replace("}\"", "}");
        } catch (JsonProcessingException e) {
            LOGGER.warn("Cannot serialize the result", e);
        }

        return jsonArrayOfProducts;
    }

    /**
     * Get a product using its name
     * @param id the product id
     * @return a Gson containing the product with the corresponding name
     */
    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    @ResponseBody public String getProductById(@PathVariable String id) throws FunctionalException {
        return productsRepository.find(id);
    }

    /**
     * Order a product using its name
     * @param id the product id
     * @param quantity the quantity to order
     * @return a Gson containing the product with the corresponding name
     * @throws com.alma.group8.model.exceptions.NotEnoughProductsException
     */
    @RequestMapping(value = "/product/{id}/order/{quantity}", method = RequestMethod.POST)
    @ResponseBody public String orderProductById(@PathVariable String id ,@PathVariable int quantity) throws FunctionalException {
        String productAsString = productsRepository.find(id);

        productAsString = productService.decreaseQuantity(productAsString, quantity);

        productsRepository.updateProduct(productAsString);
        return productAsString;
    }
}
