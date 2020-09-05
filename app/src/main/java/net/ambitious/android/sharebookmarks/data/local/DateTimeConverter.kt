package net.ambitious.android.sharebookmarks.data.local

import androidx.room.TypeConverter
import org.joda.time.DateTime

class DateTimeConverter {
  @TypeConverter
  fun fromTimestamp(value: Long?) = value?.let {
    DateTime(value)
  }

  @TypeConverter
  fun toTimestamp(date: DateTime?) = date?.toInstant()?.millis
}