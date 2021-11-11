package com.example.shopcrawler.controller;

import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class FileCreator {
    public void createFile (StringBuilder sbIn, String outputFileName){
        try (PrintWriter writer = new PrintWriter(outputFileName)){
            writer.write(sbIn.toString());
            JOptionPane.showMessageDialog(null, "File out with name : "+outputFileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error encountered while creating file: "+outputFileName);
        }
    }
}
