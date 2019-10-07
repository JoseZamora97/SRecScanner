package com.josezamora.tcscanner.Classes;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class IOCompositions {

    public static List<Composition> recoverCompositions(String path) {
        List<Composition> object;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            object = (List<Composition>) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            object = new ArrayList<>();
        }
        return object;
    }

    public static void saveCompositions(String path, List<Composition> compositions) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(compositions);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void copyPhoto(File src, File dst) throws IOException {
        if(!src.getAbsolutePath().equals(dst.getAbsolutePath())){

            InputStream is = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dst);

            byte[] buff=new byte[1024];
            int len;

            while((len=is.read(buff))>0){
                os.write(buff,0,len);
            }

            is.close();
            os.close();
        }

    }

}
