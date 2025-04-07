package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.models.WarrantyModel;
import com.adobe.aem.guides.wknd.core.services.WarrantyService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static com.adobe.granite.rest.Constants.CT_JSON;
import static com.adobe.granite.rest.Constants.DEFAULT_CHARSET;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class, property = {
        SLING_SERVLET_PATHS + "=/bin/warranty/set-warranty",
        SLING_SERVLET_EXTENSIONS + "=html",
        SLING_SERVLET_METHODS + "=POST",
})
public class SetWarrantyServlet extends SlingAllMethodsServlet {

    private static final String EXCEPTION = "Exception::";
    private static final String WENT_WRONG = "Something went wrong";
    private static final String ALREADY_EXISTS = "Product Id already exists";
    private static final String INSERTED = "Data Inserted Successfully";
    private final Logger logger = LoggerFactory.getLogger(SetWarrantyServlet.class);

    @Reference
    private transient WarrantyService warrantyService;

    /**
     * doPost method for implementation of inserting data using servlet
     * @param request sent from js
     * @param response sent to js
     * @throws ServletException servletException
     * @throws IOException IOException
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = null;

        try (ResourceResolver resourceResolver = request.getResourceResolver()) {
            writer = response.getWriter();
            response.setContentType(CT_JSON);
            response.setCharacterEncoding(DEFAULT_CHARSET);

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }

            String jsonString = sb.toString();

            Gson gson = new Gson();
            WarrantyModel warrantyModel = gson.fromJson(jsonString, WarrantyModel.class);

            boolean insertData = warrantyService.setWarranty(warrantyModel, resourceResolver);
            if (!insertData) {
                response.setStatus(409);
                writer.write(ALREADY_EXISTS);
            }
            else {
                writer.write(INSERTED);
            }
        }
        catch (IllegalArgumentException e) {
            response.setStatus(400);
            logger.info(EXCEPTION, e.getCause(), e.getMessage());
            if (writer != null) {
                writer.write(e.getMessage());
            }
        }
        catch (RepositoryException | IOException e) {
            logger.info(EXCEPTION, e.getCause(), e.getMessage());
            if (writer != null) {
                writer.write(WENT_WRONG);
            }
        }
    }
}
