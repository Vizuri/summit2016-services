package com.redhat.vizuri.brms.service;

import java.util.Arrays;

import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import com.redhat.vizuri.insurance.Answer;
import com.redhat.vizuri.insurance.Incident;
import com.redhat.vizuri.insurance.IncidentType;
import com.redhat.vizuri.insurance.Question;
import com.redhat.vizuri.insurance.Questionnaire;

public class RuleProcessorTest {
	private static final transient Logger logger = Logger.getLogger(RuleProcessorTest.class);
	private RuleProcessor ruleProcessor;
	
	@Before
	public void setup() {
		ruleProcessor = new RuleProcessor();
	}
	
	@Test
	public void testCustomerQuestions() {
		
		for (IncidentType type : IncidentType.values()) {
			Incident incident = new Incident();
			incident.setType(type);
			incident.setStateCode("NM");
			
			Questionnaire questionnaire = ruleProcessor.getQuestionnaireForCustomer(incident);
			logger.info("Questionnaire: " + questionnaire);
			
			questionnaire.getAnswers().add(new Answer("col-5", "Yes"));
			questionnaire.getAnswers().add(new Answer("h-1", "Other"));
			questionnaire.getAnswers().add(new Answer("h-3", "Golfballs"));
			questionnaire.getAnswers().add(new Answer("win-1", "Yes"));
			
			ruleProcessor.updateQuestionnaire(questionnaire);
			logger.info("Updated questionnaire: " + questionnaire);
			
			for (Question question : questionnaire.getQuestions()) {
				if (Arrays.asList("col-6", "h-2", "h-4", "win-2").contains(question.getQuestionId())) {
					logger.info(">>>>>>>" + question.getQuestionId() + " : " + question.getEnabled());
				}
			}
			
			questionnaire.getAnswers().clear();
			questionnaire.getAnswers().add(new Answer("col-5", "No"));
			questionnaire.getAnswers().add(new Answer("h-1", "Roof"));
			questionnaire.getAnswers().add(new Answer("h-3", "Peas"));
			questionnaire.getAnswers().add(new Answer("win-1", "No"));
			
			ruleProcessor.updateQuestionnaire(questionnaire);
			logger.info("Updated questionnaire2: " + questionnaire);
			
			for (Question question : questionnaire.getQuestions()) {
				if (Arrays.asList("col-6", "h-2", "h-4", "win-2").contains(question.getQuestionId())) {
					logger.info(">>>>>>>" + question.getQuestionId() + " : " + question.getEnabled());
				}
			}
		}
		
	}
	
	
}
