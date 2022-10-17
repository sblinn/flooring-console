
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Product;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author sarablinn
 */
public class ProductDaoFileImplTest {
    
    private ProductDao testProductDao;
    private final String TEST_FILE;
    
    
    public ProductDaoFileImplTest() {
        ApplicationContext appContext = 
                new ClassPathXmlApplicationContext("testApplicationContext.xml");
        testProductDao = appContext.getBean("productDao", ProductDao.class);
        TEST_FILE = appContext.getBean("testProductsFile", String.class);   
    }

    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() throws Exception {
        // set up and clear the DAO test file so it is empty
        // before each test
        try {
            new FileWriter(TEST_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("TESTPRODUCTS FILE NOT FOUND");
        } catch (IOException e) {
            System.out.println("IOEXCEPTION CAUGHT WHILE TRYING TO CLEAR "
                    + "TESTPRODUCTSFILE BEFORE TESTING.");
        }
        
        // might throw IOException or fileNotFoundException
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        new FileWriter(TEST_FILE);
    }

    
    
    @Test
    public void testCreateAndGetProduct() throws PersistenceException {
        // create a Product
        Product testProduct = new Product();
        String productType = "Tile";
        testProduct.setProductType(productType);
        testProduct.setCostPerSqFoot(new BigDecimal("3.50"));
        testProduct.setLaborCostPerSqFoot(new BigDecimal("4.15"));
        
        // Add it to the DAO
        testProductDao.createProduct(productType, testProduct);
        // Get it from the DAO
        Product retrievedProduct = testProductDao.getProduct(productType);
        // Check that the retrieved TaxInfo is the same as the created one
        assertEquals(testProduct, retrievedProduct,
                "Checking productType is the same.");
    }
    
    @Test
    public void testGetAllProducts() throws PersistenceException {
        // create two Products
        String productTypeOne = "Tile";
        Product testProductOne = new Product(productTypeOne, 
                new BigDecimal("3.50"), new BigDecimal("4.15"));
        
        String productTypeTwo = "Wood";
        Product testProductTwo = new Product(productTypeTwo,
                new BigDecimal("5.15"), new BigDecimal("4.75"));
                      
        // add both to the DAO
        testProductDao.createProduct(productTypeOne, testProductOne);
        testProductDao.createProduct(productTypeTwo, testProductTwo);
        // get the list of Products from the DAO
        List<Product> productsList = testProductDao.getAllProducts();
        
        // check that the list != null && contains exactly 2 items
        assertNotNull(productsList, "Checking that the list is not null.");
        assertEquals(2, productsList.size(), "Checking that the list has "
                + "exactly 2 items.");
        
        assertTrue(productsList.contains(testProductOne));
        assertTrue(productsList.contains(testProductTwo));
    }
    
    @Test
    public void testUpdateProduct() throws PersistenceException {
        // create a Product
        BigDecimal costPerSqFt = new BigDecimal("3.50");
        BigDecimal costPerSqFtUpdate = new BigDecimal("3.00");
        String productType = "Tile";
        Product testProduct = new Product(productType, costPerSqFt, 
                new BigDecimal("4.15"));

        // add it to the DAO
        testProductDao.createProduct(productType, testProduct);
        // modify it and update in the DAO
        testProduct.setCostPerSqFoot(costPerSqFtUpdate);
        testProductDao.updateProduct(productType, testProduct);
        
        // get it from the DAO;
        Product updatedProduct = testProductDao.getProduct(productType);

        assertNotNull(updatedProduct);
        assertEquals(updatedProduct, testProduct, 
                "Retrieved product should equal the updated project.");
        assertTrue(updatedProduct.getCostPerSqFoot()
                .compareTo(costPerSqFtUpdate) == 0,
                "Updated CostPerSqFt in testProduct should equal 3.00.");
    }
    
    @Test
    public void testDeleteProduct() throws PersistenceException {
        // create a Product
        String productType = "Tile";
        Product testProduct = new Product(productType, new BigDecimal("3.50"), 
                new BigDecimal("4.15"));
        
        // add it to the DAO
        testProductDao.createProduct(productType, testProduct);
        // delete it from the DAO
        testProductDao.deleteProduct("Tile");
        // get a list of all taxInfo in the DAO
        List<Product> productsList = testProductDao.getAllProducts();
        
        // check that the list is empty, since there was only one item in it
        assertTrue(productsList.isEmpty(), 
                "Checking that list of Products is empty."); 
    }
    
}
