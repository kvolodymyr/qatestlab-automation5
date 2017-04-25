:: java -Dwebdriver.chrome.driver=".\Drivers\chromedriver.exe"  -Dwebdriver.gecko.driver=".\Drivers\geckodriver.exe" -jar selenium-server-standalone-3.4.0.jar -role node -hub http://localhost:4444/grid/register -browser "browserName=firefox,maxInstances=1" -browser "browserName=chrome,maxInstances=1" -port 5555
java -Dwebdriver.phantomjs.driver=".\Drivers\phantomjs.exe" -jar selenium-server-standalone-3.4.0.jar -role node -hub http://localhost:4444/grid/register -browser "browserName=phantomjs,maxInstances=2" -port 6666


:: java -jar selenium-server-standalone-3.4.0.jar -role node -hub http://localhost:4444/grid/register