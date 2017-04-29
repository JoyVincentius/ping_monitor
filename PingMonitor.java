import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PingMonitor {

  public static int runSystemCommand(String command) {

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

			String s = "";
			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				if(s.indexOf("could not find host")!=-1){
					return -1;
				}
				int start = s.indexOf("time=");
				if(start!=-1){
					int end = s.indexOf("ms");
					s = s.substring(start+5,end);
					return Integer.parseInt(s);
				}
			}

		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void main(String[] args) {
		FrameGenerator window = new FrameGenerator("Ping Monitor"); 
		window.setVisible(true);
		window.setSize(400,400);
	}
}

class FrameGenerator extends Frame{
	private int count = RanGen.generate();
	private int averagePing;
	private int pingData[];
	private int high=200, mid=100, low=0;
	public FrameGenerator(String title){
		super(title);

		pingData = new int[20];

		for(int i = 1; i < 20; i++){
			pingData[i] = -2;
		}
		pingData[0] = 0;

		MyTimerTask myTask = new MyTimerTask();
		Timer myTimer = new Timer();
		myTimer.schedule(myTask, 1000, 1000);

		addWindowListener(new WindowAdapter(){
							public void windowClosing(WindowEvent we){
								myTimer.cancel();
								System.exit(0);
							}
						});
	}

	public void paint(Graphics g){
		String ip = "google.com";
		int ping = PingMonitor.runSystemCommand("ping " + ip+" -t");

		g.fillRect(40,55,342,205);
		int oldLineX = 40, oldLineY;
		if(pingData[0]==-1){
			oldLineY = 60;
		}
		else{
			oldLineY = 260-pingData[0];
		}

		for(int i = 0; i < 20; i++){
			if(pingData[i]==-2){
				continue;
			}
			if(pingData[i]==-1){
				g.setColor(Color.red);
				g.drawLine(oldLineX, oldLineY, oldLineX=40+i*18, oldLineY=60);
				continue;
			}
			g.setColor(Color.green);
			g.drawLine(oldLineX, oldLineY, oldLineX=40+i*18, oldLineY=260-pingData[i]);
		}
		g.setColor(Color.black);
		g.setFont(new Font("Arial",Font.PLAIN,10));
		g.drawString(Integer.toString(high),20,70);
		g.drawString(Integer.toString(mid),20,165);
		g.drawString(Integer.toString(low),30,255);


		//Ping : 20 | Average Ping : 23
		int loss = getLossPercentage();
		g.setFont(new Font("Arial",Font.PLAIN,15));
		g.drawString("Ping : "+count+"ms",20,290);
		g.drawString("Average Ping : "+getAveragePing()+"ms",20,320);
		g.drawString("Loss Percentage : "+loss+"%",20,350);

		g.drawString("NetHealth",265,290);
		if(loss<=5){
			g.setColor(Color.green);	
		}
		else if(loss<=20){
			g.setColor(Color.yellow);	
		}
		else if(loss<=30){
			g.setColor(Color.orange);	
		}
		else{
			g.setColor(Color.red);	
		}
		g.fillRect(280,300,30,30);
	}

	public int getAveragePing(){
		int totalPing = 0;
		int noOfPing = 20;
		for(int i = 0; i < 20; i++){
			if(pingData[i] == -1 || pingData[i] == -2){
				noOfPing--;
				continue;
			}
			totalPing += pingData[i];
		}
		return totalPing/noOfPing;
	}

	public int getLossPercentage(){
		int totalPing = 20;
		int lossPing = 0;
		for(int i = 0; i < 20; i++){
			if(pingData[i] == -2){
				totalPing--;
				continue;
			}
			if(pingData[i] == -1){
				lossPing++;
				continue;
			}
		}
		return lossPing*100/totalPing;
	}

	class MyTimerTask extends TimerTask {
		public void run() {
			for(int i = 19; i > 0; i--){
				pingData[i] = pingData[i-1];
			}
			pingData[0] = count;
			
			repaint();
			count = RanGen.generate();
		}	
	}

}


class RanGen{
	public static int generate(){
		Random rn = new Random();
		if(rn.nextInt(10)<2){
			return -1;
		}
		return rn.nextInt(200)-1;
	}
}