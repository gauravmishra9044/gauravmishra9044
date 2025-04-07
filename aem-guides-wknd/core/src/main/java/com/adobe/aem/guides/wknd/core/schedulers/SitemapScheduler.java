package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.services.SitemapGeneratorService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class,
        immediate = true,
        property = {
                "scheduler.name=SiteMapScheduler",
                "scheduler.period:Long=12"
        })
public class SitemapScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SitemapScheduler.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SitemapGeneratorService sitemapGeneratorService;

    /**
     * Activation function of scheduler
     */
    @Activate
    protected void activate() {
        log.info("[Scheduler Registered] with PERIOD: 12 seconds");
    }

    /**
     * Run method to generate xml file after very 12 seconds
     */
    @Override
    public void run() {
        log.info("SimpleHardcodedScheduler RUN method executed");
        Map<String, Object> authMap = new HashMap<>();
        authMap.put(ResourceResolverFactory.SUBSERVICE, "sitemap-service-user");

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authMap)) {
            sitemapGeneratorService.generateSitemap(resolver);
            log.info("Sitemap successfully generated and saved.");
        }
        catch (LoginException e) {
        log.error("LoginException: Failed to obtain service resource resolver: {}", e.getMessage(), e);
    } catch (RuntimeException e) {
        log.error("RuntimeException during sitemap generation: {}", e.getMessage(), e);
    }
    }
}