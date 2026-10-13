package com.dailyhydrate.app.data

/**
 * A single logged intake event.
 *
 * @param id stable identity (millis at creation) used as a list key.
 * @param amountMl volume consumed, in millilitres.
 * @param timestampMillis epoch millis at which the entry was recorded.
 */
data class WaterEntry(
    val id: Long,
    val amountMl: Int,
    val timestampMillis: Long
)
