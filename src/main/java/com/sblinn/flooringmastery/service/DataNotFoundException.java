
package com.sblinn.flooringmastery.service;

/**
 *
 * @author sarablinn
 */
public class DataNotFoundException extends Exception {
 
    public DataNotFoundException(String message) {
        super(message);
    }
    
    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
