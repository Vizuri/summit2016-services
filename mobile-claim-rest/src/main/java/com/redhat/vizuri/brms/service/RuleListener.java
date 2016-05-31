package com.redhat.vizuri.brms.service;

import org.jboss.logging.Logger;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;



public class RuleListener implements RuleRuntimeEventListener {
	private static final Logger log = Logger.getLogger(RuleListener.class);

	@Override
	public void objectInserted(ObjectInsertedEvent event) {
		//log.info("objectInserted");

		Object factObject = event.getObject();
		log.info("inserted Object : " + factObject);
	}

	@Override
	public void objectUpdated(ObjectUpdatedEvent event) {
		//log.info("objectUpdated");

		Object factObject = event.getObject();

		log.info("updated Object : " + factObject);


	}

	@Override
	public void objectDeleted(ObjectDeletedEvent event) {
		//log.info("objectDeleted");

		Object factObject = event.getOldObject();

		log.info("deleted Object : " + factObject);
	}


}