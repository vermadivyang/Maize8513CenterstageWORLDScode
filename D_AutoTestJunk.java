package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name="Junk", group="blue")
@Disabled
public class D_AutoTestJunk extends LinearOpMode {

    /*public class BO_Lift{
        private DcMotor BO_Lift;

        public BO_Lift(HardwareMap hardwareMap){
            BO_Lift = hardwareMap.get(DcMotor.class,"liftM");
            BO_Lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            BO_Lift.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        public class LiftUp implements Action{
            private boolean initialized = false;

            public boolean run(@NonNull TelemetryPacket packet){
                if(!initialized){
                    BO_Lift.setPower(0);
                    initialized = true;
                }
                double pos = BO_Lift.getCurrentPosition();
                packet.put("liftPos",pos);
                if(pos < 2920){
                    return true;
                }else{
                    BO_Lift.setPower(0);
                    return false;
                }
            }
        }
        public  Action liftUp(){
            return new LiftUp();
        }
        public class LiftDown implements Action{
            private boolean initialized = false;

            public boolean run(@NonNull TelemetryPacket packet){
                if(!initialized){
                    BO_Lift.setPower(0);
                    initialized = true;
                }
                double pos = BO_Lift.getCurrentPosition();
                packet.put("liftPos",pos);
                if(pos >20){
                    return true;
                }else{
                    BO_Lift.setPower(0);
                    return false;
                }
            }
        }
        public Action liftDown(){
            return new LiftDown();
        }
    }
    public class BO_Bucket{
        private Servo BO_Bucket;

        public BO_Bucket(HardwareMap hardwareMap){
            BO_Bucket = hardwareMap.get(Servo.class,"BO_Bucket");
        }
        public class DropBucket implements Action{
            public boolean run(@NonNull TelemetryPacket packet){
                BO_Bucket.setPosition(0.43);
                return false;
            }
        }
        public  Action DropBucket(){
            return new DropBucket();
        }

        public class IntakeBucket implements Action{
            public boolean run(@NonNull TelemetryPacket packet){
                BO_Bucket.setPosition(0.75);
                return false;
            }
        }
        public Action IntakeBucket(){
            return new IntakeBucket();
        }

    }
    public class Gate{
        private Servo Gate;

        public Gate(HardwareMap hardwareMap){
            Gate = hardwareMap.get(Servo.class, "BucketGate");
        }

        public class OpenGate implements Action{
            public boolean run(@NonNull TelemetryPacket packet){
                Gate.setPosition(0.5);
                return false;
            }
        }
        public  Action OpenGate(){
            return new OpenGate();
        }
        public class CloseGate implements Action{
            public boolean run(@NonNull TelemetryPacket packet){
                Gate.setPosition(0.1);
                return false;
            }
        }

        public Action CloseGate(){
            return new CloseGate();
        }

    }
    public class BO_Intake{
        private DcMotor BO_Intake;

        public BO_Intake(HardwareMap hardwareMap){
            BO_Intake = hardwareMap.get(DcMotor.class,"intakeM");
        }

        public class IntakeDropPixel implements Action{
            private boolean intialized = false;

            public boolean run(@NonNull TelemetryPacket packet){
                if(!intialized){
                    BO_Intake.setPower(-0.37);
                    intialized = true;
                }
                return false;
            }
        }
        public  Action IntakeDropPixel(){
            return new IntakeDropPixel();
        }
    }*/

    OpenCvCamera webcam;
    AC_ObjectDetector1 OD = new AC_ObjectDetector1(telemetry);


    @Override
    public void runOpMode() {

        View relativeLayout;
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);
        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(214, 41, 41)));

        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, new Pose2d(0,0,Math.toRadians(-90)));

        BO_Lift OLift = new BO_Lift();
        BO_Intake OIntake = new BO_Intake();
        BO_Bucket OBucketAuto = new BO_Bucket();

        OLift.init(hardwareMap);
        OIntake.init(hardwareMap);
        OBucketAuto.init(hardwareMap);


        //Action thing;

        //X+ robot goes towards the middle
        //Y+ robot goes towards the backboard

       Action DropPixelTape = drive.actionBuilder(drive.pose)
                .strafeToLinearHeading(new Vector2d(46,-8),Math.toRadians(-90))
                .build();

       Action DropPixelBoard = drive.actionBuilder(drive.pose)
                .waitSeconds(2)
                .strafeToLinearHeading(new Vector2d(5,55),Math.toRadians(-110))
                .build();

        Action waits = drive.actionBuilder(drive.pose)
                .waitSeconds(5)
                .build();

       Action Park = drive.actionBuilder(drive.pose)
                .waitSeconds(7)
                .strafeToLinearHeading(new Vector2d(1,-4),Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(40,-8),Math.toRadians(-90))
                .build();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(OD);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);}
            @Override
            public void onError(int errorCode) {}});

        //Set telemetry
        while(!isStarted()) {
            if (OD.getIntLocation() == 2) {
                telemetry.addData("Team Element Position;", OD.getIntLocation());
                telemetry.addLine("Center");
                telemetry.update();
                sleep(50);
            } else if (OD.getIntLocation() == 1) {
                telemetry.addData("Team Element Position;", OD.getIntLocation());
                telemetry.addLine("Left");
                telemetry.update();
                sleep(50);

            } else {
                telemetry.addData("Team Element Position;", OD.getIntLocation());
                telemetry.addLine("Right (GONE)");
                telemetry.update();
                sleep(50);
            }

        }

        waitForStart();

        if (OD.getIntLocation() == 1){ // Left
            Actions.runBlocking(
                    new SequentialAction(

                            DropPixelTape,
                            OIntake.RealInverse(),
                            waits,
                            DropPixelBoard,
                            waits,
                            OLift.Pos1(),
                            waits,
                            OBucketAuto.BucketToggle(),
                            waits,
                            OBucketAuto.BucketGateToggle(),
                            waits

                            ));} else if (OD.getIntLocation() == 2) { // Center

            Actions.runBlocking( new SequentialAction());

        } else { // Left

            Actions.runBlocking( new SequentialAction());

        } if(isStopRequested()) return;}}
