package ru.mipt.physics.biref

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by darksnake on 08-Jun-16.
 */
class Vector(val values: DoubleArray) {

    fun size(): Int {
        return values.size;
    }

    operator fun get(i: Int): Double {
        return values[i];
    }

    private fun transform(trans: (Double, Int) -> Double): Vector {
        val newValues = DoubleArray(size()) { i -> trans(values[i], i) };
        return Vector(newValues);
    }

    /**
     * Sum of vector values
     * @return
     */
    fun sum(): Double {
        return values.sum();
    }

    //Reloading operators to work with vectors

    operator fun plus(other: Vector): Vector {
        return transform { entry, i -> entry + other[i] }
    }

    operator fun plus(number: Number): Vector {
        return transform { entry, _ -> entry + number.toDouble() }
    }

    operator fun minus(other: Vector): Vector {
        return transform { entry, i -> entry - other[i] }
    }

    operator fun minus(number: Number): Vector {
        return transform { entry, _ -> entry - number.toDouble() }
    }

    operator fun times(other: Vector): Vector {
        return transform { entry, i -> entry * other[i] }
    }

    operator fun times(number: Number): Vector {
        return transform { entry, _ -> entry * number.toDouble() }
    }

    operator fun div(other: Vector): Vector {
        return transform { entry, i -> entry / other[i] }
    }

    operator fun div(number: Number): Vector {
        return transform { entry, _ -> entry / number.toDouble() }
    }

    infix fun pow(pow: Number): Vector {
        return transform { entry, _ -> entry.pow(pow.toDouble()) }
    }

    operator fun unaryMinus(): Vector {
        return transform { entry, _ -> -entry }
    }

    val sin: Vector get() = transform { entry, _ -> sin(entry) }

    val cos: Vector get() = transform { entry, _ -> cos(entry) }

    val sqrt: Vector get() = transform { entry, _ -> sqrt(entry) }

}