package com.rizqi.wideloc.utils

object MatrixUtils {
    fun transposeMatrix(matrix: List<List<Double>>): List<List<Double>> =
        List(matrix[0].size) { col -> List(matrix.size) { row -> matrix[row][col] } }

    fun multiply2DMatrix(a: List<List<Double>>, b: List<List<Double>>): List<List<Double>> {
        val rowsA = a.size
        val colsA = a[0].size
        val colsB = b[0].size
        return List(rowsA) { i ->
            List(colsB) { j ->
                (0 until colsA).sumOf { k -> a[i][k] * b[k][j] }
            }
        }
    }

    fun multiplyMatrix(a: List<List<Double>>, b: List<Double>): List<Double> {
        return a.map { row -> row.zip(b).sumOf { (a, b) -> a * b } }
    }

    fun invertMatrix(matrix: List<List<Double>>): List<List<Double>> {
        val n = matrix.size
        val a = Array(n) { matrix[it].toDoubleArray() }
        val inv = Array(n) { i ->
            DoubleArray(n) { j ->
                if (i == j) 1.0 else 0.0
            }
        }

        for (i in 0 until n) {
            var factor = a[i][i]
            if (factor == 0.0) throw ArithmeticException("Singular matrix")
            for (j in 0 until n) {
                a[i][j] /= factor
                inv[i][j] /= factor
            }
            for (k in 0 until n) {
                if (k != i) {
                    factor = a[k][i]
                    for (j in 0 until n) {
                        a[k][j] -= factor * a[i][j]
                        inv[k][j] -= factor * inv[i][j]
                    }
                }
            }
        }
        return inv.map { it.toList() }
    }
}