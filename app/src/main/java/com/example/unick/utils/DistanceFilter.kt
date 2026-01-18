package com.example.unick.utils

import kotlin.math.*

/**
 * ONE-FILE distance + filtering helper
 * - Uses Haversine (accurate enough for km radius filtering)
 * - No separate backend/repo needed
 */

/** Returns distance in KM between two geo points */
fun distanceInKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

/**
 * Filters items within a radius in KM.
 *
 * Example:
 * val result = filterWithinRadius(
 *    userLat, userLng, 5.0, schools,
 *    latOf = { it.lat }, lngOf = { it.lng }
 * )
 */
fun <T> filterWithinRadius(
    userLat: Double,
    userLng: Double,
    radiusKm: Double,
    items: List<T>,
    latOf: (T) -> Double,
    lngOf: (T) -> Double
): List<T> {
    return items.filter { item ->
        val d = distanceInKm(userLat, userLng, latOf(item), lngOf(item))
        d <= radiusKm
    }
}

/**
 * Same as filterWithinRadius BUT returns (item, distanceKm) pairs
 * which is super useful to show: "2.3 km away"
 */
fun <T> filterWithinRadiusWithDistance(
    userLat: Double,
    userLng: Double,
    radiusKm: Double,
    items: List<T>,
    latOf: (T) -> Double,
    lngOf: (T) -> Double
): List<Pair<T, Double>> {
    return items.map { item ->
        item to distanceInKm(userLat, userLng, latOf(item), lngOf(item))
    }.filter { (_, distanceKm) ->
        distanceKm <= radiusKm
    }
}

/**
 * Sorts items by nearest first (optionally after filtering).
 * If you want: sort all schools by nearest, just call this directly.
 */
fun <T> sortByNearest(
    userLat: Double,
    userLng: Double,
    items: List<T>,
    latOf: (T) -> Double,
    lngOf: (T) -> Double
): List<T> {
    return items.sortedBy { item ->
        distanceInKm(userLat, userLng, latOf(item), lngOf(item))
    }
}

/**
 * Utility: checks if a single school is inside radius.
 */
fun isWithinRadius(
    userLat: Double,
    userLng: Double,
    schoolLat: Double,
    schoolLng: Double,
    radiusKm: Double
): Boolean {
    return distanceInKm(userLat, userLng, schoolLat, schoolLng) <= radiusKm
}
