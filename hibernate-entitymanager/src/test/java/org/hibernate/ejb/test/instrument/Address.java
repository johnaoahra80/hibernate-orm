package org.hibernate.ejb.test.instrument;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 6/13/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Address{

    private String street1;
    private String city;

    public Address() {
    }
    public Address(String street1, String city) {
        this.street1 = street1;
        this.city    = city;
    }

    public String toString() {
        return street1 + "\n" + city;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
