package org.pax_db.api.shared;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proteinInfo",propOrder={"id", "species", "abundance"})
public class ResultId implements Serializable {

	protected String id;

	protected String species;
    //TODO remove, used only for dataset descriptions on protein load
    @XmlTransient
	protected String description;

	protected String abundance;

	public ResultId() {
		this(null, null);
	}

	public ResultId(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public ResultId(String id, String description, String species) {
		this.id = id;
		this.description = description;
		this.species = species;
	}

	public ResultId(String id, String description, String species, String abundance) {
		this.id = id;
		this.description = description;
		this.species = species;
		this.abundance = abundance;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getSpecies() {
		return species;
	}

	public String getAbundance() {
		return abundance;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public void setAbundance(String abundance) {
		this.abundance = abundance;
	}

	@Override
	public String toString() {
		return "ResultId [id=" + id + ", description=" + description + ", species=" + species + ", abundance=" + abundance + "]";
	}

	private static final long serialVersionUID = 23570588831189018L;
}
