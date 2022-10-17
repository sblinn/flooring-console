
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.OrderDao;
import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dao.ProductDao;
import com.sblinn.flooringmastery.dao.TaxInfoDao;
import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class FlooringServiceImplTest {
    
    private FlooringService service;
    
    private OrderDao orderDaoStub;
    private ProductDao productDaoStub;
    private TaxInfoDao taxInfoDaoStub;
    
    // use testOrdersDir - it will be empty since we are using stubs
    private final String TEST_DATA_EXPORT_DIR;
    
    
    public FlooringServiceImplTest() {
        ApplicationContext appContext = 
                new ClassPathXmlApplicationContext("testApplicationContext.xml");
        service = appContext.getBean("service", FlooringService.class);
        TEST_DATA_EXPORT_DIR = appContext.getBean("testOrdersDir", String.class);
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        // reset OrderDaoStub so that onlyOrder is reset before each test
        ApplicationContext appContext = 
                new ClassPathXmlApplicationContext("testApplicationContext.xml");
        orderDaoStub = appContext.getBean("orderDaoStub", OrderDao.class);
        
        // reset orders directory so it will be empty before each test
        File ordersDir = new File(TEST_DATA_EXPORT_DIR);
        File[] ordersFromDir = ordersDir.listFiles();

        for (File orderFile : ordersFromDir) {
            orderFile.delete();
        }
        ordersDir.delete();
        ordersDir.mkdir();
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    

    @Test
    public void testCreateAndGetOrderWithValidDate() throws 
            PersistenceException,
            DataNotFoundException, 
            InvalidDataException {
        
        // create order with valid orderDate (same as onlyOrder in orderDaoStub)
        Order testOrder = new Order();
        int expectedOrderNumber = 1;
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        // create the test order
        try {
            // service layer sets order number
            service.createOrder(testOrder);
        } catch (PersistenceException | 
                InvalidDataException | 
                DataNotFoundException e) {
            fail("Order creation should not throw any exceptions, all required "
                    + "data was complete and valid.");
        }
        
        // retrieve the new order 
        Order retrievedOrder = service.getOrder(validOrderDate, 
                expectedOrderNumber);
        
        assertNotNull(retrievedOrder, "Retrieved Order should not equal null.");
        
        assertEquals(testOrder.getOrderDate(), retrievedOrder.getOrderDate(), 
                "order date");
        assertEquals(expectedOrderNumber, retrievedOrder.getOrderNumber(), 
                "order number");
        assertEquals(testOrder.getCustomerName(), retrievedOrder.getCustomerName(), 
                "customer name");
        assertEquals(testOrder.getProduct(), retrievedOrder.getProduct(), 
                "product");
        assertEquals(testOrder.getTaxInfo(), retrievedOrder.getTaxInfo(), 
                "tax info");
        assertEquals(testOrder.getArea(), retrievedOrder.getArea(), 
                "area");
        assertEquals(testOrder.getLaborCost(), retrievedOrder.getLaborCost(), 
                "labor cost");
        assertEquals(testOrder.getMaterialCost(), retrievedOrder.getMaterialCost(), 
                "material cost");
        assertEquals(testOrder.getTax(), retrievedOrder.getTax(), 
                "tax");
        assertEquals(testOrder.getTotal(), retrievedOrder.getTotal(), 
                "total");     
    }
    
    @Test
    public void testCreateOrderWithInvalidDate() {
        // create order with invalid orderDate (a future date)
        Order testOrder = new Order();
        LocalDate invalidOrderDate = LocalDate.now();       
        testOrder.setOrderDate(invalidOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");
        
        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        // instantiate orderDaoStub using test order
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        // create the test order
        try {
            service.createOrder(testOrder);
            fail("Order creation should have thrown InvalidDataException, "
                    + "orderDate was invalid.");
        } catch (InvalidDataException e) {
            // pass
        } catch (PersistenceException | DataNotFoundException e) {
            fail("No other exceptions should have been thrown.");
        }
        // if orderDate doesn't exist in memory, nullpointerexception is 
        // thrown. if the order doesn't exist, order will be null.
    }
    
    @Test
    public void testGetAllOrdersOnDate() throws 
            PersistenceException,
            DataNotFoundException,
            InvalidDataException {
        
        // create valid order (same as in orderDaoStub)
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        // create Order
        service.createOrder(testOrder);
        
        // get orders on date
        List<Order> ordersOnDate; 
        try {
            ordersOnDate = service.getAllOrdersOnDate(validOrderDate);
            assertTrue(ordersOnDate.contains(testOrder), 
                "List of orders should contain test order.");
        } catch (DataNotFoundException e) {
            fail("DataNotFoundException should not have been thrown for "
                    + "existing order date.");
        }
        
        // try getting list of orders on date that has no orders
        try {
            LocalDate invalidOrderDate = LocalDate.now(); 
            service.getAllOrdersOnDate(invalidOrderDate);
            fail("DataNotFoundException should have been thrown for "
                    + "order date that does not exist in memory.");
        } catch (DataNotFoundException e) {
            // pass
        }
    }
    
    @Test
    public void testUpdateOrder() throws 
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        // updateOrder throws a dataNotFoundException if orderdate doesn't exist
        // This has been tested in other tests so we don't need to test that here.
        
        // create valid order (same as in orderDaoStub)
        Order testOrder = new Order();
        int expectedOrderNumber = 1;
        testOrder.setOrderNumber(expectedOrderNumber);
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        // create order
        service.createOrder(testOrder);
        Order originalOrder = service.getOrder(validOrderDate, expectedOrderNumber);
        
        // modify original order customer name to use for update
        Order updatedOrder = service.getOrder(validOrderDate, expectedOrderNumber);
        updatedOrder.setCustomerName("Target");
                
        // update order and get the order
        Order retrievedOrder = service.updateOrder(updatedOrder);
        
        assertNotNull(retrievedOrder);
        assertEquals(retrievedOrder.getCustomerName(), "Target",
                "Updated order's customer name should be the same as test order's.");
        
        assertEquals(testOrder.getOrderDate(), retrievedOrder.getOrderDate(), 
                "order date");
        assertEquals(expectedOrderNumber, retrievedOrder.getOrderNumber(), 
                "order number");

        assertEquals(testOrder.getProduct(), retrievedOrder.getProduct(), 
                "product");
        assertEquals(testOrder.getTaxInfo(), retrievedOrder.getTaxInfo(), 
                "tax info");
        assertEquals(testOrder.getTaxInfo().getStateName(), 
                retrievedOrder.getTaxInfo().getStateName(), 
                "tax info -> state name");
        assertEquals(testOrder.getTaxInfo().getTaxRate(), 
                retrievedOrder.getTaxInfo().getTaxRate(), 
                "tax info -> tax rate");
        assertEquals(testOrder.getArea(), retrievedOrder.getArea(), 
                "area");
        assertEquals(testOrder.getLaborCost(), retrievedOrder.getLaborCost(), 
                "labor cost");
        assertEquals(testOrder.getMaterialCost(), retrievedOrder.getMaterialCost(), 
                "material cost");
        assertEquals(testOrder.getTax(), retrievedOrder.getTax(), 
                "tax");
        assertEquals(testOrder.getTotal(), retrievedOrder.getTotal(), 
                "total"); 
    }
    
    @Test
    public void testUpdateOrderWithEmptyField() throws 
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        
        // create valid order (same as in orderDaoStub)
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        // create order
        service.createOrder(testOrder);

        // modify test order to EMPTY customer name to use for update
        testOrder.setCustomerName(" ");
        // update order
        Order updatedOrder = service.updateOrder(testOrder);
        
        /*
        service layer checks updated Order for empty fields, then replaces them
        with the values from the original Order.
        */

        assertEquals(updatedOrder.getCustomerName(), testOrder.getCustomerName(),
                "Updated order's customer name should be the same as test order's.");
        assertEquals(testOrder, updatedOrder, 
                "Updated order should equal the updated test order.");
    }
    
    @Test
    public void testDeleteOrder() throws PersistenceException, DataNotFoundException, InvalidDataException {
        // try deleting order with orderDate that doesn't exist
        // --> should throw nullpointer
        // try deleting order with orderNumber that doesn't exist
        // --> should return null
        
        // create order (same as in orderDaoStub)
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        service.createOrder(testOrder);
        
        // delete the order 
        try {
            Order deletedOrder = service.deleteOrder(validOrderDate, 
                    testOrder.getOrderNumber());
            assertEquals(testOrder, deletedOrder);
        } catch (DataNotFoundException e) {
            fail("No DataNotException should have been thrown, order existed.");
        }
        
        // try deleting order with orderDate that doesn't exist
        // --> should throw nullpointer in DAO, then service throws DataNotFoundException
        try {
            service.deleteOrder(LocalDate.now(), testOrder.getOrderNumber());
            fail("DataNotFoundException should have been thrown.");
        } catch (DataNotFoundException e) {
            // pass
        }
        
        // try deleting order with orderNumber that doesn't exist
        // --> should return null, which service layer catches and throws DataNotFoundException
        try {
            service.deleteOrder(validOrderDate, 2);
            fail("DataNotFoundException should have been thrown.");
        } catch (DataNotFoundException e) {
            // pass
        }
    }
    
    /*
    - test using Order with invalid order data – check that all the cases caught 
        in service.validateRequiredOrderData() are catching invalid data 
        properly.
    - test using Order with all valid data.   
    */
    @Test
    public void testCalculateOrderValidData() throws 
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        // create order 
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        BigDecimal expectedMaterialCost = new BigDecimal("871.50");
        BigDecimal expectedLaborCost = new BigDecimal("1033.35");
        BigDecimal expectedTax = new BigDecimal("476.21");
        BigDecimal expectedTotal = new BigDecimal("2381.06");
        
        // instantiate orderDaoStub with this testOrder
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        try {
            Order calculatedOrder = service.calculateOrder(testOrder);
            assertEquals(expectedMaterialCost, calculatedOrder.getMaterialCost(),
                    "Calculated order's material cost should equal expected value.");
            assertEquals(expectedLaborCost, calculatedOrder.getLaborCost(), 
                    "Calculated order's labor cost should equal expected value.");
            assertEquals(expectedTax, calculatedOrder.getTax(),
                    "Caluclated order's tax should equal expected value.");
            assertEquals(expectedTotal, calculatedOrder.getTotal(),
                    "Calculated order's total should equal expected value.");
        } catch (DataNotFoundException | InvalidDataException e) {
            fail("No exceptions should have been thrown: required order data "
                    + "was valid, complete and ready to be calculated.");
        }
    }
    
    @Test
    public void testCalculateOrderInvalidArea() throws 
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        // create order 
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        BigDecimal invalidArea = new BigDecimal("0");
        
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        testOrder.setArea(invalidArea);
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        // instantiate orderDaoStub with this testOrder
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        try {
            service.calculateOrder(testOrder);
            fail("InvalidDataException should have been thrown, area value "
                    + "for order was invalid: should be >= 100.00.");
        } catch (DataNotFoundException | InvalidDataException e) {
            // pass
        }      
    }
    
    @Test
    public void testCalculateOrderInvalidCustomerName() throws
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        // create order 
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        String invalidCustomerName = "Sam's Ice Cream";
        
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);       
        testOrder.setCustomerName(invalidCustomerName);

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        // instantiate orderDaoStub with this testOrder
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        try {
            Order calculatedOrder = service.calculateOrder(testOrder);
            fail("InvalidDataException should have been thrown: required order "
                    + "data was valid, complete and ready to be calculated.");
        } catch (InvalidDataException e) {
            // pass
        }
    }
    
    @Test
    public void testCalculateOrderEmptyCustomerName() throws
            PersistenceException, 
            DataNotFoundException,
            InvalidDataException {
        
        // create order 
        Order testOrder = new Order();
        LocalDate validOrderDate = LocalDate.now().plusDays(1);
        String emptyCustomerName = " ";
        
        testOrder.setOrderDate(validOrderDate);
        testOrder.setOrderNumber(1);       
        testOrder.setCustomerName(emptyCustomerName);

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        // instantiate orderDaoStub with this testOrder
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        try {
            service.calculateOrder(testOrder);
            fail("InvalidDataException should have been thrown: customer name "
                    + "field was empty.");
        } catch (DataNotFoundException e) {
            // pass
        }
    }
    
    @Test
    public void testCaluculateOrderNullRequiredData() throws
            PersistenceException, 
            DataNotFoundException,
            InvalidDataException {
        
        // create order 
        Order testOrder = new Order();
        
        testOrder.setOrderDate(null);
        testOrder.setOrderNumber(1);       
        testOrder.setCustomerName("Joe, Inc.");

        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setProduct(onlyProduct);
        testOrder.setTaxInfo(onlyTaxInfo);
        
        // instantiate orderDaoStub with this testOrder
        orderDaoStub = new OrderDaoStubImpl(testOrder);
        
        try {
            service.calculateOrder(testOrder);
            fail("InvalidDataException should have been thrown: required order "
                    + "data was valid, complete and ready to be calculated.");
        } catch (DataNotFoundException e) {
            // pass
        }
    }
    
    @Test
    public void testBackupDataToFile() {
        // here we're just passing along the file path to the OrderDao
        String exportFileName = "serviceBackup.txt";
        // tell service layer to export backup data
        try {
            service.exportBackupDataToFile(exportFileName);
        } catch (PersistenceException e) {           
            fail("File should have been created.");
        }        
    }
    
    @Test
    public void testGetProduct() throws 
            PersistenceException, 
            DataNotFoundException {
        
        // Expected product 
        Product expectedProduct = new Product("Tile", new BigDecimal("3.50"), 
                new BigDecimal("4.15"));
        
        // get onlyProduct from the productDaoStub
        Product retrievedOnlyProduct = 
                service.getProduct(expectedProduct.getProductType());
        
        // check that the expected and the actual are the same
        assertEquals(retrievedOnlyProduct, expectedProduct);
    }
    
    @Test
    public void testGetAllProducts() throws 
            PersistenceException, 
            DataNotFoundException {
     
        // Only product expected to be in list of all Products
        Product expectedProduct = new Product("Tile", new BigDecimal("3.50"), 
                new BigDecimal("4.15"));
        
        // get all products list
        List<Product> allProducts = service.getAllProducts();
        
        // check that list only contains one product and that it is expectedProduct
        assertFalse(allProducts.isEmpty(), "Product list is not empty.");
        assertTrue(allProducts.size() == 1, 
                "Product list should contain only 1 item.");
        assertTrue(allProducts.contains(expectedProduct), 
                "Product list contains expected product.");
    }
    
    @Test
    public void testGetTaxInfo() throws 
            PersistenceException, 
            DataNotFoundException {
        
        // Expected taxInfo
        TaxInfo expectedTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        // get onlyTaxInfo from the taxInfoDaoStub
        TaxInfo retrievedOnlyTaxInfo = 
                service.getTaxInfo(expectedTaxInfo.getStateAbbr());
        
        // check that the expected and the actual are the same
        assertEquals(retrievedOnlyTaxInfo, expectedTaxInfo);
    }
    
    @Test
    public void testGetAllTaxInfo() throws 
            PersistenceException,
            DataNotFoundException {
        // Only taxInfo expected to be in list of all TaxInfo
        TaxInfo expectedTaxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        
        // get all taxInfo list
        List<TaxInfo> allTaxInfo = service.getAllTaxInfo();
        
        // check that list only contains one product and that it is expectedProduct
        assertFalse(allTaxInfo.isEmpty(), "Tax Info list is not empty.");
        assertTrue(allTaxInfo.size() == 1, 
                "Tax Info list should contain only 1 item.");
        assertTrue(allTaxInfo.contains(expectedTaxInfo), 
                "Tax Info list contains expected tax info item.");
    }
    
    
    
}
