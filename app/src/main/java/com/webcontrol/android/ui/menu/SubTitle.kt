package com.webcontrol.android.ui.menu

import android.os.Parcel
import android.os.Parcelable

open class SubTitle : Parcelable {
    var name: String
    var count: Int

    constructor(name: String, count: Int) {
        this.name = name
        this.count = count
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(count)
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()!!
        count = `in`.readInt()
    }

    companion object {
        val CREATOR: Parcelable.Creator<SubTitle> = object : Parcelable.Creator<SubTitle> {
            override fun createFromParcel(source: Parcel): SubTitle {
                return SubTitle(source)
            }

            override fun newArray(size: Int): Array<SubTitle?> {
                return arrayOfNulls(size)
            }
        }
    }
}