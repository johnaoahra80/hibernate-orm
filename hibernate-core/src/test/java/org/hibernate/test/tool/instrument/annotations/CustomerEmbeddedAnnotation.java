package org.hibernate.test.tool.instrument.annotations;

import org.hibernate.test.tool.instrument.Address;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Customer
 *
 * @author John O'Hara
 */
@Entity()
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CustomerEmbeddedAnnotation implements Serializable {

    Long id;
    String name;

    @Embedded
    @AttributeOverrides(
            {@AttributeOverride(name="street1",column=@Column(name="C_STREET1")),
                    @AttributeOverride(name="street2",column=@Column(name="C_STREET2")),
                    @AttributeOverride(name="city",   column=@Column(name="C_CITY")),
                    @AttributeOverride(name="state",  column=@Column(name="C_STATE")),
                    @AttributeOverride(name="country",column=@Column(name="C_COUNTRY")),
                    @AttributeOverride(name="zip",    column=@Column(name="C_ZIP")),
                    @AttributeOverride(name="phone",  column=@Column(name="C_PHONE"))})
    private EmbeddedAddress address;

    protected CustomerEmbeddedAnnotation() {
    }

    public CustomerEmbeddedAnnotation(String name) {

        this.name = name;
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, updatable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmbeddedAddress getAddress() {
        return address;
    }

    public void setAddress(EmbeddedAddress address) {
        this.address = address;
    }
}
