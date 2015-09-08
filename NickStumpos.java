package Buckingham.Team3;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot {
	private static double WALLDIST = 100;

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
		private List<Point2D> locs = new ArrayList<Point2D>();
		private List<Double> energy = new ArrayList<Double>();
		private List<Double> heading = new ArrayList<Double>();
		private List<Double> distance = new ArrayList<Double>();
		private List<Double> velocity = new ArrayList<Double>();
		private long last;

		Enemy(double bearing, double energy, double distance, double heading,
				double velocity, long time, Point2D pos) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.locs.add(pos);
			this.last = time;
		}

		public void addStats(double bearing, double energy, double distance,
				double heading, double velocity, long time, Point2D pos) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.locs.add(pos);
			this.last = time;
		}



		public Point2D findLastPos() {
			return locs.get(locs.size() - 1);
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

		public long getLastTimeSeen() {
			return this.last;
		}
	}

	private Map<String, Enemy> enemies = new HashMap<String, Enemy>();
	private Enemy currentTarget;

	private void doMove() {
		double xForce = 0;
		double yForce = 0;
		Point2D me = new Point2D.Double(getX(),getY());
		if (getBattleFieldWidth() - WALLDIST < getX()) {
			// force from closest point on GETy,WIDTH
			Body exertingBody = findForcesFromBody(new Point2D.Double(getBattleFieldWidth(),getY()), me);
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		if (0 + WALLDIST > getX()) {
			// force from closest point on getY,0
			Body exertingBody = findForcesFromBody(new Point2D.Double(0, getY()), me);
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		if (getBattleFieldHeight() - WALLDIST < getY()) {
			// force from closest point on getY,Height
			Body exertingBody = findForcesFromBody(new Point2D.Double(getX(),getBattleFieldHeight()), me);
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		if (0 + WALLDIST > getY()) {
			// force from closest point on getY,Width
			Body exertingBody = findForcesFromBody(new Point2D.Double(getX(),0), me);
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		for (Enemy e : enemies.values()) {
			Body exertingBody = findForcesFromBody(e.findLastPos(), me);
			xForce -= exertingBody.getXForce();
			yForce -= exertingBody.getyForce();
		}
		
		double theta = Math.atan2(xForce, yForce);
		if (xForce == 0 && yForce == 0) {
			// If no force, do nothing
		} else {
			double relativeHeading = Utils.normalRelativeAngle(theta - getHeadingRadians());
			if (!Utils.isNear(0, relativeHeading)) {
				turnRightRadians(relativeHeading);
			}
			double dist;
			boolean goingForward = Math.abs(theta - getHeadingRadians()) < Math.PI / 2;
			dist = computeDistanceToMove(theta,goingForward);
			if (xForce == 0 && yForce == 0) {
				System.out.println("Not Moving");
			} else if (goingForward) {
				ahead(dist - WALLDIST);
				System.out.println("Moving: "+(dist - WALLDIST));
				
			} else {
				ahead(-1 * (dist - WALLDIST));
				System.out.println("Moving: "+(-1 * (dist - WALLDIST)));
			}
		}
		while(true){
			double distremaining =getDistanceRemaining();
			execute();
			if(distremaining==0)break;
		}
		
	}

	private double computeDistanceToMove(double theta) {
		double dist;
		theta=Math.abs(Utils.normalAbsoluteAngle(theta));
		if (Utils.isNear(0,theta) || Utils.isNear(theta, Math.PI * 2) ) {
			dist = getBattleFieldWidth() - getX();
		} else if (Utils.isNear(Math.PI/2,theta)) {
			dist =getBattleFieldHeight()-getY();
		}  else if (Utils.isNear(Math.PI, theta)) {
			dist = getX();
		} else if (Utils.isNear(3*Math.PI/2, theta)) {
			dist = getY();
		} else if (0 < theta && theta <= Math.PI / 4) {
			dist = (getBattleFieldWidth() - getX()) / Math.sin(Math.PI / 2 - theta);
		} else if (Math.PI / 4 < theta && theta < Math.PI / 2) {
			dist = (getBattleFieldWidth() - getX()) / Math.sin(theta);
		} else if (Math.PI / 2 < theta && theta <= 2*Math.PI / 3) {
			dist = (getX()) / Math.sin(theta);
		} else if (2*Math.PI / 3 < theta && theta < Math.PI) {
			dist = (getX()) / Math.sin(theta);
		} else if (Math.PI  < theta && theta < 5*Math.PI/4) {
			dist = (getX()) / Math.sin(theta);
		} 
		return Math.abs(dist);
	}

	private Body findForcesFromBody(Point2D me, Point2D body) {
		double deltaX = body.getX() - me.getX();
		double deltaY = body.getY() - me.getY();		
		double absoluteAngle = Utils.normalAbsoluteAngle(Math.atan2(deltaX,deltaY));
		double distSq = me.distanceSq(body);
		Body exertingBody = new Body(Math.sin(absoluteAngle) / distSq, Math.cos(absoluteAngle) / distSq);
		return exertingBody;
	}

	int direction = 1;
	
	/**
	 * run: NickStumpos's default behavior
	 */
	public void run() {
		setColors(Color.red, Color.blue, Color.green); // body,gun,radar
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		do {
			turnRadarRightRadians(Double.POSITIVE_INFINITY); 
		} while (true);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();
		if (!enemies.containsKey(e.getName())) {
			enemies.put(
					e.getName(),
					new Enemy(e.getBearingRadians(), e.getEnergy(), e.getDistance(), e
							.getHeading(), e.getVelocity(), getTime(), new Point2D.Double(
							+ e.getDistance() * Math.sin(absBearing), getY()
							+ e.getDistance() * Math.cos(absBearing))));
		} else {
			enemies.get(e.getName()).addStats(e.getBearingRadians(), e.getEnergy(),
					e.getDistance(), e.getHeading(), e.getVelocity(),
					getTime(), new Point2D.Double(getX() + e.getDistance() * Math.sin(absBearing),
					getY() + e.getDistance() * Math.cos(absBearing)));
		}
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()) * 2);
		doMove();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		doMove();
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		doMove();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		super.onRobotDeath(e);
	}
}
