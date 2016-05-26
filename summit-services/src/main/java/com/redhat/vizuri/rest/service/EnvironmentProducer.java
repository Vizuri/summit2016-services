package com.redhat.vizuri.rest.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@javax.ejb.Singleton
@javax.ejb.Startup
public class EnvironmentProducer {
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentProducer.class);
	//private static final Logger cancel_logger = LoggerFactory.getLogger("cancel_log");
	//private static final Logger create_logger = LoggerFactory.getLogger("create_log");


	@PersistenceUnit(unitName = "com.redhat.vizuri.jbpm.domain")
	private EntityManagerFactory emf;

	@Inject
	private KieContainer kieContainer;
	
//	@Inject 
//	private Properties properties;
	
	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return this.emf;
	}

	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		EntityManager em = emf.createEntityManager();
		return em;
	}
	

	public void close(@Disposes EntityManager em) {
		em.close();
	}

	/**
	 * Dummy implementation.  No Human Task Server Wired at this time.
	 * 
	 * @return
	 */
	@Produces
    public UserGroupCallback produceSelectedUserGroupCalback() {
		UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl("classpath:/roles.properties");
        return userGroupCallback;
    }


    /**
     * Dummy implementation.  No Human Task Server Wired at this time.
     * 
     * @return
     */
    @Produces
    public IdentityProvider produceIdentityProvider() {
        return new IdentityProvider() {
			
			@Override
			public boolean hasRole(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public List<String> getRoles() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
    }
    /**
     * Produces the RuntimeEnvironment use to create RuntimeManager for BPM processes
     * 
     * @param emf
     * @return
     */
    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
		logger.info(">>>> In produceEnvironment");
		//String kbaseName = properties.getProperty("bpms.kjar.kbase");
		//logger.info("Kbase:" + kbaseName);

				
		KieBase kbase = kieContainer.getKieBase();
		
		UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl("classpath:/roles.properties");
		DefaultRegisterableItemsFactory df = new DefaultRegisterableItemsFactory();
		
		RuntimeEnvironment runtimeEnvironment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
				.entityManagerFactory(emf)
				.userGroupCallback(userGroupCallback)
				.knowledgeBase(kbase).persistence(true)
				.registerableItemsFactory(df)
				.get();
		
		return runtimeEnvironment;
    }
    
    @PostConstruct
    public void initPostConstruct(){
    	logger.info(">>initPostConstruct");
    	logger.info("kieContainer>>{}",kieContainer);
    	RuntimeEnvironment env = produceEnvironment(this.emf);
    	logger.info("RuntimeEnvironment >>{}",env);
    }

    

}
