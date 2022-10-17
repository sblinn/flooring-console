
package com.sblinn.flooringmastery.controller;

import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.service.DataNotFoundException;
import com.sblinn.flooringmastery.service.FlooringService;
import com.sblinn.flooringmastery.service.InvalidDataException;
import com.sblinn.flooringmastery.ui.FlooringView;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public class FlooringController {
    
    private FlooringView view;
    private FlooringService service;
    
    
    public FlooringController(FlooringView view, FlooringService service) {
        // this will be modified once Spring is implemented
        this.view = view;
        this.service = service;
    }
    
    
    public void run() {
        boolean keepGoing = true;
        
        while (keepGoing) {
            try {
                int menuSelection = getMenuSelection();
                
                switch (menuSelection) {
                    case 1: 
                        displayOrdersOnDate();
                        break;
                    case 2:
                        createOrder();
                        break;
                    case 3:
                        updateOrder();
                        break;
                    case 4:
                        deleteOrder();
                        break;
                    case 5:
                        exportAllData();
                        break;
                    case 6: // QUIT
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                } 
    
            } catch (PersistenceException 
                | DataNotFoundException 
                | InvalidDataException e) {
                view.displayErrorMessage(e.getMessage());
            }
        } exitMessage();
    }
    
    private int getMenuSelection() {
        return view.printMainMenuAndGetSelection();
    }
    
    private void displayOrdersOnDate() throws 
            PersistenceException,
            DataNotFoundException {
        try {
            LocalDate orderDate = view.getInputOrderDate();
            view.displayOrdersBanner(orderDate);
            List<Order> ordersList = service.getAllOrdersOnDate(orderDate);
            view.displayOrdersList(ordersList);
            view.displayContinuePrompt();
        } catch (DataNotFoundException e) {
            view.displayErrorMessage(e.getMessage());
            view.displayContinuePrompt();
        }
        
    }
    
    private void createOrder() throws 
            PersistenceException, 
            DataNotFoundException,
            InvalidDataException {

        boolean keepGoing = false;
        while (keepGoing == false) {
            Order order = view.getNewOrderInfo(service.getAllProducts(),
                    service.getAllTaxInfo());

            if (order != null) {
                Order calculatedOrder = service.calculateOrder(order);

                // True for yes, False for no
                boolean response
                        = view.displayOrderSummaryAndGetYesNoResponse(calculatedOrder, 
                                "Are you sure you would like to "
                                        + "create this order?");
                if (response == true) {
                    service.createOrder(calculatedOrder);
                    view.displayActionMessageBanner("ORDER #"
                            + calculatedOrder.getOrderNumber() + " SUCCESSFULLY CREATED");
                } else {
                    view.displayActionMessageBanner("NO NEW ORDER CREATED");
                }
            } 

            keepGoing = true;
        }

        view.displayContinuePrompt();
    }
    
    private void updateOrder() throws 
            PersistenceException,
            DataNotFoundException,
            InvalidDataException {
        
        LocalDate orderDate = view.getInputOrderDate();
        int orderNumber = view.getInputOrderNumber();

        Order oldOrder = service.getOrder(orderDate, orderNumber);
        view.displayActionMessageBanner("ORDER FOUND");

        Order updatedOrder = view.getUpdatedOrderInfo(oldOrder,
                service.getAllProducts(), service.getAllTaxInfo());

        updatedOrder = service.calculateOrder(updatedOrder);

        boolean response = view.displayOrderSummaryAndGetYesNoResponse(
                updatedOrder, "Are you sure you would like to save "
                    + "the following updates to this order?");
        if (response == true) {
            service.updateOrder(updatedOrder);
            view.displayActionMessageBanner("Order #" 
                    + updatedOrder.getOrderNumber()
                    + " SUCCESSFULLY UPDATED");
        } else {
            view.displayActionMessageBanner("NO ORDER UPDATES SAVED");
        }
        
        view.displayContinuePrompt();
    }
    
    private void deleteOrder() throws 
            PersistenceException,
            DataNotFoundException {
        
        LocalDate orderDate = view.getInputOrderDate();
        int orderNumber = view.getInputOrderNumber();
        
        Order orderToDelete = service.getOrder(orderDate, orderNumber);

        boolean response = view.displayOrderSummaryAndGetYesNoResponse(
                orderToDelete, "Are you sure you want to delete order #"
                + orderNumber + "?");
        if (response == true) {
            service.deleteOrder(orderDate, orderNumber);
            view.displayActionMessageBanner("ORDER #" + orderNumber
                    + " SUCCESSFULLY DELETED");
        } else {
            view.displayActionMessageBanner("NO ORDER DELETED");
        }
        
        view.displayContinuePrompt();
    }
    
    private void exportAllData() throws PersistenceException {
        view.displayExportDataBanner();
        String filename = view.getInputFileName();
        service.exportBackupDataToFile(filename);
        view.displayActionMessageBanner("BACKUP DATA EXPORTED TO " + filename);
        view.displayContinuePrompt();
    }
    
    
    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }
    
    private void exitMessage() {
        view.displayExitBanner();
    }
    
}
