
package org.hibernate.jpa.test.instrument.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
@Entity
@Table(name="O_CUSTINVENTORY")
@IdClass(CustomerInventoryPK.class)
public class CustomerInventory implements Serializable, 
    Comparator<CustomerInventory> {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="CI_ID")
    private Long         id;

    @Id
    @Column(name = "CI_CUSTOMERID", insertable = false, updatable = false)
    private int             custId;
    
    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name="CI_CUSTOMERID")
    private Customer        customer;
    
    @Column(name="CI_VALUE")
    private BigDecimal      totalCost;

    @Column(name="CI_QUANTITY")
    private int             quantity;
      

    protected CustomerInventory() {
    }

    CustomerInventory(Customer customer, int quantity,
            BigDecimal totalValue) {
        this.customer = customer;
        this.quantity = quantity;
        this.totalCost = totalValue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }
    
    public int getCustId() {
    	return custId;
    }

    public int compare(CustomerInventory cdb1, CustomerInventory cdb2) {
        return cdb1.id.compareTo(cdb2.id);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof CustomerInventory)) 
            return false;
        if (this.id == ((CustomerInventory)obj).id)
          return true;
        if (this.id != null && ((CustomerInventory)obj).id == null)
          return false;
        if (this.id == null && ((CustomerInventory)obj).id != null)
          return false;
        
        return this.id.equals(((CustomerInventory)obj).id);
    }
}
