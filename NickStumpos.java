package Buckingham.Team3;
import robocode.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot
{
	private static class Enemy{
		List<Double> bearings = new ArrayList<Double>();
		List<Double> energy = new ArrayList<Double>();
		List<Double> heading = new ArrayList<Double>();
		List<Double> distance = new ArrayList<Double>();
		List<Double> velocity = new ArrayList<Double>();
	}
	Map<String,Enemy> enemies = new HashMap<String, Enemy>();
	private String lastSeen;
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	/**
	 * run: NickStumpos's default behavior
	 */
	public void run() {

		 setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		 
		// Robot main loop
		while(true) {
			Enemy e = enemies.get(lastSeen);
			if (e!=null) {
				Double lastBearing = e.bearings.get(e.bearings.size() - 1);
				
				turnGunRight(normalizeBearing(getHeading() - getGunHeading() +lastBearing));
				while (getGunTurnRemaining()>0) {
			        execute();
			    }
				fire(2);
				turnRight(normalizeBearing(lastBearing+180));
				while (getTurnRemaining() > 0 ) {
			        execute();
			    }
			}
			turnRadarLeft(360);
			if(getX()<20 )
		    ahead(20);
		    while (getDistanceRemaining() > 0 && getRadarTurnRemaining()>0) {
		        execute();
		    }

		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if(!enemies.containsKey(e.getName())){
			enemies.put(e.getName(), new Enemy());
		}
		enemies.get(e.getName()).bearings.add(e.getBearing());
		enemies.get(e.getName()).distance.add(e.getDistance());
		enemies.get(e.getName()).energy.add(e.getEnergy());
		enemies.get(e.getName()).heading.add(e.getHeading());
		enemies.get(e.getName()).velocity.add(e.getVelocity());
		lastSeen = e.getName();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
