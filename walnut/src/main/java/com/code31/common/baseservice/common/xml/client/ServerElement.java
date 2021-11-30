package com.code31.common.baseservice.common.xml.client;

/**
 * Date: 11-10-20
 * Time: 下午4:28
 */


import com.code31.common.baseservice.common.xml.common.XmlPropertyElement;
import com.google.common.collect.Maps;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "server")
public class ServerElement {

	@XmlAttribute(name="name",required = true)
	private String name = "";
    @XmlJavaTypeAdapter(XmlPropertyElement.MapAdapter.class)
    private Map<String, String> properties = Maps.newHashMap();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
