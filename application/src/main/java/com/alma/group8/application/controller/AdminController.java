package com.alma.group8.application.controller;

import com.alma.group8.api.exceptions.FunctionalException;
import com.alma.group8.api.interfaces.ProductService;
import com.alma.group8.api.interfaces.ProductsRepository;
import com.alma.group8.application.util.CommonVariables;
import com.alma.group8.domain.exceptions.AlreadyExistingProductException;
import com.alma.group8.domain.exceptions.ProductNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Thibault on 18/11/16.
 * Spring controller meant to access to the private functionalities of fluffy-stock
 * Is not meant to be used by outsiders
 */
@CrossOrigin
@RestController
@RequestMapping(value = CommonVariables.ADMIN_URL)
public class AdminController {
    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductService productService;

    private static final Logger LOGGER = Logger.getLogger(AdminController.class);

    /**
     * Delete a product from the database
     * @param id the id of the product to delete
     */
    @RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable String id) throws FunctionalException {
        productsRepository.delete(id);
    }

    /**
     * Add a product in the database
     * @param productAsString the product
     * @throws FunctionalException
     */
    @RequestMapping(value = "/product", method = RequestMethod.POST, consumes = "application/json")
    public void addProduct(@RequestBody String productAsString) throws FunctionalException {
        LOGGER.info("Receiving a POST method to add the product");
        LOGGER.debug(String.format("POST on /admin/product with body %s", productAsString));

        productService.validate(productAsString);

        String productAsStringWithId = productService.generateId(productAsString);

        try {
            productsRepository.store(productAsStringWithId);
        } catch (FunctionalException e) {
            throw new AlreadyExistingProductException(e);
        }
    }

    /**
     * Update a product in the database
     * @param productAsString the product as a string
     * @param id the id of the product
     * @throws FunctionalException if the product is not in the database
     */
    @RequestMapping(value = "/product/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public void updateProduct(@RequestBody String productAsString, @PathVariable String id) throws FunctionalException {
        LOGGER.info(String.format("Receiving a PUT method to update a product with the id %s", id));
        LOGGER.debug(String.format("PUT on /admin/product with body %s", productAsString));

        productService.validate(productAsString);

        try {
            productsRepository.updateProduct(productAsString);
        } catch (FunctionalException e) {
            throw new ProductNotFoundException(e);
        }
    }
}
