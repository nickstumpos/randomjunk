package Buckingham.Team3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BulletMissedEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.util.Utils;

public class NickStumpos extends AdvancedRobot {
	private static final int MAX_RAD = 600;
	private static final int MIN_MIN_RAD = 50;
	private static final int MIN_MAX_RAD = 150;
	private static final int MAX_MOVE = 100;
	private static int STICK = 25;
	private double minRad = 100;
	private double maxRad = 300;
	private Map<String, Enemy> enemies = new HashMap<String, NickStumpos.Enemy>();
	private Map<Point2D, Double> pointsTried = new HashMap<Point2D, Double>();
	private Enemy targetEnemy;
	private Point2D.Double next;
	private Point2D.Double me;
	boolean hit = false;
	int moving = 0;
	private static Random rand = new Random();
	private Ellipse2D maxCircle;
	private Rectangle reducedBattleField;
	private Ellipse2D minCircle;
	private double hypot;
	private Line2D crow;
	private Rectangle battleField;

	private Point2D.Double getNext() {
		Point2D.Double point = new Point2D.Double(getX(), getY());
		maxCircle = new Ellipse2D.Double( me.x - maxRad, me.y - maxRad, maxRad*2, maxRad*2);
		minCircle = new Ellipse2D.Double( me.x - minRad, me.y - minRad, minRad*2, minRad*2);
		pointsTried.clear();
		int i;
		double minForce = Double.POSITIVE_INFINITY;
		for (i = 0; i < 300; i++) {
			Point2D.Double randPoint = new Point2D.Double(maxCircle.getMinX()
					+ Math.abs(rand.nextInt() % maxCircle.getWidth()), maxCircle.getMinY()
					+ Math.abs(rand.nextInt() % maxCircle.getHeight()));
			
			
			
			if (reducedBattleField.contains(randPoint) && maxCircle.contains(randPoint) && !minCircle.contains(randPoint)) {
				double force = 0;

				for (Enemy e : this.enemies.values()) {
					
					force += Math.abs(100*e.getEnergy()
							/ (randPoint.distanceSq(e.guestimatedLocationOfImpact(me.distance(randPoint)/8, getTime()))));
				}
				pointsTried.put(randPoint,force);
				if (force < minForce) {
					minForce=force;
					System.out.println(minForce);
					point = randPoint;
					crow = new Line2D.Double(me, randPoint);
				}
			}
		}
		System.out.println(i);
		
		return point;
	}

	public void run() {
		
		setColors(new Color(24, 69, 59), Color.WHITE, new Color(24, 69, 59)); // #GOGREEN
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		battleField = new Rectangle(0, 0, (int)getBattleFieldWidth(), (int)getBattleFieldHeight());
		reducedBattleField = new Rectangle((int)STICK, STICK, (int)getBattleFieldWidth()-STICK*2, (int)getBattleFieldHeight()-STICK*2);
		hypot=Math.sqrt(getBattleFieldHeight()*getBattleFieldHeight()+getBattleFieldWidth()*getBattleFieldWidth());
		me = new Point2D.Double(getX(), getY());
		next = me;
		do {
			maxRad=Math.max(MAX_RAD/getOthers(),MIN_MAX_RAD);
			minRad=Math.max(maxRad/3,MIN_MIN_RAD);
			
			me = new Point2D.Double(getX(), getY());
			setupNext();
			engageEnemy();
			doMove();
			execute();

		} while (true);
	}

	private void setupNext() {
		if (moving >= MAX_MOVE || me.distance(next) <= STICK) {
			next = getNext();
			moving = 0;
		}
	}

	private void doMove() {
		setAhead(Double.POSITIVE_INFINITY);
		double turnRadians = getTurnToNext();
		setTurnRightRadians(turnRadians);
		setMaxVelocity(getNextV(turnRadians));
		moving++;
	}

	private double getTurnToNext() {
		return getTurnToPoint(next);
	}

	private double getTurnToPoint(Point2D.Double point){
		return shortestTurn(Utils.normalRelativeAngle(angleFromXwithMeAsOrigin(point) - getHeadingRadians()));
	}

	double getNextV(double turnRadians) {
		double ret = 0;
		if (Utils.isNear(0, turnRadians)) {
			ret = 8;
		} else {
			ret = Math
					.min((int) Math.floor(Math.abs(Math.PI / turnRadians)), 8);
		}
		return ret;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (!enemies.containsKey(e.getName())) {
			enemies.put(
					e.getName(),
					new Enemy(
							e.getEnergy(),
							e.getHeadingRadians(),
							pointAtAngleFromMe(e.getDistance(),
									getHeadingRadians() + e.getBearingRadians()),
							e.getVelocity(), e.getTime(), e.getBearingRadians()));
		} else {
			enemies.get(e.getName()).updateStats(
					e.getEnergy(),
					e.getHeadingRadians(),
					pointAtAngleFromMe(e.getDistance(),
							getHeadingRadians() + e.getBearingRadians()),
					e.getVelocity(), e.getTime(), e.getBearingRadians());
		}
		if (targetEnemy == null
				|| threatLevel(enemies.get(e.getName())) > threatLevel(targetEnemy)) {
			targetEnemy = enemies.get(e.getName());
			setTurnGunRightRadians(shortestTurn(Utils
					.normalRelativeAngle(targetEnemy.getBearing()
							+ getHeadingRadians() - getGunHeadingRadians())));
		}
		if(getOthers()==1){
			setTurnRadarRightRadians(Utils.normalRelativeAngle(getHeadingRadians() + e.getBearingRadians()- getRadarHeadingRadians()));
		}
	}

	private void engageEnemy() {
		if (targetEnemy != null) {
			if (getEnergy() > 1) {
				double power = Math.min(
						Math.min(
								Math.min(getEnergy()/10,hypot / me.distance(targetEnemy.getLocation())
								),
							targetEnemy.energy / 4
							),3);
				if (aimGun(power) && Utils.isNear(0, getGunHeat())						
						&& Math.abs(getGunTurnRemainingRadians())<Math.PI/16) {
					setFire(power);
				}
	
			}
			
		}
	}
	public void onWin(WinEvent e) {
		stop();
		for (int i = 0; i < 50; i++) {
			turnRightRadians(Math.PI / 4);
			turnLeftRadians(Math.PI / 4);
		}
	}
	private boolean aimGun(double power) {
		double distance = me.distance(targetEnemy.getLocation());
		double speed=(20 - power * 3);
		long time = (long) Math.ceil(distance/speed);
		Point2D.Double locationGuess=targetEnemy.guestimatedLocationOfImpact(time,getTime());
		
		
		setTurnGunRightRadians(
				shortestTurn(
						Utils.normalRelativeAngle(
								angleFromXwithMeAsOrigin(locationGuess) - getGunHeadingRadians())));
		return battleField.contains(locationGuess);
	}

	private double threatLevel(Enemy e) {
		return e.getEnergy()
				/ e.getLocation().distanceSq(me);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		targetEnemy = enemies.values().iterator().next();

	}

	private Point2D.Double pointAtAngleFromMe(double dist, double ang) {
		return new Point2D.Double(me.x + dist * Math.sin(ang), me.y + dist
				* Math.cos(ang));
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(Color.RED);
	//	g.fillRect((int) next.x - 10, (int) next.y - 10, 20, 20);
		if (targetEnemy != null) {
			g.fillRect((int) targetEnemy.getLocation().x - 10,
					(int) targetEnemy.getLocation().y - 10, 20, 20);
		}
		if(maxCircle!=null){
			g.draw(maxCircle);
		}
		if(minCircle!=null){
			g.draw(minCircle);
		}
		if(reducedBattleField!=null){
			g.draw(reducedBattleField);
		}
		if(crow!=null){
			g.draw(crow);
		}
		
		for(Entry<Point2D, Double> p : pointsTried.entrySet()){
			if (p.getValue()<50) {
				g.fillRect((int) (p.getKey().getX() - p.getValue()), (int) (p
						.getKey().getY() - p.getValue()),
						(int) (p.getValue() * 2), (int) (p.getValue() * 2));
			}
		}
		super.onPaint(g);
	}
	
	private double angleFromXwithMeAsOrigin(Point2D.Double p) {
		return Math.atan2(p.x - me.x, p.y - me.y);
	}

	double shortestTurn(double angle) {
		if (angle > Math.PI) {
			angle -= 2 * Math.PI;
		} else if (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	private static class Enemy {
		private Point2D.Double loc;
		private double energy;
		private double velocity;
		private double heading;
		private double bearing;
		private long time;

		Enemy(double energy, double heading, Point2D.Double pos,
				double velocity, long time, double bearing) {
			this.energy = energy;
			this.heading = heading;
			this.loc = pos;
			this.velocity = velocity;
			this.time = time;
			this.bearing = bearing;
			
		}

		public void updateStats(double energy, double heading,
				Point2D.Double pos, Double velocity, long time, double bearing) {
			this.energy = energy;
			this.heading = heading;
			this.loc = pos;
			this.velocity = velocity;
			this.time = time;
			this.bearing = bearing;
		}

		public Point2D.Double getLocation() {
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

		public Point2D.Double guestimatedLocationOfImpact(double deltaT,double currentTime){
			deltaT=currentTime-time+deltaT;
			return new Point2D.Double(loc.x + Math.sin(heading) * velocity *deltaT,loc.y + Math.cos(heading) * velocity * deltaT);
		}
		
	}
}
