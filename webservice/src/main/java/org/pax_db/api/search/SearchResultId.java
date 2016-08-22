package org.pax_db.api.search;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * A single hit for one query item.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "protein")
public class SearchResultId implements Serializable {

    @XmlAttribute(name = "id", required = true)
    protected String proteinId;

    @XmlElement(name = "highlighted")
    protected String highlighted;

    /**
     * GWT requires a no-arg constructor
     */
    public SearchResultId() {

    }

    public SearchResultId(String proteinId, String highlighted) {
        this.proteinId = proteinId;
        this.highlighted = highlighted;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public String getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(String highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return "SearchResultId [protein=" + proteinId + "]";
    }

    private static final long serialVersionUID = 2319354946704708765L;

}
