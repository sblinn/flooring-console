
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sarablinn
 */
public class OrderDaoFileImpl implements OrderDao {

    // HashMap<orderDate, HashMap<orderNumber, Order>>
    private HashMap<LocalDate, HashMap<Integer, Order>> orders = 
            new HashMap<LocalDate, HashMap<Integer,Order>>();
    private final String ORDERS_DIR;
    private static final String DELIMITER = ",";
    
    
    public OrderDaoFileImpl() {
        this.ORDERS_DIR = "../FlooringMastery/Orders";  
    }
    
    public OrderDaoFileImpl(String ordersDirFilepath) {
        this.ORDERS_DIR = ordersDirFilepath;
    }
    
    
    /**
     * Returns new Order if successful, else returns null.
     * @param order
     * @return
     * @throws PersistenceException 
     */
    @Override
    public Order createOrder(Order order) throws PersistenceException {
        loadOrders();
        LocalDate orderDate = order.getOrderDate();
        int orderNumber = order.getOrderNumber();
        
        // add orderDate key if it doesn't yet exist
        if(!orders.containsKey(orderDate)) {
            orders.put(orderDate, new HashMap<>());
        }
        
        Order createResult = orders.get(orderDate).put(orderNumber, order);
        writeOrders();
        
        if (createResult == null) {
            return order;
        } else {
            return null;
        }
    }

    @Override
    public Order getOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException {
        
        loadOrders();
        // if orderDate is not in memory, NullPointerException is thrown
        Order retrievedOrder = orders.get(orderDate).get(orderNumber);
        
        return retrievedOrder;
    }

    /**
     * Returns an List of Orders on a given date, sorted by Order number.
     * 
     * @param orderDate LocalDate
     * @return sortedOrdersOnDate List
     * @throws PersistenceException 
     */
    @Override
    public List<Order> getAllOrdersOnDate(LocalDate orderDate) throws 
            PersistenceException {
        
        loadOrders();
        // if orderDate is not in memory, NullPointerException is thrown
        HashMap<Integer, Order> ordersOnDate = orders.get(orderDate);
        List<Order> sortedOrdersOnDate = new ArrayList(ordersOnDate.values());
        sortedOrdersOnDate.sort(Comparator.comparing((i) -> i.getOrderNumber()));
        
        return sortedOrdersOnDate;
    }

    /**
     * Returns a List of Orders sorted by Order number.
     * 
     * @return sortedOrders List of orders sorted by order number
     * @throws PersistenceException
     */
    @Override
    public List<Order> getAllOrders() throws PersistenceException {
        
        loadOrders();
        List<Order> sortedOrders = new ArrayList();
        try {
            for (HashMap<Integer, Order> ordersInnerMap : orders.values()) {
                for (Order order : ordersInnerMap.values()) {
                    sortedOrders.add(order);
                }
            }
            // sort the ArrayList by orderNumber
            sortedOrders.sort(Comparator.comparing((i) -> i.getOrderNumber()));
        } catch (NullPointerException e) {
            return sortedOrders;
        }

        return sortedOrders;
    }

    /**
     * Returns null if the updatedOrder was added to memory without replacing 
     * anything, else returns the order that updatedOrder replaced in memory.
     * 
     * @param updatedOrder
     * @return original Order that was updated or null 
     * @throws PersistenceException 
     */
    @Override
    public Order updateOrder(Order updatedOrder) throws PersistenceException {
        
        loadOrders();
        LocalDate orderDate = updatedOrder.getOrderDate();
        int orderNumber = updatedOrder.getOrderNumber();      
        Order result = orders.get(orderDate).put(orderNumber, updatedOrder);

        writeOrders();
        
        return result;
    }

    /**
     * Deletes and returns the Order if it exists, else returns null.
     * 
     * @param orderDate
     * @param orderNumber
     * @return deletedOrderResult
     * @throws PersistenceException 
     */
    @Override
    public Order deleteOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException {
        
        loadOrders();
        // throws NullPointerException is there is no order to delete
        HashMap<Integer, Order> ordersOnDate = orders.get(orderDate);
        Order deletedOrderResult = ordersOnDate.remove(orderNumber);       
        // if only one order on date, delete date so theres no null value.
        int numOrders = orders.get(orderDate).size();
        if (numOrders == 0) {
            orders.remove(orderDate);
        } else {
            orders.put(orderDate, ordersOnDate);
        }
        writeOrders();
        
        return deletedOrderResult;
    }

    /**
     * Exports/writes all the Orders to a single backup file. 
     * 
     * @param filepath String
     * @throws PersistenceException 
     */
    @Override
    public void exportBackupDataToFile(String filepath) 
            throws PersistenceException {
        
        PrintWriter out = null;
        String fileHeaderText = 
                "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
                + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,"
                + "LaborCost,Tax,Total,OrderDate";

        List<Order> ordersList = this.getAllOrders();
        // if no file exists at this valid file path, PrintWriter creates new
        try {
            File exportFile = new File(filepath);
            out = new PrintWriter(new FileWriter(exportFile));
            out.println(fileHeaderText);
            out.flush();
        } catch (IOException e) {
            throw new PersistenceException(
                    "Unable to export data to " + filepath + ". "
                            + "Check file name and path.");
        }
        
        for (Order currentOrder : ordersList) {
            String currentOrderAsText = marshallOrder(currentOrder);
            out.println(currentOrderAsText);
            out.flush();
        }
        
        ZonedDateTime today = ZonedDateTime.now();
        out.println("\nData backed up on: " 
                + today.format(DateTimeFormatter.ISO_OFFSET_DATE));
        out.close();
    }
    
    /**
     * Returns the String representation of an Order. 
     * 
     * @param order Order
     * @return orderAsText String
     */
    private String marshallOrder(Order order) {
        String orderAsText = Integer.toString(order.getOrderNumber()) + DELIMITER;
        orderAsText += "'" + order.getCustomerName() + "'" + DELIMITER;
        orderAsText += order.getTaxInfo().getStateAbbr() + DELIMITER;
        orderAsText += order.getTaxInfo().getTaxRate() + DELIMITER;
        orderAsText += order.getProduct().getProductType() + DELIMITER;
        orderAsText += order.getArea() + DELIMITER;
        orderAsText += order.getProduct().getCostPerSqFoot() + DELIMITER;
        orderAsText += order.getProduct().getLaborCostPerSqFoot() + DELIMITER;
        orderAsText += order.getMaterialCost() + DELIMITER;
        orderAsText += order.getLaborCost() + DELIMITER;
        orderAsText += order.getTax() + DELIMITER;
        orderAsText += order.getTotal() + DELIMITER;
        // include orderDate, it will be needed for creating order file name
        // and is included in exportBackupDataToFile method.
        orderAsText += order.getOrderDate()
                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));      
        
        return orderAsText;
    }
    
    /**
     * Returns an Order from its String representation.
     * 
     * @param orderAsText String
     * @return order Order
     * @throws PersistenceException
     */
    private Order unmarshallOrder(String orderAsText) throws 
            PersistenceException {
        /*
        orderDate (MMddyyyy) is last delimited item 
        -- needed for creating the Order object.
        */

        /*
        REMINDER: 
        the if/else statements can be removed in the future, but for now we will
        keep them since the sample files do not separate customerName.
        */
        
        // cut out and save 'customerName' from orderAsText
        String customerName = "";
        boolean hasQuotes = orderAsText.contains("'");
        if (hasQuotes == true) {
            int start = orderAsText.indexOf("'") + 1; // excludes the '' 
            int end = orderAsText.lastIndexOf("'");
            customerName = orderAsText.substring(start, end);
            // replace customerName with void, so commas from name won't 
            // interfere and array indices will stay the same when text is split
            orderAsText = orderAsText.replaceFirst(customerName, "");
        }
        
        String[] orderData = orderAsText.split(DELIMITER);
        Order orderFromFile;
        
        // MMddyyyy -> MM-dd-yyyy
        LocalDate orderDate = LocalDate.parse(orderData[12], 
                DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        
        int orderNumber = Integer.parseInt(orderData[0]);
        orderFromFile = new Order(orderDate, orderNumber);
        
        if (hasQuotes == true) {
            orderFromFile.setCustomerName(customerName);
        } else {
            orderFromFile.setCustomerName(orderData[1]);
        }

        // this constructor will set the state name
        TaxInfo taxInfo = new TaxInfo(orderData[2]);
        taxInfo.setTaxRate(new BigDecimal(orderData[3]));
        orderFromFile.setTaxInfo(taxInfo);
        
        Product product = new Product();
        product.setProductType(orderData[4]);
        product.setCostPerSqFoot(new BigDecimal(orderData[6]));
        product.setLaborCostPerSqFoot(new BigDecimal(orderData[7]));
        orderFromFile.setProduct(product);
        
        orderFromFile.setArea(new BigDecimal(orderData[5]));
        orderFromFile.setMaterialCost(new BigDecimal(orderData[8]));
        orderFromFile.setLaborCost(new BigDecimal(orderData[9]));
        orderFromFile.setTax(new BigDecimal(orderData[10]));
        orderFromFile.setTotal(new BigDecimal(orderData[11]));
        
        return orderFromFile; 
    }
    
    /**
     * Loads the HashMap with Order objects from their files stored in the 
     * Orders directory.
     * 
     * @throws PersistenceException 
     */
    private void loadOrders() throws PersistenceException {
        Scanner scanner;
        
        File ordersDir = new File(ORDERS_DIR);
        File[] ordersDirFiles = ordersDir.listFiles(
                (dir, name) -> name.matches("Orders_" + "\\d{8}.txt"));
        
        if (ordersDirFiles != null) {
            for (File currentOrderFile : ordersDirFiles) {
                String currentOrderFileName;
                
                try {
                    // set up the current order file to load to memory
                    currentOrderFileName = currentOrderFile.getName();
                    scanner = new Scanner(new BufferedReader(
                            new FileReader(currentOrderFile)));
                } catch (FileNotFoundException e) {
                    throw new PersistenceException(
                            "Unable to load order data from file into memory.");
                }
       
                if (scanner.hasNext()) {
                    // read and ignore header on first line
                    scanner.nextLine();

                    // get orderDate from file name and format it for unmarshalling
                    String orderDateStrFromFileName
                            = currentOrderFileName.substring(7, 15);
                    LocalDate orderDate = LocalDate.parse(orderDateStrFromFileName,
                            DateTimeFormatter.ofPattern("MMddyyyy"));
                    String orderDateStr = orderDate.format(
                            DateTimeFormatter.ofPattern("MM-dd-yyyy"));

                    String currentLine;
                    Order currentOrder;

                    // add orderDate key 
                    orders.put(orderDate, new HashMap<>());

                    while (scanner.hasNext()) {
                        currentLine = scanner.nextLine();
                        currentOrder = unmarshallOrder(currentLine
                                + DELIMITER + orderDateStr);
                        // add current order to memory
                        orders.get(currentOrder.getOrderDate())
                                .put(currentOrder.getOrderNumber(), currentOrder);
                    }
                }
            }
        } else {
            throw new PersistenceException("Unable to find Orders directory.");
        }
    }
    
    /**
     * Writes all the Orders from the HashMap into individual order files stored
     * in the Orders directory.
     *
     * @throws PersistenceException
     */
    private void writeOrders() throws PersistenceException {
        PrintWriter out = null;
        String fileHeaderText =
                 "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,"
                + "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,"
                + "LaborCost,Tax,Total";

        resetOrdersDir();
        
        if (!orders.isEmpty()) {
            
            for (LocalDate date : orders.keySet()) {
                String orderDateStr = date.format(
                        DateTimeFormatter.ofPattern("MMddyyyy"));
                //Create a file for that orderDate
                String orderFileName = "Orders_" + orderDateStr + ".txt";
                try {
                    File currentOrderFile = new File(ORDERS_DIR, orderFileName);
                    out = new PrintWriter(
                            new FileWriter(currentOrderFile, true), true);
                } catch (IOException e) {
                    throw new PersistenceException(
                            "Unable to save data to order file.");
                }
                //Write header
                out.println(fileHeaderText);

                for (Order order : orders.get(date).values()) {
                    //Write Order to file
                    // get order as text String & remove orderDate data at end of String
                    String orderAsText = marshallOrder(order);
                    int lastDelimiterIndex = orderAsText.lastIndexOf(DELIMITER);
                    orderAsText = orderAsText.substring(0, lastDelimiterIndex);
                    out.println(orderAsText);
                    out.flush();
                }
            }

            out.close();
        }
    }
    
    /**
     * Deletes all the files in the ORDERS_DIR directory, then deletes and 
     * recreates the empty ORDERS_DIR.
     */
    private void resetOrdersDir() {
        File ordersDir = new File(ORDERS_DIR);
        File[] ordersFromDir = ordersDir.listFiles();

        for (File orderFile : ordersFromDir) {
            orderFile.delete();
        }
        ordersDir.delete();
        ordersDir.mkdir();
    }

}
