java -Dfile.encoding=UTF-8 -cp "selendroid-grid-plugin-0.17.0.jar;selenium-server-standalone-3.4.0.jar" org.openqa.grid.selenium.GridLauncherV3 -capabilityMatcher io.selendroid.grid.SelendroidCapabilityMatcher -role hub -host 127.0.0.1 -port 4444


:: java -jar selenium-server-standalone-3.4.0.jar -role node -hub http://localhost:4444/grid/register