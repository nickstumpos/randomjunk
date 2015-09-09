package Buckingham.Team3;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot {
	private static double WALLDIST = 100;
	private static double EAST = 3 * Math.PI / 2;;
	private static double NORTH_1 = Math.PI * 2;
	private static double NORTH_EAST = 7 * Math.PI / 4;;
	private static double NORTH = 0;
	private static double NORTH_WEST = Math.PI / 4;
	private static double WEST = Math.PI / 2;
	private static double SOUTH_WEST = 3 * Math.PI / 4;
	private static double SOUTH = Math.PI;
	private static double SOUTH_EAST = 5 * Math.PI / 4;

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
		private Map<Point2D,ArrayList<Integer>> locPos= new HashMap();
		private long last;

		public Point2D.Double guessPosition(long when) {
			double diff = when - last;
			double newY = findLastPos().getY();
			double newX = findLastPos().getY();
			if(locPos.get(findLastPos()).get(0)+diff<locs.size()){
				Integer pos = locPos.get(findLastPos()).get(0);
				newY=locs.get((int) (pos+diff)).getY();
				newX=locs.get((int) (pos+diff)).getX();
			}else{
				newY=findLastPos().getY() + Math.cos(findLastHeading()) * findLastVelocity() * diff;
				newX=findLastPos().getX() + Math.sin(findLastHeading()) * findLastVelocity() * diff;
			}

			return new Point2D.Double(newX, newY);
		}

		Enemy(double bearing, double energy, double distance, double heading, double velocity, long time, Point2D pos) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.locs.add(pos);
			locPos.put(pos, new ArrayList<Integer>());
			locPos.get(pos).add(locs.size()-1);
			this.last = time;
		}

		public void addStats(double bearing, double energy, double distance, double heading, double velocity, long time,
				Point2D pos) {
			this.bearings.add(bearing);
			this.energy.add(energy);
			this.heading.add(heading);
			this.distance.add(distance);
			this.velocity.add(velocity);
			this.locs.add(pos);
			if(!locPos.containsKey(pos)){
				locPos.put(pos, new ArrayList<Integer>());
			}
			locPos.get(pos).add(locs.size()-1);
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
	public double getRange(double x1, double y1, double x2, double y2) {
	    double xo = x2 - x1;
	    double yo = y2 - y1;
	    return Math.sqrt(xo * xo + yo * yo);
	  }
	private Map<String, Enemy> enemies = new HashMap<String, Enemy>();
	private Enemy targetEnemy;

	private void doMove() {
		double xForce = 0;
		double yForce = 0;
		Point2D me = new Point2D.Double(getX(), getY());
		// force from closest point on GETy,WIDTH
		xForce += 5000 / Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
		xForce -= 5000 / Math.pow(getRange(getX(), getY(), 0, getY()), 3);
		yForce += 5000 / Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
		yForce -= 5000 / Math.pow(getRange(getX(), getY(), getX(), 0), 3);
		
		for (Enemy e : enemies.values()) {
			Body exertingBody = findForcesFromBody(e.findLastPos(), me,1000);
			xForce +=  exertingBody.getXForce();
			yForce +=  exertingBody.getyForce();
		}
		double theta = Math.atan2(xForce, yForce);
		if (xForce == 0 && yForce == 0) {
			// If no force, do nothing
		} else {
			double relativeHeading = Utils.normalRelativeAngle(theta - getHeadingRadians());
			if (!Utils.isNear(0, relativeHeading)) {
				setTurnRightRadians(relativeHeading);
			}
			double dist=Double.POSITIVE_INFINITY;
			//dist = computeDistanceToMove(theta);
			if (dist - WALLDIST < WALLDIST) {
				setAhead(dist);
			} else {
				setBack(dist);
			}

		}

	}

	void setupMoveGun(double firePower) {
		if (targetEnemy != null) {
			long time = getTime() + (int) Math.round(
					(new Point2D.Double(getX(), getY()).distance(targetEnemy.findLastPos()) / (20 - (3 * firePower))));
			Point2D.Double p = targetEnemy.guessPosition(time);

			// offsets the gun by the angle to the next shot based on linear
			// targeting
			double gunOffset = getGunHeadingRadians() - (Math.PI / 2 - Math.atan2(p.y - getY(), p.x - getX()));
			setTurnGunLeftRadians(normaliseBearing(gunOffset));
		}
	}

	double normaliseBearing(double angle) {
		double newAngle = angle;
		if (angle > Math.PI) {
			newAngle -= 2 * Math.PI;
		} else if (angle < -Math.PI) {
			newAngle += 2 * Math.PI;
		}
		return newAngle;
	}

//	private double computeDistanceToMove(double theta) {
//		double dist = 0;
//		theta = Math.abs(Utils.normalAbsoluteAngle(theta));
//		if (Utils.isNear(EAST, theta)) {
//			dist = getBattleFieldWidth() - getX();
//		} else if (Utils.isNear(NORTH, theta) || Utils.isNear(NORTH_1, theta)) {
//			dist = getBattleFieldHeight() - getY();
//		} else if (Utils.isNear(WEST, theta)) {
//			dist = getX();
//		} else if (Utils.isNear(SOUTH, theta)) {
//			dist = getY();
//		} else if (NORTH < theta && theta <= NORTH_WEST) {
//			dist = (getBattleFieldHeight() - getY()) / Math.sin(Math.PI / 2 - theta);
//		} else if (NORTH_WEST < theta && theta < WEST) {
//			dist = getX() / Math.sin(theta);
//		} else if (WEST < theta && theta <= SOUTH_WEST) {
//			dist = getX() / Math.sin(Math.PI - theta);
//		} else if (SOUTH_WEST < theta && theta < SOUTH) {
//			dist = (getY()) / Math.sin(-1 * Math.PI / 2 + theta);
//		} else if (SOUTH < theta && theta < SOUTH_EAST) {
//			dist = (getY()) / Math.sin(3 * Math.PI / 2 - theta);
//		} else if (SOUTH_EAST < theta && theta < EAST) {
//			dist = (getBattleFieldWidth() - getX()) / Math.sin(-Math.PI + theta);
//		} else if (EAST < theta && theta < NORTH_EAST) {
//			dist = (getBattleFieldWidth() - getX()) / Math.sin(2 * Math.PI - theta);
//		} else if (NORTH_EAST < theta && theta < NORTH_1) {
//			dist = (getBattleFieldHeight() - getY()) / Math.sin(-3 * Math.PI / 2 + theta);
//		}
//		return Math.abs(dist);
//	}

	private Body findForcesFromBody(Point2D me, Point2D body, double weight) {
		double deltaX = me.getX() - body.getX();
		double deltaY = me.getY() - body.getY();
		double absoluteAngle = Utils.normalAbsoluteAngle(Math.atan2(deltaX, deltaY));
		double distSq = body.distanceSq(me);
		Body exertingBody = new Body(Math.sin(absoluteAngle)*weight / distSq, Math.cos(absoluteAngle)*weight / distSq);
		return exertingBody;
	}

	int direction = 1;

	/**
	 * run: NickStumpos's default behavior
	 */
	public void run() {
		setColors(Color.red, Color.blue, Color.green); // body,gun,radar
		setAdjustRadarForGunTurn(true);

		do {
			setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			doMove();
			double firePower = getFirePower();
			setupMoveGun(firePower);
			if ( getGunHeat()==0) {
				setFire(firePower);
			}
			execute();
		} while (true);
	}

	private double getFirePower() {
		double firePower = 1;
		if (targetEnemy != null) {
			firePower = 400 / targetEnemy.findLastDistance();
		}
		if (firePower > 3) {
			firePower = 3;
		}
		return firePower;
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();
		if (!enemies.containsKey(e.getName())) {
			enemies.put(e.getName(),
					new Enemy(e.getBearingRadians(), e.getEnergy(), e.getDistance(), e.getHeading(), e.getVelocity(),
							getTime(), new Point2D.Double(+e.getDistance() * Math.sin(absBearing),
									getY() + e.getDistance() * Math.cos(absBearing))));
		} else {
			enemies.get(e.getName()).addStats(e.getBearingRadians(), e.getEnergy(), e.getDistance(), e.getHeading(),
					e.getVelocity(), getTime(), new Point2D.Double(getX() + e.getDistance() * Math.sin(absBearing),
							getY() + e.getDistance() * Math.cos(absBearing)));
		}
		if (targetEnemy == null) {
			targetEnemy = enemies.get(e.getName());
		} else if (enemies.get(e.getName()).findLastDistance() < targetEnemy.findLastDistance()) {
			targetEnemy = enemies.get(e.getName());
		}
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians()) * 2);

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
		targetEnemy=null;
		super.onRobotDeath(e);
	}
}
