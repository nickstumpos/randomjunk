package Buckingham.Team3;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot {
	private static double WALLDIST = 50;

	private class Body {
		public Body(double xForce, double yForce) {
			super();
			this.xForce = xForce;
			this.yForce = yForce;
		}

		private double xForce;
		private double yForce;

		public double getXForce() {
			return xForce;
		}

		public double getyForce() {
			return yForce;
		}

	}

	private static class Enemy {
		private List<Double> bearings = new ArrayList<Double>();
		private List<Double> xS = new ArrayList<Double>();
		private List<Double> yS = new ArrayList<Double>();
		private List<Double> energy = new ArrayList<Double>();
		private List<Double> heading = new ArrayList<Double>();
		private List<Double> distance = new ArrayList<Double>();
		private List<Double> velocity = new ArrayList<Double>();
		private double last;

		Enemy(double bearing, double energy, double distance, double heading, double velocity, double time, double x,
				double y) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.xS.add(x);
			this.yS.add(y);
			this.last = time;
		}

		public void addStats(double bearing, double energy, double distance, double heading, double velocity,
				double time, double x, double y) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.xS.add(x);
			this.yS.add(y);
			this.last = time;
		}

		public double findLastBearing() {
			return bearings.get(bearings.size() - 1);
		}

		public double findLastX() {
			return xS.get(xS.size() - 1);
		}

		public double findLastY() {
			return yS.get(yS.size() - 1);
		}

		public double findLastEnergy() {
			return energy.get(energy.size() - 1);
		}

		public double findLastHeading() {
			return heading.get(heading.size() - 1);
		}

		public double findLastDistance() {
			return distance.get(distance.size() - 1);
		}

		public double findLastVelocity() {
			return velocity.get(velocity.size() - 1);
		}

		public double getLastTimeSeen() {
			return this.last;
		}
	}

	private Map<String, Enemy> enemies = new HashMap<String, Enemy>();
	private Enemy currentTarget;
	
	private Enemy findCurrentEnemyToTarget() {
		if (!enemies.isEmpty()) {
			currentTarget = enemies.values().iterator().next();
		}
		return currentTarget;
	}

	private void doMove(boolean ignoreEnemies) {
		double xForce = 0;
		double yForce = 0;
		if (getBattleFieldWidth() - WALLDIST < getX()) {
			// force from closest point on GETy,WIDTH
			Body exertingBody = findForcesFromBody(getBattleFieldWidth(), getX(), getY(), getY());
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		} 
		if (0 + WALLDIST > getX()) {
			// force from closest point on getY,0
			Body exertingBody = findForcesFromBody(0, getX(), getY(), getY());
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		if (getBattleFieldHeight() - WALLDIST > getY()) {
			// force from closest point on getY,Height
			Body exertingBody = findForcesFromBody(getX(), getX(), getBattleFieldHeight(), getY());
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		} 
		if (0 + WALLDIST > getY()) {
			// force from closest point on getY,Width
			Body exertingBody = findForcesFromBody(getX(), getX(), 0, getY());
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		if (!ignoreEnemies) {
			for (Enemy e : enemies.values()) {
				Body exertingBody = findForcesFromBody(e.findLastX(), getX(), e.findLastY(), getY());
				xForce -= exertingBody.getXForce();
				yForce -= exertingBody.getyForce();
			}
		}
		double angle = Math.atan2(xForce, yForce);
		if (xForce == 0 && yForce == 0) {
			// If no force, do nothing
		} else {
			double absRads=angle - getHeadingRadians();
			double finalHeading = Utils.normalRelativeAngle(absRads);
			setTurnRightRadians(finalHeading);
			
			double interceptRadians=Math.PI/2-absRads;
			double dist;
			dist = computeDistanceToMove(absRads, interceptRadians);
			if (xForce == 0 && yForce == 0) {
			    // If no force, do nothing
			} else if(Math.abs(angle-getHeadingRadians())<Math.PI/2){
			    setAhead(dist-WALLDIST);
			} else {
			    setAhead(-1*(dist-WALLDIST));
			}
		}
	}

	private double computeDistanceToMove(double absRads, double interceptRadians) {
		double dist;
		if(0==absRads||absRads<Math.PI*2){
			dist=getBattleFieldWidth()-getX();
			
		}else if(0<absRads&&absRads<=Math.PI/4){
			dist = getBattleFieldWidth()-getX()/Math.sin(interceptRadians);
		}else if(Math.PI/4<absRads&&absRads<Math.PI/2){
			dist = getBattleFieldHeight()-getY()/Math.sin(interceptRadians);				
		}else if(absRads==Math.PI/2){
			dist = getBattleFieldHeight()-getY();
		}else if(Math.PI/2<absRads&&absRads<=3*Math.PI/4){
			dist = getBattleFieldHeight()-getY()/Math.sin(interceptRadians);
			
		}else if(3*Math.PI/4<absRads&&absRads<Math.PI){
			dist = getX()/Math.sin(interceptRadians);
			
		}else if(Math.PI==absRads){
			dist = getX();				
		}else if(Math.PI<absRads&&absRads<=5*Math.PI/4){
			dist = getX()/Math.sin(interceptRadians);
			
		}else if(5*Math.PI/4<absRads&&absRads<=3*Math.PI/2){
			dist = getY()/Math.sin(interceptRadians);
			
		}else if(3*Math.PI/2==absRads){
			dist = getY();				
		}else{
			dist = getY()/Math.sin(interceptRadians);
		}
		return dist;
	}

	private Body findForcesFromBody(double bodyX, double myX, double bodyY, double myY) {
		double absBearing = Utils.normalAbsoluteAngle(Math.atan2(bodyX - myX, bodyY - myY));
		double dist = distanceFromPoint(bodyX, myX, bodyY, myY);
		Body exertingBody = new Body(Math.sin(absBearing) / Math.pow(dist, 2),
				Math.cos(absBearing) / Math.pow(dist, 2));
		return exertingBody;
	}
	private double distanceFromPoint(double bodyX, double myX, double bodyY, double myY) {
		double ysquare = Math.pow(bodyY - myY, 2);
		double xsquare = Math.pow(bodyX - myX, 2);
		double dist = Math.sqrt(xsquare + ysquare);
		return dist;
	}

	double normalizeBearing(double angle) {
		while (angle > 180)
			angle -= 360;
		while (angle < -180)
			angle += 360;
		return angle;
	}

	/**
	 * run: NickStumpos's default behavior
	 */
	public void run() {

		setColors(Color.red, Color.blue, Color.green); // body,gun,radar

		// Robot main loop
		while (true) {
			Enemy e = findCurrentEnemyToTarget();
			if (e != null) {
				Double lastBearing = e.bearings.get(e.bearings.size() - 1);

				turnGunRight(normalizeBearing(getHeading() - getGunHeading() + lastBearing));
				while (getGunTurnRemaining() > 0) {
					execute();
				}
				fire(3);
				doMove(false);
			}
			turnRadarLeft(360);

			while (getDistanceRemaining() > 0 && getRadarTurnRemaining() > 0) {
				execute();
			}

		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();
		if (!enemies.containsKey(e.getName())) {

			enemies.put(e.getName(),
					new Enemy(e.getBearing(), e.getEnergy(), e.getDistance(), e.getHeading(), e.getVelocity(),
							getTime(), getX() + e.getDistance() * Math.sin(absBearing),
							getY() + e.getDistance() * Math.cos(absBearing)));
		} else {
			enemies.get(e.getName()).addStats(e.getBearing(), e.getEnergy(), e.getDistance(), e.getHeading(),
					e.getVelocity(), getTime(), getX() + e.getDistance() * Math.sin(absBearing),
					getY() + e.getDistance() * Math.cos(absBearing));
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		doMove(false);
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		doMove(true);
	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		super.onRobotDeath(e);
	}
}
