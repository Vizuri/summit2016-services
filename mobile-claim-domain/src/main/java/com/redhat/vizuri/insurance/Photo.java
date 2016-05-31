package com.redhat.vizuri.insurance;

import java.io.Serializable;
import java.util.Date;

public class Photo implements Serializable {
	private static final long serialVersionUID = -4598181367474412773L;

	private String photoUrl;
	private String description;
	private String uploaderName;
	private Date uploadeDate;
	private Date takenDate;
	
	public Photo() {
		super();
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUploaderName() {
		return uploaderName;
	}

	public void setUploaderName(String uploaderName) {
		this.uploaderName = uploaderName;
	}

	public Date getUploadeDate() {
		return uploadeDate;
	}

	public void setUploadeDate(Date uploadeDate) {
		this.uploadeDate = uploadeDate;
	}

	public Date getTakenDate() {
		return takenDate;
	}

	public void setTakenDate(Date takenDate) {
		this.takenDate = takenDate;
	}

	@Override
	public String toString() {
		return "Photo [photoUrl=" + photoUrl + ", description=" + description + ", uploaderName=" + uploaderName
				+ ", uploadeDate=" + uploadeDate + ", takenDate=" + takenDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((photoUrl == null) ? 0 : photoUrl.hashCode());
		result = prime * result + ((takenDate == null) ? 0 : takenDate.hashCode());
		result = prime * result + ((uploadeDate == null) ? 0 : uploadeDate.hashCode());
		result = prime * result + ((uploaderName == null) ? 0 : uploaderName.hashCode());
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
		Photo other = (Photo) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (photoUrl == null) {
			if (other.photoUrl != null)
				return false;
		} else if (!photoUrl.equals(other.photoUrl))
			return false;
		if (takenDate == null) {
			if (other.takenDate != null)
				return false;
		} else if (!takenDate.equals(other.takenDate))
			return false;
		if (uploadeDate == null) {
			if (other.uploadeDate != null)
				return false;
		} else if (!uploadeDate.equals(other.uploadeDate))
			return false;
		if (uploaderName == null) {
			if (other.uploaderName != null)
				return false;
		} else if (!uploaderName.equals(other.uploaderName))
			return false;
		return true;
	}
	
}
