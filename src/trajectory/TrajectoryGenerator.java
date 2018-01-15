package trajectory;

public class TrajectoryGenerator {
    private double acc = 12;
    private double maxVel = 12; //physical limitations of robot
    private double cruiseVel = maxVel; //velocity that is set
    private double dt = 0.005;

    public double accelDistance, decelDistance, cruiseDistance;

    public TrajectoryGenerator(double acc, double maxVel, double dt){
        this.acc = acc;
        this.maxVel = maxVel;
        this.dt = dt;
    }

    public Trajectory generateTrajectory(double startVel, double endVel, double distance){
        cruiseVel = Math.min(Math.sqrt((distance * acc) + ((Math.pow(startVel, 2) + Math.pow(endVel, 2))/2)), maxVel);
        double accelTime = (cruiseVel - startVel)/acc;
        double decelTime = Math.abs(cruiseVel - endVel)/acc;
        accelDistance = startVel * accelTime + (0.5 * acc * Math.pow(accelTime, 2));
        decelDistance = cruiseVel * decelTime - (0.5 * acc * Math.pow(decelTime, 2));
        cruiseDistance = distance - accelDistance - decelDistance;
        double cruiseTime = cruiseDistance/ cruiseVel;
        double totalTime = accelTime + decelTime + cruiseTime;
        int size = (int)(totalTime/dt);
        Trajectory trajectory = new Trajectory(size);
        double currTime = 0;
        System.out.println(acc);
        System.out.println((cruiseVel-startVel)/(accelTime));
        for (int i = 0; i < size; i++){
            double currPos, currVel, currAccel;
            if (currTime <= accelTime){
                currAccel = acc;
                currPos = startVel * currTime + (0.5 * acc * Math.pow(currTime, 2));
                currVel = startVel + (currAccel * currTime);
            }

            else if (currTime > accelTime && currTime < (accelTime + cruiseTime)){
                currPos = accelDistance + (cruiseVel * (currTime - accelTime));
                currVel = cruiseVel;
                currAccel = 0;

            }

            else {
                double tempCurrTime = currTime - (accelTime + cruiseTime);
                double adjustedCurrTime = totalTime - accelTime - cruiseTime - tempCurrTime;
                double adjustedCurrPos = endVel * adjustedCurrTime + 0.5 * acc * Math.pow(adjustedCurrTime, 2);
                currAccel = -acc;
                currPos = distance - adjustedCurrPos;
                currVel = cruiseVel + (currAccel * tempCurrTime);
            }
            currTime += dt;

            Trajectory.Point point = new Trajectory.Point(currPos, currVel, currAccel, currTime);
            trajectory.addPoint(i, point);
        }

        return trajectory;
    }

    public Trajectory generateScaledTrajectory(Trajectory leadTrajectory, double scale){
        Trajectory followTrajectory = new Trajectory(leadTrajectory.points.size());
        for (int i = 0; i < leadTrajectory.points.size(); i++){
            Trajectory.Point leadPoint = leadTrajectory.points.get(i);
            double followPosition = scale * leadPoint.getPos();
            double followVel = scale * leadPoint.getVel();
            Trajectory.Point followPoint = new Trajectory.Point(followPosition, followVel, leadPoint.getAcc(), leadPoint.getTime());
            followTrajectory.addPoint(i, followPoint);
        }

        return followTrajectory;
    }

    public static void main (String [] args){
        TrajectoryGenerator trajectoryGenerator = new TrajectoryGenerator(12, 12, 0.005);
        System.out.println(trajectoryGenerator.generateTrajectory(4, 4, 100));
        System.out.println("Accel Dist " + trajectoryGenerator.accelDistance);
        System.out.println("Decel Dist " + trajectoryGenerator.decelDistance);
        System.out.println("Cruise Dist " + trajectoryGenerator.cruiseDistance);
        System.out.println("Cruise Vel " + trajectoryGenerator.cruiseVel);
    }
}
