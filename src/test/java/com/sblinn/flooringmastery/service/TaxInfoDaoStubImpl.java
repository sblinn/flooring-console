
package com.sblinn.flooringmastery.service;

import com.sblinn.flooringmastery.dao.PersistenceException;
import com.sblinn.flooringmastery.dao.TaxInfoDao;
import com.sblinn.flooringmastery.dto.TaxInfo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sarablinn
 */
public class TaxInfoDaoStubImpl implements TaxInfoDao {

    public TaxInfo onlyTax;
    
    
    /**
     * Creates a hard-coded TaxInfo for the stub.
     */
    public TaxInfoDaoStubImpl() {
//        this.onlyTax = new TaxInfo("TX", "Texas", new BigDecimal("4.45"));
        onlyTax = new TaxInfo("CA", "California", new BigDecimal("25.00"));
    }
    
    /**
     * Uses a test TaxInfo that is injected by another class. 
     * 
     * @param testTaxInfo 
     */
    public TaxInfoDaoStubImpl(TaxInfo testTaxInfo) {
        this.onlyTax = testTaxInfo;
    }
    
    
    @Override
    public TaxInfo createTaxInfo(String stateAbbr, TaxInfo taxInfo) throws 
            PersistenceException {
        
        if(stateAbbr.equals(onlyTax.getStateAbbr())) {
            return onlyTax;
        } else {
            return null;
        }
    }

    @Override
    public TaxInfo getTaxInfo(String stateAbbr) throws PersistenceException {
        if (stateAbbr.equals(onlyTax.getStateAbbr())) {
            return onlyTax;
        } else {
            return null;
        }
    }

    @Override
    public List<TaxInfo> getAllTaxInfo() throws PersistenceException {
        List<TaxInfo> taxes = new ArrayList<>();
        taxes.add(onlyTax);
        return taxes;
    }

    @Override
    public TaxInfo updateTaxInfo(String stateAbbr, TaxInfo updatedTaxInfo) throws PersistenceException {
        if(stateAbbr.equals(onlyTax.getStateAbbr())) {
            return onlyTax;
        } else {
            return null;
        }
    }

    @Override
    public TaxInfo deleteTaxInfo(String stateAbbr) throws PersistenceException {
        if(stateAbbr.equals(onlyTax.getStateAbbr())) {
            return onlyTax;
        } else {
            return null;
        }
    }
    
}
