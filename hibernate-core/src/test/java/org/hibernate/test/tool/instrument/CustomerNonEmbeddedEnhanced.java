package org.hibernate.test.tool.instrument;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Customer
 *
 * @author John O'Hara
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "O_CUSTOMER")
public class CustomerNonEmbeddedEnhanced implements Serializable {

    @Id
    @Column(name = "C_ID")
    private int id;

    @Column(name = "C_FIRST")
    private String firstName;

    @Column(name = "C_LAST")
    private String lastName;

    @Column(name = "C_STREET1")
    private String street1;

    @Column(name = "C_STREET2")
    private String street2;

    @Column(name = "C_CITY")
    private String city;

    @Column(name = "C_STATE")
    private String state;

    @Column(name = "C_COUNTRY")
    private String country;

    @Column(name = "C_ZIP")
    private String zip;

    @Column(name = "C_PHONE")
    private String phone;

    @Version
    @Column(name = "C_VERSION")
    private int version;

    protected CustomerNonEmbeddedEnhanced() {
    }

    public CustomerNonEmbeddedEnhanced(String first, String last, Address address) {

        this.firstName = first;
        this.lastName = last;
        this.street2 = address.getStreet2();
        this.city = address.getCity();
        this.state = address.getState();
        this.country = address.getCountry();
        this.zip = address.getZip();
        this.phone = address.getPhone();

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

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        return id == ((CustomerNonEmbeddedEnhanced) o).id;
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
