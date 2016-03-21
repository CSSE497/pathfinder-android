package xyz.thepathfinder.chimneyswap;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class ParcelableChimney implements Parcelable {

    private Chimney chimney;

    public ParcelableChimney(Chimney chimney) {
        this.chimney = chimney;
    }

    public ParcelableChimney(Parcel source) {
        this.chimney = new Chimney();
        this.chimney.setId(source.readInt());
        this.chimney.setName(source.readString());
        this.chimney.setImageUrl(source.readString());
        this.chimney.setImage((Bitmap) source.readParcelable(Bitmap.class.getClassLoader()));
        double lat = source.readDouble();
        double lng = source.readDouble();
        this.chimney.setPosition(new LatLng(lat, lng));
    }

    public Chimney getChimney() {
        return this.chimney;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.chimney.getId());
        dest.writeString(this.chimney.getName());
        dest.writeString(this.chimney.getImageUrl());
        dest.writeParcelable(this.chimney.getImage(), PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeDouble(this.chimney.getPosition().latitude);
        dest.writeDouble(this.chimney.getPosition().longitude);
    }

    public static final Parcelable.Creator<ParcelableChimney> CREATOR = new Parcelable.Creator<ParcelableChimney>() {

        @Override
        public ParcelableChimney createFromParcel(Parcel source) {
            return new ParcelableChimney(source);
        }

        @Override
        public ParcelableChimney[] newArray(int size) {
            return new ParcelableChimney[size];
        }
    };
}
