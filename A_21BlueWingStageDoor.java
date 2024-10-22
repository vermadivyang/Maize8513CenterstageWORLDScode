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

@Autonomous(name="2+1|BlueWingStageDoor", group="Worlds_Blue")

public class A_21BlueWingStageDoor extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_BlueObjectDetector4 OD = new AC_BlueObjectDetector4(telemetry);
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

        View relativeLayout;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(42, 42, 222)));


        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));// Starting position
        C_AutoMecanumDrive drive = new C_AutoMecanumDrive(hardwareMap, startPose);


        BO_Lift Lift = new BO_Lift();
        BO_Intake Intake = new BO_Intake();
        BO_Bucket BucketAuto = new BO_Bucket();

        Lift.init(hardwareMap);
        Intake.AutoInit(hardwareMap);
        BucketAuto.init(hardwareMap);


        //OpenCV Team element Detection
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        InitWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 2"), cameraMonitorViewId);
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
                .waitSeconds(1)
                .stopAndAdd(Intake.stop())
                .build();

        Action PushPixelIGF = drive.actionBuilder(drive.pose) // Pushes and drops pixel FASTER
                .stopAndAdd(Intake.IntakeRevserveFull())
                .waitSeconds(1)
                .stopAndAdd(Intake.stop())
                .build();


        Action DropPixelBB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.76)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.42)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        Action DropPixelBBNB = drive.actionBuilder(drive.pose) // Drops the pixel on the Backdrop with going up while doing so.
                .stopAndAdd(BucketAuto.BucketGateOpen())
                .waitSeconds(.75)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.09)
                .build();

        //Waits for the opMode to start
        waitForStart();

            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...


                Actions.runBlocking(new SequentialAction(
                        //X- robot goes towards the middle
                        //Y- robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-45.5, 0, -20, 26),//-20,50(10)
                        drive.MoveXY(-2, -6.5),
                        PushPixelIG,
                        drive.MoveXY(-8, 8),
                        drive.rotate(4.03)
                        ));


                Actions.runBlocking(new SequentialAction(
                        drive.TurnMoveXY(-5, -1),
                        Intake.XSTART(),
                        drive.TurnMoveXY(6, -16.5),
                        drive.TurnMoveXY(5.8, 14.75),
                        drive.TurnMoveXY(-12, -1),
                        //drive.rotate(-0.5),
                        drive.TurnMoveXY(1, 76),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.TurnMoveXY(40, -1),
                        drive.TurnMoveXY(6, 13),
                        DropPixelBBNB,
                        Lift.Pos4(),
                        drive.waitS(1),
                        drive.TurnMoveXY(8, -5),
                        drive.TurnMoveXY(21, -1),
                        Lift.Pos0(),
                        drive.waitS(2)

                      /*  Intake.XSTART(),
                       // drive.TurnMoveXY(0.01, -1),
                        drive.TurnMoveXY(0.01, 6),
                        drive.waitS(1),
                        BucketAuto.BucketOpen(),
                        Intake.RealInverse(),
                        drive.TurnMoveXY(-37.8, 5),// -25

                        drive.TurnMoveXY(1, 75),
                        drive.TurnMoveXY(31.5, 2),// 30
                                BucketAuto.BucketOpen(),
                                Lift.Pos2(),
                                drive.waitS(.24),

                        //SWITCH X and Y

                        drive.MoveXY(8.5, -7),
                        DropPixelBB,
                        drive.MoveXY(-8, -0),
                        drive.MoveXY(-6, 20),
                        Lift.Pos0(),
                        drive.waitS(2)*/));

            } else if (OD.getIntLocation() == 1 ) {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y- robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-46, 0, -20, 30),//-20,50(10)
                        drive.rotate(4.05)
                ));


                Actions.runBlocking(new SequentialAction(
                        PushPixelIGF,
                        drive.TurnMoveXY(-0, 4),
                        drive.TurnMoveXY(-23, 2),
                        Intake.XSTART(),
                        drive.TurnMoveXY(-0, -22.5),
                        drive.TurnMoveXY(-0, 6),
                       
                       // drive.rotate(0.05),
                        drive.TurnMoveXY(2.5, 86),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.TurnMoveXY(18.99, 2),

                        drive.TurnMoveXY(-0.5, 12),
                        DropPixelBBNB,
                        Lift.Pos4(),
                        drive.waitS(0.7),
                        drive.TurnMoveXY(-2, -6),
                        Lift.Pos0(),
                        drive.TurnMoveXY(35, 3)

                ));


            } else {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y- robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-52.5, 10, -20, 26),//-20,50(10)
                        PushPixelIG,
                        drive.MoveXY(0, 5),
                        drive.rotate(4)
                ));


                Actions.runBlocking(new SequentialAction(
                        //drive.TurnMoveXY(0, 4), // back
                        //drive.TurnMoveXY(23, 2) // side

                        Intake.XSTART(),
                        drive.TurnMoveXY(-17, -9),
                        drive.TurnMoveXY(-8, 6),
                        drive.TurnMoveXY(-40, 1),
                        Intake.IntakeRevserveFull(),
                        drive.TurnMoveXY(-1, 82),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.TurnMoveXY(14, 9),
                        drive.TurnMoveXY(-15, 8), // KEEP
                        DropPixelBBNB,
                        Lift.Pos4(),
                        drive.waitS(0.7),
                        drive.TurnMoveXY(15, -7),
                        Lift.Pos0(),
                        drive.waitS(2)
                ));
            }
            if (isStopRequested()) return;
    }}