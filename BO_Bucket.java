package teamcode;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class BO_Bucket {
    static final double IntakeSide = 0.77;
    static double OutputSide = 0.40;

    static boolean BucketInB = false;
    private static final double ClosedBucketGate = 0;
    private static final double OpenBucketGate = 1;
    static boolean BucketGateClosedB = false;
    static Servo Bucket;
    static Servo BucketGate ;


    public void init(HardwareMap hwMap) {
        Bucket = hwMap.get(Servo.class,"Bucket");
        BucketGate = hwMap.get(Servo.class,"BucketGate");

        Bucket.setPosition(IntakeSide);
        BucketGate.setPosition(0.1);

    }

    //BO_Bucket
    public void BucketOut(){
        Bucket.setPosition(OutputSide);
        BucketInB=true;
    }
    public void FarBucketOut(){
        Bucket.setPosition(0.2);
        BucketInB=true;
    }
    public  void BucketIn(){
        Bucket.setPosition(IntakeSide);
        BucketInB=false;
    }
    public  void ToggleBucket(){
        if(BucketInB){
            BucketIn();
        }else{
            BucketOut();
        }
    }
    public static double getBucketPosition(){ return Bucket.getPosition(); }


    //BucketGate
    public  void BucketGateOut(){
        BucketGate.setPosition(ClosedBucketGate);
        BucketGateClosedB=true;

    }
    public  void BucketGateIn(){
        BucketGate.setPosition(OpenBucketGate);
        BucketGateClosedB=false;
    }
    public  void ToggleBucketGate(){
        if(BucketGateClosedB){
            BucketGateIn();
        }else{
            BucketGateOut();
        }
    }
   /* public void BucketGate(int BucketGatePos) {

        switch(BucketGatePos){
            case 1:
                BucketGate.setPosition(OpenBucket);
                break;
            case 2:
                BucketGate.setPosition(ClosedBucket);
                break;

        }
    }*/
   public class OutBOpen implements Action {
       public void init() {
           BucketIn();}
       @Override
       public boolean run(TelemetryPacket telemetryPacket) {
           BucketOut();
           return false;}
   }

    public class OutB implements Action {
        public void init() {
            BucketToggle();}
        @Override
        public boolean run(TelemetryPacket telemetryPacket) {
            BucketToggle();
            return false;}
    }

    public class OutBG implements Action {
        public void init() {
            BucketGateToggle();}
        @Override
        public boolean run(TelemetryPacket telemetryPacket) {
            BucketGateToggle();
            return false;}
    }

    public class OutBClose implements Action {
        public void init() {
            BucketIn();}
        @Override
        public boolean run(TelemetryPacket telemetryPacket) {
            BucketIn();
            return false;}
    }

    public class OutBGOpen implements Action {
        public void init() {
            BucketGateOut();}
        @Override
        public boolean run( TelemetryPacket telemetryPacket) {
            BucketGateOut();
            return false;}
    }

    public class OutBGClose implements Action {
        public void init() {
            BucketGateIn();}
        @Override
        public boolean run( TelemetryPacket telemetryPacket) {
            BucketGateIn();
            return false;}
    }



    public Action BucketOpen() {
        return new OutBOpen();
    }
    public Action BucketClose() {
        return new OutBClose();
    }
    public Action BucketGateOpen() {
        return new OutBGOpen();
    }
    public Action BucketGateClose() {
        return new OutBGClose();
    }


    public Action BucketToggle() {
        return new OutBGOpen();
    }
    public Action BucketGateToggle() {
        return new OutBGClose();
    }


}