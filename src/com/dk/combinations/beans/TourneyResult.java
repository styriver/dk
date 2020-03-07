package com.dk.combinations.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class TourneyResult implements Serializable {

	private static final long serialVersionUID = 1L;
	private String position = "";
	private String playerId = "";
	private String name = "";
	private String score;
	private String tourneyName;
	private String tourneyId;
	private String course;
	
	public String getTourneyId() {
		return tourneyId;
	}

	public void setTourneyId(String tourneyId) {
		this.tourneyId = tourneyId;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	private int round1Score = 0;
	private int round2Score = 0;
	private int round3Score = 0;
	private int round4Score = 0;
	private int totalStrokes = 0;
	private int year;
	
	public TourneyResult() {
		
	}
	
	public TourneyResult(String delimitedString) {
		parseResult(delimitedString);
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String mov) {
		this.playerId = mov;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getTourneyName() {
		return tourneyName;
	}

	public void setTourneyName(String tourneyName) {
		this.tourneyName = tourneyName;
	}

	public int getRound1Score() {
		return round1Score;
	}

	public void setRound1Score(int round1Score) {
		this.round1Score = round1Score;
	}

	public int getRound2Score() {
		return round2Score;
	}

	public void setRound2Score(int round2Score) {
		this.round2Score = round2Score;
	}

	public int getRound3Score() {
		return round3Score;
	}

	public void setRound3Score(int round3Score) {
		this.round3Score = round3Score;
	}

	public int getRound4Score() {
		return round4Score;
	}

	public void setRound4Score(int round4Score) {
		this.round4Score = round4Score;
	}

	public int getTotalStrokes() {
		return totalStrokes;
	}

	public void setTotalStrokes(int totalStrokes) {
		this.totalStrokes = totalStrokes;
	}

	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public void parseResult(String delimited) {
		String[] parsedValues = delimited.split(",");
		if (parsedValues != null && parsedValues.length > 13) {
			if (parsedValues.length == 14) {
				parsedValues = correctName(parsedValues);
			}
			else {
				System.out.println("Failed to parse record " + delimited);
				throw new IllegalStateException("Failed to parse record " + delimited + " Illegal Length \"" + parsedValues.length + "\"");							
			}
		}
		
		if (parsedValues != null && parsedValues.length == 13) {
			try {
				position = parsedValues[0].trim();
				if (position.isEmpty()) {
					position = "CUT";
				}
				
				playerId = parsedValues[1].trim();
				name = parsedValues[2].replace("\"", "").trim();
				
				if (parsedValues.length >= 4) {
					score = StringUtils.isNotEmpty(parsedValues[3]) ? parsedValues[3].trim() : "";
				} 
				
				if (parsedValues.length >= 5) {
					totalStrokes = StringUtils.isNotEmpty(parsedValues[4]) ? Integer.valueOf(parsedValues[4]) : 0;		
				}
				
				if (parsedValues.length >= 6) {
					round1Score = StringUtils.isNotEmpty(parsedValues[5]) ? Integer.valueOf(parsedValues[5]) : 0;
				}
				
				if (parsedValues.length >= 7) {
					round2Score = StringUtils.isNotEmpty(parsedValues[6]) ? Integer.valueOf(parsedValues[6]) : 0;				
				}
				
				if (parsedValues.length >= 8) {
					round3Score = StringUtils.isNotEmpty(parsedValues[7]) ? Integer.valueOf(parsedValues[7]) : 0;					
				}
				
				if (parsedValues.length >= 9) {
					round4Score = StringUtils.isNotEmpty(parsedValues[8]) ? Integer.valueOf(parsedValues[8]) : 0;					
				}
				
				if (parsedValues.length >= 10) {
					tourneyName = parsedValues[9].trim();
				}
				
				if (parsedValues.length >= 11) {
					tourneyId = parsedValues[10].trim();									
				}
				
				if (parsedValues.length >= 12) {
					course = parsedValues[11].trim();									
				}
				
				if (parsedValues.length >= 13) {
					year = StringUtils.isNotEmpty(parsedValues[12]) ? Integer.valueOf(parsedValues[12]) : 0;										
				}				
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				System.err.println("Unhandled Array Length. Delimited Record \"" + delimited);
				throw new IllegalStateException("Unhandled Array Length. Delimited Record \"" + delimited);
			}
		}
		else {
			System.err.println("Unhandled Array Length. Delimited Record \"" + delimited);
			throw new IllegalStateException("Unhandled Array Length. Delimited Record \"" + delimited + " Illegal Length \"" + parsedValues.length + "\"");
		}		
	}
	
	public String[] correctName(String[] parsedValues) {
		// fix name
		String firstName = parsedValues[2].replaceAll("\"", ""); 
		String lastName = parsedValues[3].replaceAll("\"", "").replaceAll(",", "").trim();
		String name = firstName + " " + lastName;				
		String correctedDelimitedString = new String();
		for (int i=0; i < parsedValues.length; i++ ) {
			if (i != 2 && i != 3) {
				correctedDelimitedString += parsedValues[i];
			}
			else {
				if (i == 2) {
					correctedDelimitedString += name + ",";
				}
			}
			
			if (i < (parsedValues.length - 1) && i != 2 && i !=3 ) {
				correctedDelimitedString += ",";
			}
		}
		System.out.println("Corrected name for Record \"" + correctedDelimitedString);
		return correctedDelimitedString.split(",");
		
	}
	
	public boolean isWin() {
		return getFinish() == 1;
	}
	
	public boolean isTopFive() {
		int finish = getFinish();
		return finish < 6 && finish > 1;
	}
	
	public boolean isTopTen() {
		int finish = getFinish();
		return finish < 11 && finish > 5;
	}
	
	public boolean isTop15() {
		int finish = getFinish();
		return finish < 16 && finish > 10;
	}
	
	public boolean isTop20() {
		int finish = getFinish();
		return finish < 21 && finish > 15;
	}
	
	public boolean isTop30() {
		int finish = getFinish();
		return finish < 31 && finish > 20;
	}
	
	public int getFinish() {
		try {
			return Integer.valueOf((getPosition().replace("T","")).trim());
		}
		catch (NumberFormatException ex) {
			return -99;
		}
	}
	
	public String toMapKey() {
		return name.replaceAll(" ", "");
	}
	
	public String toCsvString() {
		if (name != "null") {
			StringBuffer buffer = new StringBuffer(100);
			buffer.append(name);
			buffer.append(",");
			buffer.append(position);
			buffer.append(",");
			buffer.append(score);
			buffer.append(",");
			buffer.append(year);
			buffer.append(",");
			buffer.append(tourneyName);
			buffer.append(",");
			buffer.append(course);	
			buffer.append(",");
			buffer.append(tourneyId);				
			buffer.append("\r");
			return buffer.toString();
		}
		else {
			return "";
		}
	}
	
	public static String csvHeader() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("Name");
		buffer.append(",");
		buffer.append("Position");
		buffer.append(",");
		buffer.append("Score");
		buffer.append(",");
		buffer.append("Year");
		buffer.append(",");
		buffer.append("Tourney Name");
		buffer.append(",");
		buffer.append("course");
		buffer.append(",");
		buffer.append(",");
		buffer.append("tourneyId");
		buffer.append("\r");
		return buffer.toString();
	}
}
