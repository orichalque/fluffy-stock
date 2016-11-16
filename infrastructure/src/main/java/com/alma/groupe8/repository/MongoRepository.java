package com.alma.groupe8.repository;

import model.interfaces.ProductsRepository;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import model.exceptions.AlreadyExistingProductException;
import model.exceptions.NotEnoughProductsException;
import model.exceptions.ProductNotFoundException;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Thibault on 04/11/16.
 * ProductRepository implementation using MongoDB collections
 */
@Repository
public class MongoRepository implements ProductsRepository {

    private static final Gson GSON_MAPPER = new Gson();

    @Autowired
    private MongoCollection<Document> mongoCollection;

    @Override
    public String find(String uuid) {
        //Returning the first item corresponding. the UUID are unique in the database so there is only 1 product, or none
        Document document = mongoCollection.find(eq("id", uuid)).first();

        String documentFoundAsString = null;
        if (document != null) {
            //MongoDb adds an index that we don't need
            document.remove("_id");
            documentFoundAsString = document.toJson();
        }
        return documentFoundAsString;
    }

    @Override
    public Collection<String> findAll() {
        //Transform a List<Document> to a list<ProductDTO>
        return Lists.transform(Lists.newArrayList(mongoCollection.find(Document.class)), document -> document.toJson());
    }

    @Override
    public void store(String product) throws AlreadyExistingProductException {
        Document document = Document.parse(product);
        String currentProduct = find(document.getString("id").toString());

        if (Strings.isNullOrEmpty(currentProduct)) {
            //Product found, when we only want to insert it
            mongoCollection.insertOne(Document.parse(product));
        } else {
            throw new AlreadyExistingProductException();
        }
    }

    @Override
    public void updateProduct(String product) throws ProductNotFoundException {
        Document document = Document.parse(product);

        //We delete the products before inserting it again
        //The delete will throw the ProductNotFoundException if the product doesn't exist
        delete(document.getString("id"));

        mongoCollection.insertOne(document);
    }

    @Override
    public void delete(String uuid) throws ProductNotFoundException {
        String currentProduct = find(uuid);

        if (Strings.isNullOrEmpty(currentProduct)) {
            throw new ProductNotFoundException();
        } else {
            mongoCollection.deleteOne(Document.parse(currentProduct));
       }

    }

    @Override
    public String orderProduct(String uuid, int quantity) throws NotEnoughProductsException {
        //FIXME NOK
        return null;
    }
}