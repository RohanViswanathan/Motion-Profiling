import java.awt.*;

public class TrajectoryGenerator {
    private double acc;
    private double maxVel;
    private double dt;
    public TrajectoryGenerator(double acc, double maxVel, double dt){
        this.acc = acc;
        this.maxVel = maxVel;
        this.dt = dt;
    }

    public Trajectory generateTrajectory(double startVel, double endVel, double distance){
        double accelTime = (maxVel - startVel)/acc;
        double decelTime = (maxVel - endVel)/acc;
        double accelDistance = startVel * accelTime + (1/2 * acc * Math.pow(accelTime, 2));
        double decelDistance = endVel * decelTime + (1/2 * acc * Math.pow(decelTime, 2));
        double cruiseDistance = distance - accelDistance - decelDistance;
        double cruiseTime = cruiseDistance/maxVel;
        double totalTime = accelTime + decelTime + cruiseTime;
        int size = (int)(totalTime/dt);
        Trajectory trajectory = new Trajectory(size);
        double currTime = 0;
        for (int i = 0; i < size; i++){
            double currPos, currVel, currAccel;
            if (currTime < accelTime){
                currPos = startVel * currTime + (0.5 * acc * Math.pow(currTime, 2));
                currVel = startVel + (acc * currTime);
                currAccel = acc;
            }

            else if (currTime > accelTime && currTime < totalTime - decelTime){
                currPos = accelDistance + maxVel * (currTime - accelTime);
                currVel = maxVel;
                currAccel = 0;
            }

            else {
                double tempCurrTime = currTime - (totalTime - decelTime);
                double adjustedCurrTime = totalTime - accelTime - cruiseTime - tempCurrTime;
                double adjustedCurrPos = (0.5 * acc * Math.pow(adjustedCurrTime, 2));
                currPos = distance - adjustedCurrPos;
                currVel = maxVel - (acc * tempCurrTime);
                currAccel = -acc;
            }

            Trajectory.Point point = new Trajectory.Point(currPos, currVel, currAccel, currTime);
            trajectory.addPoint(i, point);
            currTime += dt;
        }

        return trajectory;
    }
}
