package com.example.hermes_travelapp.domain

/**
 * Represents a travel destination surfaced in the Explore Screen.
 * Fetched from a remote catalogue.
 */
data class RecommendedPlace(
    val id: Int,
    val name: String,
    val location: String,
    val description: String,
    val imageUrl: String = ""
) {

    /**
     * Future feature: Fetches the full catalogue of recommended places from the backend.
     */
    fun fetchAll(): List<RecommendedPlace> {
        // @TODO Call PlacesRepository.getRecommended() via API
        return emptyList()
    }

    /**
     * Future feature: Searches the catalogue by a query string.
     */
    fun search(query: String): List<RecommendedPlace> {
        // @TODO Delegate search logic to PlacesRepository
        return emptyList()
    }

    /**
     * Future feature: Saves this place as a favourite for the given user.
     */
    fun saveToFavorites(userId: String) {
        // @TODO Convert to FavoritePlace and persist via FavoritesRepository
    }

    /**
     * Future feature: Adds this place as an activity in a trip's itinerary.
     */
    fun addToTrip(tripId: String) {
        // @TODO Create ItineraryItem from this data and persist
    }
}