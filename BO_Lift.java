package teamcode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BO_Lift {


    final int groundlevel  = 3;
    final int low     = 2920;
    final int med     = 4050;
    final int high    = 5935;

    final int MaxLiftH = 8000;
    final int MinLiftH = -7000;
    double MANUAL_LIFT_SPEED = 1;
    private int lift_target_position = 0;

    private int hanger_target_position = 0;
    double HANGER_SPEED = 0.75;
    static boolean HangerDown = false;



    static DcMotor liftM;
    DcMotor hangerM;
    DigitalChannel limit;
    boolean InitLimitWhile = true;
    boolean LimitWhile = true;

    //public testLift(HardwareMap hwMap) {
    public void init(HardwareMap hwMap) {

        limit = hwMap.get(DigitalChannel.class, "Limit"); // digitalTouch.getState() == ture

        liftM = hwMap.get(DcMotor.class, "liftM");
        liftM.setPower(0);

        liftM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        while(InitLimitWhile){
        liftM.setTargetPosition(liftM.getCurrentPosition() - 375);
            liftM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftM.setPower(MANUAL_LIFT_SPEED);
        if(limit.getState()) {
            InitLimitWhile = false;
        }}
        liftM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        liftM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        hangerM = hwMap.get(DcMotor.class, "hangerM");
        hangerM.setPower(0);

        hangerM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hangerM.setTargetPosition(-15);
        hangerM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hangerM.setPower(HANGER_SPEED);

    }

    public void check() {
        if (lift_target_position < MinLiftH)
            lift_target_position = MinLiftH;

        if (lift_target_position > MaxLiftH)
            lift_target_position = MaxLiftH;

        liftM.setTargetPosition(lift_target_position);
        liftM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftM.setPower(MANUAL_LIFT_SPEED);
    }

    public void Hcheck() {
        hangerM.setTargetPosition(hanger_target_position);
        hangerM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hangerM.setPower(HANGER_SPEED);
    }




    public void raiseLiftManual(){
        lift_target_position = liftM.getCurrentPosition()+170;
    }
    public void resetToZero(){
        lift_target_position = 0;
    }


    public void lowerLiftManual(){
        lift_target_position=liftM.getCurrentPosition()-170;
    }
    public int getLiftTargetPosition(){
        return lift_target_position;
    }




    public void resetHanger(){
        hanger_target_position = 1300;
        HangerDown=false;
    }
    public void upHanger(){
        hanger_target_position = 4400;
        HangerDown=true;

    }
    public void Zero(){
        hanger_target_position = 0;
    }
    public int getHangerTargetPosition(){
        return hanger_target_position;
    }

    public  void toggleHanger(){
        if(HangerDown){
            resetHanger();
        }else{
            upHanger();
        }
    }





    public void Auto1() {
        lift_target_position = 2300;

    }

    public void Auto2() {
        lift_target_position = 2600;

    }

    public void Auto3() {
        lift_target_position = 2900;

    }

    public void Auto4() {
        lift_target_position = 3200;

    }


    public void Lift_To_Position(int LiftPosition) {
        switch (LiftPosition) {
            case 0:
                lift_target_position = groundlevel;
                    /*while(LimitWhile){
                        liftM.setTargetPosition(liftM.getCurrentPosition() - 375);
                        liftM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        liftM.setPower(MANUAL_LIFT_SPEED);
                        if(limit.getState()) {
                            liftM.setTargetPosition(liftM.getCurrentPosition() - 50);
                            liftM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            liftM.setPower(MANUAL_LIFT_SPEED);
                            LimitWhile = false;
                        }} LimitWhile = true;*/

                break;
            case 1:
                lift_target_position = low;
                break;
            case 2:
                lift_target_position = med;
                break;
            case 3:
                lift_target_position = high;
                break;


        }
    }

    public void AutoZero() {
        liftM.setTargetPosition(-20);
        liftM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftM.setPower(MANUAL_LIFT_SPEED);
    }


    public boolean LiftSafety(){
        if(liftM.getCurrentPosition() > MaxLiftH){
            return true;
        }else{
            return false;
        }
    }


    public class liftToOne implements Action {
        public void init() {Lift_To_Position(1);}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Auto1();
            check();
            return false;}

    }

    public class liftToTwo implements Action {
        public void init() {}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Auto2();
            check();
            return false;}

    }public class liftToThree implements Action {
        public void init() {}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Auto3();
            check();
            return false;}
    }
    public class liftToFour implements Action {
        public void init() {}
        public boolean loop(TelemetryPacket packet) {return false;}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Auto4();
            check();
            return false;}
    }

    public class liftToZero implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            AutoZero();
            return false;}
    }


    public Action Pos0() {
        return new liftToZero();
    }
    public Action Pos1() {
        return new liftToOne();
    }
    public Action Pos2() {
        return new liftToTwo();
    }
    public Action Pos3() {
        return new liftToThree();
    }
    public Action Pos4() {
        return new liftToFour();
    }

    public static double getLiftPosition(){ return liftM.getCurrentPosition(); }
    public double getHangerPosition(){ return hangerM.getCurrentPosition(); }

}
