package com.github.richteaman.bot.services;

/**
 * PID controller.
 */
public class PidController {

    /**
     * Proportional Tuning Parameter.
     */
    private double kp;

    /**
     * Integral Tuning Parameter.
     */
    private double ki;

    /**
     * Derivative Tuning Parameter.
     */
    double kd;

    private Direction controllerDirection;
    private Proportional pOn;
    private Mode mode;

    private long lastTime;
    private double lastOutput = 0;
    private double outputSum;
    private double lastInput;

    private long SampleTime;
    private double outMin;
    private double outMax;


    /**
     * PID controller constructor.
     *
     * @param Kp                  Proportional tuning parameter.
     * @param Ki                  Integration tuning parameter.
     * @param Kd                  Derivative tuning parameter.
     * @param controllerDirection Controller direction.
     */
    public PidController(double Kp, double Ki, double Kd, Direction controllerDirection) {
        mode = Mode.MANUAL;

        setOutputLimits(0, 255);

        //default Controller Sample Time is 0.1 seconds
        SampleTime = 100;

        setControllerDirection(controllerDirection);
        resetTunings(Kp, Ki, Kd, Proportional.ProportionalOnError);

        lastTime = System.currentTimeMillis() - SampleTime;
    }

    /**
     * Performs a single execution of the PID loop. Returns true if changes have been made, or
     * false if a loop execution was not required.
     *
     * @param input  Actual measurement.
     * @param target Target.
     * @return Boolean.
     */
    public boolean compute(double input, double target) {
        if (mode == Mode.MANUAL) return false;
        long now = System.currentTimeMillis();
        long timeChange = (now - lastTime);
        if (timeChange >= SampleTime) {

            // compute all the working error variables
            double error = target - input;
            double dInput = (input - lastInput);
            outputSum += (ki * error);

            // add proportional on measurement, if P_ON_M is specified
            if (pOn == Proportional.ProportionalOnMeasurement) {
                outputSum -= kp * dInput;
            }

            if (outputSum > outMax) outputSum = outMax;
            else if (outputSum < outMin) outputSum = outMin;

            // add proportional on error if P_ON_E is specified
            double output;
            if (pOn == Proportional.ProportionalOnError) {
                output = kp * error;
            } else {
                output = 0;
            }

            output += outputSum - kd * dInput;

            if (output > outMax) output = outMax;
            else if (output < outMin) output = outMin;
            lastOutput = output;

            lastInput = input;
            lastTime = now;
            return true;
        } else {
            return false;
        }

    }

    /**
     * Reset tunings to specified values.
     *
     * @param Kp           Proportional.
     * @param Ki           Integration.
     * @param Kd           Derivative.
     * @param proportional Proportional gain type.
     */
    public void resetTunings(double Kp, double Ki, double Kd, Proportional proportional) {
        if (Kp < 0 || Ki < 0 || Kd < 0) return;

        pOn = proportional;

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
     * Set Tunings using the last-remembered proportional setting.
     *
     * @param Kp Proportional.
     * @param Ki Integration.
     * @param Kd Derivative.
     */
    public void resetTunings(double Kp, double Ki, double Kd) {
        resetTunings(Kp, Ki, Kd, pOn);
    }

    /**
     * Sets the period, in Milliseconds, at which the calculation is performed.
     */
    public void setSampleTime(int newSampleTime) {
        if (newSampleTime > 0) {
            double ratio = (double) newSampleTime
                    / (double) SampleTime;
            ki *= ratio;
            kd /= ratio;
            SampleTime = (long) newSampleTime;
        }
    }

    /**
     * Set output limits for the controller.
     *
     * @param min Minimum value.
     * @param max Maximum value.
     */
    public void setOutputLimits(double min, double max) {
        if (min >= max) return;
        outMin = min;
        outMax = max;

        if (mode == Mode.AUTOMATIC) {
            if (lastOutput > outMax) lastOutput = outMax;
            else if (lastOutput < outMin) lastOutput = outMin;

            if (outputSum > outMax) outputSum = outMax;
            else if (outputSum < outMin) outputSum = outMin;
        }
    }

    /**
     * Allows the controller mode to be set to manual or automatic
     * when the transition from manual to auto occurs, the controller is
     * automatically initialized.
     *
     * @param mode Mode.
     */
    public void setMode(Mode mode) {
        boolean newAuto = mode == Mode.AUTOMATIC;
        if (this.mode == Mode.MANUAL && mode == Mode.AUTOMATIC) {  /*we just went from manual to auto*/
            initialise();
        }
        this.mode = mode;
    }

    /**
     * Initialise controller.
     */
    private void initialise() {
        outputSum = lastOutput;

        // Ideally this would reread input
        lastInput = 0;

        if (outputSum > outMax) outputSum = outMax;
        else if (outputSum < outMin) outputSum = outMin;
    }

    /**
     * Sets controller direction, adjusting constants as necessary.
     */
    private void setControllerDirection(Direction direction) {
        if (mode == Mode.AUTOMATIC && direction != controllerDirection) {
            kp = (0 - kp);
            ki = (0 - ki);
            kd = (0 - kd);
        }
        controllerDirection = direction;
    }

    /**
     * Gets mode.
     *
     * @return Mode.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Gets direction.
     *
     * @return Direction.
     */
    public Direction getDirection() {
        return controllerDirection;
    }

    /**
     * Gets last output. This is the controller result to pass to the system.
     *
     * @return Output.
     */
    public double getLastOutput() {
        return lastOutput;
    }

    /**
     * Controller mode.
     */
    public enum Mode {
        AUTOMATIC,
        MANUAL
    }

    /**
     * Controller direction.
     */
    public enum Direction {

        DIRECT,
        REVERSE
    }

    /**
     * Proportional gain type.
     */
    public enum Proportional {
        ProportionalOnMeasurement,
        ProportionalOnError
    }


}

