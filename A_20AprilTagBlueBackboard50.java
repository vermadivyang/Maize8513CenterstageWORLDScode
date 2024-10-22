package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.util.Size;
import android.view.View;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.concurrent.TimeUnit;

/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="2+0|APRILBlueBackboard", group="Worlds_Blue")

public class A_20AprilTagBlueBackboard50 extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_ObjectDetectorWorlds OD = new AC_ObjectDetectorWorlds(telemetry);
    // private ElapsedTime AprilTagTimer = new ElapsedTime();


    //Doubles
    public double whileTrue = 0;
    public double waitTime = 0;
    public double WantedTagID = 1;

    //Boolean
    boolean prevDdown = false;
    boolean prevDleft = false;
    boolean prevDright = false;
    boolean prevDup = false;
    boolean prevX = false;
    boolean prevY = false;
    boolean prevA = false;
    boolean prevB = false;


    @Override
    public void runOpMode() {

        AprilTagProcessor tagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                // .setLensIntrinsics()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .build();



        View relativeLayout;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(42, 42, 222)));


        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));// Starting position
        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, startPose);


        BO_Lift Lift = new BO_Lift();
        BO_Intake Intake = new BO_Intake();
        BO_Bucket BucketAuto = new BO_Bucket();

        Lift.init(hardwareMap);
        Intake.AutoInit(hardwareMap);
        BucketAuto.init(hardwareMap);


        //OpenCV Team element Detection
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        InitWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        InitWebcam.setPipeline(OD);
        InitWebcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                InitWebcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
            }
        });


        //Call and init robot stuff


        //Actions for auto (makes code cleaner and simpler)


        /*Action CloseGate = drive.actionBuilder(drive.pose) // Closes Bucket Gate
                .stopAndAdd(BucketAuto.BucketGateToggle())
                .build();

        Action DropPixelBB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                .waitSeconds(0.5)
                .stopAndAdd(BucketAuto.BucketOpen())
                .stopAndAdd(Lift.Pos1())
                .waitSeconds(3)
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(1)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(1)
                .stopAndAdd(BucketAuto.BucketClose())
                .stopAndAdd(drive.Line_Y(-2))
                .stopAndAdd(Lift.Pos0())
                .waitSeconds(2)
                .build();*/


        //init LOOP
       while(!isStarted()) {
            if (OD.getIntLocation() == 2) {
                telemetry.addData("Location: CENTER ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 3;
                sleep(30);
            } else if (OD.getIntLocation() == 1) {
                telemetry.addData("Location: LEFT ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 2;
                sleep(30);
            } else {
                telemetry.addData("Location: RIGHT ;", OD.getIntLocation());
                telemetry.addLine("_");
                telemetry.addData("Delay :" , waitTime );
                telemetry.update();
                WantedTagID = 3;
                sleep(30);
            }

            //SET WAIT TIME (Delay)
            if(gamepad1.dpad_down && !prevDdown){
                waitTime = 1;
                telemetry.addLine("1");
                telemetry.update();
            }
            prevDdown = gamepad1.dpad_down;

            if(gamepad1.dpad_left && !prevDleft){
                waitTime = 2;
                telemetry.addLine("2");
                telemetry.update();
            }
            prevDleft = gamepad1.dpad_left;

            if(gamepad1.dpad_right && !prevDright){
                waitTime = 3;
                telemetry.addLine("3");
                telemetry.update();
            }
            prevDright = gamepad1.dpad_right;

            if(gamepad1.dpad_up && !prevDup){
                waitTime = 4;
                telemetry.addLine("4");
                telemetry.update();
            }
            prevDup = gamepad1.dpad_up;

            if(gamepad1.x && !prevX){
                waitTime *= 2;
                telemetry.addLine("x2");
                telemetry.update();
            }
            prevX = gamepad1.x;

            if(gamepad1.a && !prevA){
                waitTime += 1;
                telemetry.addLine("+1");
                telemetry.update();
            }
            prevA = gamepad1.a;

            if(gamepad1.b && !prevB){
                waitTime -= 1;
                telemetry.addLine("-1");
                telemetry.update();
            }
            prevB = gamepad1.b;

            if(gamepad1.y && !prevY){
                waitTime = 0;
                telemetry.addLine("RESET waitTime");
                telemetry.update();
            }
            prevY = gamepad1.y;

            telemetry.update();

            sleep(10);
        }


        Action PushPixelIG = drive.actionBuilder(drive.pose) // Pushes and drops pixel
                .stopAndAdd(Intake.RealInverse())
                .waitSeconds(0.7475239)
                .stopAndAdd(Intake.stop())
                .build();


        Action DropPixelBB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                .stopAndAdd(BucketAuto.BucketOpen())
                .stopAndAdd(Lift.Pos1())
                .waitSeconds(2)
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.5)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.3)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        //Waits for the opMode to start
        waitForStart();
        InitWebcam.pauseViewport();
        InitWebcam.stopStreaming();
        InitWebcam.closeCameraDevice();

        VisionPortal visionPortalAprilTag = new VisionPortal.Builder()
                .addProcessor(tagProcessor)
                .setCamera(hardwareMap.get(WebcamName.class, "AprilTagCam"))
                .setCameraResolution(new Size(640, 480)) // 640,480
                //.enableCameraMonitoring(true)
                //.setCameraMonitorViewId()//Set mulitple Cameras
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .build();

        //AprilTag Code; creates vision portal for aprilTag

        visionPortalAprilTag.resumeStreaming();

        //AprilTag Code; sets exposure (fps) and gain(brightness). A higher exposure means more accurcy but a darker image.
        ExposureControl exposure = visionPortalAprilTag.getCameraControl(ExposureControl.class);
        exposure.setMode(ExposureControl.Mode.Manual);
        exposure.setExposure(15, TimeUnit.MILLISECONDS);

        GainControl gain = visionPortalAprilTag.getCameraControl(GainControl.class);
        gain.setGain(90);


            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...
                //AprilTag Code; creates processor


                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(37, 0, -20, 20),//-20,50(10)
                        drive.MoveXY(0, -5),//-20,50(10)

                        PushPixelIG,
                        // PushPixelIG,
                        drive.MoveXY(0, 20)));


                if (tagProcessor.getDetections().size() > 0) { // if it sees something
                    AprilTagDetection tag = tagProcessor.getDetections().get(0);
                    if (tag.id == (2)) {

                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(1.6 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x+2), (y ), -20, 20)));

                    }else if(tag.id == 3){
                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(1.6 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x), (y ), -20, 20)));

                    }else{Actions.runBlocking(new SequentialAction(drive.MoveXYACEL(-1, 5, -20, 20)));}

                }
                Actions.runBlocking(new SequentialAction(DropPixelBB,

                        drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                                .stopAndAdd(drive.MoveXY(-34, 30))
                                .stopAndAdd(Lift.Pos0())
                                .waitSeconds(1)
                                .build()));

            } else if (OD.getIntLocation() == 1 ) {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(38, 0, -20, 20),//-20,50(10)
                        drive.MoveXY(0, 12),//-20,50(10)

                        PushPixelIG,
                        // PushPixelIG,
                        drive.MoveXY(0, 7)));


                if (tagProcessor.getDetections().size() > 0) { // if it sees something
                    AprilTagDetection tag = tagProcessor.getDetections().get(0);
                    if (tag.id == (2)) {

                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(1.6 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x+2), (y ), -20, 20)));

                    }else if(tag.id == 3){
                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(2 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x-23), (y+1.2500001), -20, 20)));

                    }else{Actions.runBlocking(new SequentialAction(drive.MoveXYACEL(-1, 5, -20, 20)));}

                }
                Actions.runBlocking(new SequentialAction(DropPixelBB,

                        drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                                .stopAndAdd(drive.MoveXY(-27, 30))
                                .stopAndAdd(Lift.Pos0())
                                .waitSeconds(1)
                                .build()));




            }



            ///dwgfuhufghowfhvpqeufhg qpwfuhpiuwp frrouqpror rpooroeouqpwr 2





            else {


                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(50, 9, -20, 20),//-20,50(10)
                       // drive.MoveXY(0, 12),//-20,50(10)

                        PushPixelIG,
                        // PushPixelIG,
                        drive.MoveXY(0, 3),
                        drive.MoveXY(-12, 8)


                ));


                if (tagProcessor.getDetections().size() > 0) { // if it sees something
                    AprilTagDetection tag = tagProcessor.getDetections().get(0);
                    if (tag.id == (2)) {

                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(1.6 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x+2), (y ), -20, 20)));

                    }else if(tag.id == 3){
                        double x = Math.abs(3 * tag.ftcPose.x);
                        double y = Math.abs(2 * tag.ftcPose.y);
                        telemetry.speak(String.valueOf(tag.id));
                        telemetry.update();

                        Actions.runBlocking(new SequentialAction(drive.MoveXYACEL((x-20), (y-1.3), -20, 20)));

                    }else{Actions.runBlocking(new SequentialAction(drive.MoveXYACEL(-1, 5, -20, 20)));}

                }
                Actions.runBlocking(new SequentialAction(DropPixelBB,

                        drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                                .stopAndAdd(drive.MoveXY(-27, 30))
                                .stopAndAdd(Lift.Pos0())
                                .waitSeconds(1)
                                .build()));



            }
            if (isStopRequested()) return;
    }}