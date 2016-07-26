package org.hibernate.jpa.test.instrument.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CustomerInventoryPK implements Serializable {
    private Long id;
    private int custId;

    public CustomerInventoryPK() {
    }

    public CustomerInventoryPK(Long id, int custId) {
        this.id = id;
        this.custId = custId;  
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        CustomerInventoryPK cip = (CustomerInventoryPK) other;
        return (custId == cip.custId && (id == cip.id || 
                        ( id != null && id.equals(cip.id))));
    }

    public int hashCode() {
        return (id == null ? 0 : id.hashCode()) ^ custId;
    }

    public Long getId() {
        return id;
    }

    public int getCustId() {
        return custId;
    }
}
