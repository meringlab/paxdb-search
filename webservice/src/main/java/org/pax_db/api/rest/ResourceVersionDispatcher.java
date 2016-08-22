package org.pax_db.api.rest;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public abstract class ResourceVersionDispatcher {
    public static final String ARCHIVES_V2 = "http://archive.pax-db.org/v2/api";

    protected final Map<Integer, Versioned> resourceMap = new HashMap();
    public final Integer LATEST;
    protected final AcceptHeaderParser versionParser;

    public ResourceVersionDispatcher(Integer latest) {
        LATEST = latest;
        versionParser = new AcceptHeaderParser(LATEST);
    }

    @Path("/")
    public Versioned getResourceVersion(@HeaderParam("Accept") String contentType) {
        Integer version = versionParser.parse(contentType);
        if (resourceMap.containsKey(version)) {
            return resourceMap.get(version);
        }
        return resourceMap.get(LATEST);
    }
}
