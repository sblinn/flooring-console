
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public interface FlooringService {
    
    Order createOrder(Order order) throws 
            PersistenceException,
            DataNotFoundException, 
            InvalidDataException;
    
    Order getOrder(LocalDate orderDate, int orderNumber) throws
            PersistenceException,
            DataNotFoundException;
    
    List<Order> getAllOrdersOnDate(LocalDate orderDate) throws 
            PersistenceException,
            DataNotFoundException;
    
    Order updateOrder(Order updatedOrder) throws
            PersistenceException,
            DataNotFoundException,
            InvalidDataException;
    
    Order deleteOrder(LocalDate orderDate, int orderNumber) throws 
            PersistenceException,
            DataNotFoundException;
    
    Order calculateOrder(Order order) throws 
            PersistenceException,
            DataNotFoundException,
            InvalidDataException;
    
    void exportBackupDataToFile(String filename) throws PersistenceException;
    
    
    Product getProduct(String productType) throws
            PersistenceException,
            DataNotFoundException;
    
    List<Product> getAllProducts() throws 
            PersistenceException,
            DataNotFoundException;
    
    TaxInfo getTaxInfo(String state) throws 
            PersistenceException,
            DataNotFoundException ;
    
    List<TaxInfo> getAllTaxInfo() throws 
            PersistenceException,
            DataNotFoundException;
    
}
