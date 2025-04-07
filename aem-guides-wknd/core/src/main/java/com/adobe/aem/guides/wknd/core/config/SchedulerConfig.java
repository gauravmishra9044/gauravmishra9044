package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Simple Sitemap Scheduler Config", description = "Scheduler that triggers sitemap generation based on configuration")
public @interface SchedulerConfig {

    @AttributeDefinition(name = "Enable Scheduler", description = "Enable or disable the sitemap scheduler")
    boolean enabled() default true;

    @AttributeDefinition(name = "Scheduler Expression (cron)", description = "Cron expression to define the scheduler frequency")
    String cron_expression() default "*/12 * * * * ?";  // Every 12 seconds

    @AttributeDefinition(name = "Subservice Name", description = "Service User name used to access resources")
    String subservice_name() default "sitemap-service-user";
}
