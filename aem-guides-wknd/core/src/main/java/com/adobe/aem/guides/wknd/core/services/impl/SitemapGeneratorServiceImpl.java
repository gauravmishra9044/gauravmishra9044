package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.SitemapGeneratorService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component(service = SitemapGeneratorService.class)
public class SitemapGeneratorServiceImpl implements SitemapGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(SitemapGeneratorServiceImpl.class);

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String SERVICE_USER = "sitemap-service-user";
    private static final String PAGE_NOT_FOUND="<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Root page not found</error>";
    private static final String localHostLink="http://localhost:4502";
    private static final String LOGIN_FAIL= "Failed to login with system user:";
    private static final String SITEMAP_GENERATED="sitemap is generated";
    private static final String SITEMAP_NAME="sitemap.xml";
    private static final String SITEMAP_PATH="/var/sitemaps";
    private static final String PAGE_PATH="/content/wknd";

    /**
     * Function to generate a xml file, and save it locally
     * @param incomingResolver Resource resolver object
     * @return string of xml file
     */
    @Override
    public String generateSitemap(ResourceResolver incomingResolver) {
        StringBuilder sitemapXml = new StringBuilder();
        try {
            PageManager pageManager = incomingResolver.adaptTo(PageManager.class);
            Page rootPage = pageManager.getPage(PAGE_PATH);

            if (rootPage == null) {
                log.info("root is empty");
                return PAGE_NOT_FOUND;
            }

            List<Page> pages = new ArrayList<>();
            collectPages(rootPage, pages);

            sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

            for (Page page : pages) {
                sitemapXml.append("  <url><loc>")
                        .append(localHostLink)
                        .append(page.getPath())
                        .append(".html</loc></url>\n");
            }

            sitemapXml.append("</urlset>");

            log.info(SITEMAP_GENERATED);
            log.info(sitemapXml.toString());
            saveSitemapToVar(sitemapXml.toString());

        } catch (Exception e) {
            sitemapXml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>")
                    .append(e.getMessage()).append("</error>");
        }
        return sitemapXml.toString();
    }

    /**
     * Recursive function to get all pages in list of page, and update it
     * @param page Page object with the current page resource
     * @param pages List of page with stores page and its children
     */
    private void collectPages(Page page, List<Page> pages) {
        ValueMap properties = page.getProperties();

        boolean excludeThisPage = Boolean.parseBoolean(properties.get("excludeThisPage", "false"));
        boolean excludeChildren = Boolean.parseBoolean(properties.get("excludeChildren", "false"));

        if (!excludeThisPage) {
            pages.add(page);
        }

        if (!excludeChildren) {
            Iterator<Page> children = page.listChildren();
            while (children.hasNext()) {
                collectPages(children.next(), pages);
            }
        }
    }

    /**
     * Saves the xml file in given repo with the help of system user
     * @param xmlContent Stringify version of xml file which stores all pages link
     */
    private void saveSitemapToVar(String xmlContent) {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authInfo)) {
            Resource varFolder = resolver.getResource(SITEMAP_PATH);
            if (varFolder == null) {
                Resource parent = resolver.getResource("/var");
                if (parent != null) {
                    Map<String, Object> folderProps = new HashMap<>();
                    folderProps.put("jcr:primaryType", "nt:folder");
                    varFolder = resolver.create(parent, "sitemaps", folderProps);
                }
            }

            Resource fileResource = varFolder.getChild(SITEMAP_NAME);
            if (fileResource == null) {
                Map<String, Object> fileProps = new HashMap<>();
                fileProps.put("jcr:primaryType", "nt:file");
                fileResource = resolver.create(varFolder, SITEMAP_NAME, fileProps);
            }

            Resource contentNode = fileResource.getChild("jcr:content");
            Map<String, Object> contentProps = new HashMap<>();
            contentProps.put("jcr:primaryType", "nt:resource");
            contentProps.put("jcr:mimeType", "application/xml");
            contentProps.put("jcr:data", new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
            contentProps.put("jcr:lastModified", Calendar.getInstance());

            if (contentNode != null) {
                ModifiableValueMap mvm = contentNode.adaptTo(ModifiableValueMap.class);
                if (mvm != null) {
                    mvm.put("jcr:data", contentProps.get("jcr:data"));
                    mvm.put("jcr:mimeType", contentProps.get("jcr:mimeType"));
                    mvm.put("jcr:lastModified", contentProps.get("jcr:lastModified"));
                }
            } else {
                resolver.create(fileResource, "jcr:content", contentProps);
            }
            resolver.commit();
        } catch (LoginException e) {
            log.info(LOGIN_FAIL + "{}", e.getMessage());
        } catch (PersistenceException e) {
            log.info(e.getMessage());
        }
    }
}
