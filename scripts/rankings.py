# import libraries
import requests
import csv
from pathlib import Path
import sys, getopt
import os
from bs4 import BeautifulSoup
from urllib import parse
def main(argv):
   playercount = '200'
   try:
      opts, args = getopt.getopt(argv,"hp:",["playercount="])
   except getopt.GetoptError:
      print ('test.py -p <playercount to return>')
      sys.exit(2)
   for opt, arg in opts:
      if opt == '-h':
         print ('test.py -p <playercount to return>')
         sys.exit()
      elif opt in ("-p", "--playercount"):
         playercount = arg
   url = 'http://www.owgr.com/ranking?pageNo=1&pageSize={0}&country=All'.format(playercount)
   print ('Scraping site - {0}'.format(url))
   response = requests.get(url)
   soup = BeautifulSoup(response.text, 'html.parser')
   
   file = Path('C:/javaProjects/dk/owg-rankings.csv')
   with open(file, 'w', newline='') as csvfile:
     filewriter = csv.writer(csvfile)
     filewriter.writerow(['This Week','Last Week','End','Name','ID','Events Played'])
     
     # Pull all player rows from table
     player_rows = soup.find_all('tr')
     for player in player_rows:   
       player_data = player.find_all('td')
       row = []
       i=0
       while i < len(player_data):
         if i == 0 or i == 1 or i ==2 or i == 7:
           row.append (player_data[i].text)
         if i == 4:
           row.append (player_data[i].text)
           id_url = player_data[i].find('a',href=True, text=True)['href']
           player_id =  (parse.parse_qs(parse.urlparse(id_url).query)['playerID'][0])
           row.append (player_id)
         i += 1
       if row:
         filewriter.writerow(row)

if __name__ == "__main__":
   main(sys.argv[1:])
