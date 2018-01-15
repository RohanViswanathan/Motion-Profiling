import java.util.ArrayList;

public class TrajectoryCurveGenerator {

    public double robotTrack = 2.4; //feet
    private PoseEstimate poseEstimate = new PoseEstimate();

    TrajectoryGenerator trajectoryGenerator;
    Trajectory leadPath;
    Trajectory followPath;

    public TrajectoryCurveGenerator(double acc, double maxVel, double dt){
        trajectoryGenerator = new TrajectoryGenerator(acc, maxVel, dt);
    }

    boolean right;
    double angle;

    public void generateTrajectoryCurve(double startVel, double endVel, double degrees, double turnRadius){
        angle = degrees;
        right = degrees > 0 ? true : false;
        double arcLeadLength = 2 * (turnRadius + (robotTrack * 0.5)) * Math.PI * (degrees/360);
        double arcFollowLength = 2 * (turnRadius - (robotTrack * 0.5)) * Math.PI * (degrees/360);
        double followStartVel = (turnRadius - (robotTrack * 0.5))/((turnRadius + (robotTrack * 0.5))) * startVel;
        double followEndVel = (turnRadius - (robotTrack * 0.5))/((turnRadius + (robotTrack * 0.5))) * endVel;
        System.out.println("startVel: " + startVel + "\n" + "endVel: " + endVel + "\n" + "arcLength: " + arcLeadLength);
        leadPath = trajectoryGenerator.generateTrajectory(startVel, endVel, arcLeadLength);
        followPath = trajectoryGenerator.generateTrajectory(followStartVel, followEndVel, arcFollowLength);
    }

    public void generateScaledCurve(double startVel, double endVel, double degrees, double turnRadius){
        double arcLeadLength = 2 * (turnRadius + (robotTrack * 0.5)) * Math.PI * (degrees/360);
        double followScaledVel = (turnRadius - (robotTrack * 0.5))/((turnRadius + (robotTrack * 0.5)));
        leadPath = trajectoryGenerator.generateTrajectory(startVel, endVel, arcLeadLength);
    }


    public Trajectory getLeadPath() {
        return leadPath;
    }

    public Trajectory getFollowPath() {
        return followPath;
    }


    public void plot(){
        ArrayList<Trajectory.Point> leftPoints;
        ArrayList<Trajectory.Point> rightPoints;
        ArrayList<Trajectory.Point> leadPoints = getLeadPath().points;;
        ArrayList<Trajectory.Point> followPoints = getFollowPath().points;;
        if (right) {
            leftPoints = getLeadPath().points;
            rightPoints = getFollowPath().points;
        } else {
            rightPoints = getLeadPath().points;
            leftPoints = getFollowPath().points;
        }

        for (int i = 0; i < leftPoints.size(); i++){
            double currLeftPos = leftPoints.get(i).getPos();
            double currRightPos = rightPoints.get(i).getPos();
            double currLeadPos = leadPoints.get(i).getPos();
            double currFollowPos = followPoints.get(i).getPos();

            double innerRadius = robotTrack * currFollowPos / (currLeadPos - currFollowPos);
            double angle = currFollowPos * 180 / (Math.PI * innerRadius);

            poseEstimate.update(currLeftPos, currRightPos, angle);
            System.out.println(poseEstimate.getPose());
        }
    }

    public static void main (String [] args){
        TrajectoryCurveGenerator trajectoryCurveGenerator = new TrajectoryCurveGenerator(12, 12, 0.005);
        trajectoryCurveGenerator.generateTrajectoryCurve(4, 4, 90, 10);
        System.out.println(trajectoryCurveGenerator.getFollowPath());
    }

}
