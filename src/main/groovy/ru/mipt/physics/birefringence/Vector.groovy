package ru.mipt.physics.birefringence

import groovy.transform.CompileStatic

/**
 * Created by darksnake on 18-May-16.
 */
@CompileStatic
class Vector {
    private double[] values;

    Vector(double[] values) {
        this.values = values
    }


    Vector(List<Double> values) {
        this.values = values;
    }


    int size() {
        values.size();
    }

    Double getAt(int i) {
        return values[i];
    }

    private Vector transform(Closure<Double> trans) {
        double[] newValues = new double[size()];
        values.eachWithIndex { double entry, int i -> newValues[i] = trans(entry, i) }
        return new Vector(newValues);
    }

    double[] values() {
        return values;
    }

    /**
     * Sum of vector values
     * @return
     */
    double sum() {
        return values.sum();
    }

    //Reloading operators to work with vectors

    Vector plus(Vector other) {
        return transform { double entry, int i -> entry + other[i] }
    }

    Vector plus(Number number) {
        return transform { double entry, int i -> entry + number }
    }

    Vector minus(Vector other) {
        return transform { double entry, int i -> entry - other[i] }
    }

    Vector minus(Number number) {
        return transform { double entry, int i -> entry - number }
    }

    Vector multiply(Number number) {
        return transform { double entry, int i -> entry * number }
    }

    Vector multiply(Vector other) {
        return transform { double entry, int i -> entry * other[i] }
    }

    Vector div(Number number) {
        return transform { double entry, int i -> entry / number }
    }

    Vector div(Vector other) {
        return transform { double entry, int i -> entry / other[i] }
    }

    Vector power(Number number) {
        return transform { double entry, int i -> Math.pow(entry, number.doubleValue())}
    }

    Vector negative() {
        return transform { double entry, int i -> -entry }
    }

    Vector sin() {
        return transform { double entry, int i -> Math.sin(entry) }
    }

    Vector cos() {
        return transform { double entry, int i -> Math.cos(entry) }
    }

    Vector sqrt() {
        return transform { double entry, int i -> Math.sqrt(entry) }
    }
}
