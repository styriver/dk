package com.dk.combinations.processor;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import com.dk.combinations.beans.PlayerTourneyResults;
import com.dk.combinations.beans.TourneyResult;

public class DkProcessor {
	private static final String WORK_DIR = "c:/javaProjects/dk";

	@SuppressWarnings("deprecation")
	@Test
	public void parseTourneyResults() {
		try {
		
			File resultDirectory = new File((String) (System.getProperty("input.path",WORK_DIR) + "/tourneys/" + System.getProperty("tourneyDir")));
			Collection<File> tourneyFiles = FileUtils.listFiles(resultDirectory,new String[] {"csv"},false);
			Object[] inputFiles = tourneyFiles.toArray();
			Arrays.sort(inputFiles);

			PlayerTourneyResults playerResults = new PlayerTourneyResults();
			for (Object tourneyFile : inputFiles) {
				String csvFile = FileUtils.readFileToString((File)tourneyFile);
				String[] fileName = ((File) tourneyFile).getName().split("-");
				String[] tourneyResults = csvFile.split("\r");
				System.err.println("Processing File \"" + ((File)tourneyFile).getPath() + "\"");
				for (String result : tourneyResults) {
					TourneyResult tourneyResult = new TourneyResult(result);
					playerResults.addTourneyResult(tourneyResult);
				}
			}
			
			/*File allResults = new File(resultDirectory.getPath() + "-allResults.csv");
			FileUtils.writeStringToFIle(allResults,playerResults.toCsvString());
			System.out.println("File created \"" + allResults.getPath() + "\"");*/

			File summaryReport = new File(resultDirectory.getPath() + "-summaryReport.csv");
			FileUtils.writeStringToFile(summaryReport,playerResults.generatePerformanceSummaryReport(System.getProperty("tourneyDir")));
			System.out.println("File created \"" + summaryReport.getPath() + "\"");
		}
		catch (Throwable throwable) {
			throwable.printStackTrace();
			Assert.fail();
		}
	}
}
