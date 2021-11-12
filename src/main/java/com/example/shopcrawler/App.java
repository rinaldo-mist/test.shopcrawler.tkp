package com.example.shopcrawler;

import javax.swing.JOptionPane;

import com.example.shopcrawler.controller.Crawler;
import com.example.shopcrawler.controller.FileCreator;
import com.example.shopcrawler.controller.PublicVar;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Crawler cr = new Crawler();
        FileCreator fc = new FileCreator();
        try {
            JOptionPane.showMessageDialog(null, "Start processing the Crawler. Please wait a moment.");
            //GETTING DATA
            StringBuilder crtxt = cr.buildFileContent(PublicVar.CONST_URL_IN, PublicVar.CONST_MOST_REVIEW);
            //WRITE TO FILE
            fc.createFile(crtxt, PublicVar.CONST_OUTPUT_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
