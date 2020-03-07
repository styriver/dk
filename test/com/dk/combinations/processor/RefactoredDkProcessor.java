package com.dk.combinations.processor;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.dk.combinations.beans.PlayerAnalysis;
import com.dk.combinations.beans.PlayerTourneyResults;
import com.dk.combinations.beans.TourneyResult;

public class RefactoredDkProcessor {

	private static final String WORK_DIR = "c:/javaProjects/dk";

	private HashMap<String,String>	tourneyMap = new LinkedHashMap<String,String>();
	private int HISTORICAL_YEARS = 5;

	@SuppressWarnings("deprecation")
	@Test
	public void parseTourneyResults() {
		try {
			String workingDir = System.getProperty("input.path",WORK_DIR);
			String tournamentId = System.getProperty("input.tournament.id");

			int year = Calendar.getInstance().get(Calendar.YEAR);
			int historicalYears = Integer.getInteger("input.historical",HISTORICAL_YEARS);
			
			// reads in current pga schedule list for current year
			File current_schedule = new File ( workingDir + "/schedules/" + year + "-schedule.csv"  );
			List<String> tourneys = FileUtils.readLines(current_schedule);
			for (String tourney : tourneys) {
				String[] data = tourney.split(",");
				tourneyMap.put(data[0],data[1]);
			}
			
			// parse current year
			PlayerTourneyResults playerResults = new PlayerTourneyResults();
			for (String tourneyId : tourneyMap.keySet()) {
				File tourneyFile = new File((String) (System.getProperty("input.path",WORK_DIR) + "/tourneys/" + tourneyId + "/" + year + "/" + year + "-" + tourneyId + ".csv"));
				if (tourneyFile.exists()) {
					processTournament(tourneyFile,playerResults);
				}
			}			
			
			// generate report for current year
			File summaryReport = new File(WORK_DIR + "/" + "currentyear-summaryReport.csv");
			generateSummaryReport(summaryReport,PlayerAnalysis.CURRENT_TOURNEY_KEY,playerResults);
			
			System.out.println("###################### Processing tournament - " + tournamentId);
			// navigate to tournament directory based on tournament id and year and process each historical tournament file
			playerResults = new PlayerTourneyResults();
			for (int i=year;  i >= (year - historicalYears); i--) {
				File tourneyFile = new File((String) (System.getProperty("input.path",WORK_DIR) + "/tourneys/" + tournamentId + "/" + i + "/" + i + "-" + tournamentId + ".csv"));
				if (tourneyFile.exists()) {
					processTournament(tourneyFile,playerResults);
				}
			}
			
			// generate report for current tournament
			summaryReport = new File(WORK_DIR + "/summary/" + year + "-" + tournamentId +"-summaryReport.csv");
			generateSummaryReport(summaryReport,tournamentId,playerResults);
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();			
			Assert.fail();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void processTournament(File tournamentFile, PlayerTourneyResults playerResults)
		throws IOException {
		List<String> results = FileUtils.readLines(tournamentFile);
		
		System.out.println("Processing File \"" + ((File)tournamentFile).getPath() + "\"");
		int i=0;
		String tourneyName = null;
		for (String result : results) {
			if (i != 0) {
				try {
					TourneyResult tourneyResult = new TourneyResult(result);
					playerResults.addTourneyResult(tourneyResult);
					tourneyName = tourneyResult.getTourneyName();
				}
				catch (IllegalStateException ex) {
					ex.printStackTrace();
				}
			}
			i++;
		}
		System.out.println("Processed tournament \"" + tourneyName + "\"");	
	}
	
	@SuppressWarnings("deprecation")
	private void generateSummaryReport(File summaryReport,String tourneyKey,PlayerTourneyResults playerResults)
	 throws IOException {
		FileUtils.writeStringToFile(summaryReport,playerResults.generatePerformanceSummaryReport(tourneyKey));
		System.out.println("File created \"" + summaryReport.getPath() + "\"");
	}
	
	public static void main(String[] args) {
		RefactoredDkProcessor processor = new RefactoredDkProcessor();
		processor.parseTourneyResults();
		System.exit(0);
	}
}