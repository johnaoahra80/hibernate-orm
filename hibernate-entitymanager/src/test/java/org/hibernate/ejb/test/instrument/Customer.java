package org.hibernate.ejb.test.instrument;

/**
 * Customer
 *
 * @author John O'Hara
 */
@SuppressWarnings("serial")
public class Customer {

    private int id;

    private String firstName;

    private String lastName;

    private Address address;

    private int version;

    protected Customer() {
    }

    public Customer(String first, String last, Address address) {

        this.firstName = first;
        this.lastName = last;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getVersion() {
        return version;
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