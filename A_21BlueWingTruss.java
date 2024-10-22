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
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="2+1|BlueWingTruss", group="Worlds_Blue")

public class A_21BlueWingTruss extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_BlueObjectDetector4 OD = new AC_BlueObjectDetector4(telemetry);
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
                InitWebcam.startStreaming(640, 480, OpenCvCameraRotation.UPSIDE_DOWN);
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
               telemetry.addData("Location", OD.getIntLocation());
               telemetry.addLine("Center");
               telemetry.addLine("_");
               telemetry.addData("Delay :" , waitTime );
               telemetry.update();
               sleep(30);
           } else if (OD.getIntLocation() == 1) {
               telemetry.addData("Location", OD.getIntLocation());
               telemetry.addLine("Right");
               telemetry.addLine("_");
               telemetry.addData("Delay :" , waitTime );
               telemetry.update();
               sleep(30);

           } else {
               telemetry.addData("Location", OD.getIntLocation());
               telemetry.addLine("Right");
               telemetry.addLine("_");
               telemetry.addData("Delay :" , waitTime );
               telemetry.update();
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
                .waitSeconds(1)
                .stopAndAdd(Intake.stop())
                .build();


        Action PushPixelIGFast = drive.actionBuilder(drive.pose) // Pushes and drops pixel
                .stopAndAdd(Intake.RealInverse())
                .waitSeconds(0.5)
                .stopAndAdd(Intake.stop())
                .build();


        Action DropPixelBB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.76)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.42)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        //Waits for the opMode to start
        waitForStart();

            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...

                Actions.runBlocking(new SequentialAction(
                        //X- robot goes towards the middle
                        //Y- robot goes towards the backboard
                        //drive.waitS(waitTime),

                        drive.MoveXYACEL(-45, 0, -20, 23),
                        drive.MoveXY(0, -6.5),

                        PushPixelIG,

                        drive.MoveXY(0, 8),
                        drive.MoveXY(5, 1 ),
                        drive.rotate(Math.PI*1.44)));

                Actions.runBlocking(new SequentialAction(
                        drive.MoveXYACEL(-12, 10, -18, 37),

                        Intake.XSTART(),
                        drive.MoveXY(-3.5, 0),
                        drive.MoveXY(7.8, 0.1),
                        drive.waitS(.1),
                        Intake.IntakeMoveDown(),
                        drive.waitS(.9),
                        Intake.RealInverse(),
                        BucketAuto.BucketOpen(),

                        drive.MoveXY(2, 0),
                        drive.MoveXY(0, -26),
                        drive.MoveXY(73, 1.75),
                        drive.MoveXY(9, 34.563),

                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.waitS(.24),
                        drive.MoveXY(5.5, 10),
                        drive.MoveXY(8, 0),
                        DropPixelBB,

                        drive.MoveXY(-5, 0),
                        drive.MoveXY(-1, 27),
                        drive.MoveXY(6.5, 2),
                        Lift.Pos0(),
                        drive.waitS(2)));

            } else if (OD.getIntLocation() == 1 ) {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-43, 1.5, -20, 25),//-20,50(10)
                        drive.MoveXY(0, -2.2),
                        drive.rotate(Math.PI*1.44)));

                Actions.runBlocking(new SequentialAction(
                        PushPixelIG,
                        drive.MoveXY(3, 0),
                        drive.MoveXY(0, 20),
                        drive.MoveXY(-18.5, 0),
                        drive.MoveXY(0, -54),
                        Intake.START(),
                        drive.MoveXY(-7.1, 0),
                        drive.MoveXY(5, -2),
                        drive.waitS(.75),
                        Intake.RealInverse(),
                        BucketAuto.BucketOpen(),

                        drive.MoveXY(87, -3),
                        drive.MoveXY(-2, 18),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.waitS(.24),
                        drive.MoveXY(15, 1),

                        DropPixelBB,
                        drive.MoveXY(-8, 0),
                        drive.MoveXY(1, 40),
                        Lift.Pos0(),
                        drive.waitS(2)));
            }

            else {


                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-53.75, 9, -20, 20),//-20,50(10)
                       // drive.MoveXY(0, 12),//-20,50(10)
                        PushPixelIGFast,
                        drive.MoveXY(-2, 4),

                        drive.rotate(Math.PI*1.4321)
                        // drive.waitS(.5),
                        //drive.MoveXYH(2, -8,-90)
                ));

                Actions.runBlocking(new SequentialAction(
                      //  drive.MoveXY(1, 0.1),
                        //drive.MoveXY(0, 10),
                        //drive.MoveXY(-11, 0),
                        Intake.START(),
                        drive.MoveXYVELACEL(-7, 13, 500,-30, 500),
                        drive.MoveXY(6, 17),
                        drive.MoveXY(0, 31.5),
                        drive.waitS(0.45),
                        Intake.RealInverse(),
                        BucketAuto.BucketOpen(),
                        drive.MoveXY(1, 8.5),

                        drive.MoveXY(74, 0),
                       // drive.MoveXY(2, -27),
                       // BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.waitS(.24),
                        drive.MoveXY(16.75, 18.5),
                        DropPixelBB,
                        drive.waitS(0.3),
                        Lift.Pos0(),
                        drive.waitS(0.58),

                        // drive.MoveXY(6, 0),
// Start of unessuary code

                       /* drive.MoveXY(-96.5, 13),
                        BucketAuto.BucketGateToggle(),

                        // drive.MoveXY(-3, 10),
                        Intake.START(),
                        drive.MoveXY(-6.4, 9),
                        drive.waitS(0.4),
                        drive.MoveXY(6.5, 7),
                        Intake.IntakeMoveDown(),
                        //Intake.RealInverse(),
                        BucketAuto.BucketGateOpen(),

                        drive.MoveXY(87, 2),
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        DropPixelBB,
                         Lift.Pos0(),*/
                         drive.MoveXY(2, 27)



//End


                       // Lift.Pos0(),
                       ));
            }
            if (isStopRequested()) return;
    }}