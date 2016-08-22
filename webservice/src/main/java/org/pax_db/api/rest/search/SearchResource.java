package org.pax_db.api.rest.search;

import org.pax_db.api.rest.Versioned;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * jax-rs annotated free text search service. Implementations are supposed
 * to be versioned.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface SearchResource extends Versioned {

    @GET
    @Produces({"application/vnd.paxdb.search+json", "application/vnd.paxdb.search+xml",
            MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    Object search(@QueryParam("q") String q,
                  @QueryParam("species") Long species,
                  @DefaultValue("1") @QueryParam("page") Integer page,
                  @DefaultValue("10") @QueryParam("pageSize") Integer pageSize);
}
