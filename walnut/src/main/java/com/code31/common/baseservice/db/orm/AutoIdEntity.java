package com.code31.common.baseservice.db.orm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnegative;
import javax.persistence.*;


@Entity
public abstract class AutoIdEntity implements IEntity<Long> {
    protected Long id;

    //
    @Id
    @Column(name = "id",columnDefinition="NOT NULL AUTO_INCREMENT")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Override
    public  Long getId() {
        return id;
    }

    @Override
    public void setId(@Nonnegative Long id) {
   //     Preconditions.checkArgument(id > 0, "id");
        this.id = id;
    }
    @JsonIgnore
    @Override
    public String[] getSubKeys() {
    	return null;
    }

    @JsonIgnore
    @Override
	public String getMainKey() {
		return this.getClass().getSimpleName() + "_auto_" + "_%s";
	}
}
