package com.ndtech.ipldashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ndtech.ipldashboard.model.Team;
import com.ndtech.ipldashboard.repository.MatchRepository;
import com.ndtech.ipldashboard.repository.TeamRepository;

@RestController
public class TeamController {
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private MatchRepository matchRepository;
	
	public TeamController(TeamRepository teamRepository, MatchRepository matchRepository) {
		super();
		this.teamRepository = teamRepository;
		this.matchRepository = matchRepository;
	}


	@GetMapping("team/{teamName}")
	public Team getTeam(@PathVariable String teamName) {
		Team team = this.teamRepository.findByTeamName(teamName);
		Pageable pageable = PageRequest.of(0,4);
		team.setMatches(this.matchRepository.getByTeam1OrTeam2OrderByDate(teamName, teamName, pageable));
		return team;
	}
	
}
