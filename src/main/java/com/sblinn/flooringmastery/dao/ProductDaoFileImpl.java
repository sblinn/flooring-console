
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Product;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sarablinn
 */
public class ProductDaoFileImpl implements ProductDao {

    private HashMap<String, Product> products = new HashMap<>();;
    private final String PRODUCTS_FILE;
    private static final String DELIMITER = ",";
    
    
    public ProductDaoFileImpl() {
        this.PRODUCTS_FILE = "../FlooringMastery/Data/Products.txt";
    }
    
    public ProductDaoFileImpl(String productsDataFilepath) {
        this.PRODUCTS_FILE = productsDataFilepath;
    }
    
    
    @Override
    public Product createProduct(String productType, Product product) 
            throws PersistenceException {
        
        loadProducts();
        Product newProduct = products.put(productType, product);
        writeProducts();
        
        return newProduct;
    }

    @Override
    public Product getProduct(String productType) 
            throws PersistenceException {
        
        loadProducts();
        Product product = products.get(productType);
        return product;
    }

    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        loadProducts();
        List<Product> sortedProducts = new ArrayList<>(products.values());
        sortedProducts.sort(Comparator.comparing((p) -> p.getProductType()));
        
        return sortedProducts;
    }

    @Override
    public Product updateProduct(String productType, Product updatedProduct) 
        throws PersistenceException {
        
        loadProducts();
        products.remove(productType);
        products.put(updatedProduct.getProductType(), updatedProduct);
        writeProducts();
        
        return updatedProduct;
    }

    @Override
    public Product deleteProduct(String productType) 
            throws PersistenceException {
        
        loadProducts();
        Product deletedProduct = products.remove(productType);
        writeProducts();
        
        return deletedProduct;
    }
    
    private String marshallProduct(Product product) {
        String productAsText = product.getProductType() + DELIMITER;
        productAsText += product.getCostPerSqFoot() + DELIMITER;
        productAsText += product.getLaborCostPerSqFoot();
        
        return productAsText;
    }
    
    private Product unmarshallProduct(String productAsText) {
        String[] productData = productAsText.split(DELIMITER);
        
        String productType = productData[0];
        BigDecimal costPerSqFoot = new BigDecimal(productData[1]);
        BigDecimal laborCostPerSqFoot = new BigDecimal(productData[2]);
        
        Product productFromFile = 
                new Product(productType, costPerSqFoot, laborCostPerSqFoot);
        
        return productFromFile;
    }
    
    private void loadProducts() throws PersistenceException {
        Scanner scanner;
        
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCTS_FILE)));
        } catch(FileNotFoundException e) {
            throw new PersistenceException("Unable to load data into memory.");
        }
        
        if (scanner.hasNext()) {
            scanner.nextLine(); // header -- ignore
            
            String currentLine;
            Product currentProduct;

            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();
                currentProduct = unmarshallProduct(currentLine);
                products.put(currentProduct.getProductType(), currentProduct);
            }
        }
        
        scanner.close();
    }
    
    private void writeProducts() throws PersistenceException {
        PrintWriter out;
        
        try {
            out = new PrintWriter(new FileWriter(PRODUCTS_FILE));
        } catch(IOException e) {
            throw new PersistenceException("Unable to "
                    + "save data to products data file.");
        }
        
        String header = "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot";
        out.println(header);
        
        String productAsText;       
        
        for(Product product : products.values()) {
            productAsText = marshallProduct(product);
            out.println(productAsText);
            out.flush();
        }
        
        out.close();   
    }
    
}
