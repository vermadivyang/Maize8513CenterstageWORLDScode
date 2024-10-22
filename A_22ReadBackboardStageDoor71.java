package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="2+2|StageDoorRedBackboard", group="Worlds_Read")

public class A_22ReadBackboardStageDoor71 extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_ObjectDetectorWorlds OD = new AC_ObjectDetectorWorlds(telemetry);
    // private ElapsedTime AprilTagTimer = new ElapsedTime();


    //Doubles
    public double whileTrue = 0;
    public boolean TrussTrue = true;
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

                .waitSeconds(.2)
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.4)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.2)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        //Waits for the opMode to start
        waitForStart();
            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...
              if(!TrussTrue){  Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-46, 0, -20, 25),//-20,50(10)
                        drive.MoveXY(-0, -6.5),//-20,50(10)
                        drive.rotate(0.01),

                        PushPixelIG,

                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(-14.5, 39),
                        DropPixelBB,
                        drive.MoveXY(-21, -2),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),

                      drive.rotate(0.02)));

                      Actions.runBlocking(new SequentialAction(
                      drive.waitS(1),
                        drive.MoveXYACEL(-0.3, -98, -20, 20),
                        Intake.XSTART(),
                       // drive.MoveXY(2, -4),
                        drive.MoveXY(0, -6),
                        drive.waitS(1.5),
                        drive.MoveXY(0, 90),

                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(24, 5),
                        drive.MoveXY(2, 12),
                        DropPixelBB,
                        BucketAuto.BucketGateClose(),

                              drive.MoveXY(3, -6),
                              Lift.Pos0(),
                              drive.MoveXY(33, -1),
                        drive.waitS(1)));
              }
              else{ Actions.runBlocking(new SequentialAction(
                        //X- robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-41, 0, -20, 25),//-20,50(10)
                        drive.MoveXY(-0, -6),//-20,50(10)
                        drive.rotate(-0.01),

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(-15, 38.5),
                        DropPixelBB,
                        drive.MoveXY(39, -2),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),
                        drive.waitS(1),
                        drive.rotate(-0.07)
                       ));

                Actions.runBlocking(new SequentialAction(
                        drive.MoveXY(-2, -41),
                        drive.MoveXY(-9, 38),
                        //drive.MoveXY(0.1, ),
                        drive.rotate(-0.65)));

                drive.resetRuntime();

                Actions.runBlocking(new SequentialAction(
                        Intake.XSTART(),
                        drive.Line_Y(-68.5),
                        drive.waitS(0.75),
                        drive.Line_Y(-43),
                        drive.rotate(0.7)

                        ));
                drive.resetRuntime();

                Actions.runBlocking(new SequentialAction(

                        drive.MoveXY(-1.25, -60),
                        drive.MoveXY(-1, -6),
                        drive.MoveXY(-38, -60),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(-0, -29.5),
                        BucketAuto.BucketGateOpen(),
                        drive.waitS(0.5),
                        drive.MoveXY(-0, -57),
                        Lift.Pos0(),
                        drive.waitS(3)));}

            } else if (OD.getIntLocation() == 1 ) {

               if(!TrussTrue){ Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-42, 17, -20, 20),

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(19.5, 23),
                        DropPixelBB,
                        drive.MoveXY(-39.1, -2),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),
                        drive.waitS(1),
                        drive.MoveXY(-0.3, -94),
                        Intake.XSTART(),
                        // drive.MoveXY(2, -4),
                        drive.MoveXY(0, -5),
                        drive.waitS(1.5),
                        drive.MoveXY(0, 93),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(38, 5),
                        drive.MoveXY(2, 13),
                        DropPixelBB,
                        BucketAuto.BucketGateClose(),
                        drive.MoveXY(27, -2),
                        Lift.Pos0(),
                        drive.waitS(1)
                ));} else{
                   Actions.runBlocking(new SequentialAction(
                           //X+ robot goes towards the middle
                           //Y+ robot goes towards the backboard
                           //drive.waitS(waitTime),
                           drive.MoveXYACEL(-42, 17, -20, 20),

                           PushPixelIG,
                           BucketAuto.BucketOpen(),
                           Lift.Pos1(),
                           drive.MoveXY(19.5, 23),
                           DropPixelBB,
                           drive.MoveXY(40.1, -2),
                           Lift.Pos0(),
                           BucketAuto.BucketGateOpen(),
                           drive.waitS(1),
                           drive.waitS(1),
                           drive.rotate(0.07)
                   ));

                   Actions.runBlocking(new SequentialAction(
                           drive.MoveXY(-2, -41),
                           drive.MoveXY(-9, 38),
                           //drive.MoveXY(0.1, ),
                           drive.rotate(0.65)));

                   drive.resetRuntime();

                   Actions.runBlocking(new SequentialAction(
                           Intake.XSTART(),
                           drive.Line_Y(-68.5),
                           drive.waitS(0.75),
                           drive.Line_Y(-43),
                           drive.rotate(-0.7)

                   ));
                   drive.resetRuntime();

                   Actions.runBlocking(new SequentialAction(

                           drive.MoveXY(-1.25, -60),
                           drive.MoveXY(-1, -6),
                           drive.MoveXY(-38, -60),
                           BucketAuto.BucketOpen(),
                           Lift.Pos2(),
                           drive.MoveXY(-0, -29.5),
                           BucketAuto.BucketGateOpen(),
                           drive.waitS(0.5),
                           drive.MoveXY(-0, -57),
                           Lift.Pos0(),
                           drive.waitS(3)));

               }
            }
            else {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-51.25, 8.5, -20, 20),
                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(24, 29.8),
                        DropPixelBB,
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),
                        drive.waitS(1),
                        drive.MoveXY(-0.3, -95),
                        Intake.XSTART(),
                        // drive.MoveXY(2, -4),
                        drive.MoveXY(0, -5.3),
                        drive.waitS(1.5),
                        drive.MoveXY(0, 65),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(0, 35),
                        DropPixelBB,
                        BucketAuto.BucketGateClose(),
                        drive.MoveXY(35, -2),
                        Lift.Pos0(),
                        drive.waitS(1)
                ));
            }
            if (isStopRequested()) return;
    }}