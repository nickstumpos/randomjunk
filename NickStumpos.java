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
/***********
 * Robot that uses some random anti gravity and basic predictive targeting
 * trying to keep it under 1500 bytes. eventually lower after i see where i can simplify
 * @author o254802
 *
 */
public class NickStumpos extends AdvancedRobot {
	private static final int MAX_RAD = 600;
	private static final int MIN_MIN_RAD = 50;
	private static final int MIN_MAX_RAD = 150;
	private static final int MAX_MOVE = 100;
	private static int STICK = 25;
	private Map<String, Enemy> enemies = new HashMap<String, NickStumpos.Enemy>();
	// private Map<Point2D, Double> pointsTried = new HashMap<Point2D,
	// Double>();
	private Enemy targetEnemy;
	private Point2D.Double next;
	private Point2D.Double me;
	boolean hit = false;
	int moving = 0;
	private Rectangle reducedBattleField;
	private double hypot;
	// private Line2D crow;
	private Rectangle battleField;

	public void run() {

		setColors(new Color(24, 69, 59), Color.WHITE, new Color(24, 69, 59)); // #GOGREEN
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		battleField = new Rectangle(0, 0, (int) getBattleFieldWidth(),
				(int) getBattleFieldHeight());
		reducedBattleField = new Rectangle(battleField);
		reducedBattleField.grow(-STICK, -STICK);
		hypot = Math.sqrt(getBattleFieldHeight() * getBattleFieldHeight()
				+ getBattleFieldWidth() * getBattleFieldWidth());
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
			int i = 100;
			double minForce = Double.POSITIVE_INFINITY;
			try {
				while (true) {
					//pick a random point in a donut surrounding me.
					//donut gets bigger as number of other robots reduced
					//doing a random sample to make the robot harder to predict and not just go back and forth
					double maxRadius = Math.max(MAX_RAD / getOthers(),
							MIN_MAX_RAD);
					double randAngle = 2 * Math.PI * Math.random();
					double randPercentOfRad = Math.max(maxRadius / 3,
							MIN_MIN_RAD) / maxRadius + Math.random() / 2;
					Point2D.Double randPoint = new Point2D.Double(me.x
							+ maxRadius * randPercentOfRad
							* Math.sin(randAngle), me.y + maxRadius
							* randPercentOfRad * Math.cos(randAngle));
					if (reducedBattleField.contains(randPoint)) {
						double force = 0 / i--;
						Iterator<Enemy> eIter = this.enemies.values()
								.iterator();
						try {
							//stay away from threats
							while (true) {
								Enemy e = eIter.next();
								force += Math
										.abs(100
												* e.e.getEnergy()
												/ (randPoint.distanceSq(e
														.guestimatedLocationOfImpact(
																me.distance(randPoint) / 8,
																getTime()))));
							}
						} catch (Exception e) {
							//went through all enemies. this way is less bytes to stay under 1500
							// pointsTried.put(randPoint, force);
							if (force < minForce) {
								minForce = force;
								next = randPoint;
								// crow = new Line2D.Double(me, randPoint);
							}
						}

					}
				}
			} catch (ArithmeticException e) {
				//division by 0 = 100 random points. this way is less bytes to stay under 1500
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
		return shortestTurn(angleFromXwithMeAsOrigin(next)
						- getHeadingRadians());
	}

	double getNextV(double turnRadians) {
		try {
			return Math.min((int) Math.floor(Math.abs(Math.PI / turnRadians)),
					8);
		} catch (Exception e) {
			return 8;
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		Enemy scannedEnemy = enemies.get(e.getName());
		if (scannedEnemy == null) {
			enemies.put(e.getName(), scannedEnemy = new Enemy());
		}
		scannedEnemy.updateStats(
				pointAtAngleFromMe(e.getDistance(),
						getHeadingRadians() + e.getBearingRadians()), e);

		if (targetEnemy == null
				|| threatLevel(scannedEnemy) > threatLevel(targetEnemy)) {
			targetEnemy = scannedEnemy;
			engageEnemy();
		}
		if (getOthers() == 1) {
			setTurnRadarRightRadians(Utils
					.normalRelativeAngle(getHeadingRadians()
							+ e.getBearingRadians() - getRadarHeadingRadians()));
		}
	}

	private void engageEnemy() {
		if (targetEnemy != null) {
			if (getEnergy() > 1) {
				double power = Math.min(Math.min(
						Math.min(getEnergy() / 10,
								hypot / me.distance(targetEnemy.location)),
						targetEnemy.e.getEnergy() / 4), 3);
				if (aimGun(power)
						&& 0 == getGunHeat()
						&& Math.abs(getGunTurnRemainingRadians()) < Math.PI / 16) {
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
		//bullet speed
		double speed = (20 - power * 3);
		//target X/Y velocity robocode is rotated 90
		double tXV =Math.sin(targetEnemy.e.getHeadingRadians()) * targetEnemy.e.getVelocity();
		double tYV =Math.cos(targetEnemy.e.getHeadingRadians()) * targetEnemy.e.getVelocity();
		//bullet B distance is a circle expanding at its velocity 
		//pow(B.X-me.X,2) + pow(B.Y-me.Y,2) = pow (t*B.speed)
		//target is the same 
		//target.X == t * tXV + target.X
		//target.Y == t * tYV + target.Y
		//if bullet has traveled the same distance from me as the target is from me at a given time. Bam
		//pow (t*B.speed) = pow(t * tXV + target.X - me.X,2) + pow(t * tYV + target.Y - me.Y,2)
		//set it equal to zero 
		//0 = pow(t * tXV + target.X - me.X,2)+ pow(t * tXY + target.Y - me.Y,2)- pow(t * B.speed,2)
		//0=(t*tXV+target.X-me.X)*(t*tXV+target.X-me.X)+(t * tXY + target.Y - me.Y)*(t * tXY + target.Y - me.Y) - (t * B.speed)*(t * B.speed)
		//(/1)=t t^2*(tXV^2)+t*(tXV*target.X)-t(*tXV*me.X) +t*(target.X*tXV) +target.X^2-target.X*me.X-t*(me.X*tXV)-me.X*target.X+me.X^2
		//(/2)=t t^2*(tYV^2)+t*(tYV*target.Y)-t(*tYV*me.Y) +t*(target.Y*tYV) +target.Y^2-target.Y*me.X-t*(me.X*tYV)-me.Y*target.Y+me.Y^2
		//(/3)=t^2*B.speed^2
		//at^2+bt+c=0 = t^2 * (tXv^2+tYV^2-B.speed^2)+t(2tXV(target.X-me.X)+2tYV(target.Y-me.Y))+(target.X-me.X)^2(target.Y-me.Y)^2
		double a = tXV*tXV + tYV*tYV - speed*speed;
		double b=2 * (tXV * (targetEnemy.location.x - me.x) + tYV * (targetEnemy.location.y - me.y));
		double c = (targetEnemy.location.x-me.x)*(targetEnemy.location.x-me.x) + (targetEnemy.location.y-me.y)*(targetEnemy.location.y-me.y);
		double timeA=(-b+ Math.sqrt(b*b-4*a*c) )/(2*a);
		double timeB=(-b- Math.sqrt(b*b-4*a*c) )/(2*a);
		double time=Double.NaN;
		if(!Double.isNaN(timeA) && timeA>0){
			time= timeA;
		}else if(!Double.isNaN(timeB) && timeB>0){
			time=timeB;
		}
		Point2D.Double locationGuess=targetEnemy.guestimatedLocationOfImpact(time,getTime());	
		setTurnGunRightRadians(
				shortestTurn(
						Utils.normalRelativeAngle(
								angleFromXwithMeAsOrigin(locationGuess) - getGunHeadingRadians())));
		return battleField.contains(locationGuess);

	}

	private double threatLevel(Enemy e) {
		//return e.e.getEnergy() / e.location.distanceSq(me);
		return e.location.distance(me);
	}

	public void onRobotDeath(RobotDeathEvent e) {
		enemies.remove(e.getName());
		targetEnemy = enemies.values().iterator().next();

	}

	private Point2D.Double pointAtAngleFromMe(double dist, double ang) {
		return new Point2D.Double(me.x + dist * Math.sin(ang), me.y + dist
				* Math.cos(ang));
	}

	// @Override
	// public void onPaint(Graphics2D g) {
	// g.setColor(Color.RED);
	// g.fillRect((int) next.x - 10, (int) next.y - 10, 20, 20);
	// if (targetEnemy != null) {
	// g.fillRect((int) targetEnemy.getLocation().x - 10,
	// (int) targetEnemy.getLocation().y - 10, 20, 20);
	// }
	//
	// if(reducedBattleField!=null){
	// g.draw(reducedBattleField);
	// }if(battleField!=null){
	// g.draw(battleField);
	// }
	// if(crow!=null){
	// g.draw(crow);
	// }

	// for(Entry<Point2D, Double> p : pointsTried.entrySet()){
	// if (p.getValue()<50) {
	// g.fillRect((int) (p.getKey().getX() - p.getValue()), (int) (p
	// .getKey().getY() - p.getValue()),
	// (int) (p.getValue() * 2), (int) (p.getValue() * 2));
	// }
	// }
	// pointsTried.clear();
	// super.onPaint(g);
	// }
	//
	private double angleFromXwithMeAsOrigin(Point2D.Double p) {
		return Math.atan2(p.x - me.x, p.y - me.y);
	}

	double shortestTurn(double angle) {
		if ((angle=Utils
				.normalRelativeAngle(angle)) > Math.PI) {
			angle -= 2 * Math.PI;
		} else if (angle < -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	private static class Enemy {
		public Point2D.Double location;
		
		public ScannedRobotEvent e;

		public void updateStats(Point2D.Double pos, ScannedRobotEvent e) {
			
			this.location = pos;
			this.e = e;
		 
		}

		public Point2D.Double guestimatedLocationOfImpact(double deltaT,
				double currentTime) {
			deltaT = currentTime - e.getTime() + deltaT;
			return new Point2D.Double(location.x
					+ Math.sin(e.getHeadingRadians()) * e.getVelocity()
					* deltaT, location.y + Math.cos(e.getHeadingRadians())
					* e.getVelocity() * deltaT);
		}

	}
}
