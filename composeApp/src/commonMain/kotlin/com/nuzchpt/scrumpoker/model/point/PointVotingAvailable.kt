package com.nuzchpt.scrumpoker.model.point

enum class PointVotingAvailable(val key: String, val display: String) {
    POINT_FIVE(key = "0.5", display = "0.5"),
    ONE(key = "1", display = "1"),
    TWO(key = "2", display = "2"),
    THREE(key = "3", display = "3"),
    FIVE(key = "5", display = "5"),
    EIGHT(key = "8", display = "8"),
    THIRTEEN(key = "13", display = "13"),
    QUESTIONMARK(key = "?", display = "?");


    companion object {
        fun getPointVotingAvailableList(): List<PointVotingAvailable> = PointVotingAvailable.entries.toList()
    }
}