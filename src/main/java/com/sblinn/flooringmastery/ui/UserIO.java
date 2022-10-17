
package com.sblinn.flooringmastery.ui;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author sarablinn
 */
public interface UserIO {
    
    void print(String msg);
    
    String readString(String msgPrompt);
    
    int readInt(String msgPrompt);
    
    int readInt(String msgPrompt, int min, int max);
    
    BigDecimal readBigDecimal(String msgPrompt, boolean allowNull);
    
    BigDecimal readBigDecimal(String msgPrompt, BigDecimal min, BigDecimal max, 
            boolean allowNull);
    
    LocalDate readLocalDate(String msgPrompt);
    
    LocalDate readLocalDate(String msgPrompt, LocalDate minDate, 
            LocalDate maxDate);
    
    String readStateAbbreviation(String msgPrompt);
  
    boolean readYesNoResponse(String msgPrompt);
    
}
