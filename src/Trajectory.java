import java.util.ArrayList;

public class Trajectory {
    public static class Point {
        private double pos;
        private double vel;
        private double acc;
        private double time;

        public Point(double pos, double vel, double acc, double time){
            this.pos = pos;
            this.vel = vel;
            this.acc = acc;
            this.time = time;
        }

        public double getPos(){
            return pos;
        }

        public double getVel(){
            return vel;
        }

        public double getAcc(){
            return acc;
        }

        public double getTime(){
            return time;
        }
    }
    ArrayList<Point> points;

    public Trajectory(int size){
        points = new ArrayList<Point>(size);
    }

    public void addPoint(int index, Point point){

        points.add(index, point);
    }

    @Override
    public String toString(){
        String rv = "";
        rv += "Pos,Time\n";
        for(Point p : points){
            rv += p.pos + "," + p.time + "\n";
        }
        return rv;
    }
}
