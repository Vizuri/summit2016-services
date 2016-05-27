package com.redhat.vizuri.insurance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Claim implements Serializable {
	private static final long serialVersionUID = 8817532564043280353L;
	
	private Long id;
	private Incident incident;
	private List<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
	private List<String> photoUrls = new ArrayList<String>();
	private boolean approved;
	private Double statedValue = 0d;
	private Double adjustedValue = 0d;
	private List<String> messages = new ArrayList<String>();
	
	public Claim() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Incident getIncident() {
		return incident;
	}

	public void setIncident(Incident incident) {
		this.incident = incident;
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

	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}
	
	public void addPhotoUrl(String photoUrl) {
		this.photoUrls.add(photoUrl);
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

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String message) {
		this.messages.add(message);
	}

	@Override
	public String toString() {
		return "Claim [id=" + id + ", incident=" + incident + ", questionnaires=" + questionnaires + ", photoUrls="
				+ photoUrls + ", approved=" + approved + ", statedValue=" + statedValue + ", adjustedValue="
				+ adjustedValue + ", messages=" + messages + "]";
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
