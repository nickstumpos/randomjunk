package Buckingham.Team3;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import robocode.AdvancedRobot;
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
	private Map<String, Enemy> enemies = new HashMap<String, NickStumpos.Enemy>();
	//private Map<Point2D, Double> pointsTried = new HashMap<Point2D, Double>();
	private Enemy targetEnemy;
	private Point2D.Double next;
	private Point2D.Double me;
	boolean hit = false;
	int moving = 0;
	private Rectangle reducedBattleField;
	private double hypot;
	//private Line2D crow;
	private Rectangle battleField;

	

	public void run() {
		
		setColors(new Color(24, 69, 59), Color.WHITE, new Color(24, 69, 59)); // #GOGREEN
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		battleField = new Rectangle(0, 0, (int)getBattleFieldWidth(), (int)getBattleFieldHeight());
		reducedBattleField = new Rectangle(battleField);
		reducedBattleField.grow(-STICK, -STICK);
		hypot=Math.sqrt(getBattleFieldHeight()*getBattleFieldHeight()+getBattleFieldWidth()*getBattleFieldWidth());
		me = new Point2D.Double(getX(), getY());
		next = me;
		do {		
			me = new Point2D.Double(getX(), getY());
			setupNext();
			engageEnemy();
			doMove();
			execute();

		} while (true);
	}

	private void setupNext() {
		if (moving >= MAX_MOVE || me.distance(next) <= STICK) {
			int i=100;
			double minForce = Double.POSITIVE_INFINITY;
			try {
				while(true){
					double maxRadius=Math.max(MAX_RAD/getOthers(),MIN_MAX_RAD);
					double randAngle=2*Math.PI*Math.random();
					double randPercentOfRad= Math.max(maxRadius/3,MIN_MIN_RAD)/maxRadius+Math.random()/2;					
					Point2D.Double randPoint = new Point2D.Double(me.x + maxRadius*randPercentOfRad*Math.sin(randAngle), me.y + maxRadius*randPercentOfRad*Math.cos(randAngle));					
					if (reducedBattleField.contains(randPoint)) {
						double force = 0/i--;
						Iterator<Enemy> eIter = this.enemies.values().iterator();
						try {
							while (true) {
								Enemy e = eIter.next();
								force += Math
										.abs(100
												* e.energy
												/ (randPoint.distanceSq(e
														.guestimatedLocationOfImpact(
																me.distance(randPoint) / 8,
																getTime()))));
							}
						} catch (Exception e) {
							//pointsTried.put(randPoint, force);
							if (force < minForce) {
								minForce=force;
								next = randPoint;
								//crow = new Line2D.Double(me, randPoint);
							}
						}
						
						
					}
				}
			} catch (ArithmeticException e) {
				moving = 0;
			}
			
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
		return shortestTurn(Utils.normalRelativeAngle(angleFromXwithMeAsOrigin(next) - getHeadingRadians()));
	}

	double getNextV(double turnRadians) {
		try{
			return Math.min((int) Math.floor(Math.abs(Math.PI / turnRadians)), 8);
		}catch(Exception e ){
			return 8;
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		Enemy scannedEnemy = enemies.get(e.getName());
		if(scannedEnemy==null){
			enemies.put(
					e.getName(),
					scannedEnemy=new Enemy());
		}
		scannedEnemy.updateStats(
					pointAtAngleFromMe(e.getDistance(),
							getHeadingRadians() + e.getBearingRadians()),e);
		
		if (targetEnemy == null
				|| threatLevel(scannedEnemy) > threatLevel(targetEnemy)) {
			targetEnemy = scannedEnemy;
			setTurnGunRightRadians(shortestTurn(Utils
					.normalRelativeAngle(targetEnemy.bearing
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
								Math.min(getEnergy()/10,hypot / me.distance(targetEnemy.location)
								),
							targetEnemy.energy / 4
							),3);
				if (aimGun(power) && 0== getGunHeat()						
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
		double distance = me.distance(targetEnemy.location);
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
		return e.energy
				/ e.location.distanceSq(me);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		targetEnemy = enemies.values().iterator().next();

	}

	private Point2D.Double pointAtAngleFromMe(double dist, double ang) {
		return new Point2D.Double(me.x + dist * Math.sin(ang), me.y + dist
				* Math.cos(ang));
	}

//	@Override
//	public void onPaint(Graphics2D g) {
//		g.setColor(Color.RED);
	//	g.fillRect((int) next.x - 10, (int) next.y - 10, 20, 20);
//		if (targetEnemy != null) {
//			g.fillRect((int) targetEnemy.getLocation().x - 10,
//					(int) targetEnemy.getLocation().y - 10, 20, 20);
//		}
//		
//		if(reducedBattleField!=null){
//			g.draw(reducedBattleField);
//		}if(battleField!=null){
//			g.draw(battleField);
//		}
//		if(crow!=null){
//			g.draw(crow);
//		}
		
//		for(Entry<Point2D, Double> p : pointsTried.entrySet()){
//			if (p.getValue()<50) {
//				g.fillRect((int) (p.getKey().getX() - p.getValue()), (int) (p
//						.getKey().getY() - p.getValue()),
//						(int) (p.getValue() * 2), (int) (p.getValue() * 2));
//			}
//		}
//		pointsTried.clear();
//		super.onPaint(g);
//	}
//	
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
		public Point2D.Double location;
		public double energy;
		private double velocity;
		private double heading;
		public double bearing;
		private long time;
		public void updateStats(
				Point2D.Double pos, ScannedRobotEvent e) {
			this.energy = e.getEnergy();
			this.heading = e.getHeadingRadians();
			this.location = pos;
			this.velocity = e.getVelocity();
			this.time = e.getTime();
			this.bearing = e.getBearingRadians();
		}
		public Point2D.Double guestimatedLocationOfImpact(double deltaT,double currentTime){
			deltaT=currentTime-time+deltaT;
			return new Point2D.Double(location.x + Math.sin(heading) * velocity *deltaT,location.y + Math.cos(heading) * velocity * deltaT);
		}
		
	}
}
