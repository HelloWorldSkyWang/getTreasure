package com.king.mavenweb.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
 
public class CheckCodeGenUtils {
    public static Random random = new Random();
    public static int randomNum(int min,int max){
        int num=0;
        num = random.nextInt(max-min)+min;
        return num;
    }
    
    public static BufferedImage getImage(int width, int height){
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	
    	//在图片上画一个矩形当背景
        Graphics graphics = img.getGraphics();
        graphics.setColor(new Color(randomNum(50,250),randomNum(50,250),randomNum(50,250)));
        graphics.fillRect(0, 0, width, height);
        
        char[] code = {'0','0','0','0'};
        
        String str = "aqzxswedcfrvgtbhyujklp23456789";
        for(int i=0;i<4;i++){
            graphics.setColor(new Color(randomNum(50,180),randomNum(50,180),randomNum(50,180)));
            graphics.setFont(new Font("黑体",Font.PLAIN,40));
            char contentChar = str.charAt(randomNum(0,str.length()));
            code[i] = contentChar;
            graphics.drawString(String.valueOf(contentChar), 10+i*30, randomNum(height-30,height));
        }
        
        System.out.println(code);
         
        //画随机线
        for(int i=0;i<25;i++){
            graphics.setColor(new Color(randomNum(50,180),randomNum(50,180),randomNum(50,180)));
            graphics.drawLine(randomNum(0,width), randomNum(0,height),randomNum(0,width), randomNum(0,height));
        }
        
    	return img;
    }
    
    
    public static void main(String[] args)  {
        outputPng(null);
    }

	private static void outputPng(String fileName) {
		// TODO Auto-generated method stub
        //在内存中创建一副图片
        int width=120;
        int height=50;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //在图片上画一个矩形当背景
        Graphics graphics = img.getGraphics();
        graphics.setColor(new Color(randomNum(50,250),randomNum(50,250),randomNum(50,250)));
        graphics.fillRect(0, 0, width, height);
         
        String str = "aqzxswedcfrvgtbhyujklp0123456789";
        char[] code = {'0','0','0','0'};
        for(int i=0;i<4;i++){
            graphics.setColor(new Color(randomNum(50,180),randomNum(50,180),randomNum(50,180)));
            graphics.setFont(new Font("Times New Roman",Font.PLAIN,40));
            char contentChar = str.charAt(randomNum(0,str.length()));
            graphics.drawString(String.valueOf(contentChar), 10+i*30, randomNum(height-30,height));
            code[i] = contentChar;
        }
        
        System.out.println(code);
         
        //画随机线
        for(int i=0;i<25;i++){
            graphics.setColor(new Color(randomNum(50,180),randomNum(50,180),randomNum(50,180)));
            graphics.drawLine(randomNum(0,width), randomNum(0,height),randomNum(0,width), randomNum(0,height));
        }
        //把内存中创建的图像输出到文件中
        if(fileName == null || "".equalsIgnoreCase(fileName)){
        	fileName = FileNameUtils.generateFileName("img_");
        }
        File file =new File(fileName +".png");
        try {
			if(ImageIO.write(img, "png", file)){
				System.out.println("图片输出完成");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("图片输出出错");
		}
	}
}
