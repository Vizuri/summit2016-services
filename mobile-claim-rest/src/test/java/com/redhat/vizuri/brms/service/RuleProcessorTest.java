package com.redhat.vizuri.brms.service;

import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import com.redhat.vizuri.insurance.Incident;
import com.redhat.vizuri.insurance.IncidentType;
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
			
			Questionnaire questionnaire = ruleProcessor.getQuestionnaireForCustomer(incident);
			logger.info("Questionnaire: " + questionnaire);
		}
		
	}
}
