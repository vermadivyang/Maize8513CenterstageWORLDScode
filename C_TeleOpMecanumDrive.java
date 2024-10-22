package teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.HolonomicController;
import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.MotorFeedforward;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.PoseVelocity2dDual;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.TimeTrajectory;
import com.acmerobotics.roadrunner.TimeTurn;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.TurnConstraints;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.FlightRecorder;
import com.acmerobotics.roadrunner.ftc.LynxFirmware;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Config
@TeleOp
@Disabled

public final class C_TeleOpMecanumDrive extends OpMode{
    public static class Params {

        // drive model parameters
        public double MulitStrafeTick= 1;//226
        public double inPerTick = 0.00057112;
        public double lateralInPerTick = 0.00051895096481978 / MulitStrafeTick;//0.00051895096481978
        public double trackWidthTicks = 22026.409770070717;
        public double kS =  1.3543152066378314;//1.3543152066378314
        public double kV =  0.00011575386884492354;//0.00011575386884492354
        public double kA = 0.000002;//5

        // public double inPerTick = 0.0007624251472;

        //public double trackWidthTicks = 22839.56902993141
        // \;
        //public double kS =  1.3294136388944033;
        //public double kS =  0.9640962667515671;//1.3543152066378314
        //public double kV =  0.00011138813405827846;
  //      public double kV =  0.00011370942725399379;//0.00011575386884492354

        // path profile parameters (in inches)
        public  double maxWheelVel = 200;


        //Changes Accel for Auto  (min; -30, max; 50)
        public double minProfileAccel = -30;
        public  double maxProfileAccel = 200;


        // turn profile parameters (in radians)
        public double maxAngVel = Math.PI; // shared with path
        public double maxAngAccel = Math.PI;

        // path controller gains
        public double axialGain = 0.0;
        public double lateralGain = 0.0;//3
        public double headingGain = 9.4; // shared with turn
        public double axialVelGain = 0.0;
        public double lateralVelGain = 0.0;
        public double headingVelGain = 0.0; // shared with turn

    }

    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }

    public static Params PARAMS = new Params();

    public final MecanumKinematics kinematics = new MecanumKinematics(
            PARAMS.inPerTick * PARAMS.trackWidthTicks, PARAMS.inPerTick / PARAMS.lateralInPerTick);

    public final MotorFeedforward feedforward = new MotorFeedforward(PARAMS.kS, PARAMS.kV / PARAMS.inPerTick, PARAMS.kA / PARAMS.inPerTick);

    public final TurnConstraints defaultTurnConstraints = new TurnConstraints(
            PARAMS.maxAngVel, -PARAMS.maxAngAccel, PARAMS.maxAngAccel);
    public final VelConstraint defaultVelConstraint =
            new MinVelConstraint(Arrays.asList(
                    kinematics.new WheelVelConstraint(PARAMS.maxWheelVel),
                    new AngularVelConstraint(PARAMS.maxAngVel)
            ));
    public final AccelConstraint defaultAccelConstraint =
            new ProfileAccelConstraint(PARAMS.minProfileAccel, PARAMS.maxProfileAccel);

    public final DcMotorEx leftFront, leftBack, rightBack, rightFront;

    public final VoltageSensor voltageSensor;

    public final IMU imu;

    public final E_Localizer ELocalizer;
    public Pose2d pose;

    private final LinkedList<Pose2d> poseHistory = new LinkedList<>();

    public class DriveELocalizer implements E_Localizer {
        public final Encoder leftFront, leftRear, rightRear, rightFront;

        private int lastLeftFrontPos, lastLeftRearPos, lastRightRearPos, lastRightFrontPos;
        public Rotation2d lastHeading;

        public DriveELocalizer() {
            leftFront = new OverflowEncoder(new RawEncoder(C_TeleOpMecanumDrive.this.leftFront));
            rightFront = new OverflowEncoder(new RawEncoder(C_TeleOpMecanumDrive.this.rightFront));
            leftRear = new OverflowEncoder(new RawEncoder(C_TeleOpMecanumDrive.this.leftBack));
            rightRear = new OverflowEncoder(new RawEncoder(C_TeleOpMecanumDrive.this.rightBack));


            lastLeftFrontPos = leftFront.getPositionAndVelocity().position;
            lastLeftRearPos = leftRear.getPositionAndVelocity().position;
            lastRightRearPos = rightRear.getPositionAndVelocity().position;
            lastRightFrontPos = rightFront.getPositionAndVelocity().position;

            lastHeading = Rotation2d.exp(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
        }

        @Override
        public Twist2dDual<Time> update() {
            PositionVelocityPair leftFrontPosVel = leftFront.getPositionAndVelocity();
            PositionVelocityPair leftRearPosVel = leftRear.getPositionAndVelocity();
            PositionVelocityPair rightRearPosVel = rightRear.getPositionAndVelocity();
            PositionVelocityPair rightFrontPosVel = rightFront.getPositionAndVelocity();

            Rotation2d heading = Rotation2d.exp(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
            double headingDelta = heading.minus(lastHeading);

            Twist2dDual<Time> twist = kinematics.forward(new MecanumKinematics.WheelIncrements<>(
                    new DualNum<Time>(new double[]{
                            (leftFrontPosVel.position - lastLeftFrontPos),
                            leftFrontPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (leftRearPosVel.position - lastLeftRearPos),
                            leftRearPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (rightRearPosVel.position - lastRightRearPos),
                            rightRearPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (rightFrontPosVel.position - lastRightFrontPos),
                            rightFrontPosVel.velocity,
                    }).times(PARAMS.inPerTick)
            ));

            lastLeftFrontPos = leftFrontPosVel.position;
            lastLeftRearPos = leftRearPosVel.position;
            lastRightRearPos = rightRearPosVel.position;
            lastRightFrontPos = rightFrontPosVel.position;

            lastHeading = heading;

            return new Twist2dDual<>(
                    twist.line,
                    DualNum.cons(headingDelta, twist.angle.drop(1))
            );
        }
    }

    public C_TeleOpMecanumDrive(HardwareMap hardwareMap, Pose2d pose) {
        this.pose = pose;

        LynxFirmware.throwIfModulesAreOutdated(hardwareMap);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");

       /* if(ObserverSetter.CurrentModeOfBreak = true){ //If it is false, then BRAKE
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else /*if (ObserverSetter.CurrentModeOfBreak = false)*//* {
            leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        */

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);

        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        ELocalizer = new E_ThreeDeadWheelELocalizer(hardwareMap,PARAMS.inPerTick);

        FlightRecorder.write("MECANUM_PARAMS", PARAMS);
    }

    public void setDrivePowers(PoseVelocity2d powers) {
        MecanumKinematics.WheelVelocities<Time> wheelVels = new MecanumKinematics(1).inverse(
                PoseVelocity2dDual.constant(powers, 1));

        double maxPowerMagLB = 1;
        double maxPowerMagLF = 1;
        double maxPowerMagRB = 1;
        double maxPowerMagRF = 1;

        for (DualNum<Time> power : wheelVels.all()) {
            maxPowerMagLF = Math.max(maxPowerMagLF, power.value());
            maxPowerMagLB = Math.max(maxPowerMagLB, power.value());
            maxPowerMagRB = Math.max(maxPowerMagRB, power.value());
            maxPowerMagRF = Math.max(maxPowerMagRF, power.value());
        }

        leftFront.setPower(wheelVels.leftFront.get(0) / maxPowerMagLF);
        leftBack.setPower(wheelVels.leftBack.get(0) / maxPowerMagLB);
        rightBack.setPower(wheelVels.rightBack.get(0) / maxPowerMagRB);
        rightFront.setPower(wheelVels.rightFront.get(0) / maxPowerMagRF);
    }

    public final class FollowTrajectoryAction implements Action {
        public final TimeTrajectory timeTrajectory;
        private double beginTs = -1;

        private final double[] xPoints, yPoints;

        public FollowTrajectoryAction(TimeTrajectory t) {
            timeTrajectory = t;

            List<Double> disps = com.acmerobotics.roadrunner.Math.range(
                    0, t.path.length(),
                    (int) Math.ceil(t.path.length() / 2));
            xPoints = new double[disps.size()];
            yPoints = new double[disps.size()];
            for (int i = 0; i < disps.size(); i++) {
                Pose2d p = t.path.get(disps.get(i), 1).value();
                xPoints[i] = p.position.x;
                yPoints[i] = p.position.y;
            }
        }

        @Override
        public boolean run(@NonNull TelemetryPacket p) {
            double t;
            if (beginTs < 0) {
                beginTs = Actions.now();
                t = 0;
            } else {
                t = Actions.now() - beginTs;
            }

            if (t >= timeTrajectory.duration) {
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                rightFront.setPower(0);

                return false;
            }

            Pose2dDual<Time> txWorldTarget = timeTrajectory.get(t);

            PoseVelocity2d robotVelRobot = updatePoseEstimate();

            PoseVelocity2dDual<Time> command = new HolonomicController(
                    PARAMS.axialGain, PARAMS.lateralGain, PARAMS.headingGain,
                    PARAMS.axialVelGain, PARAMS.lateralVelGain, PARAMS.headingVelGain
            )
                    .compute(txWorldTarget, pose, robotVelRobot);

            MecanumKinematics.WheelVelocities<Time> wheelVels = kinematics.inverse(command);
            double voltage = voltageSensor.getVoltage();
            leftFront.setPower(feedforward.compute(wheelVels.leftFront) / voltage);
            leftBack.setPower(feedforward.compute(wheelVels.leftBack) / voltage);
            rightBack.setPower(feedforward.compute(wheelVels.rightBack) / voltage);
            rightFront.setPower(feedforward.compute(wheelVels.rightFront) / voltage);

            FlightRecorder.write("TARGET_POSE", new E_PoseMessage(txWorldTarget.value()));

            p.put("x", pose.position.x);
            p.put("y", pose.position.y);
            p.put("heading (deg)", Math.toDegrees(pose.heading.log()));

            Pose2d error = txWorldTarget.value().minusExp(pose);
            p.put("xError", error.position.x);
            p.put("yError", error.position.y);
            p.put("headingError (deg)", Math.toDegrees(error.heading.log()));

            // only draw when active; only one drive action should be active at a time
            Canvas c = p.fieldOverlay();
            drawPoseHistory(c);

            c.setStroke("#4CAF50");
            drawRobot(c, txWorldTarget.value());

            c.setStroke("#3F51B5");
            drawRobot(c, pose);

            c.setStroke("#4CAF50FF");
            c.setStrokeWidth(1);
            c.strokePolyline(xPoints, yPoints);

            return true;
        }

        @Override
        public void preview(Canvas c) {
            c.setStroke("#4CAF507A");
            c.setStrokeWidth(1);
            c.strokePolyline(xPoints, yPoints);
        }
    }

    public double getPosX(){
        return pose.position.x;
    }
    public double getPosY(){
        return pose.position.y;
    }
    public double getRealHeading(){
        return pose.heading.real;
    }
    public double getImaginaryHeading(){
        return pose.heading.imag;
    }
    public double getLogHeading(){
        return pose.heading.log();
    }



    public final class TurnAction implements Action {
        private final TimeTurn turn;

        private double beginTs = -1;

        public TurnAction(TimeTurn turn) {
            this.turn = turn;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket p) {
            double t;
            if (beginTs < 0) {
                beginTs = Actions.now();
                t = 0;
            } else {
                t = Actions.now() - beginTs;
            }

            if (t >= turn.duration) {
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                rightFront.setPower(0);

                return false;
            }

            Pose2dDual<Time> txWorldTarget = turn.get(t);

            PoseVelocity2d robotVelRobot = updatePoseEstimate();

            PoseVelocity2dDual<Time> command = new HolonomicController(
                    PARAMS.axialGain, PARAMS.lateralGain, PARAMS.headingGain,
                    PARAMS.axialVelGain, PARAMS.lateralVelGain, PARAMS.headingVelGain
            )
                    .compute(txWorldTarget, pose, robotVelRobot);

            MecanumKinematics.WheelVelocities<Time> wheelVels = kinematics.inverse(command);
            double voltage = voltageSensor.getVoltage();
            leftFront.setPower(feedforward.compute(wheelVels.leftFront) / voltage);
            leftBack.setPower(feedforward.compute(wheelVels.leftBack) / voltage);
            rightBack.setPower(feedforward.compute(wheelVels.rightBack) / voltage);
            rightFront.setPower(feedforward.compute(wheelVels.rightFront) / voltage);

            FlightRecorder.write("TARGET_POSE", new E_PoseMessage(txWorldTarget.value()));

            Canvas c = p.fieldOverlay();
            drawPoseHistory(c);

            c.setStroke("#4CAF50");
            drawRobot(c, txWorldTarget.value());

            c.setStroke("#3F51B5");
            drawRobot(c, pose);

            c.setStroke("#7C4DFFFF");
            c.fillCircle(turn.beginPose.position.x, turn.beginPose.position.y, 2);

            return true;
        }

        @Override
        public void preview(Canvas c) {
            c.setStroke("#7C4DFF7A");
            c.fillCircle(turn.beginPose.position.x, turn.beginPose.position.y, 2);
        }
    }

    public PoseVelocity2d updatePoseEstimate() {
        Twist2dDual<Time> twist = ELocalizer.update();
        pose = pose.plus(twist.value());

        poseHistory.add(pose);
        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        FlightRecorder.write("ESTIMATED_POSE", new E_PoseMessage(pose));

        return twist.velocity().value();
    }

    private void drawPoseHistory(Canvas c) {
        double[] xPoints = new double[poseHistory.size()];
        double[] yPoints = new double[poseHistory.size()];

        int i = 0;
        for (Pose2d t : poseHistory) {
            xPoints[i] = t.position.x;
            yPoints[i] = t.position.y;

            i++;
        }

        c.setStrokeWidth(1);
        c.setStroke("#3F51B5");
        c.strokePolyline(xPoints, yPoints);
    }

    private static void drawRobot(Canvas c, Pose2d t) {
        final double ROBOT_RADIUS = 9;

        c.setStrokeWidth(1);
        c.strokeCircle(t.position.x, t.position.y, ROBOT_RADIUS);

        Vector2d halfv = t.heading.vec().times(0.5 * ROBOT_RADIUS);
        Vector2d p1 = t.position.plus(halfv);
        Vector2d p2 = p1.plus(halfv);
        c.strokeLine(p1.x, p1.y, p2.x, p2.y);
    }

    public TrajectoryActionBuilder actionBuilder(Pose2d beginPose) {
        return new TrajectoryActionBuilder(
                TurnAction::new,
                FollowTrajectoryAction::new,
                beginPose, 1e-6, 0.0,
                defaultTurnConstraints,
                defaultVelConstraint, defaultAccelConstraint,
                0.25, 0.
        ); //See what dispResolution and angResolution do. {0.25,0.1}
    }



    public Action MoveXY(double xMove31, double yMove31) {
        return actionBuilder(pose)
                .strafeToConstantHeading(new Vector2d(xMove31, yMove31))
                .build();
    }

    public DcMotorEx getLeftBack() {
        return leftBack;
    }

    public Action TurnMoveXY(double xMove3154, double yMove3154) {
        return actionBuilder(pose)
                .strafeToConstantHeading(new Vector2d(yMove3154, xMove3154))
                .build();
    }


    public Action MoveXYVEL(double xMove32, double yMove32, double VelChange2) {
        return actionBuilder(pose)
                .strafeToConstantHeading(new Vector2d(xMove32, yMove32), new TranslationalVelConstraint(VelChange2))
                .build();
    }

    public Action MoveXYVELACEL(double xMove32, double yMove32, double VelChange2, double AccelChangeMin2, double AccelChangeMax2) {
        return actionBuilder(pose)
                .strafeToConstantHeading(new Vector2d(xMove32, yMove32), new TranslationalVelConstraint(VelChange2), new ProfileAccelConstraint(AccelChangeMin2, AccelChangeMax2))
                .build();
    }

    public Action MoveXYACEL(double xMove32, double yMove32, double AccelChangeMin2, double AccelChangeMax2) {
        return actionBuilder(pose)
                .strafeToConstantHeading(new Vector2d(xMove32, yMove32), null, new ProfileAccelConstraint(AccelChangeMin2, AccelChangeMax2))
                .build();
    }

    public Action MoveXYHVELACEL(double xMove32, double yMove32, double headingChange69, double VelChange2, double AccelChangeMin2, double AccelChangeMax2) {
        return actionBuilder(pose)
                .strafeToLinearHeading(new Vector2d(xMove32, yMove32), Math.toRadians(headingChange69), new TranslationalVelConstraint(VelChange2), new ProfileAccelConstraint(AccelChangeMin2, AccelChangeMax2))
                .build();
    }

    public Action MoveXYHACEL(double xMove32, double yMove32, double headingChange696, double AccelChangeMin2, double AccelChangeMax2) {
        return actionBuilder(pose)
                .strafeToLinearHeading(new Vector2d(xMove32, yMove32), Math.toRadians(headingChange696), null, new ProfileAccelConstraint(AccelChangeMin2, AccelChangeMax2))

                .build();
    }

    public Action MoveXYHVEL(double xMove33, double yMove33, double headingMove33, double VelChange33) {
        return actionBuilder(pose)
                .strafeToLinearHeading(new Vector2d(xMove33, yMove33), Math.toRadians(headingMove33), new TranslationalVelConstraint(VelChange33))
                .build();
    }
    public Action MoveXYH(double xMove3, double yMove3, double headingMove3) {
        return actionBuilder(pose)
                .strafeToLinearHeading(new Vector2d(xMove3, yMove3), Math.toRadians(headingMove3))
                .build();
    }

    public Action Line_Y(double yMove4) {
        return actionBuilder(pose)
                .lineToY(yMove4)
                //.strafeToConstantHeading(new Vector2d(0,yMove4))
                .build();
    }

    public Action Line_X(double xMove4) {
        return actionBuilder(pose)
                .lineToX(xMove4)
                .build();
    }

    public Action waitS(double waitSecondsTime) {
        return actionBuilder(pose)
                .waitSeconds(waitSecondsTime)
                .build();
    }

    public Action rotate(double TurnAngle) {
        return actionBuilder(pose)
                .turn(TurnAngle)
                .build();
    }

    public void MoveForward(){
        leftBack.setPower(0.75);
        rightBack.setPower(0.75);
        leftFront.setPower(0.75);
        rightFront.setPower(0.75);
    }


}
