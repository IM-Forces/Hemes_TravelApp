package com.example.hermes_travelapp.domain

/** * Represents a place saved as a favourite by a user.
 */
data class FavoritePlace(
    val id: Int,
    val name: String,
    val location: String
) {

    /**
     * Future feature: Persists this place as a favourite in local/remote storage.
     */
    fun save(userId: String) {
        // @TODO Implement save logic via FavoritesRepository
    }

    /**
     * Future feature: Removes this place from the user's favourites.
     */
    fun delete(userId: String) {
        // @TODO Implement deletion via FavoritesRepository
    }

    /**
     * Future feature: Converts this favourite into an itinerary item for a trip.
     */
    fun addToTrip(tripId: String) {
        // @TODO Build ItineraryItem from this data and call TripRepository
    }
}