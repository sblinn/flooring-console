
package com.sblinn.flooringmastery.ui;

import com.sblinn.flooringmastery.dto.Order;
import com.sblinn.flooringmastery.dto.Product;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author sarablinn
 */
public class FlooringView {
    
    private UserIO io;
    
    
    public FlooringView(UserIO io) {
        this.io = io;
    }
    
    public int printMainMenuAndGetSelection() {
        io.print("\n==== Flooring Order Management ====");
        io.print("1. Display Orders");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export All Data");
        io.print("6. Quit");
        
        return io.readInt("Please select from the above options.", 1, 6);
    }
 
    public void displayOrdersList(List<Order> ordersList) {
        for (Order currentOrder : ordersList) {
            displayOrder(currentOrder);
        }
    }
    
    public void displayOrder(Order order) {
        String orderNumberStr = Integer.toString(order.getOrderNumber());
        if (order.getOrderNumber() == 0) {
            orderNumberStr = "TBD";
        }
        String orderInfo = String.format("\nORDER#: %s \nDate: %s"
                + "\nCustomer Name: %s \nState: %s "
                + "\nState Tax Rate: %s \nProduct Type: %s"
                + "\nArea: %s sqft \nCost/SqFt: $%s/sqft"
                + "\nLabor Cost/SqFt: $%s/sqft"
                + "\nMaterial Cost: $%s \nLabor Cost: $%s"
                + "\nTax: $%s \nTotal: $%s \n",
                orderNumberStr,
                order.getOrderDate()
                        .format(DateTimeFormatter.ofPattern("MM-dd-yyyy")),
                order.getCustomerName(),
                order.getTaxInfo().getStateName(),
                order.getTaxInfo().getTaxRate(),
                order.getProduct().getProductType(),
                order.getArea(),
                order.getProduct().getCostPerSqFoot(),
                order.getProduct().getLaborCostPerSqFoot(),
                order.getMaterialCost(),
                order.getLaborCost(),
                order.getTax(),
                order.getTotal());
        io.print(orderInfo);
    }

    /**
     * Returns a new Order, to have remaining fields be calculated, or returns
     * null if user input errors are encountered.
     *
     * @param productsList
     * @param taxInfoList
     * @return Order
     */
    public Order getNewOrderInfo(List<Product> productsList,
            List<TaxInfo> taxInfoList) {

        displayCreateOrderBanner();

        LocalDate orderDate = getInputNewOrderDate();
        String customerName = getInputNewCustomerName();
        TaxInfo taxInfo;
        Product product;

        // GET TAX INFO -- RETURN NULL ORDER IF NO STATE
        String stateAbbrInput = getInputStateAbbr();
        // filter the taxInfoList to contain only that state.
        List<TaxInfo> filteredTaxList = taxInfoList.stream()
                .filter((t) -> t.getStateAbbr()
                .equalsIgnoreCase(stateAbbrInput))
                .collect(Collectors.toList());

        if (!filteredTaxList.isEmpty()) {
            taxInfo = filteredTaxList.get(0);
        } else {
            displayErrorMessage(
                    "Error: No tax data found for " + stateAbbrInput
                    + ". Unable to create order.");
            return null;
        }

        // GET PRODUCT INFO -- RETURN NULL ORDER IF NO PRODUCT
        displayProductsList(productsList);
        String productTypeInput = getInputProductType();
        // filter productsList to contain only the input productType
        List<Product> filteredProducts = productsList.stream()
                .filter((p) -> p.getProductType()
                .equalsIgnoreCase(productTypeInput))
                .collect(Collectors.toList());

        if (!filteredProducts.isEmpty()) {
            product = filteredProducts.get(0);
        } else {
            displayErrorMessage(
                    "Error: " + productTypeInput + " unavailable. "
                    + "Unable to create order.");
            return null;
        }

        // GET AREA VALUE
        BigDecimal area = getInputAreaValue();

        // SET ORDER FIELDS
        Order order = new Order(orderDate);
        order.setCustomerName(customerName);
        order.setTaxInfo(taxInfo);
        order.setProduct(product);
        order.setArea(area);

        return order; 
    }
    
    /**
     * Returns an Order with user input updated fields, ready to be calculated.
     * 
     * @param oldOrder Order
     * @param productsList
     * @param taxInfoList
     * @return updatedOrder
     */
    public Order getUpdatedOrderInfo(Order oldOrder, List<Product> productsList, 
            List<TaxInfo> taxInfoList) {
        
        displayUpdateOrderBanner();  
        displayOrder(oldOrder);

        LocalDate orderDate = oldOrder.getOrderDate();
        int orderNumber = oldOrder.getOrderNumber();
        Order updatedOrder = new Order(orderDate, orderNumber);

        // UPDATE CUSTOMER NAME
        String customerName = getInputCustomerName();
        if (customerName.trim().isEmpty()) {
            displayErrorMessage("NOTICE: No input found. No update made to "
                    + "customer name.");
            updatedOrder.setCustomerName(oldOrder.getCustomerName());
        } else {
            updatedOrder.setCustomerName(customerName);
        }

        // UPDATE TAX INFO
        String stateAbbrInput = getInputStateAbbr();
        // filter the taxInfoList to contain only that state.
        taxInfoList = taxInfoList.stream()
                .filter((t) -> t.getStateAbbr()
                .equalsIgnoreCase(stateAbbrInput))
                .collect(Collectors.toList());

        if (taxInfoList.isEmpty()) {
            displayErrorMessage("NOTICE: No tax data found from input. " 
                    + "No update made to tax information for Order #" 
                            + orderNumber + ".");
            updatedOrder.setTaxInfo(oldOrder.getTaxInfo());
        } else {
            updatedOrder.setTaxInfo(taxInfoList.get(0));
        }
        
        // UPDATE PRODUCT INFO
        displayProductsList(productsList);
        String productTypeInput = getInputProductType();
        // filter productsList to contain only the input productType
        List<Product> filteredProducts = productsList.stream()
                .filter((p) -> p.getProductType()
                .equalsIgnoreCase(productTypeInput))
                .collect(Collectors.toList());

        if (filteredProducts.isEmpty()) {
            displayErrorMessage("NOTICE: No product by that name was found. "
                        + "No update made to product information for Order #"
                            + orderNumber + ".");
            updatedOrder.setProduct(oldOrder.getProduct());
        } else {
            updatedOrder.setProduct(filteredProducts.get(0));
        }

        // UPDATE AREA 
        // allow blank input
        try {
            BigDecimal area = io.readBigDecimal("Enter area value (sq.ft): ", 
                new BigDecimal("100.00"), new BigDecimal(Integer.MAX_VALUE),
                true);
            updatedOrder.setArea(area);
        } catch (NullPointerException e) {
            displayErrorMessage("NOTICE: No area value input found. "
                        + "No update made to area for Order # "
                            + orderNumber + ".");
            updatedOrder.setArea(oldOrder.getArea());
        }
        
        return updatedOrder;
    }
    
    /**
     * Gets user input order date for creating new orders (future dates) only.
     * @return orderDate
     */
    public LocalDate getInputNewOrderDate() {
        // must be a future date
        LocalDate minDate = LocalDate.now().plusDays(1);        
        LocalDate newOrderDate = io.readLocalDate("Enter new order date: "
                + "(format: MM-dd-yyyy)", minDate, LocalDate.MAX);
        return newOrderDate;
    }
    
    public LocalDate getInputOrderDate() {
        LocalDate inputOrderDate = 
                io.readLocalDate("Enter the order date: (format: MM-dd-yyyy)");
        return inputOrderDate;
    }
    
    public int getInputOrderNumber() {
        int inputOrderNum = io.readInt("Enter the order number:");
        return inputOrderNum;
    }
    
    /**
     * Returns user input customer name from console for new Orders, 
     * checks that input is not empty.
     * 
     * @return String 
     */
    public String getInputNewCustomerName() {
        String customerName = "";
        boolean isInvalidName = true;
        while (isInvalidName) {
            customerName = io.readString(
                    "Enter customer name [A-Z],[0-1],[,.]:");
            customerName = customerName.trim();
            
            if (customerName.isEmpty()) {
                displayErrorMessage("Error: Customer name cannot be empty.");
            } 
            else {
                // check that customerName does not contain any special chars
                int validChars = 0;
                for (int index = 0; index < customerName.length(); index++) {
                    if (Character.isLetterOrDigit(customerName.charAt(index))
                            || customerName.charAt(index) == ','
                            || customerName.charAt(index) == '.'
                            || customerName.charAt(index) == ' ') {
                        validChars++;
                    } else {
                        displayErrorMessage("Error: Customer name cannot"
                                + " contain special characters.");
                        break;
                    }
                }
                if (validChars == customerName.length()) {
                    isInvalidName = false;
                }
            }
        }
        return customerName;
    }
    
    /**
     * Returns user input customer name from console--allows empty string.
     * 
     * @return String
     */
    public String getInputCustomerName() {
        String customerName = "";
        boolean isInvalidName = true;
        while (isInvalidName) {
            customerName = io.readString(
                    "Enter customer name [A-Z],[0-1],[,.]:");
            customerName = customerName.trim();
            
            if (customerName.isEmpty()) {
                isInvalidName = false; // break
            } else {
                // check that customerName does not contain any special chars
                int validChars = 0;
                for (int index = 0; index < customerName.length(); index++) {
                    if (Character.isLetterOrDigit(customerName.charAt(index))
                            || customerName.charAt(index) == ','
                            || customerName.charAt(index) == '.'
                            || customerName.charAt(index) == ' ') {
                        validChars++;
                    } else {
                        displayErrorMessage("Error: Customer name cannot"
                                + " contain special characters.");
                        break;
                    }
                }
                if (validChars == customerName.length()) {
                    isInvalidName = false;
                }
            }
        }
        return customerName;
    }
    
    public String getInputStateAbbr() {             
        return io.readStateAbbreviation("Enter state abbreviation: ");
    }
    
    public String getInputProductType() {    
        return io.readString("Enter product type: ");
    }
    
    /**
     * Returns user input BigDecimal area value, not equal to zero, 
     * and greater than one hundred. No null values.
     * 
     * @return BigDecimal area
     */
    public BigDecimal getInputAreaValue() {
        BigDecimal area = io.readBigDecimal("Enter area value (sq.ft): ", 
                new BigDecimal("100.00"), new BigDecimal(Integer.MAX_VALUE),
                false);       
        return area;
    }

    public void displayProductsList(List<Product> productsList) {
        io.print("=== List of Products ===");
        for (Product currentProduct : productsList) {
            String productInfo = String.format("Product: %s"
                    + "\nCost/SqFt: $%s \nLabor Cost/SqFt: $%s \n",
                    currentProduct.getProductType(),
                    currentProduct.getCostPerSqFoot(),
                    currentProduct.getLaborCostPerSqFoot()
            );
            io.print(productInfo);
        } 
    }
    
    public void displayTaxInfoList(List<TaxInfo> taxInfoList) {
        io.print("=== List of State Tax Rates [State: tax rate(%)] ===");
        for (TaxInfo currentTaxInfo : taxInfoList) {
            String taxData = String.format("%s(%s): %s%", 
                    currentTaxInfo.getStateName(),
                    currentTaxInfo.getStateAbbr(),
                    currentTaxInfo.getTaxRate()
                    );
            io.print(taxData);
        }   
    }

    public void displayOrderActionResult(Order order, String action) {
        String result = String.format("Order #%d: (%s) for %s %s.",
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getCustomerName(),
                action.toUpperCase()
        );
        io.print(result);
    }
    
    /**
     * Prints a message to the console and returns user input response,
     * True for YES and False for No.
     * 
     * @param order
     * @param message
     * @return Boolean 
     */
    public boolean displayOrderSummaryAndGetYesNoResponse(Order order, 
            String message) {
        io.print("=== Order Summary ===");
        displayOrder(order);
        String msgPrompt = message + ": \nEnter Yes(Y) or No(N).";
        return io.readYesNoResponse(msgPrompt);
    }
    
    public String getInputFileName() {
        return io.readString("Enter name of new back up data file: ");
    }
    
    public void displayCreateOrderBanner() {
        io.print("=== CREATE NEW ORDER ===");
    }
    
    public void displayOrdersBanner(LocalDate orderDate) {
        io.print("=== ALL ORDERS FOR " 
                + orderDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                + " ===");
    }
    
    public void displayUpdateOrderBanner() {
        io.print("=== UPDATE ORDER ===");
    }
    
    public void displayDeleteOrderBanner() {
        io.print("=== DELETE ORDER ===");
    }
    
    public void displayExportDataBanner() {
        io.print("=== EXPORT ALL ORDERS TO BACKUP FILE ===");
    }
    
    public void displayActionMessageBanner(String msg) {
        io.print("--- " + msg.toUpperCase() + " ---");
    }
    
    public void displayExitBanner() {
        io.print("--- GOOD BYE ---");
    }
    
    public void displayUnknownCommandBanner() {
        io.print("UNKNOWN COMMAND");
    }
    
    public void displayErrorMessage(String errorMsg) {
        io.print(errorMsg);
    }
    
    public void displayContinuePrompt() {
        io.readString("Please hit ENTER to continue.");
    }
}