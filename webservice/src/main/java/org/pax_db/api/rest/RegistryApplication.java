package org.pax_db.api.rest;

import org.pax_db.api.rest.search.SearchResource;
import org.pax_db.api.search.SearchResponse;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Lists classes and objects that JAX-RS is supposed to deploy.
 *
 * @author milans
 */
public class RegistryApplication extends Application {

    // private final Set<Class<?>> services;
    private final Set<Object> services = new HashSet();

    public RegistryApplication() {
        if (services.isEmpty()) {
            services.add(new SearchResourceVersionDispatcher());
        }
    }

    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }

    @Override
    public Set<Object> getSingletons() {
        return services;
    }

    public static void main(String[] args) {
        //DEMO
        SearchResourceVersionDispatcher s = new SearchResourceVersionDispatcher();
        SearchResource resource = (SearchResource) s.getResourceVersion("application/xml");
        SearchResponse res = (SearchResponse) resource.search("cdc", null, 1, 10);
        System.out.println(res.getResults().get(0).getHits());
    }
}