package com.example.hp.imageprocessing;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by HP on 29/9/2017.
 */

public class ImageClass {

    public String name;
    public ArrayList<Bitmap> img;

    public ImageClass(){
        img = new ArrayList<Bitmap>();
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<Bitmap> getImg(){
        return this.img;
    }
    public void  setImg(ArrayList<Bitmap> img){
        this.img=img;
    }
}


