
package com.sblinn.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author sarablinn
 */
public class TaxInfo {
    
    private String stateAbbr;
    private String stateName;
    private BigDecimal taxRate;
    
    
    /**
     * Default Constructor.
     */
    public TaxInfo() {
        
    }
    
    /**
     * Constructor which sets stateName if the input stateAbbr is a U.S. state.
     * 
     * @param stateAbbreviation 
     */
    public TaxInfo(String stateAbbreviation) {
        this.stateAbbr = stateAbbreviation;
        
        String[] stateNames = {"Alabama", "Alaska", "Arizona", "Arkansas", 
            "California", "Colorado", "Connecticut", "Delaware", "Florida", 
            "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", 
            "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", 
            "Massachusetts", "Michigan", "Minnesota", "Mississippi", 
            "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", 
            "New Jersey", "New Mexico", "New York", "North Carolina", 
            "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", 
            "Rhode Island", "South Carolina", "South Dakota", "Tennessee", 
            "Texas", "Utah", "Vermont", "Virginia", "Washington", 
            "West Virginia", "Wisconsin", "Wyoming"};
        
        String[] stateAbbrs = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", 
            "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", 
            "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", 
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", 
            "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
        
        List<String> stateAbbrsList = Arrays.asList(stateAbbrs);
        if (stateAbbrsList.contains(stateAbbreviation)) {
            this.stateName = 
                    stateNames[stateAbbrsList.indexOf(stateAbbreviation)];
        }
    }
    
    public TaxInfo(String stateAbbreviation, String stateName, 
            BigDecimal taxRate) {
        
        this.stateAbbr = stateAbbreviation.toUpperCase();
        this.stateName = stateName;
        this.taxRate = taxRate; 
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbreviation) {
        this.stateAbbr = stateAbbreviation.toUpperCase();
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.stateAbbr);
        hash = 29 * hash + Objects.hashCode(this.stateName);
        hash = 29 * hash + Objects.hashCode(this.taxRate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaxInfo other = (TaxInfo) obj;
        if (!Objects.equals(this.stateAbbr, other.stateAbbr)) {
            return false;
        }
        if (!Objects.equals(this.stateName, other.stateName)) {
            return false;
        }
        if (!Objects.equals(this.taxRate, other.taxRate)) {
            return false;
        }
        return true;
    }
    
}
