package com.redhat.vizuri.insurance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Claim implements Serializable {
	private static final long serialVersionUID = 8817532564043280353L;
	
	private String id;
	private Incident incident;
	private Customer customer;
	private List<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
	private List<Photo> photos = new ArrayList<Photo>();
	private boolean approved;
	private Double statedValue = 0d;
	private Double adjustedValue = 0d;
	private List<Comment> comments = new ArrayList<Comment>();
	
	public Claim() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Incident getIncident() {
		return incident;
	}

	public void setIncident(Incident incident) {
		this.incident = incident;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}
	
	public void addQuestionnaire(Questionnaire q) {
		this.questionnaires.add(q);
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	
	public void addPhoto(Photo photo) {
		this.photos.add(photo);
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Double getStatedValue() {
		return statedValue;
	}

	public void setStatedValue(Double statedValue) {
		this.statedValue = statedValue;
	}

	public Double getAdjustedValue() {
		return adjustedValue;
	}

	public void setAdjustedValue(Double adjustedValue) {
		this.adjustedValue = adjustedValue;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

	@Override
	public String toString() {
		return "Claim [id=" + id + ", incident=" + incident + ", questionnaires=" + questionnaires + ", photos="
				+ photos + ", approved=" + approved + ", statedValue=" + statedValue + ", adjustedValue="
				+ adjustedValue + ", messages=" + comments + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Claim other = (Claim) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
