package app.tomcat;


import app.filters.logging.TransactionFilter;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

/**
 * Class maintains all necessary functionality to register dispatcher servlet to embedded tomcat and startup the
 * web application
 *
 */
public class TomcatInitializer {

    private static int PORT = 8080;

    public static Tomcat configureTomcat(AnnotationConfigWebApplicationContext springContext) {

        Tomcat tomcat = new Tomcat();

        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext("", docBase);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);

        tomcat.setPort(PORT);

        tomcat.getConnector();

        Wrapper wrapper = Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);

        wrapper.setLoadOnStartup(1);

        context.addServletMappingDecoded("/", "dispatcherServlet");

        TransactionFilter transactionFilter = new TransactionFilter();

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("transactionFilter");
        filterDef.setFilter(transactionFilter);
        context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("transactionFilter");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);

        return tomcat;
    }

}
