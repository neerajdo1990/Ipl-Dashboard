package com.ndtech.ipldashboard.data;


import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ndtech.ipldashboard.model.Team;

	@Component
	public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	  private final EntityManager em;

	  @Autowired
	  public JobCompletionNotificationListener(EntityManager em) {
	    this.em = em;
	  }

	  @Override
	  @Transactional
	  public void afterJob(JobExecution jobExecution) {
	    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
	      log.info("!!! JOB FINISHED! Time to verify the results");
	      
	      Map<String, Team> teamData = em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
	      .getResultList()
	      .stream()
	      .map(x-> new Team((String)x[0], (long)x[1]))
	      .collect(Collectors.toMap(x->x.getTeamName(), x->x));
	      
	      em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
	    	      .getResultList()
	    	      .stream()
	    	      .forEach(x->{
	    	    	  Team team = teamData.get((String)x[0]);
	    	    	  team.setTotalMatches(team.getTotalMatches()+(long)x[1]);
	    	      });
	      
	      em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
	      .getResultList()
	      .stream()
	      .forEach(x->{
	    	  Team team = teamData.get((String)x[0]);
	    	  if(team!=null)
	    		  team.setTotalWins((long)x[1]);
	      });
	      
	      teamData.values().forEach(team-> em.persist(team));
	      teamData.values().forEach(team-> System.out.println(team));

	    }
	  }
	}


