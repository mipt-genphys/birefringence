package ru.mipt.physics.birefringence

/**
 * Created by darksnake on 08-Jun-16.
 */
class Vector(val values: Array<Double>) {

    fun size(): Int {
        return values.size;
    }

    operator fun get(i: Int): Double {
        return values[i];
    }

    private fun transform(trans: (Double, Int) -> Double): Vector {
        val newValues = Array<Double>(size()) { i -> trans(values[i], i) };
        return Vector(newValues);
    }

    /**
     * Sum of vector values
     * @return
     */
    fun sum(): Double {
        var res = 0.0;
        for (v in values) {
            res += v;
        }
        return res;
    }

    //Reloading operators to work with vectors

    operator fun plus(other: Vector): Vector {
        return transform { entry, i -> entry + other[i] }
    }

    operator fun plus(number: Number): Vector {
        return transform { entry, i -> entry + number.toDouble() }
    }

    operator fun minus(other: Vector): Vector {
        return transform { entry, i -> entry - other[i] }
    }

    operator fun minus(number: Number): Vector {
        return transform { entry, i -> entry - number.toDouble() }
    }

    operator fun times(other: Vector): Vector {
        return transform { entry, i -> entry * other[i] }
    }

    operator fun times(number: Number): Vector {
        return transform { entry, i -> entry * number.toDouble() }
    }

    operator fun div(other: Vector): Vector {
        return transform { entry, i -> entry / other[i] }
    }

    operator fun div(number: Number): Vector {
        return transform { entry, i -> entry / number.toDouble() }
    }

    infix fun pow(pow: Number): Vector {
        return transform { entry, i -> Math.pow(entry, pow.toDouble()) }
    }

    operator fun unaryMinus(): Vector {
        return transform { entry, i -> -entry }
    }

    fun sin(): Vector {
        return transform { entry, i -> Math.sin(entry) }
    }

    fun cos(): Vector {
        return transform { entry, i -> Math.cos(entry) }
    }

    fun sqrt(): Vector {
        return transform { entry, i -> Math.sqrt(entry) }
    }
}