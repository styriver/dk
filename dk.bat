@ECHO OFF 


set /p previd=Enter Previous Tournament Id:
set /p id=Enter Next Tournament Id:
set /p year=Enter Enter tournament year:
  
echo Getting Tournament Information for tournament id - %id%

echo Scraping for current tournament
py ./scripts/tourney.py -i %previd% -y %year%

echo scraping world rankings
echo Scraping for current tournament
py ./scripts/rankings.py

java -Dinput.tournament.id=%id% -cp C:\javaProjects\dk\bin;C:\javaProjects\dk\deploy\lib\* com.dk.combinations.processor.RefactoredDkProcessor

pause
Exit