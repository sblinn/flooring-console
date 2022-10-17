
package com.sblinn.flooringmastery.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author sarablinn
 */
public class Product {
    
    private String productType;
    private BigDecimal costPerSqFoot;
    private BigDecimal laborCostPerSqFoot;
    
    public Product() {
        
    }
    
    public Product(String productType, BigDecimal costPerSqFoot, 
            BigDecimal laborCostPerSqFoot) {
        
        this.productType = productType;
        this.costPerSqFoot = costPerSqFoot;
        this.laborCostPerSqFoot = laborCostPerSqFoot;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getCostPerSqFoot() {
        return costPerSqFoot;
    }

    public void setCostPerSqFoot(BigDecimal costPerSqFoot) {
        this.costPerSqFoot = costPerSqFoot;
    }

    public BigDecimal getLaborCostPerSqFoot() {
        return laborCostPerSqFoot;
    }

    public void setLaborCostPerSqFoot(BigDecimal laborCostPerSqFoot) {
        this.laborCostPerSqFoot = laborCostPerSqFoot;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.productType);
        hash = 31 * hash + Objects.hashCode(this.costPerSqFoot);
        hash = 31 * hash + Objects.hashCode(this.laborCostPerSqFoot);
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
        final Product other = (Product) obj;
        if (!Objects.equals(this.productType, other.productType)) {
            return false;
        }
        if (!Objects.equals(this.costPerSqFoot, other.costPerSqFoot)) {
            return false;
        }
        if (!Objects.equals(this.laborCostPerSqFoot, other.laborCostPerSqFoot)) {
            return false;
        }
        return true;
    }
 
    
}
