
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Product;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public interface ProductDao {
    
    Product createProduct(String productType, Product product) 
            throws PersistenceException;
    
    Product getProduct(String productType) throws PersistenceException;
    
    List<Product> getAllProducts() throws PersistenceException;
    
    Product updateProduct(String productType, Product updatedProduct)
            throws PersistenceException;
    
    Product deleteProduct(String productType) throws PersistenceException;
    
}
