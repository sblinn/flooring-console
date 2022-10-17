
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.Order;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public interface OrderDao {
    
    // createOrder does not need orderdate or ordernumber, order is immutable.
    Order createOrder(Order order) throws PersistenceException;
    
    Order getOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException;
    
    List<Order> getAllOrdersOnDate(LocalDate orderDate) throws 
            PersistenceException;
    
    List<Order> getAllOrders() throws PersistenceException;
    
    Order updateOrder(Order updatedOrder) throws PersistenceException;
    
    Order deleteOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException;
    
    void exportBackupDataToFile(String filepath) throws PersistenceException;
    
}
