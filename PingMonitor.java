import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingMonitor {

  public static void runSystemCommand(String command) {

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

			String s = "";
			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				int start = s.indexOf("time=");
				if(start!=-1){
					int end = s.indexOf("ms");
					s = s.substring(start+5,end);
					int ping = Integer.parseInt(s);
					System.out.println(ping+"ms");
				}
			}

		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		String ip = "google.com";
		runSystemCommand("ping " + ip+" -t");

	
	}
}