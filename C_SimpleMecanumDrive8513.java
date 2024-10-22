package teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="TeleOpWorlds2024", group="0"/*, color = "red"*/)



public class C_SimpleMecanumDrive8513 extends LinearOpMode {


    private ElapsedTime timer = new ElapsedTime();

    //Drive Var
    private double strafe = 0;
    private double rotate = 0;
    private double forward = 0;
    //Switch Cases
    int stateIntake = 3;
    int driveStyle = 0;
    int intakeOn = 0;
    boolean preIntake = false;
    boolean nowIntake;
    //boolean previoiusPressedIntake = false;

    //Boolean Gards for buttons
    boolean xPrev = false;
    boolean yPrev = false;
    boolean yPrev1 = false;
    boolean yPrev2 = false;
    boolean yPrev3 = false;
    boolean yCur1;
    boolean yCur12;
    boolean aPrev = false;
    boolean UP1Prev = false;
    boolean DOWN1Prev = false;
    boolean RIGHT1Prev = false;
    boolean LEFT1Prev = false;

    boolean TurtleMode=false;
    boolean Previous1A=false;
    boolean nowDrive;
    boolean SwichMOde=false;
    boolean FastMode=false;
    boolean Previous1RB=false;
    boolean Previous1LB=false;
    boolean prevReg = false;
    boolean curReg;
    boolean prevServe = false;
    boolean curServe;
    boolean prevRocket = false;
    boolean curRocket;
    boolean prevLstickB = false;
    boolean prevRstickB = false;




    @Override
    public void runOpMode() throws InterruptedException {

        //Drive
        C_TeleOpMecanumDrive drive = new C_TeleOpMecanumDrive(hardwareMap , new Pose2d(0, 0, 0));

        //Sets features
        BO_Intake OIntake = new BO_Intake();
        BO_Lift OLift = new BO_Lift();
        BO_Bucket OBucketFunctions = new BO_Bucket();
        BO_Shooter OShooter = new BO_Shooter();

        EventSource ES = new EventSource();



        //GlobalVar Global = new GlobalVar();

        //Init
        OIntake.init(hardwareMap);
        OLift.init(hardwareMap);
        OShooter.init(hardwareMap);
        OBucketFunctions.init(hardwareMap);





        //Init Loop
        // The User is pressing the Dpad to change the Drive Style
        while(opModeInInit() && !isStopRequested()){
        nowDrive = gamepad1.dpad_down;

            //Dpad Left for Regular
            curReg = gamepad1.dpad_left;
            if(gamepad1.dpad_left && !prevReg){
                driveStyle = 1;
                telemetry.addLine("Drive Style: Regular Drive");
                telemetry.update();
            }
            prevReg = gamepad1.dpad_left;

            //Dpad Down for Serve
            curServe = gamepad1.dpad_down;
            if(gamepad1.dpad_down && !prevServe){
                driveStyle = 2;
                telemetry.addLine("Drive Style: Serve Drive");
                telemetry.update();
            }
            prevServe = gamepad1.dpad_down;

            //Dpad Right for Rocket League
            curRocket = gamepad1.dpad_right;
            if(gamepad1.dpad_right && !prevRocket){
                driveStyle = 3;
                telemetry.addLine("Drive Style: Rocket League Drive");
                telemetry.update();
            }
            prevRocket = gamepad1.dpad_right;

            intakeOn = 3;

        telemetry.update();
        }

        waitForStart();
        while (opModeIsActive() && !isStopRequested()) {

            if (intakeOn == 3) {
                OIntake.Intake_startPLUS();
                intakeOn = 2;
            }


            if (driveStyle==1){
                //REG
                strafe = (gamepad1.left_trigger - gamepad1.right_trigger);
                rotate = gamepad1.right_stick_x;
                forward = -gamepad1.left_stick_y;

                telemetry.addData("Drive Style: Regular Drive", driveStyle);
                telemetry.update();

            }else if (driveStyle == 2){
                // Serve
                forward = -gamepad1.left_stick_y;
                strafe = -gamepad1.left_stick_x;
                rotate = gamepad1.right_stick_x;

                telemetry.addData("Drive Style: Serve Drive", driveStyle);
                telemetry.update();

            }else if (driveStyle == 3){
                // Rocket
                forward = -gamepad1.left_stick_y * gamepad1.right_trigger;
                strafe = -gamepad1.left_stick_x * gamepad1.right_trigger;
                rotate = gamepad1.right_stick_x * gamepad1.right_trigger;

                telemetry.addData("Drive Style: Rocket League", driveStyle);
                telemetry.update();

            }else{

                //REG
                strafe = (gamepad1.left_trigger - gamepad1.right_trigger) * 2;
                rotate = gamepad1.right_stick_x * 0.90;
                forward = -gamepad1.left_stick_y * 2;
                /*
                // Rocket
                forward = -gamepad1.left_stick_y * gamepad1.right_trigger;
                strafe = -gamepad1.left_stick_x * gamepad1.right_trigger;
                rotate = gamepad1.right_stick_x * gamepad1.right_trigger;*/
                /*// Serve
                forward = -gamepad1.left_stick_y;
                strafe = -gamepad1.left_stick_x;
                rotate = gamepad1.right_stick_x;*/

                /*telemetry.addLine("Did you click the dpad during init?");
                telemetry.update();*/
            }


            //DRIVER (gamepad 1)

             /*
             * Right Bumper: Switch Mode
             * Dpad Up: BO_Shooter
             * A: Turtle Mode
             * B: BO_Intake
             * X: Toggle Hanger
             * Y: Hanger Down
             */

            //Robot drives





            //Driving controls (driver)

            //Slows down the robot for the driver
            if(gamepad1.a&&!Previous1A) {
                TurtleMode=!TurtleMode;
                Previous1A=true;
            }
            if(!gamepad1.a) {
                Previous1A=false;
            }
            if(TurtleMode) {
               /* strafe*=0.35; //.35
                forward*=0.3; //.3
                rotate*=0.25; //.25 */

                strafe*=0.2;
                forward*=0.125;
                rotate*=0.2;

                drive.leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            //Switches forwards and backwards for the driver
            /*if(gamepad1.right_bumper&&!Previous1RB) {
                SwichMOde=!SwichMOde;
                Previous1RB=true;
            }
            if(!gamepad1.right_bumper) {
                Previous1RB=false;
            }
            if(SwichMOde) {
                drive.leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                drive.rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }*/


            //Rotate the robot 180
            if(gamepad1.right_stick_button && !prevRstickB){
                rotate = 180;
            }
            prevRstickB = gamepad1.right_stick_button;

            if(gamepad1.left_stick_button && !prevLstickB){
                rotate = -180;
            }
            prevLstickB = gamepad1.left_stick_button;

            drive.setDrivePowers(
                    new PoseVelocity2d(new Vector2d(
                            forward,
                            strafe),
                            -rotate
                    ));

            /*drive.leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            drive.leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            drive. rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            drive.rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);*/

            drive.leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            drive.leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            drive.rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            drive.rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            //Function contol (driver)
            //BO_Intake
            nowIntake = gamepad1.x;
            if (nowIntake && nowIntake != preIntake) {
                // Normal suction
                if (stateIntake == 1) {
                    OIntake.Intake_inverse();
                    telemetry.addData("BO_Intake REVERSED;",stateIntake);
                }
                // Stop
                else if (stateIntake == 2) {
                    OIntake.Intake_stop();
                    telemetry.addData("BO_Intake STOPPED;",stateIntake);
                }
                else if (stateIntake == 3) {
                    OIntake.Intake_startPLUS();
                    telemetry.addData("BO_Intake ON;",stateIntake);
                }
                stateIntake += 1;
                if (stateIntake > 3) {
                    stateIntake = 1;
                }
            }
            preIntake = nowIntake;



            //End Game controls (driver)
            //BO_Shooter
            if(gamepad2.left_trigger > 0.999){
                BO_Shooter.shoot();
                telemetry.speak("Shot");
            }
            //aPrev



           /* if(gamepad2.y==true){
                BO_Intake.MoveIUp();
            }else{
                BO_Intake.MoveIDown();
            }*/


            //UP1Prev = gamepad1.right_bumper;

            if(gamepad2.a /*&& !UP1Prev*/){
                BO_Intake.MoveIUp();
                //BO_Intake.ToggleIntakeUPDOWN();
            }else{
                BO_Intake.MoveIDown();
            }
            //UP1Prev = gamepad2.a;

           // DOWN1Prev = gamepad1.left_bumper;

           /* if(gamepad1.right_bumper && !DOWN1Prev){
                BO_Intake.MoveIDown();
            }
            DOWN1Prev = gamepad1.right_bumper;*/

            /*if(gamepad1.dpad_up && !aPrev){
                BO_Shooter.shoot();
            }
            aPrev = gamepad1.dpad_up;*/


            // Hanger
            if(gamepad1.y && !yPrev){
                OIntake.Intake_stop();
                OLift.toggleHanger();
                OLift.Hcheck();
            }
            yPrev = gamepad1.y;

            yCur1 = gamepad1.b;
            if(gamepad1.b && !yPrev1){
                OLift.Zero();
                OLift.Hcheck();
            }
            yPrev1 = gamepad1.b;


            yPrev = gamepad2.a;

           /* yCur12 = gamepad2.b;
            if(gamepad2.b && !yPrev3){

                OBucketFunctions.FarBucketOut();
            }
            yPrev3 = gamepad2.b;*/



            // OPERATOR (gamepad 2)
            /*
             * Dpad Up: BO_Lift to 2
             * Dpad Left: BO_Lift to 1
             * Dpad Right: BO_Lift to 1
             * Dpad Down: BO_Lift to 0
            *  X: BucketGate
            *  Y: Edit
            * */

            //BO_Lift
            //Manual LIFT
            if(gamepad2.right_bumper){
                OLift.raiseLiftManual();
                OLift.check();
                OBucketFunctions.BucketGateOut();

            }
            if(gamepad2.left_bumper){
                OLift.lowerLiftManual();
                OLift.check();
                OBucketFunctions.BucketGateOut();

            }


            //Set Pos LIFT
            //BO_Lift to high
            if(gamepad2.dpad_up){
                OLift.Lift_To_Position(3);
                OLift.check();
                OBucketFunctions.BucketOut();
                OBucketFunctions.BucketGateOut();
                OIntake.Intake_stop();
            }

            //BO_Lift to middle
            if(gamepad2.dpad_right){
                OLift.Lift_To_Position(2);
                OLift.check();
                OBucketFunctions.BucketOut();
                OBucketFunctions.BucketGateOut();
                OIntake.Intake_stop();

            }
            if(gamepad2.dpad_left){
                OLift.Lift_To_Position(1);
                OLift.check();
                OBucketFunctions.BucketOut();
                OBucketFunctions.BucketGateOut();
                OIntake.Intake_stop();

            }

            //BO_Lift to ground
            if(gamepad2.dpad_down){
                OLift.Lift_To_Position(0);
                OLift.check();
                OBucketFunctions.BucketIn();
                OBucketFunctions.BucketGateOut();
                OIntake.Intake_startPLUS();
            }

            //Edit Mode (OLift)
           /* if(gamepad2.y && !yPrev2){
                OLift.Lift_To_Position(1);
                OLift.check();
                OBucketFunctions.BucketOut();
                OBucketFunctions.BucketGateOut();
                OIntake.Intake_stop();
            }
            yPrev2 = gamepad2.y;*/


            //BO_Bucket
            if(gamepad2.right_trigger > 0.6){
               OBucketFunctions.BucketGateIn();
            }else{
                OBucketFunctions.BucketGateOut();
            }
           // xPrev = gamepad2.x;


            //Encoder Positions
            telemetry.addData("LiftEncoders", OLift.getLiftPosition());
            telemetry.addData("HangerEncoders", OLift.getHangerPosition());

            //Seperator
            telemetry.addLine("");

            //Drive (deadwheel) Positions
            telemetry.addLine("INTAKE POSITIONS");
            telemetry.addLine("_______________");
            telemetry.addLine("");

            if(BO_Intake.IsIntakeDown){
                telemetry.addLine("Intake : DOWN");
            }else{
                telemetry.addLine("Intake : UP");
            }

            if(stateIntake ==2){
                telemetry.addLine("Flails : REVERSED");

            }else if(stateIntake == 3){telemetry.addLine("Flails : OFF");}
            else{
                telemetry.addLine("Flails : NORMAL");
            }

            telemetry.log();


            //telemetry.speak("NERDS!!!");
            telemetry.update();
        }
    }
}