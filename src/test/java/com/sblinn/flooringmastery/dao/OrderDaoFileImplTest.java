package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
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
public class OrderDaoFileImplTest {

    private OrderDao testOrderDao;
    private final String TEST_ORDERS_DIR;

    public OrderDaoFileImplTest() {
        ApplicationContext appContext
                = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        testOrderDao = appContext.getBean("orderDao", OrderDao.class);
        TEST_ORDERS_DIR = appContext.getBean("testOrdersDir", String.class);
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws Exception {
        // refresh the orders dir so it's empty before each test
        File ordersDir = new File(TEST_ORDERS_DIR);
        File[] ordersFromDir = ordersDir.listFiles();
        for (File orderFile : ordersFromDir) {
            orderFile.delete();
        }
        
        ordersDir.delete();
        ordersDir.mkdir();
    }

    @AfterEach
    public void tearDown() {
        // refresh the orders dir so it's empty before each test
        File ordersDir = new File(TEST_ORDERS_DIR);
        File[] ordersFromDir = ordersDir.listFiles();
        for (File orderFile : ordersFromDir) {
            orderFile.delete();
        }
        
        ordersDir.delete();
        ordersDir.mkdir();
    }

    @Test
    public void testCreateAndGetOrder() throws PersistenceException {
        // create an order 
        Order testOrder = new Order();
        testOrder.setOrderDate(LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        // Tile, cost/sqft 3.50, laborcost/sqft 4.15
        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);
        // TX,Texas, taxrate 4.45%
        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));
        testOrder.setTaxInfo(taxInfo);

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));

        // add it to the DAO
        testOrderDao.createOrder(testOrder);
        // retrieve it from the DAO
        Order retrievedOrder = testOrderDao
                .getOrder(testOrder.getOrderDate(), 1);

        assertNotNull(retrievedOrder);
        // check that the retrieved Order is the same as the test order
        assertEquals(true, retrievedOrder.equals(testOrder),
                "Order retrieved from DAO should equal test order.");
        
    }

    @Test
    public void testGetAllOrdersOnDate() throws PersistenceException {
        // create an order 
        Order testOrder = new Order();
        testOrder.setOrderDate(LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        // Tile, cost/sqft 3.50, laborcost/sqft 4.15
        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);
        // TX,Texas, taxrate 4.45%
        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));        
        testOrder.setTaxInfo(taxInfo);

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));

        // add it to the DAO
        testOrderDao.createOrder(testOrder);
        List<Order> ordersOnTestOrderDate
                = testOrderDao.getAllOrdersOnDate(testOrder.getOrderDate());

        assertFalse(ordersOnTestOrderDate.isEmpty(),
                "List of orders should not be empty.");
        assertTrue(ordersOnTestOrderDate.size() == 1, 
                "List of orders on date should contain only 1 item.");
        assertTrue(ordersOnTestOrderDate.contains(testOrder),
                "List should contain the test order.");
    }

    @Test
    public void testGetAllOrders() throws PersistenceException {
        // create two orders on different dates with different order #
        Order testOrder = new Order();
        testOrder.setOrderDate(LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        // Tile, cost/sqft 3.50, laborcost/sqft 4.15
        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);
        // TX,Texas, taxrate 4.45%
        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));        
        testOrder.setTaxInfo(taxInfo);

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));

        // CREATE SECOND ORDER
        Order testOrderTwo = new Order();
        testOrderTwo.setOrderDate(LocalDate.parse("07-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrderTwo.setOrderNumber(2);
        testOrderTwo.setCustomerName("Joe, Inc.");
        testOrderTwo.setProduct(product);
        testOrderTwo.setTaxInfo(taxInfo);
        testOrderTwo.setArea(new BigDecimal("249.00"));
        testOrderTwo.setMaterialCost(new BigDecimal("871.50")); 
        testOrderTwo.setLaborCost(new BigDecimal("1033.35")); 
        testOrderTwo.setTax(new BigDecimal("476.21")); 
        testOrderTwo.setTotal(new BigDecimal("2381.06"));

        // add both to the DAO
        testOrderDao.createOrder(testOrder);
        testOrderDao.createOrder(testOrderTwo);

        // get a list of all orders from the DAO
        List<Order> allOrders = testOrderDao.getAllOrders();

        assertFalse(allOrders.isEmpty(),
                "List of all orders should not be empty");
        assertTrue(allOrders.size() == 2,
                "List of all orders should have exactly 2 orders.");
        assertTrue(allOrders.contains(testOrder),
                "List of all orders should contain testOrder.");
        assertTrue(allOrders.contains(testOrderTwo),
                "List of all orders should contain testOrderTwo.");
    }

    @Test
    public void testUpdateOrder() throws PersistenceException {
        // create order
        Order testOrder = new Order();
        LocalDate orderDate = LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        
        testOrder.setOrderDate(orderDate);
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);
        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));        
        testOrder.setTaxInfo(taxInfo);

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));

        // add the original to the DAO
        testOrderDao.createOrder(testOrder);
        Order originalOrder = testOrderDao.getOrder(orderDate, 1);

        // modify it
        testOrder.setCustomerName("Target");

        // update the Order in the DAO 
        // updateResult should not be null and should equal original order, 
        // which it is replacing
        Order updateResult = testOrderDao.updateOrder(testOrder);
        assertNotNull(updateResult, "Update result should not equal null "
                + "since it should be replacing an existing order.");
        assertEquals(updateResult, originalOrder);
        
        // retrieve the updated order from the DAO
        Order updatedOrder = testOrderDao.getOrder(orderDate,
                testOrder.getOrderNumber());

        // check that the updated test order is the same as retrieved updatedOrder
        assertEquals(testOrder, updatedOrder,
                "Updated test order should equal the retrieved order.");
        assertEquals(updatedOrder.getCustomerName(), "Target",
                "Customer name of retrieved updated order should equal "
                        + "the updated customer name.");
    }

    @Test
    public void testDeleteOrder() throws PersistenceException {
        // create order 
        Order testOrder = new Order();
        testOrder.setOrderDate(LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);

        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));        
        testOrder.setTaxInfo(taxInfo);
        
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));

        // add it to DAO
        testOrderDao.createOrder(testOrder);

        // get it from the DAO -- make sure it was added
        Order retrievedOrder = testOrderDao.getOrder(testOrder.getOrderDate(),
                testOrder.getOrderNumber());
        assertNotNull(retrievedOrder, "Order should have been added to the DAO.");

        // delete it from the DAO
        Order deleteOrderResult = testOrderDao.deleteOrder(
                testOrder.getOrderDate(), testOrder.getOrderNumber());
        List<Order> allOrders = testOrderDao.getAllOrders();

        assertEquals(testOrder, deleteOrderResult, 
                "Return result should equal the test order and not null.");
        assertTrue(allOrders.isEmpty(), "List of orders retrieved from the DAO "
                + "should be empty -- the only existing order should have been "
                + "deleted.");
    }

    @Test
    public void testExportBackupDataToFile() throws
            PersistenceException,
            FileNotFoundException {

        // create a String file name and path from TEST_ORDERS_DIR 
        // -- where to create the test export file
        String fileName = "testBackupFile.txt";
        String filePath = TEST_ORDERS_DIR + "/" + fileName;
        String expectedHeaderText = "OrderNumber,CustomerName,State,TaxRate,ProductType,"
                + "Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,"
                + "LaborCost,Tax,Total,OrderDate";
        String expectedOrderText = "1,'Joe, Inc.',CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06,06-22-2022";
        String expectedBackupDatePartialText = "Data backed up on: "
                + LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        // create an order to add to the DAO
        Order testOrder = new Order();
        testOrder.setOrderDate(LocalDate.parse("06-22-2022",
                DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        testOrder.setOrderNumber(1);
        testOrder.setCustomerName("Joe, Inc.");

        // Tile, cost/sqft 3.50, laborcost/sqft 4.15
        Product product = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        testOrder.setProduct(product);
        // TX,Texas, taxrate 4.45%
        TaxInfo taxInfo = new TaxInfo("CA", "California", 
                new BigDecimal("25.00"));        
        testOrder.setTaxInfo(taxInfo);

        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.setMaterialCost(new BigDecimal("871.50")); 
        testOrder.setLaborCost(new BigDecimal("1033.35")); 
        testOrder.setTax(new BigDecimal("476.21")); 
        testOrder.setTotal(new BigDecimal("2381.06"));
        
        // add testOrder to DAO
        testOrderDao.createOrder(testOrder);

        // call exportBackupDataToFile method in DAO
        testOrderDao.exportBackupDataToFile(filePath);

        // read the document      
        File testExportFile = new File(filePath);
        Scanner scanner = new Scanner(new BufferedReader(
                new FileReader(testExportFile)));

        // check that the document contains all the expected lines of text
        // check that the doc contains the expected header text
        String firstLine = scanner.nextLine();
        assertEquals(firstLine, expectedHeaderText,
                "Expected header text found.");

        // check that it contains the test order on the second line
        String secondLine = scanner.nextLine();
        assertEquals(secondLine, expectedOrderText,
                "Expected order text found.");

        // third line is empty
        scanner.nextLine();

        // check that the fourth line contains the expected back up date (today)
        // but don't need to check the time
        String fourthLine = scanner.nextLine();
        assertTrue(fourthLine.contains(expectedBackupDatePartialText),
                "Expected partial backup date text found.");
    }

}
