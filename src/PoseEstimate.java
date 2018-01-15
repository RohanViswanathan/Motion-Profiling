
public class PoseEstimate {

    private Twist velocity;
    private RigidTransform pose;
    private RigidTransform prevPose;
    private double prevLeftDist = 0;
    private double prevRightDist = 0;

    public PoseEstimate(){
        reset(new RigidTransform());
    }

    public RigidTransform getPose() {
        return pose;
    }

    public Twist getVelocity() {
        return velocity;
    }

    public void reset(RigidTransform startingPose){
        velocity = new Twist();
        pose = startingPose;
        prevPose = new RigidTransform();
    }


    public void init(double timestamp) {
        prevLeftDist = 0;
        prevRightDist = 0;
    }

    public void update(double leftDist, double rightDist, double angle) {
        double deltaLeftDist = leftDist - prevLeftDist;
        double deltaRightDist = rightDist - prevRightDist;
        Rotation deltaHeading = prevPose.getRotation().inverse().rotate(Rotation.fromDegrees(angle));
        //Use encoders + gyro to determine our velocity
        velocity = Kinematics.forwardKinematics(deltaLeftDist, deltaRightDist,
                deltaHeading.radians());
        //use velocity to determine our pose
        pose = Kinematics.integrateForwardKinematics(prevPose, velocity);
        //update for next iteration
        prevLeftDist = leftDist;
        prevRightDist = rightDist;
        prevPose = pose;
    }
}

