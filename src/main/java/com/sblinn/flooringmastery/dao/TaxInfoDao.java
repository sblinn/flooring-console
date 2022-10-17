
package com.sblinn.flooringmastery.dao;

import com.sblinn.flooringmastery.dto.TaxInfo;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public interface TaxInfoDao {
    
    TaxInfo createTaxInfo(String stateAbbr, TaxInfo taxInfo) 
            throws PersistenceException;
    
    TaxInfo getTaxInfo(String stateAbbr) throws PersistenceException;
    
    List<TaxInfo> getAllTaxInfo() throws PersistenceException;
    
    TaxInfo updateTaxInfo(String stateAbbr, TaxInfo updatedTaxInfo)
            throws PersistenceException;
    
    TaxInfo deleteTaxInfo(String stateAbbr) throws PersistenceException;
    
}
