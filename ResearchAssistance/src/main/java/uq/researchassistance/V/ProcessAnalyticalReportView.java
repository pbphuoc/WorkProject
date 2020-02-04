/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.researchassistance.V;

import Utilities.SpringUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author ffpbp
 */
public class ProcessAnalyticalReportView extends JPanel{
    private JPanel pnl_main;
    private JPanel pnl_rp;   
    private JPanel pnl_main_top;
    private JPanel pnl_output; 
    private JPanel pnl_wl;
    private JFileChooser fc_rp;
    private JFileChooser fc_wl;
    private JFileChooser fc_op;
    private JButton btn_open_rp;
    private JButton btn_open_wl;
    private JButton btn_analyse;
    private JTextField txt_para;
    private JLabel lbl_wl;
    private JLabel lbl_output;
    private DefaultTableModel tm_rp;
    private int curRow;
    private TimerTask update_table_task;
    
    public ProcessAnalyticalReportView(){
        this.setPreferredSize(new Dimension(1100,800));
        this.setMaximumSize(new Dimension(1100,800));
        this.setMinimumSize(new Dimension(1100,800));        
        this.setLayout(new BorderLayout());
        
        pnl_main = new JPanel(new SpringLayout());
        pnl_rp = new JPanel(new BorderLayout());
        pnl_main_top = new JPanel();
        JPanel pnl_btn_rp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JPanel pnl_wl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl_output = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pnl_para = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lbl_wl = new JLabel("");
        lbl_output = new JLabel("");
        txt_para = new JTextField();
        btn_open_rp = new JButton("Load..");
        btn_open_wl = new JButton("Load..");
        btn_analyse = new JButton("Analyse..");
        btn_analyse.setEnabled(false);
        fc_rp = new JFileChooser();
        fc_rp.setMultiSelectionEnabled(true); 
        fc_rp.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc_rp.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc_rp.addChoosableFileFilter(new FileNameExtensionFilter("Document file(.pdf, .doc, .docx)", "pdf", "doc", "docx"));
        fc_rp.setAcceptAllFileFilterUsed(false);
        fc_wl = new JFileChooser();
        fc_wl.setMultiSelectionEnabled(false); 
        fc_wl.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc_wl.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc_wl.addChoosableFileFilter(new FileNameExtensionFilter("Excel file(.xls, .xlsx)", "xls", "xlsx"));
        fc_wl.setAcceptAllFileFilterUsed(false);        
        fc_op = new JFileChooser();
        fc_op.setMultiSelectionEnabled(false); 
        fc_op.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc_op.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc_op.addChoosableFileFilter(new FileNameExtensionFilter("CSV file (.xls)", "csv"));
        fc_op.setAcceptAllFileFilterUsed(true);        
        
        pnl_main.setPreferredSize(new Dimension(1100, 800));
        pnl_main.setMaximumSize(new Dimension(1100, 800));
        pnl_main.setMinimumSize(new Dimension(1100, 800));           
        
        pnl_rp.setBackground(Color.white);
        pnl_rp.setBorder(BorderFactory.createTitledBorder("<html>Select Analyst Report File</html>"));
        pnl_rp.setPreferredSize(new Dimension(1100, 350));
        pnl_rp.setMaximumSize(new Dimension(1100, 350));
        pnl_rp.setMinimumSize(new Dimension(1100, 350));               
        
        pnl_btn_rp.setPreferredSize(new Dimension(1000,30));
        pnl_btn_rp.setMaximumSize(new Dimension(1000,30));
        pnl_btn_rp.setMinimumSize(new Dimension(1000,30));  
        
        pnl_main_top.setLayout(new BoxLayout(pnl_main_top, BoxLayout.X_AXIS));
        pnl_main_top.setPreferredSize(new Dimension(1100, 80));
        pnl_main_top.setMaximumSize(new Dimension(1100, 80));
        pnl_main_top.setMinimumSize(new Dimension(1100, 80));            
        
        pnl_wl.setBackground(Color.white);
        pnl_wl.setBorder(BorderFactory.createTitledBorder("<html>Select Word List File</html>"));  
        pnl_wl.setPreferredSize(new Dimension(500, 80));
        pnl_wl.setMaximumSize(new Dimension(500, 80));
        pnl_wl.setMinimumSize(new Dimension(500, 80));           
        
        pnl_para.setBackground(Color.white);
        pnl_para.setBorder(BorderFactory.createTitledBorder("<html>Parameter N</html>"));    
        pnl_para.setPreferredSize(new Dimension(100, 80));
        pnl_para.setMaximumSize(new Dimension(100, 80));
        pnl_para.setMinimumSize(new Dimension(100, 80));            
        
        pnl_output.setBackground(Color.white);
        pnl_output.setBorder(BorderFactory.createTitledBorder("<html>Analyse Reports</html>"));  
        pnl_output.setPreferredSize(new Dimension(500, 80));
        pnl_output.setMaximumSize(new Dimension(500, 80));
        pnl_output.setMinimumSize(new Dimension(500, 80));           
        
        txt_para.setPreferredSize(new Dimension(50, 25));
        txt_para.setMaximumSize(new Dimension(50, 25));
        txt_para.setMinimumSize(new Dimension(50, 25)); 
        
        txt_para.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ValidateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ValidateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ValidateInput();
            }
        });  
        
        String[] column_names = {"File Name" , "Status"};
        tm_rp = new DefaultTableModel(null, column_names){
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? Integer.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }            
        };
        
        JTable tb_rp = new JTable(tm_rp);   
        tb_rp.setRowSelectionAllowed(false);
        tb_rp.setColumnSelectionAllowed(false);
        tb_rp.setColumnSelectionAllowed(false);
        tb_rp.getColumnModel().getColumn(1).setPreferredWidth(150);
        tb_rp.getColumnModel().getColumn(1).setMaxWidth(150);
        tb_rp.getColumnModel().getColumn(1).setMinWidth(150);          
        JScrollPane spane_rp = new JScrollPane();            
        spane_rp.setViewportView(tb_rp);                 
        pnl_rp.add(spane_rp, BorderLayout.CENTER);        
        
        btn_open_rp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                    
                int opt;
                opt = fc_rp.showOpenDialog(pnl_rp);
                switch (opt)
                {
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.APPROVE_OPTION:                       
                        btn_open_rp.setText("Change..");
                        while(tm_rp.getRowCount()>0)
                        {
                            tm_rp.removeRow(0);
                        }
                        for(File f: fc_rp.getSelectedFiles()){
                           Object[] row = {f.getAbsolutePath(), ""}; 
                           tm_rp.addRow(row);
                        }
                        ValidateInput();
                        break; 
                }
                
            }
        });
        
        btn_open_wl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt;
                opt = fc_wl.showOpenDialog(pnl_wl);
                switch (opt)
                {
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.APPROVE_OPTION:
                        btn_open_wl.setText("Change..");
                        lbl_wl.setText(fc_wl.getSelectedFile().getName());
                        ValidateInput();
                        break; 
                }                 
            }
        });
        
        btn_analyse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt;
                opt = fc_op.showSaveDialog(pnl_output);
                switch (opt)
                {
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.APPROVE_OPTION:
                        curRow = -1;                        
                        UpdateRPTable();
                        {
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    btn_analyse.setEnabled(false);
                                    lbl_output.setText(fc_op.getSelectedFile().getName());
                                    AnalyseReports();                          
                                }
                            };
                            timer.schedule(task, 100);
                        }                        
                        break;
                }                 
            }
        });        
                      
        pnl_wl.add(btn_open_wl);
        pnl_wl.add(lbl_wl);         
        
        pnl_para.add(txt_para);         
        
        pnl_output.add(btn_analyse);
        pnl_output.add(lbl_output);             
                       
        pnl_main_top.add(pnl_wl);
        pnl_main_top.add(Box.createHorizontalGlue());
        pnl_main_top.add(pnl_para);
        pnl_main_top.add(Box.createHorizontalGlue());
        pnl_main_top.add(pnl_output);
                                   
        pnl_btn_rp.add(btn_open_rp);
        pnl_rp.add(pnl_btn_rp, BorderLayout.NORTH);          
           
        pnl_main.add(pnl_main_top);        
        pnl_main.add(pnl_rp);         
        
        this.add(pnl_main, BorderLayout.CENTER);

        SpringUtilities.makeCompactGrid(pnl_main, 2, 1, 0, 0, 0, 0); 
    }       
    
    void UpdateRPTable(){
        update_table_task = new TimerTask() {
            @Override
            public void run() {
                for(int i = 0; i < tm_rp.getRowCount(); i++){
                    String msg = "";
                    if (i < curRow)
                        msg = "Completed";
                    else if (i == curRow)
                        msg = "In Progress";
                    else if (i == curRow + 1)
                        msg = "Next";
                    tm_rp.setValueAt(msg, i, 1);                      
                }
                if(curRow == tm_rp.getRowCount()){
                    System.out.println("cancel update table task");
                    update_table_task.cancel();
                    btn_analyse.setEnabled(true);                    
                }
            }
        };
        new Timer().schedule(update_table_task, 100,1000);         
    }   
    
    void ValidateInput(){
        boolean foundEmpty = true;
        if((fc_wl.getSelectedFile() != null) 
                && ( fc_rp.getSelectedFiles().length != 0)
                && (!txt_para.getText().isEmpty())){
            try{ 
                Integer.parseInt(txt_para.getText());
                foundEmpty = false;
            }catch(NumberFormatException e) {  
                foundEmpty = true;
            }                        
        }
        btn_analyse.setEnabled(!foundEmpty);               
    }    
    
    void AnalyseReports(){
        try {            
            File output_file = fc_op.getSelectedFile();
            //use 2 maps to make the search easier
            //each keyword may contain multiple words, so we use the first word in a keyword as a key of both map
            //1 will contain all words of a keyword as a string, the second one will have it as an array of string
            //first map to quickly get all words of the keyword
            Map<String, String> kw_name_map = new HashMap<>();
            Map<String, String[]> kw_search_map = new HashMap<>();
            //
            Map<String,List<String>> output_map = new LinkedHashMap<String, List<String>>();           
            //parameter N provided by user
            int paraN = Integer.parseInt(txt_para.getText());
            //get keyword file
            File wl_file = fc_wl.getSelectedFile().getAbsoluteFile();
            //loop through excel file to get key words
            try (FileInputStream wl_exfile = new FileInputStream(wl_file)) {
                Workbook wl_workbook = new XSSFWorkbook(wl_exfile);
                Sheet datatypeSheet = wl_workbook.getSheetAt(0);
                Iterator<Row> wl_iterator = datatypeSheet.iterator();
                while (wl_iterator.hasNext()){
                    String kw = wl_iterator.next().getCell(0).toString().toLowerCase();  
                    String[] kw_split = kw.split(" ");
                    kw_name_map.put(kw_split[0], kw);
                    kw_search_map.put(kw_split[0], kw_split);
                    if (!output_file.exists())
                        output_map.put(kw, new ArrayList<String>());
                }
                wl_exfile.close();
            }
            
            if(!output_file.exists()){
                String filename = fc_op.getSelectedFile().getName() + ".csv";
                output_file = new File(filename); 
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(output_file, false), 
                        StandardCharsets.UTF_8))
                { 
                    String headers = "Filename~Filesize~Number of words";
                    for(String key: output_map.keySet())
                    {
                        List<String> output = output_map.get(key);
                        headers += "~" + key + "~" + (key + " location");                        
                    }                    
//                    System.out.println("headers: " + headers);
                    writer.write(headers);
                    writer.write("\n");
                    writer.flush();
                    writer.close();
                }                 
            }else{
                try(BufferedReader reader = new BufferedReader(new FileReader(output_file))){
                    String line = reader.readLine();
                    String[] headers = line.split(",");
//                    System.out.println("headers: " + headers);
                    for(int i = 3; i < headers.length; i+=2){
                        output_map.put(headers[i], new ArrayList<String>());
                    }
                }
            }            
            
            //loop through list of reports to analyse each
            for(int f = 0; f < fc_rp.getSelectedFiles().length; f++)
//            for (File f : fc_rp.getSelectedFiles())
            {
                curRow = f;                
                File rp_file = fc_rp.getSelectedFiles()[f].getAbsoluteFile();
                //map to store keyword with its location in the text
                List<String> t_array = new ArrayList<>();
                for(String key: output_map.keySet()){
                    output_map.get(key).clear();
                }
                //find extension type, then use corresponding library to extract text
                switch(FilenameUtils.getExtension(rp_file.getName())){
                    case "pdf":
                        PDDocument pdf = PDDocument.load(rp_file);
                        if (!pdf.isEncrypted()) {
                            PDFTextStripper stripper = new PDFTextStripper();
                            String text = stripper.getText(pdf).toLowerCase();
                            String[] lines = text.split("\\r?\\n");
                            for(String line : lines){
                                String w_in_line[] = line.split(" ");
                                for(String w: w_in_line){
                                    t_array.add(w.replaceAll("[\\-\\+\\.\\^,;]",""));
                                }
                            }                          
                            
                            //loop through the text array to compare word by word
                            for(int i = 0; i < t_array.size(); ){
                                String cur_w = t_array.get(i);
//                                System.out.println("current word: " + cur_w);
                                //loop through the keyword list to compare
                                if(kw_search_map.containsKey(t_array.get(i))){                                                                      
                                    //count how many single-word of each keyword that matches the text
                                    int match_counter = 0;
                                    //get keyword as an array
                                    String[] kw_array = kw_search_map.get(cur_w);
                                    //get current keyword
                                    String cur_kw = kw_name_map.get(cur_w);
                                    //loop through each word in each keyword
                                    for(int k = 0; k < kw_array.length; k++){
                                        String kw = kw_array[k];
//                                        System.out.println("compare: " + kw + " & " + t_array.get(i+k));
                                        //if match, increase match_counter
                                        if(kw.equalsIgnoreCase(t_array.get(i+k))){
                                            ++match_counter;                                           
                                        }else{
                                            ++i;
                                            break;
                                        }
                                    }
                                    //if all words match, start finding start_index and end_index of the phrase
                                    //containing that keyword in the text
                                    if (match_counter == kw_array.length){
                                        //start at the index of the first word of the matching keyword in the text
                                        int start = i;
                                        //end at the index of the last word of the matching keyword in the text
                                        //from i to i + (match_counter - 1), i.e exclude last one
                                        int end = i + match_counter - 1;
                                        //new start value, shift to the left 1 position
                                        int m = start - 1;
                                        //the parameter provided by user, indicating how many words 
                                        //should be also counted before and after the target keyword
                                        int n = paraN;
                                        //keep shifting until (reaching first index) or (reaching limit of N)
                                        while(m >= 0 && n!= 0){
                                            //assiging m to start, then decrease both m and n by 1
                                            start = m;
                                            --m;
                                            --n;
                                        }
                                        //new end value, shift to the right 1 position
                                        m = end + 1;
                                        n = paraN;                                    
                                        //keep shifting until (reaching last index) or (reaching limit of N)
                                        while(m < t_array.size() && n!= 0){                                
                                            //assigning m to end, then increase m and decrease n by 1
                                            end = m;
                                            ++m;
                                            --n;
                                        }
                                        String output = "";
                                        //getting the phrase from start_index to end_index in the text
                                        for(int l = start; l <= end; l++){
                                            output += t_array.get(l);
                                            if(l != end)
                                                output += " ";
                                        }
                                        //add the output to the output_mapp
                                        if(output_map.containsKey(cur_kw))
                                        {
                                            output_map.get(cur_kw).add(output);
//                                            System.out.println(cur_kw + ": " + output);
                                        }
                                        //getting the index of the next word to be searched by
                                        //shifting current i to the right side, match_counter times
                                        //i.e i = i + match_counter
                                        i += match_counter;
                                    }                                    
                                }else{
                                    ++i;
                                }
                            }
                        }
                        pdf.close();
                        break;
                    case "docx":
                        break;
                    case "doc":
                        break;
                    default:
                        break;
                }

                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(output_file,true), 
                        StandardCharsets.UTF_8))
                {
                    String file_size = (rp_file.length() / 1024) + " kb";
                    int word_num = (t_array == null) ? 0 : t_array.size(); 
                    String row = rp_file.getName() + "~" + file_size + "~" + word_num;                    
                    for(String key: output_map.keySet())
                    {
                        List<String> output = output_map.get(key);
                        row += "~" + output.size() + "~" + output;                        
                    }
                    writer.write(row);
                    writer.write("\n");
                    writer.flush();
                    writer.close();
                }
            }
            curRow = fc_rp.getSelectedFiles().length;
        }catch (IOException ex) {
            Logger.getLogger(ProcessAnalyticalReportView.class.getName()).log(Level.SEVERE, null, ex);
        }           
    }
}
