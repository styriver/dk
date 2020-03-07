# import libraries
import requests
import json
import csv
from pathlib import Path
import sys, getopt
import os

def main(argv):
   scrape_id = ''
   scrape_year = ''
   try:
      opts, args = getopt.getopt(argv,"hi:y:",["id=","year="])
   except getopt.GetoptError:
      print ('test.py -i <tourney id to scrape> -y <year to scrape>')
      sys.exit(2)
   for opt, arg in opts:
      if opt == '-h':
         print ('test.py -i <tourney id to scrape> -y <year to scrape>')
         sys.exit()
      elif opt in ("-i", "--id"):
         scrape_id = arg
      elif opt in ("-y", "--year"):
         scrape_year = arg
   url = 'https://statdata.pgatour.com/r/{0}/{1}/leaderboard-v2mini.json'.format(scrape_id, scrape_year)
   print ('Scraping site - {0}'.format(url))
   response = requests.get(url)
   data = json.loads(response.text)
   tourney_name = (data["leaderboard"]["tournament_name"])
   tournament_id = (data["leaderboard"]["tournament_id"])
   course_name = (data["leaderboard"]["courses"][0]["course_name"])
   year = (data["debug"]["setup_year"])
   start_date = (data["leaderboard"]["start_date"])
   print(tourney_name + ',' + tournament_id + ',' + course_name + "," + year)

   file = Path('C:/javaProjects/dk/tourneys/{0}/{1}/{1}-{0}.csv'.format(tournament_id,year))
   dir = Path('C:/javaProjects/dk/tourneys/{0}/{1}'.format(tournament_id,year))
   #try:
    #os.stat(os.path.dirname(file))
   #except:
    #os.mkdir(os.path.dirname(file))   
   
   if not os.path.exists(dir):
     os.makedirs(dir)
     print ('Created:', dir)
		
   touchfile = os.path.dirname(file) + "/" + tourney_name
   Path(touchfile).touch()

   with open(file, 'w', newline='') as csvfile:
     filewriter = csv.writer(csvfile)
     filewriter.writerow(['Position', 'ID', 'Name', 'Overall', 'Total', 'Rd1', 'Rd2', 'Rd3', 'Rd4', 'Tourney Name', 'Tourney Id','Course Name', 'Year'])
     players = (data["leaderboard"]["players"])
     for player in players:
        row = []
        position = (player["current_position"])
        row.append(position.strip())
        row.append(player["player_id"])

        fname = (player["player_bio"]["first_name"])
        lname = (player["player_bio"]["last_name"])
        row.append(fname + " " + lname)

        row.append(player["total"])
        row.append(player["total_strokes"])

        # add each round
        rounds = (player["rounds"])
        for round in rounds:
          row.append(round["strokes"])
        
        row.append(tourney_name)
        row.append(tournament_id)
        row.append(course_name)
        row.append(year)
        filewriter.writerow(row)

if __name__ == "__main__":
   main(sys.argv[1:])