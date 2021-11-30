package com.code31.common.baseservice.db.orm;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public abstract class AssignIdEntity implements IEntity<Long> {
    private Long id;

    @Id
    @Column(name = "id",columnDefinition="NOT NULL")
    @Override
    public final Long getId() {
    	if (id == null)
    		return 0L;
        return id;
    }

    @Override
    public final void setId(@Nonnegative Long id) {
        Preconditions.checkArgument(id >= 0, "id");
 
        this.id = id;
    }
    

    @Override
    public String[] getSubKeys() {
    	return null;
    }
    
    @Override
    public String getMainKey(){
    	return this.getClass().getSimpleName() + "_id_" + "_%s";
    }
}
