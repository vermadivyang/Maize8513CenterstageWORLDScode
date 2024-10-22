package teamcode;

import com.acmerobotics.roadrunner.Pose2d;

public final class E_PoseMessage {
    public long timestamp;
    public double x;
    public double y;
    public double heading;

    public E_PoseMessage(Pose2d pose) {
        this.timestamp = System.nanoTime();
        this.x = pose.position.x;
        this.y = pose.position.y;
        this.heading = pose.heading.log();
    }
}

