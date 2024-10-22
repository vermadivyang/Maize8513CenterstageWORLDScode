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
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/**
 * This is sample code used to explain how to write an autonomous code
 *
 */

@Autonomous(name="2+2|TrussRedBackboard", group="Worlds_Read")

public class A_22ReadBackboardTruss71 extends LinearOpMode {

    /* Declare OpMode members. */

    //Camera Stuff
    OpenCvCamera InitWebcam;
    AC_RedObjectDetector3 OD = new AC_RedObjectDetector3(telemetry);

    DistanceSensor FrontRange;
    DistanceSensor BackRange;
    DistanceSensor RightRange;
    DistanceSensor LeftRange;


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
    boolean prevrightB = false;
    boolean prevleftB = false;
    boolean prevA = false;
    boolean prevB = false;


    @Override
    public void runOpMode() {


//        BackRange = hardwareMap.get(DistanceSensor .class, "BackRange");
       // FrontRange = hardwareMap.get(DistanceSensor .class, "FrontRange");
        //RightRange = hardwareMap.get(DistanceSensor.class, "RightRange");
        //LeftRange = hardwareMap.get(DistanceSensor.class, "LeftRange");

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

           if(gamepad1.right_bumper && !prevleftB){
               TrussTrue = true;
               telemetry.addLine("Going through the truss");
               telemetry.update();
           }
           prevleftB = gamepad1.right_bumper;

           if(gamepad1.left_bumper && !prevrightB){
               TrussTrue = false;
               telemetry.addLine("NOT NOT going through the truss");
               telemetry.update();
           }
           prevrightB = gamepad1.right_bumper;


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
                .waitSeconds(.75)
                .stopAndAdd(BucketAuto.BucketGateClose())
                .waitSeconds(.4)
                .stopAndAdd(BucketAuto.BucketClose())

                .build();

        //Waits for the opMode to start
        waitForStart();
            if (OD.getIntLocation() == 3) { // If the location is 3 then do ...
              Actions.runBlocking(new SequentialAction(
                        //X- robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        drive.MoveXYACEL(-42.5, 0, -20, 21.5),//-20,50(10)
                        drive.MoveXY(-3, -7),//Drop Pixel

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(-18, 37.2),
                        DropPixelBB,

                      drive.MoveXY(-0.45, -30),
                      Lift.Pos0(),
                      BucketAuto.BucketGateOpen(),
                      drive.MoveXY(30, -2.32),
                      drive.rotate(0.035),

                      drive.MoveXY(-2, -54.9),
                      drive.MoveXY(-9.5, -3),
                      drive.rotate(-0.80)));

if(TrussTrue){
                Actions.runBlocking(new SequentialAction(
                        Intake.XSTART(),
                        drive.Line_Y(-70),
                        drive.waitS(1.5),
                        drive.Line_Y(-45),
                        drive.rotate(0.788)));

                Actions.runBlocking(new SequentialAction(
                        drive.MoveXY(2.98, 18.5),
                        drive.rotate(0.05),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(-23.5, -54),
                        drive.MoveXY(0.5, -50.5),
                        DropPixelBB,
                        BucketAuto.BucketGateClose(),
                        drive.waitS(0.75),
                        BucketAuto.BucketClose(),
                        Lift.Pos0(),
                        drive.MoveXY(8, -61.5),
                        drive.waitS(2)
                ));} else{

    Actions.runBlocking(new SequentialAction(

            drive.rotate(0.788)));

    Actions.runBlocking(new SequentialAction(
            drive.MoveXY(2.98, 18.5),
            drive.rotate(0.05),
            BucketAuto.BucketOpen(),
            Lift.Pos2(),
            drive.MoveXY(-23.5, -54),
            drive.MoveXY(0.5, -50.5),
            DropPixelBB,
            BucketAuto.BucketGateClose(),
            drive.waitS(0.75),
            BucketAuto.BucketClose(),
            Lift.Pos0(),
            drive.MoveXY(8, -61.5),
            drive.waitS(2)));
}


            } else if (OD.getIntLocation() == 1 ) {

                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-54.9, 15, -20, 20),
                        drive.MoveXY(-4, -5),

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(14, 25.5),
                        DropPixelBB,
                        drive.waitS(0.5),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),

                        Intake.XSTART(),
                        drive.MoveXY(11.8, -107.5),
                        drive.waitS(0.4),
                        drive.MoveXY(0.05, 6),
                        drive.MoveXY(-2, 58),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(-3, 38.89),
                        BucketAuto.BucketGateClose(),
                        drive.waitS(0.75),
                        drive.MoveXY(-4, -5),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),
                        BucketAuto.BucketClose(),

                        Intake.XSTART(),
                        drive.MoveXY(9.5, -103.3),
                        drive.waitS(0.4),
                      //  Intake.IntakeMoveDown(),
                        drive.MoveXY(0.05, 6),
                        drive.MoveXY(-3, 60),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(2.98, 39),
                        BucketAuto.BucketGateClose(),
                        drive.waitS(0.75),
                        drive.MoveXY(-4, -5),
                        Lift.Pos0(),
                        BucketAuto.BucketOpen(),

                        drive.MoveXY(20, -1),
                        drive.waitS(0.75)
                        ));

            }
            else {
                Actions.runBlocking(new SequentialAction(
                        //X+ robot goes towards the middle
                        //Y+ robot goes towards the backboard
                        //drive.waitS(waitTime),
                        drive.MoveXYACEL(-3, 19, -20, 20),
                        drive.MoveXY(-36.5, 1),
                        drive.MoveXY(-1, -7.5),

                        PushPixelIG,
                        BucketAuto.BucketOpen(),
                        Lift.Pos1(),
                        drive.MoveXY(0.3, 7),
                        drive.MoveXY(5.5, 0.5),
                        drive.MoveXY(-0.4, 14),
                        DropPixelBB,
                        drive.MoveXY(16.5, -5),
                        Lift.Pos0(),
                        BucketAuto.BucketGateOpen(),
                        drive.MoveXY(-1, -27),
                        drive.rotate(0.035),

                        drive.MoveXY(-2, -48.7),
                        drive.MoveXY(-9, -3),
                        drive.rotate(-0.74)
                ));


                Actions.runBlocking(new SequentialAction(
                        Intake.XSTART(),
                        drive.Line_Y(-69),
                        drive.waitS(1.5),
                        drive.Line_Y(-35),
                        drive.rotate(0.77)));

                Actions.runBlocking(new SequentialAction(
                        drive.MoveXY(2.98, 20),
                        drive.rotate(0.05),
                        BucketAuto.BucketOpen(),
                        Lift.Pos2(),
                        drive.MoveXY(-21.5, -54),
                        drive.MoveXY(0.4, -49),
                        DropPixelBB,
                        BucketAuto.BucketGateClose(),
                        drive.waitS(0.75),
                        drive.MoveXY(-19, -54),

                        drive.MoveXY(8, -61.5),
                        Lift.Pos0(),
                        BucketAuto.BucketClose(),
                        drive.waitS(2)
                ));


            }
            if (isStopRequested()) return;
    }}