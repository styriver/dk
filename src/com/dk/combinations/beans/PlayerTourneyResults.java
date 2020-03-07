package com.dk.combinations.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerTourneyResults {
	private HashMap<String,List<TourneyResult>> playerResultMap = new HashMap<String,List<TourneyResult>>();
	
	public void addTourneyResult(TourneyResult result) {
		String key = result.toMapKey();
		List<TourneyResult> results = playerResultMap.get(key);
		if (results == null) {
			results = new ArrayList<TourneyResult>();
			playerResultMap.put(key,results);
		}
		results.add(result);
	}
	
	public String toCsvString() {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append(TourneyResult.csvHeader());
		
		for (String key : playerResultMap.keySet()) {
			List<TourneyResult> results = playerResultMap.get(key);
			for (TourneyResult result : results ) {
				buffer.append(result.toCsvString());
			}
		}
		
		return buffer.toString();
	}
	
	public String generatePerformanceSummaryReport(String analysisKey) {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append(PlayerAnalysis.analysisHeader());
		
		for (String key : playerResultMap.keySet()) {
			List<TourneyResult> results = playerResultMap.get(key);
			PlayerAnalysis analysis = new PlayerAnalysis(analysisKey);
			analysis.performAnalysis(results);
			buffer.append(analysis.toCsvString());
		}
		
		return buffer.toString();
	}
}
