package org.hibernate.jpa.test.instrument.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="O_CUSTOMER")
public class Customer implements Serializable {

    @Id
    @Column(name="C_ID")
    private int id;
    
    @Column(name="C_FIRST")
    private String firstName;
    
    @Column(name="C_LAST")
    private String lastName;

    @OneToMany(mappedBy="customer", fetch = FetchType.LAZY)
    private List<CustomerInventory> customerInventories;
    
    protected Customer() {
    }

    public Customer(String first, String last) {

        this.firstName   = first;
        this.lastName    = last;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer customerId) {
        this.id = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<CustomerInventory> getInventories() {
      if (customerInventories == null){
        customerInventories = new ArrayList<CustomerInventory>();
      }
      return customerInventories;
    }

    public CustomerInventory addInventory(int quantity,
            BigDecimal totalValue) {
     
      CustomerInventory inventory = new CustomerInventory(this,
              quantity, totalValue);
      getInventories().add(inventory);
      return inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        return id == ((Customer) o).id;
    }

    @Override
    public int hashCode() {
        return new Integer(id).hashCode();
    }

    @Override
    public String toString() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
