package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.services.SitemapGeneratorService;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/generate-sitemap",
                "sling.servlet.methods=GET"
        }
)
public class SitemapServlet extends SlingAllMethodsServlet {

    @Reference
    private transient SitemapGeneratorService sitemapGeneratorService;

    /**
     * Get Method which calls service to generate sitemap, and save it on (/bin/generate-sitemap).
     * @param request
     * @param response
     * @throws IOException if there is an exception in writing the response
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        String CONTENT_TYPE = "application/xml";
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        String xml = sitemapGeneratorService.generateSitemap(request.getResourceResolver());
        try {
            response.getWriter().write(xml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
