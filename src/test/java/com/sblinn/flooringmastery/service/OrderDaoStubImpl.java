
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.OrderDao;
import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public class OrderDaoStubImpl implements OrderDao {
    
    public Order onlyOrder;
    
    
    /**
     * Creates a hard-coded Order object for testing.
     * 
     * @throws PersistenceException 
     */
    public OrderDaoStubImpl() throws PersistenceException {
        // Tile, cost/sqft 3.50, laborcost/sqft 4.15
        Product onlyProduct = new Product("Tile", new BigDecimal("3.50"),
                new BigDecimal("4.15"));
        // TX,Texas, taxrate 4.45%
        TaxInfo onlyTaxInfo = new TaxInfo("CA", "California", new BigDecimal("25.00"));
        
        // future date
        LocalDate orderDate = LocalDate.now().plusDays(1);       
        this.onlyOrder = new Order(orderDate, 1);
        
        this.onlyOrder.setCustomerName("Joe, Inc.");
        this.onlyOrder.setArea(new BigDecimal("249.00"));
        this.onlyOrder.setProduct(onlyProduct);
        this.onlyOrder.setTaxInfo(onlyTaxInfo);
        
        // rounded half-up
        this.onlyOrder.setMaterialCost(new BigDecimal("871.50")); 
        this.onlyOrder.setLaborCost(new BigDecimal("1033.35")); 
        this.onlyOrder.setTax(new BigDecimal("476.21")); 
        this.onlyOrder.setTotal(new BigDecimal("2381.06"));
    }
    
    /**
     * Allows another class to inject a test Order object.
     * 
     * @param testOrder 
     */
    public OrderDaoStubImpl(Order testOrder) {
        this.onlyOrder = testOrder;
    }
    
    
    @Override
    public Order createOrder(Order order) throws PersistenceException {
        if (order.equals(onlyOrder)) {
            return onlyOrder;
        } else {
            return null;
        }
    }

    @Override
    public Order getOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException {

        if (orderDate.compareTo(onlyOrder.getOrderDate()) == 0) {
            if (orderNumber == onlyOrder.getOrderNumber()) {   
                return onlyOrder;
            } else {
                return null;
            }
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public List<Order> getAllOrdersOnDate(LocalDate orderDate) throws 
            PersistenceException {
        
        if (orderDate.compareTo(onlyOrder.getOrderDate()) == 0) {
            List<Order> ordersOnDate = new ArrayList<>();
            ordersOnDate.add(onlyOrder);
            return ordersOnDate;
        } else {
            throw new NullPointerException();
//            return null;
        }
    }

    @Override
    public List<Order> getAllOrders() throws PersistenceException {
        List<Order> orders = new ArrayList<>();
        orders.add(onlyOrder);
        return orders;
    }

    @Override
    public Order updateOrder(Order updatedOrder) throws PersistenceException {

        if (updatedOrder.getOrderDate().compareTo(onlyOrder.getOrderDate()) == 0) {
            if (updatedOrder.getOrderNumber() == onlyOrder.getOrderNumber()) {  
                this.onlyOrder = updatedOrder;
                return onlyOrder;
            } else {
                return null;
            }
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public Order deleteOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException {
        
        if (orderDate.compareTo(onlyOrder.getOrderDate()) == 0) {
            if (orderNumber == onlyOrder.getOrderNumber()) {
                Order deletedOnlyOrder = this.onlyOrder;
                this.onlyOrder = null;
                return deletedOnlyOrder;
            } else {
                return null;
            }
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public void exportBackupDataToFile(String filepath) throws 
            PersistenceException {
        
        try {
            File exportFile = new File(filepath);
            PrintWriter out = new PrintWriter(new FileWriter(exportFile));
            out.println();
            out.flush();
        } catch (IOException e) {
            throw new PersistenceException(
                    "ERROR WHILE TRYING TO WRITE TO BACKUP FILE.");
        }
    }
    
}
