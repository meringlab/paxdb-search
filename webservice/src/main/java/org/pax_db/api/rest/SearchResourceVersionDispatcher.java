package org.pax_db.api.rest;

import org.apache.log4j.Logger;
import org.pax_db.api.rest.search.SearchResource;
import org.pax_db.api.rest.search.SearchResourceRepository;
import org.string_db.analysis.IndexConfig;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Search-subresource locator, handles versioning issues.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@Path("/search")
public class SearchResourceVersionDispatcher extends ResourceVersionDispatcher {
    static final Logger log = Logger.getLogger(SearchResourceVersionDispatcher.class);

    public SearchResourceVersionDispatcher() {
        super(3);
        resourceMap.put(3, new SearchResourceRepository(IndexConfig.getConfig().getSearcher()));

        resourceMap.put(2, new SearchResource() {
            @Override
            public Response search(@QueryParam("q") String q, @QueryParam("species") Long species, @DefaultValue("1") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("pageSize") Integer pageSize) {
                Response.ResponseBuilder response = Response.status(Response.Status.MOVED_PERMANENTLY);
                response.location(URI.create(ARCHIVES_V2 + "/species/" + species));
                return response.build();
            }

            @Override
            public Integer getVersion() {
                return 2;
            }
        });


        resourceMap.put(1, new SearchResource() {
            @Override
            public Response search(@QueryParam("q") String q, @QueryParam("species") Long species, @DefaultValue("1") @QueryParam("page") Integer page, @DefaultValue("10") @QueryParam("pageSize") Integer pageSize) {
                Response.ResponseBuilder response = Response.status(Response.Status.MOVED_PERMANENTLY);
                response.location(URI.create(ARCHIVES_V2 + "/species/" + species));
                return response.build();
            }

            @Override
            public Integer getVersion() {
                return 1;
            }
        });
    }

}
