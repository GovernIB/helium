package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "hel_acl_class")
public class AclClass implements Serializable {

    private static final long serialVersionUID = -1923036158097057162L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "class", nullable = false, unique = true, length = 255)
    private String aclClass;

    public String getAclClass() {
        return aclClass;
    }

    public void setAclClass(String aclClass) {
        this.aclClass = aclClass;
    }
}