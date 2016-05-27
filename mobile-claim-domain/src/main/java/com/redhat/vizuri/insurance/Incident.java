package com.redhat.vizuri.insurance;

import java.io.Serializable;
import java.util.Date;

public class Incident implements Serializable {
	private static final long serialVersionUID = -1164413366918727474L;

	private Long id;
	private IncidentType type;
	private String description;
	private Date incidentDate;
	
	public Incident() {
		super();
	}

	public Incident(Long id, IncidentType type, String description, Date incidentDate) {
		super();
		this.id = id;
		this.type = type;
		this.description = description;
		this.incidentDate = incidentDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public IncidentType getType() {
		return type;
	}

	public void setType(IncidentType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(Date incidentDate) {
		this.incidentDate = incidentDate;
	}
	

	@Override
	public String toString() {
		return "Incident [id=" + id + ", type=" + type + ", description=" + description + ", incidentDate="
				+ incidentDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((incidentDate == null) ? 0 : incidentDate.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Incident other = (Incident) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (incidentDate == null) {
			if (other.incidentDate != null)
				return false;
		} else if (!incidentDate.equals(other.incidentDate))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
