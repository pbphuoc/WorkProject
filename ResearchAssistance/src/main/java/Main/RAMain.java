/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import uq.researchassistance.M.DataCrawlerView;
import uq.researchassistance.V.ProcessAnalyticalReportView;

/**
 *
 * @author ffpbp
 */
public class RAMain {
    
    //main frame of application
    private static JFrame main_frame;
    private static boolean isRunning = false;
    
    public static void main (String[] args) throws Exception{

            main_frame = new JFrame();
            isRunning = true;
            main_frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    isRunning = false;
                }                
            });
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE); 

            DataCrawlerView dcv = new DataCrawlerView();
            ProcessAnalyticalReportView parv = new ProcessAnalyticalReportView();  

            JTabbedPane tpane = new JTabbedPane();
            tpane.add(dcv, "Data Crawling");
            tpane.add(parv, "Report Processing");

            main_frame.add(tpane, BorderLayout.CENTER);
            main_frame.setResizable(false);
            main_frame.pack();
            main_frame.setTitle("UQ Research Assistance");
            main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            main_frame.setLocationRelativeTo(null);
            main_frame.setVisible(true);    
        
    }
}
