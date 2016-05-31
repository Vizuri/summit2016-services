package com.redhat.vizuri.brms.service;

import org.jboss.logging.Logger;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

public class AgendaListener implements AgendaEventListener {
	private static final Logger log = Logger.getLogger(AgendaListener.class);



	@Override
	public void matchCreated(MatchCreatedEvent event) {

		log.info("matchCreated : " + event.getMatch().getRule());
		for(Object obj: event.getMatch().getObjects()){
			//log.info(obj.toString());
		}
		//log.info("matchCreated end:");


	}

	@Override
	public void matchCancelled(MatchCancelledEvent event) {

		log.info("matchCancelled : " + event.getMatch().getRule());
	}

	@Override
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
		// log.info("beforeRuleFlowGroupDeactivated : "+event.getRuleFlowGroup());
		// log.info("beforeRuleFlowGroupDeactivated");
	}

	@Override
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

		// log.info("beforeRuleFlowGroupDeactivated" +event.getRuleFlowGroup());


	}

	@Override
	public void beforeMatchFired(BeforeMatchFiredEvent event) {
		
		log.info("beforeMatchFired" + event.getMatch().getRule());
		
	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent event) {

		// log.info("agendaGroupPushed"+event.getAgendaGroup());
	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
		// 
		// log.info("agendaagendaGroupPoppedGroupPushed "+event.getAgendaGroup());
	}

	@Override
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {

		// log.info("afterRuleFlowGroupDeactivated" + event.getRuleFlowGroup());
	}

	@Override
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

		// log.info("afterRuleFlowGroupActivated" + event.getRuleFlowGroup());
	}

	// FactHandle handle = new DefaultFactHandle();

	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		
		log.info("afterMatchFired  : " + event.getMatch().getRule());
		//log.info("event facts size: " + event.getMatch().getFactHandles().size());
	
	}

}