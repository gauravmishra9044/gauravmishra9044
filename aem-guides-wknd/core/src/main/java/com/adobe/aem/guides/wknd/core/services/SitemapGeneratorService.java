package com.adobe.aem.guides.wknd.core.services;

import org.apache.sling.api.resource.ResourceResolver;


public interface SitemapGeneratorService {
    /**
     * Interface Method declared, used in SitemapGeneratorServiceImpl.java
     * @param resourceResolver Resource resolver object
     * @return String of xml file
     */
    String generateSitemap(ResourceResolver resourceResolver);
}