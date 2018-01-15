package math;

import java.text.DecimalFormat;
/**
 * Class representing a 2D translation vector.
 */
public class Translation {

    private final double kEpsilon = 1E-9;
    private double x;
    private double y;

    /**
     * Create a translation vector from an x and y value.
     *
     * @param x x value
     * @param y y value
     */
    public Translation(double x, double y) {
        this.x = Math.abs(x) < kEpsilon ? 0.0 : x;
        this.y = Math.abs(y) < kEpsilon ? 0.0 : y;
    }

    /**
     * Default constructor: Zero vector
     */
    public Translation() {
        this(0.0, 0.0);
    }

    /**
     * Create a translation vector from two other translation vectors. This translation vector represents the difference between the two other translations.
     *
     * @param first  first translation
     * @param second second translation
     */
    public Translation(Translation first, Translation second) {
        this(second.x - first.x, second.y - first.y);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    /**
     * Translate a translation vector.
     *
     * @param other vector to translate by
     * @return the translated vector
     */
    public Translation translate(Translation other) {
        return new Translation(x + other.x, y + other.y);
    }

    /**
     * Rotate a translation vector.
     *
     * @param rotation rotation matrix to multiply the translation vector by
     * @return the rotated translation vector
     */
    public Translation rotate(Rotation rotation) {
        return new Translation(
                rotation.cos() * x - rotation.sin() * y,
                rotation.sin() * x + rotation.cos() * y);
    }

    /**
     * Inverse of this translation. Basically what "undoes" this translation.
     *
     * @return the inverse of this translation vector
     */
    public Translation inverse() {
        return new Translation((-1.0)*x, (-1.0)*y);
    }

    /**
     * Norm is the function that assigns a strictly positive length to a vector. Basically pythagorean theorem.
     *
     * @return norm of this translation vector
     */
    public double norm() {
        return Math.hypot(x, y);
    }

    /**
     * Dot product of this vector with another
     *
     * @param other vector to calculate the dot product by
     * @return dot product of the this vector with the specified vector
     */
    public double dot(Translation other) {
        return x * other.x + y * other.y;
    }

    /**
     * 2D implementation of a cross product between translation vectors. Basically the "z" component of the 3D vector is 0 when calculating the cross product.
     *
     * @param other vector to calculate the cross product by
     * @return cross product of the this vector with the specified vector
     */
    public double cross(Translation other) {
        return x * other.y - y * other.x;
    }

    /**
     * Multiply this translation vector by a scalar.
     *
     * @param scalar value to scale the vector by
     * @return scaled translation vector
     */
    public Translation scale(double scalar) {
        return new Translation(x * scalar, y * scalar);
    }

    /**
     * This calculates the angle of this translation with respect to the positive x axis.
     *
     * @return angle of this translation with respect to the positive x axis
     *
     */
    public Rotation direction() {
        return new Rotation(x, y, true);
    }

    /**
     * Calculates the angle between this translation and another specified translation
     *
     * cos(theta) = (u dot v)/(||u||*||v||)
     *
     * @param other other translation to calculate the angle between
     * @return angle between this translation and the other specified translation
     */
    public Rotation getAngle(Translation other){
        if (this.norm() == 0 || other.norm() == 0){
            return new Rotation();
        }
        double val = this.dot(other)/(this.norm()*other.norm());
        //Make sure val is between 1 and -1, as arccos will throw an error outside that bound
        return Rotation.fromRadians(Math.acos(Math.min(1.0, Math.max(val, -1.0))));
    }


    @Override
    public String toString() {
        final DecimalFormat format = new DecimalFormat("#0.000");
        return format.format(x) + "," + format.format(y);
    }
}
