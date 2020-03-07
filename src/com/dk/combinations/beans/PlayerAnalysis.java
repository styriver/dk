package com.dk.combinations.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class PlayerAnalysis {
	public static final String CURRENT_TOURNEY_KEY = "current";
	private String name;
	private String analysisKey;
	private ArrayList<String> notes = new ArrayList<String>();
	private HashSet<String> tourneyNames = new HashSet<String>(); 
	private int wins;
	private int top5;
	private int top10;
	private int top15;
	private int top20;
	private int top30;
	private int entered;
	private int missedCuts;
	private double scoreIndex;
	private double weightedIndex;
	
	private static final double INDEX_MISSED_CUT = -1.0;
	private static final double INDEX_WD_DQ_DNS = -0.25;	
	private static final double INDEX_MADE_CUT = 0.35;
	private static final double INDEX_TOP_30 = 0.5;
	private static final double INDEX_TOP_20 = 1.0;
	private static final double INDEX_TOP_15 = 1.5;
	private static final double INDEX_TOP_10 = 2.0;
	private static final double INDEX_TOP_5 = 2.5;
	private static final double INDEX_WIN = 3.0;	
	private static final double YEAR_BASE = 1.0;	
	private static final double YEAR_REGRESSION = 0.1;
	private static final double WEIGHTED_ADJUSTMENT_FACTOR = 1.0;
	private static final int WEIGHTED_TOLERANCE_TOURNEY = 2; // go back two years
	private static final int WEIGHTED_TOLERANCE_CURRENT = 3; // three most recent tournaments
	private static final Calendar NOW = Calendar.getInstance();
	
	public PlayerAnalysis(String analysisKey) {
		this.analysisKey = analysisKey;
	}
	
	public void performAnalysis(List<TourneyResult> tourneyResults) {
		for (TourneyResult result : tourneyResults) {
			try {
				name = result.getName();
				tourneyNames.add(result.getTourneyName());
				entered++;
				
				if (StringUtils.equalsIgnoreCase("CUT", result.getPosition())) {
					missedCuts++;
					adjustIndex(INDEX_MISSED_CUT,result.getYear());
					addPlayerNote(result.getTourneyName(),result.getYear() + " - CUT");
				} else if (StringUtils.equalsIgnoreCase("WD", result.getPosition())) {
					adjustIndex(INDEX_WD_DQ_DNS,result.getYear());
					addPlayerNote(result.getTourneyName(),result.getYear() + " - WD");
				} else if (StringUtils.equalsIgnoreCase("DQ", result.getPosition())) {
					adjustIndex(INDEX_WD_DQ_DNS,result.getYear());
					addPlayerNote(result.getTourneyName(),result.getYear() + " - DQ");
				} else if (StringUtils.equalsIgnoreCase("DNS", result.getPosition())) {
					adjustIndex(INDEX_WD_DQ_DNS,result.getYear());
					addPlayerNote(result.getTourneyName(),result.getYear() + " - DNS");
				} else if (StringUtils.equalsIgnoreCase("MDF", result.getPosition())) {
					adjustIndex(INDEX_MADE_CUT,result.getYear());
					addPlayerNote(result.getTourneyName(),result.getYear() + " - MDF");
				} else {
					if (StringUtils.isNotEmpty(result.getPosition().trim())) {
						adjustIndex(INDEX_MADE_CUT,result.getYear());
					
						if (result.isWin()) {
							wins++;
							adjustIndex(INDEX_WIN,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Win");
						}
						
						else if (result.isTopFive()) {
							top5++;
							adjustIndex(INDEX_TOP_5,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Top 5");
						}
						
						else if (result.isTopTen()) {
							top10++;
							adjustIndex(INDEX_TOP_10,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Top 10");
						}
						
						else if (result.isTop15()) {
							top15++;
							adjustIndex(INDEX_TOP_15,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Top 15");
						}
						
						else if (result.isTop20()) {
							top20++;
							adjustIndex(INDEX_TOP_20,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Top 20");
						}
						
						else if (result.isTop30()) {
							top30++;
							adjustIndex(INDEX_TOP_30,result.getYear());
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Top 30");
						}
						else {
							addPlayerNote(result.getTourneyName(),result.getYear() + " - Pos " + result.getFinish());							
						}
					} else {
						System.out.println("Unknown position for player \"" + result.toCsvString() + "\"");
					}
				}
			}
			catch (NumberFormatException ex) {
				ex.printStackTrace();
				System.out.println(result.toCsvString());
			}
		}
		
		createWeightedIndex(tourneyResults);
	}
	
	public static String analysisHeader() {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append("Name");
		buffer.append(",");
		buffer.append("TourneyNames");
		buffer.append(",");
		buffer.append("Wins");
		buffer.append(",");
		buffer.append("Top 5");
		buffer.append(",");
		buffer.append("Top 10");
		buffer.append(",");
		buffer.append("Top 15");
		buffer.append(",");
		buffer.append("Top 20");
		buffer.append(",");
		buffer.append("Top 30");
		buffer.append(",");
		buffer.append("Top 30 Percentage");
		buffer.append(",");
		buffer.append("Entered");
		buffer.append(",");
		buffer.append("Missed Cuts");
		buffer.append(",");
		buffer.append("Cut Percentage");
		buffer.append(",");
		buffer.append("Notes");
		buffer.append(",");
		buffer.append("Score Index");
		buffer.append(",");
		buffer.append("Top 5 Percentage");
		buffer.append(",");
		buffer.append("Top 10 Percentage");
		buffer.append(",");
		buffer.append("Top 15 Percentage");
		buffer.append(",");
		buffer.append("Top 20 Percentage");
		buffer.append(",");
		buffer.append("Weighted Index");
		buffer.append("\r");
		
		return buffer.toString();
	}
	
	public String toCsvString() {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append("\"" + name + "\"");
		buffer.append(",");
		
		boolean first = true;
		for (String tourney : tourneyNames) {
			if (!first) {
				buffer.append(";");
			}
			buffer.append(tourney);
			first = false;
		}
		buffer.append(",");
		buffer.append(wins);
		buffer.append(",");
		buffer.append(top5);
		buffer.append(",");
		buffer.append(top10);
		buffer.append(",");
		buffer.append(top15);
		buffer.append(",");
		buffer.append(top20);
		buffer.append(",");
		buffer.append(top30);
		buffer.append(",");
		
		int top30Total = top30 + top20 + top15 + top10 + top5 + wins;
		buffer.append((float)top30Total / entered);
		buffer.append(",");
		
		buffer.append(entered);
		buffer.append(",");
		buffer.append(missedCuts);
		buffer.append(",");
		buffer.append((float) missedCuts/entered);
		buffer.append(",");
		buffer.append("\"" + generateNotes().trim() + "\"");
		buffer.append(",");
		buffer.append(scoreIndex);
		buffer.append(",");
		
		int top5Total = top5 + wins;
		buffer.append((float) top5Total/entered);
		buffer.append(",");
		
		int top10Total = top10 + top5 + wins;
		buffer.append((float) top10Total/entered);
		buffer.append(",");
		
		int top15Total = top15 + top10 + top5 + wins;
		buffer.append((float) top15Total/entered);
		buffer.append(",");
		
		int top20Total = top20 + top10 + top5 + wins;
		buffer.append((float) top20Total/entered);
		buffer.append(",");
		
		buffer.append(weightedIndex);
		buffer.append("\r");
		return buffer.toString();
	}
	
	private void adjustIndex(double adjustment,int year) {
		int yearDiff = NOW.get(GregorianCalendar.YEAR) - year;
		yearDiff--;
		
		// don't discount last year performance as it is most recent
		if (yearDiff <= 0) {
			scoreIndex += adjustment;
		}
		else {
			double yearAdjustedScore = adjustment * (YEAR_BASE - (YEAR_REGRESSION * yearDiff));
			scoreIndex += yearAdjustedScore;
		}
	}
	
	private void addPlayerNote(String tourneyName,String note) {
		if (analysisKey.equalsIgnoreCase(CURRENT_TOURNEY_KEY)) {
			int index = tourneyName.indexOf(".");
			tourneyName = tourneyName.substring(index + 1);
		}
		else {
			tourneyName = "";
		}
		notes.add(tourneyName + "(" + note + ")");
	}
	
	private String generateNotes() {
		StringBuffer noteBuffer = new StringBuffer(500);
		if (!analysisKey.equalsIgnoreCase(CURRENT_TOURNEY_KEY)) {
			noteBuffer.append(tourneyNames.toArray()[0] + " - ");
		}
		
		for (int i= notes.size(); i > 0; i--) {
			noteBuffer.append(notes.get(i-1));
			if (i != 0) {
				noteBuffer.append(" ");
			}
		}
		
		String temp =  noteBuffer.toString();
		if (temp.equalsIgnoreCase("Null")) {
			temp = "";
		}
		
		return temp;
	}
	
	private void createWeightedIndex(List<TourneyResult> tourneyResults) {
		int currentYear = NOW.get(GregorianCalendar.YEAR);
		currentYear--;
		
		int top15Finishes = 0;
		int counter = 0;
		for (TourneyResult result : tourneyResults) {
			counter++;
			if (result.isWin() || result.isTopFive() || result.isTopTen() || result.isTop15() && (currentYear - result.getYear()) < WEIGHTED_TOLERANCE_TOURNEY) {
				if (!analysisKey.equalsIgnoreCase(CURRENT_TOURNEY_KEY)) {
					top15Finishes++;
				}
				else {
					// weight three most recent tournaments played in same year
					if ((tourneyResults.size() - counter) < WEIGHTED_TOLERANCE_CURRENT) {
						top15Finishes++;
					}
				}
			}
		}
		
		if (!analysisKey.equalsIgnoreCase(CURRENT_TOURNEY_KEY)) {
			weightedIndex = scoreIndex + ((float) top15Finishes / WEIGHTED_TOLERANCE_TOURNEY) * WEIGHTED_ADJUSTMENT_FACTOR; 
		}
		else {
			weightedIndex = scoreIndex + ((float) top15Finishes / WEIGHTED_TOLERANCE_CURRENT) * WEIGHTED_ADJUSTMENT_FACTOR; 			
		}
	}
}
