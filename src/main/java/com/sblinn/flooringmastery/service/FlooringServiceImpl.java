
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.OrderDao;
import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dao.ProductDao;
import com.sblinn.flooringmastery.dao.TaxInfoDao;
import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public class FlooringServiceImpl implements FlooringService {

    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxInfoDao taxInfoDao;
    
    private int maxOrderNumber; 
    private final String DATA_EXPORT_DIR;
    
    
    public FlooringServiceImpl(OrderDao orderDao, ProductDao productDao, 
            TaxInfoDao taxInfoDao) {
        
        this.productDao = productDao;
        this.taxInfoDao = taxInfoDao;
        this.orderDao = orderDao;
        
        this.DATA_EXPORT_DIR = "../FlooringMastery/Backup";
        
        setMaxOrderNumber();
    }
    
    public FlooringServiceImpl(String dataExportDirName, OrderDao orderDao,
            ProductDao productDao, TaxInfoDao taxInfoDao) {
        
        this.productDao = productDao;
        this.taxInfoDao = taxInfoDao;
        this.orderDao = orderDao;
        
        this.DATA_EXPORT_DIR = dataExportDirName;
     
        setMaxOrderNumber();
    }
            
    
    @Override
    public Order createOrder(Order order) throws 
            PersistenceException, 
            DataNotFoundException, 
            InvalidDataException {
        
        Order newOrder = new Order();     
        
        // check that orderDate is a future date
        LocalDate orderDate = order.getOrderDate();
        LocalDate today = LocalDate.now();
        if (orderDate.compareTo(today) <= 0) {
            throw new InvalidDataException(
                    "Error: Order date must be a future date.");
        }
        
        newOrder.setOrderNumber(maxOrderNumber); 
        newOrder = calculateOrder(order); 
        orderDao.createOrder(newOrder);
        setMaxOrderNumber();
        
        return newOrder;
    }

    @Override
    public Order getOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException,
            DataNotFoundException {
        
        // if orderDate doesn't exist in memory, nullpointerexception is 
        // thrown. if the order doesn't exist, order will be null
        Order order;
        try {
            order = orderDao.getOrder(orderDate, orderNumber);
            if (order == null) {
                throw new DataNotFoundException(
                        "Error: No Order #" + orderNumber + " found on "
                        + orderDate.format(DateTimeFormatter
                                .ofPattern("MM-dd-yyyy")) + ".");
            }
        } catch (NullPointerException e) {
            throw new DataNotFoundException(
                    "Error: No order found on "
                    + orderDate.format(DateTimeFormatter
                            .ofPattern("MM-dd-yyyy")) + ".");
        }

        return order;
    }

    @Override
    public List<Order> getAllOrdersOnDate(LocalDate orderDate) throws 
            PersistenceException,
            DataNotFoundException {
        
        List<Order> ordersOnDate;
        try {
            ordersOnDate = orderDao.getAllOrdersOnDate(orderDate);
        } catch (NullPointerException e) {
            throw new DataNotFoundException(
                    "No orders found for: " + orderDate.format(DateTimeFormatter
                            .ofPattern("MM-dd-yyyy")) + ".");
        }
                
        return ordersOnDate;
    }

    @Override
    public Order updateOrder(Order updatedOrder) throws 
            PersistenceException,
            DataNotFoundException,
            InvalidDataException {
        
        LocalDate orderDate = updatedOrder.getOrderDate();
        int orderNumber = updatedOrder.getOrderNumber();
        
        Order oldOrder;
        try {
            oldOrder = orderDao.getOrder(orderDate, orderNumber);
            if (oldOrder == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            throw new DataNotFoundException(
                    "Error: No Order #" + orderNumber + " found on " 
                    + orderDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) 
                    + ". No order to update.");
        }
        
        // if any fields were left blank, keep the old data
        if (updatedOrder.getCustomerName().trim().length() == 0
                || updatedOrder.getCustomerName() == null) {
            updatedOrder.setCustomerName(oldOrder.getCustomerName());
        }
        
        if (updatedOrder.getTaxInfo() == null) {
            updatedOrder.setTaxInfo(oldOrder.getTaxInfo());
        }
        
        if (updatedOrder.getProduct() == null) {
            updatedOrder.setProduct(oldOrder.getProduct());
        }
 
        if (updatedOrder.getArea() == null) {
            updatedOrder.setArea(oldOrder.getArea());
        }

        updatedOrder = calculateOrder(updatedOrder);
        orderDao.updateOrder(updatedOrder);

        return updatedOrder;
    }

    @Override
    public Order deleteOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException,
            DataNotFoundException {
        
        Order deletedOrder; 
        try {
            deletedOrder = orderDao.deleteOrder(orderDate, orderNumber);
            if (deletedOrder == null) {
                throw new DataNotFoundException(
                        "Error: No Order #" + orderNumber + " on " + orderDate
                        .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                        + " found to delete.");
            }
        } catch (NullPointerException e) {
            throw new DataNotFoundException(
                "Error: No Order #" + orderNumber + " on " + orderDate
                        .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                        + " found to delete.");
        }
        return deletedOrder;
    }

    @Override
    public Order calculateOrder(Order order) throws 
            PersistenceException,
            DataNotFoundException,
            InvalidDataException {
        
        // create a copy of the order to calculate
        Order calculatedOrder = new Order(order.getOrderDate());
        
        if (order.getOrderNumber() == 0) {
            calculatedOrder.setOrderNumber(maxOrderNumber);
        } else {
            calculatedOrder.setOrderNumber(order.getOrderNumber());
        }
        // make sure that state name is set since marshallOrder doesn't set it.
        String stateAbbr = order.getTaxInfo().getStateAbbr();
        TaxInfo taxInfo = taxInfoDao.getTaxInfo(stateAbbr);
        
        calculatedOrder.setCustomerName(order.getCustomerName());
        calculatedOrder.setProduct(order.getProduct());
        calculatedOrder.setTaxInfo(taxInfo);        
        calculatedOrder.setArea(order.getArea());
        
        validateRequiredOrderData(calculatedOrder); 
        
        try {
            BigDecimal area = order.getArea();
            BigDecimal costPerSqFt = order.getProduct().getCostPerSqFoot();
            BigDecimal laborCostPerSqFt = order.getProduct().getLaborCostPerSqFoot();

            BigDecimal hundredPercent = new BigDecimal("100.00");
            BigDecimal taxRatePercentage = order.getTaxInfo().getTaxRate()
                    .divide(hundredPercent, 2, RoundingMode.HALF_UP);

            BigDecimal materialCost = area.multiply(costPerSqFt);
                materialCost = materialCost.setScale(2, RoundingMode.HALF_UP);
            BigDecimal laborCost = area.multiply(laborCostPerSqFt);
                laborCost = laborCost.setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalCost = materialCost.add(laborCost);
                totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
            BigDecimal tax = totalCost.multiply(taxRatePercentage);
                tax = tax.setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = totalCost.add(tax);
                total = total.setScale(2, RoundingMode.HALF_UP);

            calculatedOrder.setMaterialCost(materialCost);
            calculatedOrder.setLaborCost(laborCost);
            calculatedOrder.setTax(tax);
            calculatedOrder.setTotal(total);
        } catch (NullPointerException e) {
            throw new DataNotFoundException(
                    "ERROR: Required order data is missing. Unable to "
                            + "calculate order information.");
        }
        
        return calculatedOrder;
    }

    @Override
    public void exportBackupDataToFile(String filename) throws 
            PersistenceException {   
        String filepath = DATA_EXPORT_DIR + "/" + filename;
        if (!filename.contains(".txt")) {
            filepath += ".txt";
        }

        orderDao.exportBackupDataToFile(filepath);
    }

    
    @Override
    public Product getProduct(String productType) throws 
            PersistenceException, 
            DataNotFoundException {
        
        Product product = productDao.getProduct(productType);
        if (product == null) {
            throw new DataNotFoundException(
                "Error: No data for " + productType + " found.");
        }
        
        return product;
    }

    @Override
    public List<Product> getAllProducts() throws 
            PersistenceException,
            DataNotFoundException {  
        List<Product> productsList = productDao.getAllProducts();
        if (productsList.isEmpty()) {
            throw new DataNotFoundException(
                "Error: No products available.");
        }
        return productsList;
    }

    @Override
    public TaxInfo getTaxInfo(String state) throws 
            PersistenceException,
            DataNotFoundException {
        
        TaxInfo taxInfo = taxInfoDao.getTaxInfo(state.toUpperCase());
        if (taxInfo == null) {
            throw new DataNotFoundException(
                "Error: No tax data for " + state + ".");
        }

        return taxInfo;
    }

    @Override
    public List<TaxInfo> getAllTaxInfo() throws 
            PersistenceException,
            DataNotFoundException {
        List<TaxInfo> taxInfoList = taxInfoDao.getAllTaxInfo();
        if (taxInfoList.isEmpty()) {
            throw new DataNotFoundException(
                "Error: No available tax data.");
        }
        return taxInfoList;
    }
    
    
    private void validateRequiredOrderData(Order order) 
            throws DataNotFoundException, 
            InvalidDataException {
        /*
        check that order has: 
        ordernumber, orderDate, customerName, taxInfo, product, area
        */
        
        /*
        we won't check that orderDate is in the future here, 
        so we can use validateRequiredOrderData in calculateOrder method 
        (which is used in updateOrder method) and still allow potential 
        future implementations which allow user to update past orders.
        */

        // check that required fields aren't blank
        if (order.getOrderDate() == null
                || order.getCustomerName().trim().length() == 0
                || order.getCustomerName() == null
                || order.getProduct() == null
                || order.getTaxInfo() == null
                || order.getArea() == null) {
            
            throw new DataNotFoundException(
                    "Error: Order is missing required data.");
        }
        

        // check that customerName does not contain any special chars
        String customerName = order.getCustomerName();
        for (int index = 0; index < customerName.length(); index++) {
            if (!Character.isLetterOrDigit(customerName.charAt(index))) {
                if (customerName.charAt(index) != ',') {
                    if (customerName.charAt(index) != '.') {
                        if (customerName.charAt(index) != ' ') {
                            throw new InvalidDataException(
                                "Error: Customer name cannot contain any "
                                + "special characters. It can only contain "
                                + "'A-Z', '0-9', comma ',', and period '.'.");
                        }
                    }
                }
            }
        }
        
        // check that area is a positive decimal >= 100 sqft
        BigDecimal area = order.getArea();
        BigDecimal minArea = new BigDecimal("100.00");
        if (area.compareTo(minArea) < 0) {
            throw new InvalidDataException(
                "Error: Area must be postive and greater than 100.");
        } 
    }
    
    private void setMaxOrderNumber() {
        try {
            List<Order> ordersList = orderDao.getAllOrders();
            if (!ordersList.isEmpty()) { // prevent NoSuchElementException
                this.maxOrderNumber = ordersList.stream()
                        .mapToInt((i) -> i.getOrderNumber())
                        .max().getAsInt() + 1;
            } else {
                this.maxOrderNumber = 1;
            }
        } catch (PersistenceException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
