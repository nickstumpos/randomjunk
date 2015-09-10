package Buckingham.Team3;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.tools.JavaCompiler;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot {
	private static double WALL_WEIGHT = 10000;
	private static double ENEMY_WEIGHT = 200;
	private static double MAX_NO_SPIN = 100;
	private Random random = new Random();
	private double computeAbsoluteAngle(Point2D a, Point2D b) {
		return Utils.normalAbsoluteAngle(Math.atan2(a.getX() - b.getX(), a.getY() - b.getY()));
	}

	private double computeXforce(Point2D body, double weight, int distanceFactor) {
		return Math.sin(computeAbsoluteAngle(me, body)) * weight / Math.pow(body.distance(me), distanceFactor);
	}

	private double computeYforce(Point2D body, double weight, int distanceFactor) {
		return Math.cos(computeAbsoluteAngle(me, body)) * weight / Math.pow(body.distance(me), distanceFactor);
	}

	private Point2D me;

	private Map<String, Enemy> enemies = new HashMap<String, Enemy>();

	private Enemy targetEnemy;
	private int spincount = 0;

	private void doMove() {
		double theta = findTheta();
		lastAngle=theta;
		double relativeHeading = theta - getHeadingRadians();
		if (Utils.isNear(0, relativeHeading)) {
			if(Math.abs(relativeHeading)<Math.PI/2){
				setAhead(Double.POSITIVE_INFINITY);
			}else{
				setAhead(Double.NEGATIVE_INFINITY);
			}
		}else if(Math.abs(relativeHeading)<Math.PI/2){
		    setTurnRightRadians(Utils.normalRelativeAngle(relativeHeading));
		    setAhead(Double.POSITIVE_INFINITY);
		} else {
		    setTurnRightRadians(Utils.normalRelativeAngle(relativeHeading+Math.PI-getHeadingRadians()));
		    setAhead(Double.NEGATIVE_INFINITY);
		}
	}

	private double findTheta() {
		double xForce = 0;
		double yForce = 0;
		xForce += computeXforce(new Point2D.Double(getBattleFieldWidth(), getY()), WALL_WEIGHT, 3);
		xForce += computeXforce(new Point2D.Double(0, getY()), WALL_WEIGHT, 3);
		yForce += computeYforce(new Point2D.Double(getX(), getBattleFieldHeight()), WALL_WEIGHT, 3);
		yForce += computeYforce(new Point2D.Double(getX(), 0), WALL_WEIGHT, 3);
		for (Enemy e : enemies.values()) {
			xForce += computeXforce(e.getLoc(), findEnemyWeight(e), 2);
			yForce += computeYforce(e.getLoc(), findEnemyWeight(e), 2);
		}
			Point2D randomGravityWell = new Point2D.Double(getRandomX(), getRandomY());

			xForce += computeXforce(randomGravityWell, (random.nextInt() % 10), 2);
			yForce += computeYforce(randomGravityWell, (random.nextInt() % 10), 2);
		
		double theta = Math.atan2(xForce, yForce);
		return theta;
	}

	private double getRandomY() {
		return random.nextInt() % getBattleFieldHeight()/2;
	}

	private double getRandomX() {
		return random.nextInt() % getBattleFieldWidth()/2;
	}
		private double lastAngle;
 public void onPaint(Graphics2D g) {
	     // Set the paint color to a red half transparent color
	     g.setColor(Color.RED);
	 
	     g.drawLine((int)getX(),(int)getY(), (int)(500 * Math.cos(lastAngle)), (int)(500* Math.sin(lastAngle)));
	    
	 }
	private double findEnemyWeight(Enemy e) {
		return ENEMY_WEIGHT * e.getEnergy() / getEnergy();
	}

	double shortestTurn(double angle) {
		if (angle > Math.PI) {
			angle -= 2 * Math.PI;
		} else if (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	/**
	 * run: NickStumpos's default behavior
	 */
	public void run() {
		setColors(new Color(24, 69, 59), Color.WHITE, new Color(24, 69, 59)); // body,gun,radar
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		while (true) {
			
			setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();
		if (!enemies.containsKey(e.getName())) {
			enemies.put(e.getName(),
					new Enemy(e.getEnergy(), e.getHeadingRadians(),
							new Point2D.Double(+e.getDistance() * Math.sin(absBearing),
									getY() + e.getDistance() * Math.cos(absBearing)),
							e.getVelocity(), e.getTime(), e.getBearingRadians()));
		} else {
			enemies.get(e.getName()).updateStats(e.getEnergy(), e.getHeadingRadians(),
					new Point2D.Double(+e.getDistance() * Math.sin(absBearing),
							getY() + e.getDistance() * Math.cos(absBearing)),
					e.getVelocity(), e.getTime(), e.getBearingRadians());
		}
		if (targetEnemy == null) {
			targetEnemy = enemies.get(e.getName());
		} else if (threatLevel(enemies.get(e.getName())) > threatLevel(targetEnemy)) {
			targetEnemy = enemies.get(e.getName());
		}
		if (getOthers()<=enemies.keySet().size()) {
			me = new Point2D.Double(getX(), getY());
			doMove();
			engageEnemy();
		}
		execute();
	}

	private void engageEnemy() {
		setFire(Math.min(600 / me.distance(targetEnemy.getLoc()), 3));
		setTurnGunRightRadians(
				shortestTurn(Utils.normalRelativeAngle(targetEnemy.getBearing() + getHeadingRadians() - getGunHeadingRadians())));

		if (spincount < MAX_NO_SPIN) {
			spincount++;
			setTurnRadarRightRadians(
					shortestTurn(Utils.normalRelativeAngle(targetEnemy.getBearing() + getHeadingRadians() - getRadarHeadingRadians())
							* 2));
		} else {
			spincount = 0;
			turnRadarRightRadians(2 * Math.PI);
		}
	}
	  public void onWin(WinEvent e) {
		  stop();
			for (int i = 0; i < 50; i++) {
				turnRightRadians(Math.PI/4);
				turnLeftRadians(Math.PI/4);
			}
		}
	@Override
	public void onHitRobot(HitRobotEvent e) {
		targetEnemy = enemies.get(e.getName());
		if (e.getBearing() > -90 && e.getBearing() <= 90) {
			back(100);
		} else {
			ahead(100);
		}
		engageEnemy();
		execute();
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		if (e.getBearing() > -90 && e.getBearing() <= 90) {
			back(100);
		} else {
			ahead(100);
		}
	}
	private double threatLevel(Enemy e) {
		return (e.getEnergy() / getEnergy()) / e.getLoc().distanceSq(me);
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		targetEnemy = null;
		super.onRobotDeath(e);
	}

	private static class Enemy {
		private Point2D loc;
		private Double energy;
		private Double velocity;
		private Double heading;
		private Double bearing;
		private long time;

		Enemy(double energy, double heading, Point2D pos, Double velocity, long time, double bearing) {
			this.energy = energy;
			this.heading = heading;
			this.loc = pos;
			this.velocity = velocity;
			this.time = time;
			this.bearing = bearing;
		}

		public void updateStats(double energy, double heading, Point2D pos, Double velocity, long time,
				double bearing) {
			this.energy = energy;
			this.heading = heading;
			this.loc = pos;
			this.velocity = velocity;
			this.time = time;
			this.bearing = bearing;
		}

		public Point2D getLoc() {
			return loc;
		}

		public Double getVelocity() {
			return velocity;
		}

		public Double getEnergy() {
			return energy;
		}

		public Double getHeading() {
			return heading;
		}

		public long getTime() {
			return time;
		}

		public Double getBearing() {
			return bearing;
		}

	}
}
