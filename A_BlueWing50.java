package teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/**
 * For the Blue Wing side, a 48 point Auto and made by Divyang Verma
 */

@Autonomous(name="WING_Blue50", group="blue")

public class A_BlueWing50 extends LinearOpMode {

    /* Declare OpMode members. */
    OpenCvCamera webcam;
    AC_BlueObjectDetector4 OD = new AC_BlueObjectDetector4(telemetry);
    private ElapsedTime     runtime = new ElapsedTime();


    boolean prevDdown = false;
    boolean prevDleft = false;
    boolean prevDright = false;
    boolean prevDup = false;

    boolean prevX = false;
    boolean prevY = false;
    boolean prevA = false;
    boolean prevB = false;

    double waitTime = 0.1;



    @Override
    public void runOpMode() {

        View relativeLayout;

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        relativeLayout.post(() -> relativeLayout.setBackgroundColor(Color.rgb(42, 42, 222)));

        // Works for all (start)
        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));
        Pose2d startPoseR1 = new Pose2d(0, 0, Math.toRadians(66));
        Pose2d startPoseR2 = new Pose2d(0, 0, Math.toRadians(75));
        Pose2d startPoseR3 = new Pose2d(0, 0, Math.toRadians(66));
        C_MecanumDrive drive = new C_MecanumDrive(hardwareMap, startPose);
        C_MecanumDrive driveR1 = new C_MecanumDrive(hardwareMap, startPoseR1);
        C_MecanumDrive driveR2 = new C_MecanumDrive(hardwareMap, startPoseR2);
        C_MecanumDrive driveR3 = new C_MecanumDrive(hardwareMap, startPoseR3);



        // Works for 1 (Right)
        Pose2d Pose1lL = new Pose2d(-52.5948,26.28, Math.toRadians(-83));
        Pose2d Pose2lL = new Pose2d(-69.78,-91.53588, Math.toRadians(75));
        Pose2d Pose4lL = new Pose2d(-85.8,-152.058, Math.toRadians(75));
        Pose2d Pose5lL = new Pose2d(-25.3851,-103.7629341, Math.toRadians(75));

        // 1 (Right)
        C_MecanumDrive drive2lL = new C_MecanumDrive(hardwareMap, Pose2lL);
        C_MecanumDrive drive4lL = new C_MecanumDrive(hardwareMap, Pose4lL);
        C_MecanumDrive drive5lL = new C_MecanumDrive(hardwareMap, Pose5lL);
        C_MecanumDrive drive1lL = new C_MecanumDrive(hardwareMap, Pose1lL);


        // Works for 2 (Center)
        Pose2d Pose1c = new Pose2d(0,0, Math.toRadians(-196));//-36.85,18.75
        Pose2d Pose15c = new Pose2d(-46.752,12.951, Math.toRadians(-196));

        Pose2d Pose2l = new Pose2d(-28.7,-55.535, Math.toRadians(75));
        Pose2d Pose4l = new Pose2d(-8.71,-31.53352, Math.toRadians(75));
        Pose2d Pose5l = new Pose2d(-25.3851,-103.7629341, Math.toRadians(75));

       // 2 (Center)
        C_MecanumDrive drive2lc = new C_MecanumDrive(hardwareMap, Pose2l);
        C_MecanumDrive drive4l = new C_MecanumDrive(hardwareMap, Pose4l);
        C_MecanumDrive drive5l = new C_MecanumDrive(hardwareMap, Pose5l);
        C_MecanumDrive driveRC = new C_MecanumDrive(hardwareMap, Pose1c);
        C_MecanumDrive drive15c = new C_MecanumDrive(hardwareMap, Pose15c);



        // Works for 3 (Left)
        Pose2d Pose1cR = new Pose2d(-47.7511,13.9521, Math.toRadians(-106));
        Pose2d Pose15cR = new Pose2d(-46.7521,12.9511, Math.toRadians(-106));
        Pose2d Pose2lR = new Pose2d(-28.71,-60.54, Math.toRadians(75)); // Y 45.5351
        Pose2d Pose4lR = new Pose2d(-26.11,-50.433521, Math.toRadians(79));

        Pose2d Pose5lR = new Pose2d(-25.38511,-103.762934111, Math.toRadians(75));

        // 3 (Left)
        C_MecanumDrive drive5lR = new C_MecanumDrive(hardwareMap, Pose5lR);


        BO_Lift OLift = new BO_Lift();
        BO_Intake OIntake = new BO_Intake();
        BO_Bucket OBucketAuto = new BO_Bucket();

        OLift.init(hardwareMap);
        OIntake.init(hardwareMap);
        OBucketAuto.init(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "AprilTagCam"), cameraMonitorViewId);
        webcam.setPipeline(OD);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);}
            @Override
            public void onError(int errorCode) {}});

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
                telemetry.addLine("Left");
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

            //Dpad Left for Regular
            if(gamepad1.dpad_down && !prevDdown){
                waitTime = 1;
                telemetry.addLine("1");
                telemetry.update();
            }
            prevDdown = gamepad1.dpad_down;

            //Dpad Down for Serve
            if(gamepad1.dpad_left && !prevDleft){
                waitTime = 2;
                telemetry.addLine("2");
                telemetry.update();
            }
            prevDleft = gamepad1.dpad_left;

            //Dpad Right for Rocket League
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
                telemetry.addLine("RESET WAIT TIME");
                telemetry.update();
            }
            prevY = gamepad1.y;


            telemetry.update();
        }

        waitForStart();

        telemetry.addData("Running Auto with a delay of:" , waitTime );
        telemetry.update();


        if (OD.getIntLocation() == 3){ // Right (GONE)

            Actions.runBlocking( new SequentialAction (
                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)
                            .setTangent(0)
                            .strafeToLinearHeading(new Vector2d(-46,-6),Math.toRadians(-103))//-45,-6.53
                            .build(),

                    // Drop the purple Pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.2)
                            .build(),

                    OIntake.stop(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(0.1)
                            .setTangent(0)
                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(-31.5,20),Math.toRadians(-100))
                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(-28,-70),Math.toRadians(-100))
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(20,-84),Math.toRadians(66))
                            // x decrease to move toward the middle
                            // y decrease to move toward the board side
                            .build(),

                    OLift.Pos1(),
                    OBucketAuto.BucketToggle(),

                    drive.actionBuilder(driveR1.pose)
                            .waitSeconds(1)

                            // Move the robot forward to drop the pixel
                            .strafeToLinearHeading(new Vector2d(3,16),Math.toRadians(90))
                            .strafeToLinearHeading(new Vector2d(15,23),Math.toRadians(90))
                            // x increase to move forward toward board
                            // y decrease to move right to the board
                            // Deg increase to make robot conter clockwise (strait?)
                            .build(),

                    // Opens the bucket gate
                    OBucketAuto.BucketGateToggle(),


                    //Parks
                    drive.actionBuilder(driveR1.pose)
                            .waitSeconds(1.8)
                           // .strafeToLinearHeading(new Vector2d(-6,10),Math.toRadians(75))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketGateToggle(),
                    OBucketAuto.BucketToggle(),


                    drive.actionBuilder(driveR1.pose)
                            .waitSeconds(2)
                            .build()
            ));

        } else if (OD.getIntLocation() == 2) { // Center

            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)

                            .setTangent(0)
                            .strafeToLinearHeading(new Vector2d(-40.5,16),Math.toRadians(-200))
                            .build(),

                    // Drop the purple Pixel
                    OIntake.PowerRealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)
                            .build(),

                    OIntake.stop(),


                    drive.actionBuilder(driveRC.pose)
                            .waitSeconds(1)
                            .setTangent(0)

                            .strafeToLinearHeading(new Vector2d(17,15),Math.toRadians(-200))//-5

                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(16,20),Math.toRadians(-140))
                            .strafeToLinearHeading(new Vector2d(-22,13),Math.toRadians(-140))
                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(2,-100),Math.toRadians(-140))
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(10,-107),Math.toRadians(48))
                            .build(),

                    // Set the bucket in the position to drop the pixel
                    OLift.Pos1(),
                    // Sets the BO_Bucket
                    OBucketAuto.BucketToggle(),

                    drive.actionBuilder(driveR2.pose)
                            .waitSeconds(1)
                            // Move the robot forward to drop the pixel
                            .strafeToLinearHeading(new Vector2d(2,-30),Math.toRadians(50))
                            .strafeToLinearHeading(new Vector2d(-10,-34),Math.toRadians(45))

                            //+x is closer to board
                            //(-)+y is more middle
                            .build(),

                    // Opens the bucket gate
                    OBucketAuto.BucketGateToggle(),


                    // Parks
                    drive.actionBuilder(driveR2.pose)
                            .waitSeconds(1.8)
                            .strafeToLinearHeading(new Vector2d(5,2),Math.toRadians(87))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketGateToggle(),
                    OBucketAuto.BucketToggle(),

                    // Parks
                    drive.actionBuilder(drive5l.pose)
                            .waitSeconds(2)
                            .build()
            ));

        } else { // Left

            Actions.runBlocking( new SequentialAction (

                    OBucketAuto.BucketGateToggle(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(waitTime)

                            .setTangent(0)
                            // Take the robot to the Spike mark
                            .strafeToLinearHeading(new Vector2d(-36,26),Math.toRadians(-102))
                            .strafeToLinearHeading(new Vector2d(-52,28),Math.toRadians(-107))
                            .build(),
                    // Drop the purple Pixel
                    OIntake.RealInverse(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1.5)
                            .build(),

                    OIntake.stop(),

                    drive.actionBuilder(drive.pose)
                            .waitSeconds(1)

                            // Move the robot to the center
                            .strafeToLinearHeading(new Vector2d(-1,3),Math.toRadians(-102))
                            .strafeToLinearHeading(new Vector2d(-25,2),Math.toRadians(-102))
                            // Mover the robot near the Backdrop
                            .strafeToLinearHeading(new Vector2d(-26,-100),Math.toRadians(-102))
                            // Turn the robot so that the bucket faces the backdrop
                            .strafeToLinearHeading(new Vector2d(-17,-101),Math.toRadians(66))
                            .build(),
                    // Set the bucket in the position to drop the pixel
                    OLift.Pos1(),
                    // Sets the BO_Bucket
                    OBucketAuto.BucketToggle(),

                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(1)
                            // Move the robot forward to drop the pixel
                            .strafeToLinearHeading(new Vector2d(20,13),Math.toRadians(66))
                            .strafeToLinearHeading(new Vector2d(38,20),Math.toRadians(76))
                            // x increase to move forward toward board
                            // y increase to move right to the board
                            // Deg increase to make robot conter clockwise (strait?)
                            .build(),

                    // Opens the bucket gate
                    OBucketAuto.BucketGateToggle(),

                    // Align to Park
                    drive.actionBuilder(driveR3.pose)
                            .waitSeconds(2)
                            .strafeToLinearHeading(new Vector2d(-4,1),Math.toRadians(66))
                            .strafeToLinearHeading(new Vector2d(-3,38),Math.toRadians(66))
                            .build(),

                    OLift.Pos0(),
                    OBucketAuto.BucketToggle(),
                    OBucketAuto.BucketGateToggle(),

                    // Parks
                    drive.actionBuilder(drive5lL.pose)
                            .waitSeconds(2)
                            .build()

            ));

        } if(isStopRequested()) return;}}

