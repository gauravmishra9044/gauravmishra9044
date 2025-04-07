package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.models.WarrantyModel;
import com.adobe.aem.guides.wknd.core.services.WarrantyService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.adobe.granite.rest.Constants.CT_JSON;
import static com.adobe.granite.rest.Constants.DEFAULT_CHARSET;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class, property = {
        SLING_SERVLET_PATHS + "=/bin/warranty/get-warranty",
        SLING_SERVLET_EXTENSIONS + "=html",
        SLING_SERVLET_METHODS + "=GET",
})

public class GetWarrantyServlet extends SlingSafeMethodsServlet {
    private static final String EXCEPTION = "Exception::";
    private static final String WENT_WRONG = "Something went wrong";
    private static final String DATA_NOT_FOUND = "Data not found";
    private final Logger logger = LoggerFactory.getLogger(GetWarrantyServlet.class);

    @Reference
    private transient WarrantyService warrantyService;

    /**
     * doGet method for implementation of search servlet
     * @param request sent from js
     * @param response sent to js
     * @throws ServletException servletException
     * @throws IOException IOException
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = null;

        try (ResourceResolver resourceResolver = request.getResourceResolver()) {
            writer = response.getWriter();
            response.setContentType(CT_JSON);
            response.setCharacterEncoding(DEFAULT_CHARSET);

            String type = request.getParameter("type");
            String value = request.getParameter("value").toUpperCase();

            List<WarrantyModel> warrantyDetails = warrantyService.getWarranty(type, value, resourceResolver);

            if (warrantyDetails.isEmpty()) {
                response.setStatus(404);
                response.getWriter().write(DATA_NOT_FOUND);
            }
            else {
                response.getWriter().write(warrantyDetails.toString());
            }

        } catch (RepositoryException e) {
            logger.info(EXCEPTION, e.getCause(), e.getMessage());
            if (writer != null) {
                writer.write(WENT_WRONG);
            }
        }
    }
}