package com.github.richteaman.bot.services;

public class PidController {

    /**
     * Previous error.
     */
    public double PreviousError = 0;

    /**
     * Proportional gain.
     */
    public double Kp;

    /**
     * Integral gain.
     */
    public double Ki;

    /**
     * Derivative gain.
     */
    public double Kd;

    /**
     * Minimum output.
     */
    public double MinOutput = Double.MIN_VALUE;

    /**
     * Maximum output.
     */
    public double MaxOutput = Double.MAX_VALUE;

    /**
     * Integral.
     */
    public double Integral = 0;


    public double control(double desiredValue, double measuredValue, int durationMilliseconds) {
        double error = desiredValue - measuredValue;
        Integral = Integral + (error * durationMilliseconds);
        clampIntegral();
        double derivative = 0;
        if (durationMilliseconds > 0) {
            derivative = (error - PreviousError) / (double) durationMilliseconds;
        }
        double output = (Kp * error) + (Ki * Integral) + (Kd * derivative);
        PreviousError = error;

        output = Math.max(MinOutput, output);
        output = Math.min(MaxOutput, output);

        return output;
    }

    private void clampIntegral() {

        Integral = Math.min(1.0 / Ki, Math.max(-1.0 / Ki, Integral));

    }

}

