package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.config.WarrantyConfig;
import com.adobe.aem.guides.wknd.core.models.WarrantyModel;
import com.adobe.aem.guides.wknd.core.services.WarrantyService;
import com.adobe.aem.guides.wknd.core.utils.Warrantyutil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

@Component(service = WarrantyService.class, immediate = true)
@Designate(ocd = WarrantyConfig.class)
public class WarrantyImpl implements WarrantyService {

    private static final String MODEL_ID = "modelId";
    private static final String PRODUCT_ID = "productId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String DEALER_ID = "dealerId";

    private String parentNodePath;

    @Activate
    @Modified
    public void activate(WarrantyConfig warrantyConfig) {
        parentNodePath = warrantyConfig.parentNodePath();
    }

    /**
     * setWarranty method to create a node and store the data
     * @param warrantyModel object of warrantyModel to insert store as a node
     * @param resourceResolver resourceResolver
     * @return data is inserted successfully or not
     * @throws RepositoryException repositoryException
     */
    @Override
    public boolean setWarranty(WarrantyModel warrantyModel, ResourceResolver resourceResolver) throws RepositoryException, IllegalArgumentException {

        Session session = null;
        try {
            session = resourceResolver.adaptTo(Session.class);
            Node parentNode = null;

            if (session != null) {
                parentNode = session.getNode(parentNodePath);
            }

            String productId = warrantyModel.getProductId().toUpperCase();

            Warrantyutil.validation(warrantyModel);

            if (parentNode != null && parentNode.hasNode(productId)) {
                return false;
            }

            if (parentNode != null) {
                Node newNode = parentNode.addNode(productId, NT_UNSTRUCTURED);
                newNode.setProperty(MODEL_ID, warrantyModel.getModelId().toUpperCase());
                newNode.setProperty(PRODUCT_ID, productId);
                newNode.setProperty(START_DATE, warrantyModel.getStartDate());
                newNode.setProperty(END_DATE, warrantyModel.getEndDate());
                newNode.setProperty(DEALER_ID, warrantyModel.getDealerId());
                session.save();
            }
            return true;
        }
        catch (IllegalArgumentException | ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
    }

    /**
     * getWarrantyChecker function get the data from the nodes based on the type and values searched
     * @param type the type of value on which search is being performed
     * @param value the value to be searched
     * @param resourceResolver resourceResolver
     * @return list of warrantyModel containing the searched values
     * @throws RepositoryException repositoryException
     */
    @Override
    public List<WarrantyModel> getWarranty(String type, String value, ResourceResolver resourceResolver) throws RepositoryException {
        List<WarrantyModel> warrantyData = new ArrayList<>();

        Resource parentResource = resourceResolver.getResource(parentNodePath);

        if (parentResource != null) {
            for (Resource childResource : parentResource.getChildren()) {

                ValueMap properties = childResource.getValueMap();
                String propertyContainsType = properties.get(type, String.class);

                if (propertyContainsType!= null && propertyContainsType.contains(value)) {
                    WarrantyModel warrantyModel = childResource.adaptTo(WarrantyModel.class);
                    if (warrantyModel != null) {
                        warrantyData.add(warrantyModel);
                    }
                }
            }
        }
        return warrantyData;
    }
}
