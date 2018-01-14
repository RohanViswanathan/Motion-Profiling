public class TrajectoryCurveGenerator {

    private double robotTrack = 2.4; //feet

    TrajectoryGenerator trajectoryGenerator;
    Trajectory leadPath;
    Trajectory followPath;

    public TrajectoryCurveGenerator(double acc, double maxVel, double dt){
        trajectoryGenerator = new TrajectoryGenerator(acc, maxVel, dt);
    }

    public void generateTrajectoryCurve(double startVel, double endVel, double degrees, double turnRadius) {
        double arcLeadLength = 2 * (turnRadius + (robotTrack * 0.5)) * Math.PI * (degrees/360);
        double arcFollowLength = 2 * (turnRadius - (robotTrack * 0.5)) * Math.PI * (degrees/360);
        double followStartVel = (turnRadius - (robotTrack * 0.5))/((turnRadius + (robotTrack * 0.5))) * startVel;
        double followEndVel = (turnRadius - (robotTrack * 0.5))/((turnRadius + (robotTrack * 0.5))) * endVel;

        leadPath = trajectoryGenerator.generateTrajectory(startVel, endVel, arcLeadLength);
        followPath = trajectoryGenerator.generateTrajectory(followStartVel, followEndVel, arcFollowLength);
    }


    public Trajectory getLeadPath() {
        return leadPath;
    }

    public Trajectory getFollowPath() {
        return followPath;
    }

    public static void main (String [] args){
        TrajectoryCurveGenerator trajectoryCurveGenerator = new TrajectoryCurveGenerator(12, 12, 0.005);
        trajectoryCurveGenerator.generateTrajectoryCurve(2, 2, 90, 4);
        System.out.println(trajectoryCurveGenerator.getLeadPath());
    }

}
