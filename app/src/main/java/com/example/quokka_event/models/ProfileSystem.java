package com.example.quokka_event.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import androidx.annotation.DrawableRes;

import com.google.firebase.storage.StorageReference;

import java.util.Date;

/**
 * A class to handle individual profiles.
 */
public class ProfileSystem {
    @DrawableRes
    private int profileImage;
    private String name;
    private String email;
    private int phoneNumber;
    private Date birthday;
    private String address;
    private String deviceID;
    private StorageReference profileImageRef;

    /**
     * This method creates a Profile System object.
     * @param deviceID
     * @param profileImage
     * @param name
     * @param email
     * @param phoneNumber
     * @param birthday
     * @param address
     */
    public ProfileSystem(String deviceID,@DrawableRes int profileImage, String name, String email, int phoneNumber, Date birthday, String address){
        this.deviceID = deviceID;
        this.profileImage = profileImage;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.address = address;

    }

    // Temporary empty constructor - there might be a better way to do this but not all fields are
    // required so I need an easy way to do that for now
    public ProfileSystem(){}

    /**
     * This method sets profile picture.
     * @param newImage the profile picture
     */
    public void setImage(@DrawableRes int newImage){
        profileImage = newImage;
    }

    /**
     * This method gets the profile picture.
     * @return the profile picture
     */
    @DrawableRes
    public int getImage(){
        return profileImage;
    }

    /**
     * This method sets the name for the profile.
     * @param newName user's name
     */
    public void setName(String newName){
        name = newName;
    }

    /**
     * This method gets the user's name.
     * @return the user's name
     */
    public String getName(){
        return name;
    }

    /**
     * This method sets the user's email.
     * @param newEmail the email of the user
     */
    public void setEmail(String newEmail){
        email = newEmail;
    }

    /**
     * This method gets the email of the user.
     * @return the user's email
     */
    public String getEmail(){
        return email;
    }

    /**
     * This method sets the phone number of the user.
     * @param newPhoneNumber the user's phone number
     */
    public void setPhoneNumber(int newPhoneNumber){
        phoneNumber = newPhoneNumber;
    }

    /**
     * This method gets the user's phone number.
     * @return the phone number of the user
     */
    public int getPhoneNumber(){
        return phoneNumber;
    }

    /**
     * This method sets the user's birthday.
     * @param newBirthday the user's birthday
     */
    public void setBirthday(Date newBirthday) {
        birthday = newBirthday;
    }

    /**
     * This method return's the user's birthday.
     * @return the user's birthday
     */
    public Date getBirthday(){
        return birthday;
    }

    /**
     * This method sets the address of the user.
     * @param newAddress the address of the user
     */
    public void setAddress(String newAddress){
        address = newAddress;
    }

    /**
     * This method gets the address of the user.
     * @return the user's address
     */
    public String getAddress(){
        return address;
    }

    public void setDeviceID(String newID){deviceID = newID;}

    public String getDeviceID(){return deviceID;}

    /**
     * This method generates a profile picture for user's who don't have one.
     * @see "https://stackoverflow.com/questions/5663671/creating-an-empty-bitmap-and-drawing-though-canvas-in-android"
     * @author Soaiba
     * @param name user's name
     * @return the bitmap
     */
    public Bitmap generatePfp(String name) {
        int w = 256, h = 256, radius = 256;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        // Set pfp color
        //https://developer.android.com/reference/android/graphics/Color#HSVToColor(float[])
        //https://stackoverflow.com/questions/33219638/how-to-make-a-hashcodeinteger-value-positive
        int hashCode;
        if (name != null && !name.trim().isEmpty()) {
            hashCode = name.hashCode();
        }
        // If no name is provided
        else {
            hashCode = 0;
        }

        int color = Color.HSVToColor(new float[]{
                (hashCode & 0x7FFFFFFF) % 360,
                1,
                1
        });
        paint.setColor(color);
        //https://stackoverflow.com/questions/17954596/how-to-draw-circle-by-canvas-in-android
        canvas.drawCircle(w / 2f, h / 2f, radius / 2f, paint);

        // Get initials
        //https://stackoverflow.com/questions/64567828/how-to-print-the-first-name-and-initials-of-a-full-name
        String initials = "";
        if (name != null && !name.trim().isEmpty()) {
            String[] nameSplit = name.split("\\s+");
            if (nameSplit.length > 0) {
                initials += nameSplit[0].charAt(0);
            }
            if (nameSplit.length > 1) {
                initials += nameSplit[1].charAt(0);
            }
            initials = initials.toUpperCase();
        }

        if (!initials.isEmpty()) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(w / 3f);
            paint.setTextAlign(Paint.Align.CENTER);
            float x = w / 2f;
            float y = h / 2f - (paint.descent() + paint.ascent()) / 2;
            canvas.drawText(initials, x, y, paint);
        }
        return bmp;
    }

    public StorageReference getProfileImageRef() {
        return profileImageRef;
    }

    public void setProfileImageRef(StorageReference profileImageRef) {
        this.profileImageRef = profileImageRef;
    }
}
