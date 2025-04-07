package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WarrantyModel {

    @ValueMapValue
    private String modelId;

    @ValueMapValue
    private String productId;

    @ValueMapValue
    private String startDate;

    @ValueMapValue
    private String endDate;

    @ValueMapValue
    private int dealerId;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"modelId\" : \"" + modelId + '\"' +
                ", \"productId\" : \"" + productId + '\"' +
                ", \"startDate\" : \"" + startDate + '\"' +
                ", \"endDate\" : \"" + endDate + '\"' +
                ", \"dealerId\" : " + dealerId +
                '}';
    }
}
