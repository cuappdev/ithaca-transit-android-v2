package com.example.ithaca_transit_android_v2

import android.os.Parcel
import android.os.Parcelable.Creator
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.ithaca_transit_android_v2.models.Location

class LocationAutocomplete() : SearchSuggestion {
    private var data: Location? = null
    private var desc: String? = null

    constructor(parcel: Parcel) : this() {
        desc = parcel.readString()
    }

    constructor(location: Location) : this() {
        data = location
        desc = location.name
    }
    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getBody(): String {
        return desc.toString()
    }

    public fun getPlace(): Location? {
        return data
    }

    companion object CREATOR : Creator<LocationAutocomplete> {
        override fun createFromParcel(parcel: Parcel): LocationAutocomplete {
            return LocationAutocomplete(parcel)
        }

        override fun newArray(size: Int): Array<LocationAutocomplete?> {
            return arrayOfNulls(size)
        }
    }
}