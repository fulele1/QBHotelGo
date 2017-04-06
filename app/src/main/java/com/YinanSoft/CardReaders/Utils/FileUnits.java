package com.YinanSoft.CardReaders.Utils;


import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUnits {
    private String SDPath;
    
    public FileUnits(){
        //�õ���ǰ�ⲿ�洢�豸��Ŀ¼
        SDPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }
    
    /**
     * ��SD���ϴ����ļ�
     * @param fileName
     * @return
*/
    public File createSDFile(String fileName){
        File file=new File(SDPath+fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    
    /**
     * ��SD���ϴ���Ŀ¼
     * @param dirName
     * @return
*/
    public File createSDDir(String dirName){
        File file=new File(SDPath+dirName);
        file.mkdir();
        return file;
    }
    
    /**
     * �ж�SD�����ļ��Ƿ����
     * @param fileName
     * @return
*/
    public boolean isFileExist(String fileName){
        File file=new File(SDPath+fileName);
        return file.exists();
    }
    /**
     * ��һ��inputStream���������д��SD����
     * @param path
     * @param fileName
     * @param inputStream
     * @return
*/
    public File writeToSDfromInput(String path,String fileName,InputStream inputStream){
        createSDDir(path);
        File file=createSDFile(path+"/"+fileName);
        OutputStream outStream=null;
        try {
            outStream=new FileOutputStream(file);
            byte[] buffer=new byte[4*1024];
            while(inputStream.read(buffer)!=-1){
                outStream.write(buffer);
            }
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public byte[] readSDFile(String path,String fileName) {
		File file = new File(SDPath+path+"/"+fileName);
		byte[] b = null;
		try {
			@SuppressWarnings("resource")
			FileInputStream inputStream = new FileInputStream(file);
            b = new byte[inputStream.available()];
            inputStream.read(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
}