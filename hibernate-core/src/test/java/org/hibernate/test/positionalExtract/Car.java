package org.hibernate.test.positionalExtract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/29/12
 * Time: 8:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "car")
public class Car {

    @Id
    private int id;

    @Column(name = "make")
    private String make;
    @Column(name = "model")
    private String model;
    @Column(name = "capacity")
    private int engineCapacity;


    public Car() {

    }

    public Car(String make, String model, int capacity) {
        this.make = make;
        this.model = model;
        this.engineCapacity = capacity;

    }


    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(int engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
