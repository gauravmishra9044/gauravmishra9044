package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.models.WarrantyModel;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import java.util.List;

public interface WarrantyService {
    public boolean setWarranty(WarrantyModel warrantyModel, ResourceResolver resourceResolver) throws RepositoryException;
    List<WarrantyModel> getWarranty(String type, String value, ResourceResolver resourceResolver) throws RepositoryException;
}
