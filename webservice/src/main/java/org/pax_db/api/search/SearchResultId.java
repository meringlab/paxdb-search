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

    @XmlAttribute(name = "extId")
    protected String proteinExtId;

    @XmlAttribute(name = "name")
    protected String proteinName;

    @XmlAttribute(name = "speciesId")
    private String speciesId;

    @XmlElement(name = "annotation")
    protected String proteinAnnotation;

    @XmlElement(name = "highlighted")
    protected String highlighted;


    /**
     * GWT requires a no-arg constructor
     */
    public SearchResultId() {

    }

    public SearchResultId(String proteinId, String proteinExtId, String proteinName, String annotation, String highlighted) {
        this.proteinId = proteinId;
        this.proteinExtId = proteinExtId;
        this.proteinName = proteinName;
        this.proteinAnnotation = annotation;
        this.highlighted = highlighted;
    }

    public SearchResultId(String proteinId, String proteinExtId, String proteinName, String proteinAnnotation, String highlighted, String speciesId) {
        this.proteinId = proteinId;
        this.proteinExtId = proteinExtId;
        this.proteinName = proteinName;
        this.proteinAnnotation = proteinAnnotation;
        this.highlighted = highlighted;
        this.speciesId = speciesId;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public String getProteinExtId() {
        return proteinExtId;
    }

    public void setProteinExtId(String proteinExtId) {
        this.proteinExtId = proteinExtId;
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public String getProteinAnnotation() {
        return proteinAnnotation;
    }

    public void setProteinAnnotation(String proteinAnnotation) {
        this.proteinAnnotation = proteinAnnotation;
    }

    public String getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(String highlighted) {
        this.highlighted = highlighted;
    }

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    @Override
    public String toString() {
        return "SearchResultId [protein=" + proteinId + "]";
    }

    private static final long serialVersionUID = 2319354946704708765L;

}
