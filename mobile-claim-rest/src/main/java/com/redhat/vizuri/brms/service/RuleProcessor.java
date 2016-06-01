package com.redhat.vizuri.brms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.logging.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.redhat.vizuri.insurance.Answer;
import com.redhat.vizuri.insurance.Incident;
import com.redhat.vizuri.insurance.Question;
import com.redhat.vizuri.insurance.Questionnaire;

public class RuleProcessor {


	private KieContainer kContainer = RuleProcessor.Factory.get();// kServices.getKieClasspathContainer();
	private static final Logger log = Logger.getLogger(RuleProcessor.class);
	
	private static class Factory {
		private static KieContainer kContainer;
		static {
			try {


				// if the local maven repository is not in the default ${user.home}/.m2
				// need to provide the custom settings.xml
				// pass property value
				// -Dkie.maven.settings.custom={custom.settings.location.full.path}

				KieServices kServices = KieServices.Factory.get();

				ReleaseId releaseId = kServices.newReleaseId("com.redhat.vizuri.insurance", "mobile-claim-rules", "1.0-SNAPSHOT");

				//kContainer = kServices.newKieContainer(releaseId, Factory.class.getClassLoader());
				
				kContainer = kServices.newKieContainer(releaseId);

				KieScanner kScanner = kServices.newKieScanner(kContainer);


				// Start the KieScanner polling the maven repository every 10 seconds

				kScanner.start(10000L);
				
				
			} catch (Exception e) {

				log.error("",e);
			}
		}

		public static KieContainer get() {
			return kContainer;
		}
	}

	public RuleProcessor() {

	}

	public static final String AGENDA_CUSTOMER_QUESTIONS = "construct-customer-questions";	
	public static final String AGENDA_ADJUSTER_QUESTIONS = "construct-adjuster-questions";
	public static final String AGENDA_UPDATE_QUESTIONS = "question-cleanup";
	
	public static final String AGENDA_SYNC_ANSWERS = "sync-answers";	// update answers
	public static final String AGENDA_QUOTE_ERROR_CHECK = "quote-error-check";	// check for errors
	
	public static final String AGENDA_MAIN = "MAIN";
	
	private AgendaListener agendaListener = new AgendaListener();
	private RuleListener ruleListener = new RuleListener();

	public KieSession createNewQuoteSession(boolean addListeners){		
		KieSession quoteSession = kContainer.newKieSession();
		
		if (addListeners){
			quoteSession.addEventListener(agendaListener);
			quoteSession.addEventListener(ruleListener);
		}
		
		return quoteSession;
	}
	
	public void updateQuestionnaire(Questionnaire questionnaire) {
		KieSession kSession = null;
		try {
			kSession = createNewQuoteSession(false);
			
			for (Question q : questionnaire.getQuestions()) {
				kSession.insert(q);
			}
			
			for (Answer a : questionnaire.getAnswers()) {
				kSession.insert(a);
			}
			
			log.info("Firing agenda group: " + AGENDA_SYNC_ANSWERS);
			kSession.getAgenda().getAgendaGroup(AGENDA_SYNC_ANSWERS).setFocus();
			int ruleCount = kSession.fireAllRules();
			log.info("Fired " + ruleCount + " rules.");
		} catch (Exception e) {
			log.error("Exception in updateQuestionnaire ["+AGENDA_SYNC_ANSWERS + ", " + questionnaire +"]",e);
			throw e;
		} finally {
			kSession.dispose();
		}
	}
	
	public Questionnaire getQuestionnaireForCustomer(Incident incident){
		log.info("Inside getQuestionnaireForCustomer for incident: "+ incident);
		return getQuestionnaire(incident, AGENDA_CUSTOMER_QUESTIONS);
	}
	
	public Questionnaire getQuestionnaireForAdjuster(Incident incident) {
		log.info("Inside getQuestionnaireForAdjuster for incident: " + incident);
		return getQuestionnaire(incident, AGENDA_ADJUSTER_QUESTIONS);
	}
	
	private Questionnaire getQuestionnaire(Incident incident, String constructionAgendaGroup) {	
		KieSession kSession = null;
		List<Object> questionnaires = null;
		Questionnaire questionnaire = null;
		try{
			kSession = createNewQuoteSession(false);
			
			kSession.insert(incident);
					
			log.info("Fire agenda group: " + constructionAgendaGroup);
			kSession.getAgenda().getAgendaGroup(constructionAgendaGroup).setFocus();
			int ruleCount = kSession.fireAllRules();
			log.info("Fired " + ruleCount + " rules.");
			kSession.getAgenda().getAgendaGroup(AGENDA_UPDATE_QUESTIONS).setFocus();
			ruleCount = kSession.fireAllRules();
			log.info("Fired " + ruleCount + " rules.");
			
			questionnaires = getFacts(kSession, Questionnaire.class);
			
			questionnaire = (Questionnaire) questionnaires.get(0);
			
			Collections.sort(questionnaire.getQuestions(), new Comparator<Question>() {
				@Override
				public int compare(Question o1, Question o2) {
					return Integer.valueOf(o1.getOrder()).compareTo(Integer.valueOf(o2.getOrder()));
				}
			});
			
		} catch (Exception e) {
			log.error("Exception in getQuestionnaire ["+constructionAgendaGroup + ", " + questionnaires + ", " + incident +"]",e);
			questionnaire = null;
			throw e;
		} finally {
			kSession.dispose();
		}
		return questionnaire;
	}
	
	private List<Object> getFacts(KieSession quoteSession, Class<?> factClass) {
		
		log.info("Inside getExistingQuoteFact");
		
		Collection<?> results = quoteSession.getObjects(new ClassObjectFilter(factClass));
		List<Object> facts = new ArrayList<Object>();
		
		if (results != null){
			
			log.info("have results size["+results.size()+"]");
			for (Object fact : results) {
						
				log.info("found fact: " + fact);	
				facts.add(fact);
			}
		}
		
		return facts;
	}

}
