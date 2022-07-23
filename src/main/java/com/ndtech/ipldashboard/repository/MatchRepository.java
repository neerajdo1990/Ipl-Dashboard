package com.ndtech.ipldashboard.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.ndtech.ipldashboard.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {
	
	List<Match> getByTeam1OrTeam2OrderByDate(String team1, String team2, Pageable pageable);
	
	default List<Match> findLatestMatchByTeamName (String teamName, int count) {
		return getByTeam1OrTeam2OrderByDate(teamName, teamName, PageRequest.of(0, count));
	}

}
