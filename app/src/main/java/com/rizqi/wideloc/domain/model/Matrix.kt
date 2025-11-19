package com.rizqi.wideloc.domain.model

import kotlin.math.abs

class Matrix(val rows: Int, val cols: Int) {
    private var data: Array<DoubleArray>

    constructor(array: Array<DoubleArray>) : this(array.size, array[0].size) {
        data = Array(rows) { i ->
            DoubleArray(cols) { j ->
                array[i][j]
            }
        }
    }

    init {
        data = Array(rows) { DoubleArray(cols) }
    }

    operator fun get(i: Int, j: Int): Double {
        return data[i][j]
    }

    operator fun set(i: Int, j: Int, value: Double) {
        data[i][j] = value
    }

    operator fun times(other: Matrix): Matrix {
        if (cols != other.rows) {
            throw IllegalArgumentException("Matrix dimensions don't match for multiplication")
        }

        val result = Matrix(rows, other.cols)
        for (i in 0 until rows) {
            for (j in 0 until other.cols) {
                for (k in 0 until cols) {
                    result[i, j] += this[i, k] * other[k, j]
                }
            }
        }
        return result
    }

    operator fun plus(other: Matrix): Matrix {
        if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix dimensions don't match for addition")
        }

        val result = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i, j] = this[i, j] + other[i, j]
            }
        }
        return result
    }

    operator fun minus(other: Matrix): Matrix {
        if (rows != other.rows || cols != other.cols) {
            throw IllegalArgumentException("Matrix dimensions don't match for subtraction")
        }

        val result = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i, j] = this[i, j] - other[i, j]
            }
        }
        return result
    }

    operator fun times(scalar: Double): Matrix {
        val result = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i, j] = this[i, j] * scalar
            }
        }
        return result
    }

    fun transpose(): Matrix {
        val result = Matrix(cols, rows)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[j, i] = this[i, j]
            }
        }
        return result
    }

    fun inverse(): Matrix {
        if (rows != cols) {
            throw IllegalArgumentException("Only square matrices can be inverted")
        }

        if (rows == 2 && cols == 2) {
            // Special case for 2x2 matrix
            val a = this[0, 0]
            val b = this[0, 1]
            val c = this[1, 0]
            val d = this[1, 1]

            val determinant = a * d - b * c
            if (determinant == 0.0) {
                throw IllegalArgumentException("Matrix is singular and cannot be inverted")
            }

            return Matrix(arrayOf(
                doubleArrayOf(d / determinant, -b / determinant),
                doubleArrayOf(-c / determinant, a / determinant)
            ))
        } else {
            // General case using Gaussian elimination
            val augmented = Matrix(rows, cols * 2)
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    augmented[i, j] = this[i, j]
                }
                augmented[i, i + cols] = 1.0
            }

            // Gaussian elimination
            for (i in 0 until rows) {
                // Find pivot row
                var maxRow = i
                for (k in i + 1 until rows) {
                    if (abs(augmented[k, i]) > abs(augmented[maxRow, i])) {
                        maxRow = k
                    }
                }

                // Swap rows
                if (maxRow != i) {
                    for (j in 0 until 2 * cols) {
                        val temp = augmented[i, j]
                        augmented[i, j] = augmented[maxRow, j]
                        augmented[maxRow, j] = temp
                    }
                }

                // Pivot is zero -> matrix is singular
                if (abs(augmented[i, i]) < 1e-10) {
                    throw IllegalArgumentException("Matrix is singular and cannot be inverted")
                }

                // Eliminate column i
                for (k in 0 until rows) {
                    if (k != i) {
                        val factor = augmented[k, i] / augmented[i, i]
                        for (j in i until 2 * cols) {
                            augmented[k, j] -= factor * augmented[i, j]
                        }
                    }
                }

                // Normalize row i
                val divisor = augmented[i, i]
                for (j in 0 until 2 * cols) {
                    augmented[i, j] /= divisor
                }
            }

            // Extract inverse matrix
            val inverse = Matrix(rows, cols)
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    inverse[i, j] = augmented[i, j + cols]
                }
            }

            return inverse
        }
    }

    companion object {
        fun identity(size: Int): Matrix {
            val result = Matrix(size, size)
            for (i in 0 until size) {
                result[i, i] = 1.0
            }
            return result
        }
    }
}