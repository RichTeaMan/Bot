package com.github.richteaman.bot.services;

public class PidController {

    private long millis() {
        return System.currentTimeMillis();
    }

    double dispKp;                // * we'll hold on to the tuning parameters in user-entered
    double dispKi;                //   format for display purposes
    double dispKd;                //

    double kp;                  // * (P)roportional Tuning Parameter
    double ki;                  // * (I)ntegral Tuning Parameter
    double kd;                  // * (D)erivative Tuning Parameter

    Direction controllerDirection;
    Proportional pOn;
    Mode mode;

    //double myInput;              // * Pointers to the Input, Output, and Setpoint variables
    //double myOutput;             //   This creates a hard link between the variables and the
    //double mySetpoint;           //   PID, freeing the user from having to constantly tell us
    //   what these values are.  with pointers we'll just know.

    long lastTime;
    double lastOutput = 0;
    double outputSum, lastInput;

    long SampleTime;
    double outMin, outMax;


    /*Constructor (...)*********************************************************
     *    The parameters specified here are those for for which we can't set up
     *    reliable defaults, so we need to have the user set them.
     ***************************************************************************/
    public PidController(double Kp, double Ki, double Kd, Direction controllerDirection) {
        mode = Mode.MANUAL;

        setOutputLimits(0, 255);                //default output limit corresponds to
        //the arduino pwm limits

        SampleTime = 100;                            //default Controller Sample Time is 0.1 seconds

        setControllerDirection(controllerDirection);
        setTunings(Kp, Ki, Kd, Proportional.ProportionalOnError);

        lastTime = millis() - SampleTime;
    }


    /**
     * This, as they say, is where the magic happens.  this function should be called
     * every time "void loop()" executes.  the function will decide for itself whether a new
     * pid Output needs to be computed.  returns true when the output is computed,
     * false when nothing has been done.
     */
    public boolean compute(double input, double target) {
        if (mode == Mode.MANUAL) return false;
        long now = millis();
        long timeChange = (now - lastTime);
        if (timeChange >= SampleTime) {

            /*Compute all the working error variables*/
            double error = target - input;
            double dInput = (input - lastInput);
            outputSum += (ki * error);

            /*Add Proportional on Measurement, if P_ON_M is specified*/
            if (pOn == Proportional.ProportionalOnMeasurement) {
                outputSum -= kp * dInput;
            }

            if (outputSum > outMax) outputSum = outMax;
            else if (outputSum < outMin) outputSum = outMin;

            /*Add Proportional on Error, if P_ON_E is specified*/
            double output;
            if (pOn == Proportional.ProportionalOnError) {
                output = kp * error;
            } else {
                output = 0;
            }

            /*Compute Rest of PID Output*/
            output += outputSum - kd * dInput;

            if (output > outMax) output = outMax;
            else if (output < outMin) output = outMin;
            lastOutput = output;

            // Remember some variables for next time
            lastInput = input;
            lastTime = now;
            return true;
        } else {
            return false;
        }

    }

    /* SetTunings(...)*************************************************************
     * This function allows the controller's dynamic performance to be adjusted.
     * it's called automatically from the constructor, but tunings can also
     * be adjusted on the fly during normal operation
     ******************************************************************************/
    public void setTunings(double Kp, double Ki, double Kd, Proportional proportional) {
        if (Kp < 0 || Ki < 0 || Kd < 0) return;

        pOn = proportional;

        dispKp = Kp;
        dispKi = Ki;
        dispKd = Kd;

        double SampleTimeInSec = ((double) SampleTime) / 1000;
        kp = Kp;
        ki = Ki * SampleTimeInSec;
        kd = Kd / SampleTimeInSec;

        if (controllerDirection == Direction.REVERSE) {
            kp = (0 - kp);
            ki = (0 - ki);
            kd = (0 - kd);
        }
    }

    /**
     * Set Tunings using the last-rembered POn setting.
     *
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setTunings(double Kp, double Ki, double Kd) {
        setTunings(Kp, Ki, Kd, pOn);
    }

    /**
     * Sets the period, in Milliseconds, at which the calculation is performed.
     */
    public void setSampleTime(int NewSampleTime) {
        if (NewSampleTime > 0) {
            double ratio = (double) NewSampleTime
                    / (double) SampleTime;
            ki *= ratio;
            kd /= ratio;
            SampleTime = (long) NewSampleTime;
        }
    }

    /**
     * This function will be used far more often than SetInputLimits.  while
     * the input to the controller will generally be in the 0-1023 range (which is
     * the default already,)  the output will be a little different.  maybe they'll
     * be doing a time window and will need 0-8000 or something.  or maybe they'll
     * want to clamp it from 0-125.  who knows.  at any rate, that can all be done
     * here.
     */
    public void setOutputLimits(double Min, double Max) {
        if (Min >= Max) return;
        outMin = Min;
        outMax = Max;

        if (mode == Mode.AUTOMATIC) {
            if (lastOutput > outMax) lastOutput = outMax;
            else if (lastOutput < outMin) lastOutput = outMin;

            if (outputSum > outMax) outputSum = outMax;
            else if (outputSum < outMin) outputSum = outMin;
        }
    }

    /**
     * Allows the controller Mode to be set to manual (0) or Automatic (non-zero)
     * when the transition from manual to auto occurs, the controller is
     * automatically initialized.
     */
    public void setMode(Mode mode) {
        boolean newAuto = mode == Mode.AUTOMATIC;
        if (this.mode == Mode.MANUAL && mode == Mode.AUTOMATIC) {  /*we just went from manual to auto*/
            initialize();
        }
        this.mode = mode;
    }

    /* Initialize()****************************************************************
     *	does all the things that need to happen to ensure a bumpless transfer
     *  from manual to automatic mode.
     ******************************************************************************/
    void initialize() {
        outputSum = lastOutput;

        // Ideally this would reread input
        lastInput = 0;

        if (outputSum > outMax) outputSum = outMax;
        else if (outputSum < outMin) outputSum = outMin;
    }

    /**
     * The PID will either be connected to a DIRECT acting process (+Output leads
     * to +Input) or a REVERSE acting process(+Output leads to -Input.)  we need to
     * know which one, because otherwise we may increase the output when we should
     * be decreasing.  This is called from the constructor.
     */
    void setControllerDirection(Direction direction) {
        if (mode == Mode.AUTOMATIC && direction != controllerDirection) {
            kp = (0 - kp);
            ki = (0 - ki);
            kd = (0 - kd);
        }
        controllerDirection = direction;
    }

    /* Status Funcions*************************************************************
     * Just because you set the Kp=-1 doesn't mean it actually happened.  these
     * functions query the internal state of the PID.  they're here for display
     * purposes.  this are the functions the PID Front-end uses for example
     ******************************************************************************/
    public double getKp() {
        return dispKp;
    }

    public double getKi() {
        return dispKi;
    }

    public double getKd() {
        return dispKd;
    }

    public Mode getMode() {
        return mode;
    }

    public Direction getDirection() {
        return controllerDirection;
    }

    public double getLastOutput() {
        return lastOutput;
    }

    public enum Mode {
        AUTOMATIC,
        MANUAL
    }

    public enum Direction {

        DIRECT,
        REVERSE
    }

    public enum Proportional {
        ProportionalOnMeasurement,
        ProportionalOnError
    }


}

