
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dao.ProductDao;
import com.sblinn.flooringmastery.dto.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public class ProductDaoStubImpl implements ProductDao {

    public Product onlyProduct;
    
    
    /**
     * Creates a hard-coded Product for testing.
     */
    public ProductDaoStubImpl() {
        this.onlyProduct = new Product("Tile", new BigDecimal("3.50"), new BigDecimal("4.15"));
    }
    
    /**
     * Uses a test Product injected by another class. 
     * 
     * @param testProduct 
     */
    public ProductDaoStubImpl(Product testProduct) {
        this.onlyProduct = testProduct;
    }
    
    
    //This is the same for most of the stub methods:
    /**
     * Returns the stub's only Product if input productType matches 
     * onlyProduct's, else returns null.
     * 
     * @param productType
     * @param product
     * @return Product or null
     * @throws PersistenceException 
     */
    @Override
    public Product createProduct(String productType, Product product) throws 
            PersistenceException {

        if(productType.equals(onlyProduct.getProductType())) {
            return onlyProduct;
        } else {
            return null;
        }
    }

    @Override
    public Product getProduct(String productType) throws PersistenceException {
        if (productType.equals(onlyProduct.getProductType())) {
            return onlyProduct;
        } else {
            return null;
        }
    }

    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        List<Product> products = new ArrayList<>();
        products.add(onlyProduct);
        return products;
    }

    @Override
    public Product updateProduct(String productType, Product updatedProduct) 
            throws PersistenceException {
        
        if (productType.equals(onlyProduct.getProductType())) {
            return onlyProduct;
        } else {
            return null;
        }
    }

    @Override
    public Product deleteProduct(String productType) throws 
            PersistenceException {
        
        if (productType.equals(onlyProduct.getProductType())) {
            return onlyProduct;
        } else {
            return null;
        }
    }
    
}
