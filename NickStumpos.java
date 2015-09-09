package Buckingham.Team3;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html
/**
 * NickStumpos - a robot by (your name here)
 */
public class NickStumpos extends AdvancedRobot {
	private static double WALL_WEIGHT = 3;
	private static double EAST = 3 * Math.PI / 2;;
	private static double NORTH_1 = Math.PI * 2;
	private static double NORTH_EAST = 7 * Math.PI / 4;;
	private static double NORTH = 0;
	private static double NORTH_WEST = Math.PI / 4;
	private static double WEST = Math.PI / 2;
	private static double SOUTH_WEST = 3 * Math.PI / 4;
	private static double SOUTH = Math.PI;
	private static double SOUTH_EAST = 5 * Math.PI / 4;

	private double computeAbsoluteAngle(Point2D a, Point2D b){
		return Utils.normalAbsoluteAngle(Math.atan2(a.getX()-b.getX(),
				a.getY()-b.getY()));
	}
	private double computeXforce(Point2D body,double weight){
		return Math.sin(computeAbsoluteAngle(me, body))*weight / body.distanceSq(me);
	}
	private double computeYforce(Point2D body,double weight){
		return Math.cos(computeAbsoluteAngle(me, body))*weight / body.distanceSq(me);
	}
	private Point2D me;
	
	
	private Map<String, Enemy> enemies = new HashMap<String, Enemy>();
	
	private Enemy targetEnemy;

	private void doMove() {
		double theta = findTheta();
		double relativeHeading = Utils.normalRelativeAngle(theta - getHeadingRadians());
		if (!Utils.isNear(0, relativeHeading)) {
			setTurnRightRadians(relativeHeading);
		}
		setAhead(Double.POSITIVE_INFINITY);
	}
	private double findTheta() {
		double xForce = 0;
		double yForce = 0;
		xForce += computeXforce(new Point2D.Double(getBattleFieldWidth(),getY()), WALL_WEIGHT);
		xForce += computeXforce(new Point2D.Double(0,getY()), WALL_WEIGHT);
		yForce += computeYforce(new Point2D.Double(getX(), getBattleFieldHeight()), WALL_WEIGHT);
		yForce += computeYforce(new Point2D.Double(getX(), 0), WALL_WEIGHT);
		for (Enemy e : enemies.values()) {									
			xForce +=  computeXforce(e.getLoc(), findEnemyWeight(e)) ;
			yForce +=  computeYforce(e.getLoc(), findEnemyWeight(e)) ;
		}
		Point2D randomGravityWell = new Point2D.Double(getRandomX(),getRandomY());	
		xForce +=  computeXforce(randomGravityWell, (Math.random()));
		yForce +=  computeXforce(randomGravityWell, (Math.random()));
		double theta = Math.atan2(xForce, yForce);
		return theta;
	}
	
	private double getRandomY() {
		return getBattleFieldHeight()-Math.random()%getBattleFieldHeight()/2;
	}
	private double getRandomX() {
		return getBattleFieldWidth()-Math.random()%(getBattleFieldWidth()/2);
	}

	private double findEnemyWeight(Enemy e) {
		return e.getEnergy()/getEnergy();
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
		setColors(Color.GREEN, Color.WHITE, Color.GRAY); // body,gun,radar
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		do {
			me=new Point2D.Double(getX(),getY());
			setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			doMove();
			if(Math.abs(getGunTurnRemaining()) < 10 && targetEnemy!=null && getEnergy() > 1) {
				double firePower = Math.min(Math.min(getEnergy()/6d, 1300d/me.distance(targetEnemy.loc)), targetEnemy.energy/3d);
				double bulletSpeed = 20 - firePower * 3;
				long time = (long)(me.distance(targetEnemy.getLoc()) / bulletSpeed);
				setFire( firePower );
				setTurnGunRightRadians(Utils.normalRelativeAngle(computeAbsoluteAngle(
						guestimateEnemyLocation(time), me) - getGunHeadingRadians()));
			}
			
			execute();
		} while (true);
	}
	private java.awt.geom.Point2D.Double guestimateEnemyLocation(long time) {
		return new Point2D.Double(targetEnemy.getLoc().getX()+Math.sin(Math.toRadians(targetEnemy.getHeading())) * targetEnemy.getVelocity() * time,
				targetEnemy.getLoc().getY()+Math.cos(Math.toRadians(targetEnemy.getHeading())) * targetEnemy.getVelocity() * time);
	}

	

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absBearing = e.getBearingRadians() + getHeadingRadians();
		if (!enemies.containsKey(e.getName())) {
			enemies.put(e.getName(),
					new Enemy( e.getEnergy(),  e.getHeadingRadians(), new Point2D.Double(+e.getDistance() * Math.sin(absBearing),
									getY() + e.getDistance() * Math.cos(absBearing)),e.getVelocity()));
		} else {
			enemies.get(e.getName()).updateStats(e.getEnergy(),  e.getHeadingRadians(), new Point2D.Double(+e.getDistance() * Math.sin(absBearing),
					getY() + e.getDistance() * Math.cos(absBearing)),e.getVelocity());
		}
		if (targetEnemy == null) {
			targetEnemy = enemies.get(e.getName());
		} else if (threatLevel(enemies.get(e.getName())) > threatLevel(targetEnemy)) {
			targetEnemy = enemies.get(e.getName());
		}
		setTurnRadarRightRadians(Utils.normalRelativeAngle(targetEnemy.getHeading() + getHeadingRadians() - getRadarHeadingRadians()) * 2);

	}
	private double threatLevel(Enemy e) {
		return (getEnergy()/e.getEnergy())/e.getLoc().distanceSq(me);
	}

	

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		targetEnemy=null;
		super.onRobotDeath(e);
	}
	private static class Enemy {
		private Point2D  loc;
		private Double energy;
		

		private Double velocity;
		private Double heading;	


		Enemy( double energy, double heading, Point2D pos,Double velocity) {
			this.energy=energy;
			this.heading=heading;
			this.loc=pos;
			this.velocity=velocity;
		}

		public void updateStats(double energy, double heading, Point2D pos,Double velocity) {
			this.energy=energy;
			this.heading=heading;
			this.loc=pos;
			this.velocity=velocity;
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

		
	}
}
