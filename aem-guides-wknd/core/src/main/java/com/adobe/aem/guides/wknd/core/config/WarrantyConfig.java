package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(name = "Warranty Config" , description = "Warranty Details Config")
public @interface WarrantyConfig {

    @AttributeDefinition(name = "Warranty Data Path" , description = "Path of nodes having Warranty data")
    String parentNodePath();
}

