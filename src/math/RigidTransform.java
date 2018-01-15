package math;

/**
 * Class representing a 2d Rigid-Body Homogeneous Transformation Matrix
 * Basically combines {@link Rotation} and {@link Translation}
 * [[cos, -sin, x]
 *  [sin,  cos, y]
 *  [0,    0,   1]]
 */
public class RigidTransform {

    private static final double kEpsilon = 1E-9;
    private Translation translation;
    private Rotation rotation;

    public RigidTransform(Translation translation, Rotation rotation){
        this.translation = translation;
        this.rotation = rotation;
    }

    public RigidTransform(){
        this(new Translation(), new Rotation());
    }

    public Translation getTranslation(){
        return translation;
    }

    public Rotation getRotation(){
        return rotation;
    }

    /**
     * Transforms (rotates + translates) this transformation matrix by another transformation matrix
     * Basically multiplies two transformation matrices (see above the class declaration) together.
     *
     * In other words, we first rotation this translation vector by the other rotation vector (taking into account this rotation vector),
     * then we translate this translation vector by the other translation vector
     *
     * @param other Transformation matrix to transform this matrix by
     * @return The transformed matrix
     */
    public RigidTransform transform(RigidTransform other){
        return new RigidTransform(translation.translate(other.getTranslation().rotate(rotation)), rotation.rotate(other.getRotation()));
    }

    /**
     * Calculates the inverse of this transformation matrix
     * Basically what undoes this transform
     *
     * @return Inverse of this transformation matrix
     */
    public RigidTransform inverse(){
        return new RigidTransform(translation.inverse().rotate(rotation.inverse()), rotation.inverse());
    }

    /**
     * ethaneade.com/lie_groups.pdf
     * Exponential map for 2D rigid transformation
     * Basically this converts a {@link Twist} ---(exponential map)---> transformation
     * Kind of like an differential position ---(integral)---> position
     *
     * math.Twist: [[dx]
     *         [dy]
     *         [dtheta]]
     *
     * math.Rotation: [[cos(dtheta), -sin(dtheta)]
     *            [sin(dtheta),  cos(dtheta)]]
     *
     * math.Translation: [[sin(dtheta)/dtheta,      -(1-cos(dtheta)/detheta)]  * [[dx]
     *               [(1-cos(dtheta)/dtheta),  sin(dtheta)/dtheta)     ]]    [dy]]
     *
     * @param twist Input twist
     * @return Transformation based on the twist
     */
    public static RigidTransform exp(Twist twist){
        double dtheta = twist.dtheta();
        double cos = Math.cos(dtheta);
        double sin = Math.sin(dtheta);
        Rotation rot = new Rotation(cos, sin);
        double sin_theta_over_theta;
        double one_minus_cos_theta_over_theta;
        //if theta is very small, we need to use taylor series to approximate the values
        //as we can't divide by 0
        if (Math.abs(dtheta) < kEpsilon){
            sin_theta_over_theta = 1.0-Math.pow(dtheta, 2)/6.0+Math.pow(dtheta, 4)/120.0;
            one_minus_cos_theta_over_theta = 1.0/2.0*dtheta-Math.pow(dtheta, 3)/24.0+Math.pow(twist.dtheta(), 5)/720.0;
        }
        else{
            sin_theta_over_theta = sin/dtheta;
            one_minus_cos_theta_over_theta = (1.0-cos)/dtheta;
        }
        Translation translation = new Translation(sin_theta_over_theta*twist.dx(), one_minus_cos_theta_over_theta*twist.dx());
        return new RigidTransform(translation, rot);
    }

    /**
     * Inverse of above
     */
    public static Twist log(RigidTransform transform){
        double dtheta = transform.getRotation().radians();
        double half_dtheta = dtheta/2.0;
        double cos_minus_one = transform.rotation.cos() - 1.0;
        double halftheta_by_tan_of_halftheta;
        if (Math.abs(cos_minus_one) < kEpsilon){
            halftheta_by_tan_of_halftheta = 1.0-1.0/12.0*Math.pow(dtheta, 2);
        }
        else{
            halftheta_by_tan_of_halftheta = -(half_dtheta * transform.getRotation().sin())/cos_minus_one;
        }
        Translation ret = transform.getTranslation().rotate(new Rotation(halftheta_by_tan_of_halftheta, -half_dtheta));
        return new Twist(ret.x(), ret.y(), dtheta);
    }

    public boolean isColinear(RigidTransform other){
        Twist twist = log(inverse().transform(other));
        return Math.abs(twist.dy()) < kEpsilon && Math.abs(twist.dtheta()) < kEpsilon;
    }

    /**
     * Helper method to calculate intersection of two Transforms
     * @param a Transform a
     * @param b Transform b
     * @return Intersection point of the two Transforms
     */
    private Translation intersection(RigidTransform a, RigidTransform b){
        Rotation rotA = a.getRotation();
        Rotation rotB = b.getRotation();
        Translation transA = a.getTranslation();
        Translation transB = b.getTranslation();

        //Magic
        double tanB = rotB.tan();
        double val = ((transA.x() - transB.x())*tanB + transB.y() - transA.y())/(rotA.sin() - rotA.cos()*tanB);

        return transA.translate(rotA.toTranslation().scale(val));
    }

    /**
     * Calculates the intersection point of two transforms
     * @param other Transform to calculate with
     * @return Intersection point of this transform and the specified transform
     */
    public Translation intersection(RigidTransform other){
        Rotation otherRot = other.getRotation();
        if (this.rotation.isParallel(otherRot)){
            //We should never reach here (hopefully)
            return new Translation(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        //Check which way to calculate intersection
        if (Math.abs(this.rotation.cos()) < Math.abs(otherRot.cos())){
            return intersection(this, other);
        }
        else return intersection(other, this);
    }

    @Override
    public String toString(){
        return translation.toString();
    }
}
