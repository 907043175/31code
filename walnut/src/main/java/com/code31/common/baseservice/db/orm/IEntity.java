package com.code31.common.baseservice.db.orm;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nonnegative;
import java.io.Serializable;

/**
 */
public interface IEntity<T extends Serializable> extends Serializable {

    T getId();

    void setId(@Nonnegative T id);

    @JsonIgnore
    String[] getSubKeys();

    @JsonIgnore
    String getMainKey();

}
