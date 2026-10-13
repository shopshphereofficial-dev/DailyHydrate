package com.dailyhydrate.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Single, process-wide DataStore instance (Preferences flavour). */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_hydrate")

/**
 * Offline persistence layer. Everything is stored on-device; there is no
 * network access anywhere in this app.
 *
 * Entries are serialised into one compact string so the storage schema stays
 * trivial and migration-free for the baseline release.
 */
class WaterRepository(context: Context) {

    private val appContext = context.applicationContext

    private val keyEntries = stringPreferencesKey("entries")
    private val keyGoal = intPreferencesKey("goal_ml")

    /** All recorded entries, newest first. */
    val entries: Flow<List<WaterEntry>> = appContext.dataStore.data.map { prefs ->
        decode(prefs[keyEntries].orEmpty())
    }

    /** The user's daily target in millilitres. */
    val goalMl: Flow<Int> = appContext.dataStore.data.map { prefs ->
        prefs[keyGoal] ?: DEFAULT_GOAL_ML
    }

    suspend fun addEntry(amountMl: Int) {
        if (amountMl <= 0) return
        appContext.dataStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val current = decode(prefs[keyEntries].orEmpty()).toMutableList()
            current.add(WaterEntry(id = now, amountMl = amountMl, timestampMillis = now))
            prefs[keyEntries] = encode(current)
        }
    }

    suspend fun reset() {
        appContext.dataStore.edit { prefs -> prefs.remove(keyEntries) }
    }

    suspend fun setGoal(ml: Int) {
        if (ml <= 0) return
        appContext.dataStore.edit { prefs -> prefs[keyGoal] = ml }
    }

    companion object {
        const val DEFAULT_GOAL_ML = 2500

        // Wire format: "id,amount,timestamp;id,amount,timestamp;..."
        private fun encode(list: List<WaterEntry>): String =
            list.joinToString(separator = ";") { "${it.id},${it.amountMl},${it.timestampMillis}" }

        private fun decode(raw: String): List<WaterEntry> {
            if (raw.isBlank()) return emptyList()
            return raw.split(";").mapNotNull { row ->
                val parts = row.split(",")
                if (parts.size != 3) return@mapNotNull null
                val id = parts[0].toLongOrNull() ?: return@mapNotNull null
                val amount = parts[1].toIntOrNull() ?: return@mapNotNull null
                val ts = parts[2].toLongOrNull() ?: return@mapNotNull null
                WaterEntry(id = id, amountMl = amount, timestampMillis = ts)
            }
        }
    }
}
