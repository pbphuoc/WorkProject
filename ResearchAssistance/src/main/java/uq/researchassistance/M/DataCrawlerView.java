/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uq.researchassistance.M;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;

/**
 *
 * @author ffpbp
 */
public class DataCrawlerView extends JPanel{
    private final String firmDataFile = "FirmPerformance.csv";
    private final String stockDataFile = "StockData.csv";
//    private String collectDataConfigure = "Configure.csv";
    private final String firmDataLogFile = "FirmPerfLog";
    private final String stockDataLogFile = "StockDataLog";
//    private final String tickerListFile = "VNStockTicker.xlsx";
    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private final int currentQuarter = Calendar.getInstance().get(Calendar.MONTH) / 3;
    private final String tickerKeywordSearchID = "ctl00$ContentPlaceHolder1$ctl03$txtKeyword";
    private final String pagingID1 = "ctl00$ContentPlaceHolder1$ctl03$pager1";
    private final String pagingID2 = "ctl00$ContentPlaceHolder1$ctl03$pager2"; 
    private final String eventTargetID = "__EVENTTARGET";
    private final String eventArgumentID = "__EVENTARGUMENT";
    
    private JScrollPane spane_log = new JScrollPane();;
    private JTextArea txtar_log = new JTextArea();
    private JButton btn_get_stock_data;
    private JButton btn_get_firm_data;
    private JButton btn_load_ticker_list;
    private JLabel lbl_stock_data;
    private JLabel lbl_firm_data;
    private JLabel lbl_ticker_list_data;
    private JFileChooser fc_tl;
    private JPanel pnl_top;
    private LinkedList<String> log_msg = new LinkedList<String>();
    private TimerTask update_log_task;
    private boolean can_crawl_data = false;
    private final String line_seperator = "------------------------------------------------------------------------------------------------------------------------------------";
    private List<String> ticker_list;
//    private boolean is_log_task_cancelled = true;
  
    public DataCrawlerView(){
        this.setPreferredSize(new Dimension(1000,800));
        this.setMaximumSize(new Dimension(1000,800));
        this.setMinimumSize(new Dimension(1000,800));          
        this.setLayout(new BorderLayout());   
        
//        ticker_list = GetTickerList();
        btn_get_stock_data = new JButton("Get Stock Data");
        btn_get_stock_data.setEnabled(false);
        btn_get_firm_data = new JButton("Get Firm Data");
        btn_get_firm_data.setEnabled(false);        
        btn_load_ticker_list = new JButton("Load Ticker List");   
        fc_tl = new JFileChooser();
        fc_tl.setMultiSelectionEnabled(false); 
        fc_tl.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc_tl.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc_tl.addChoosableFileFilter(new FileNameExtensionFilter("Excel file(.xls, .xlsx)", "xls", "xlsx"));
        fc_tl.setAcceptAllFileFilterUsed(false);           
        lbl_stock_data = new JLabel();
        lbl_firm_data = new JLabel();
        lbl_ticker_list_data = new JLabel();
        txtar_log.setEditable(false);
        
        btn_load_ticker_list.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = fc_tl.showOpenDialog(pnl_top);                
                switch (opt)
                {
                    case JFileChooser.CANCEL_OPTION:
                        break;
                    case JFileChooser.APPROVE_OPTION:                       
                        btn_load_ticker_list.setText("Re-load Ticker List..");
                        ticker_list = GetTickerList(fc_tl.getSelectedFile()); 
                        if(ticker_list != null && ticker_list.size() > 0)
                        {
                            log_msg.add("Load Ticker List Successfully");
                            lbl_ticker_list_data.setText(fc_tl.getSelectedFile().getName());
                            btn_get_stock_data.setEnabled(true);
                            btn_get_firm_data.setEnabled(true);                                                           
                        }else{
                            btn_get_stock_data.setEnabled(false);
                            btn_get_firm_data.setEnabled(false);                             
                            log_msg.add("Load Ticker List Unsuccessfully");
                            lbl_ticker_list_data.setText("");                                                      
                        }
                        UpdateLog();                        
                        break; 
                }                                        
            }
        });
        
        btn_get_stock_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btn_get_stock_data.getText().equals("Stop")){                                    
                    btn_get_stock_data.setText("Get Stock Data"); 
                    log_msg.add("Stop Getting Stock Data"); 
                    can_crawl_data = false;  
                    btn_load_ticker_list.setEnabled(true);
                    btn_get_firm_data.setEnabled(true);                                            
//                    update_log_task.cancel();                    
                }
                else{
                    can_crawl_data = true;                    
//                    UpdateLog();
                    {
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                btn_load_ticker_list.setEnabled(false);
                                btn_get_firm_data.setEnabled(false);
                                btn_get_stock_data.setText("Stop"); 
                                lbl_stock_data.setText(stockDataFile);
                                log_msg.add("Start Getting Stock Data"); 
                                log_msg.add("Getting Data Header..");
                                WriteStockDataHeader(); 
                                log_msg.add("Getting Stock Data..");
                                WriteAllStockData();                            
                            }
                        };
                        timer.schedule(task, 100);
                    }                     
                }                        
            }
        });
  
        btn_get_firm_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btn_get_firm_data.getText().equals("Stop")){                                   
                    btn_get_firm_data.setText("Get Firm Data"); 
                    log_msg.add("Stop Getting Firm Data");
                    can_crawl_data = false; 
                    btn_load_ticker_list.setEnabled(true);
                    btn_get_stock_data.setEnabled(true);                                           
//                    update_log_task.cancel();                    
                }
                else{
                    can_crawl_data = true;                    
//                    UpdateLog();
                    {
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                btn_load_ticker_list.setEnabled(false);
                                btn_get_stock_data.setEnabled(false);
                                btn_get_firm_data.setText("Stop");
                                lbl_firm_data.setText(firmDataFile);
                                log_msg.add("Start Getting Firm Data"); 
                                log_msg.add("Getting Data Header..");
                                WriteFirmDataHeader(); 
                                log_msg.add("Getting Firm Data..");
                                WriteAllFirmDataByQuarter();                            
                            }
                        };
                        timer.schedule(task, 100);
                    }                     
                }                 
            }
        });        
        
        pnl_top = new JPanel();
        pnl_top.setLayout(new BoxLayout(pnl_top, BoxLayout.X_AXIS));        
        pnl_top.setPreferredSize(new Dimension(100,60));
        pnl_top.setMaximumSize(new Dimension(100,60));
        pnl_top.setMinimumSize(new Dimension(100,60));          
           
        pnl_top.add(btn_load_ticker_list);
        pnl_top.add(lbl_ticker_list_data);
        pnl_top.add(Box.createHorizontalGlue());        
        pnl_top.add(btn_get_stock_data);
        pnl_top.add(lbl_stock_data);
        pnl_top.add(Box.createHorizontalGlue());        
        pnl_top.add(btn_get_firm_data);  
        pnl_top.add(lbl_firm_data);
        pnl_top.add(Box.createHorizontalGlue());        
                     
        spane_log.setViewportView(txtar_log);
        
        this.add(pnl_top, BorderLayout.NORTH);
        this.add(spane_log, BorderLayout.CENTER);       
    }
    
    void UpdateLog(){
//        if(txtar_log.getText().length()!=0)
//            txtar_log.append("\n" + line_seperator);
//        if(is_log_task_cancelled){
            update_log_task = new TimerTask() {
                @Override
                public void run() {
                   while (log_msg.size() > 0)
                   {
                        if(txtar_log.getText().length()!=0)
                            txtar_log.append("\n");
                        if(txtar_log.getLineCount() > 5000){                        
                            txtar_log.setText("...Continuing...");
                            txtar_log.append("\n");
                            }
                        txtar_log.append(log_msg.pop());        
                    }
    //               if(log_msg.isEmpty() && !can_crawl_data){
    //                   System.out.println("Stop Logging");
    //                   update_log_task.cancel();
    //                   TimerTask cancel_task = new TimerTask() {
    //                       @Override
    //                       public void run() {
    //                           update_log_task.cancel();
    //                       }
    //                   };
    //                   new Timer().schedule(cancel_task, 1000);
    //               }else
    //                    System.out.println("Logging is running");
                }
            };
            new Timer().schedule(update_log_task, 100, 1000);
//            is_log_task_cancelled = false;
//        }
    }
    
    public void WriteFirmDataHeader(){
        try {           
            File firmFile = new File(this.firmDataFile);
            if (!firmFile.exists())  {          
                firmFile.createNewFile();
                String headers = "Ticker, Năm, Quý";
                TickerURL ticker = new TickerURL();
                ticker.SetTicker("VNM");
                ticker.SetYear(currentYear);
                ticker.SetQuarter(currentQuarter);                
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(firmFile,false), 
                        StandardCharsets.UTF_8)) 
                {
                    Document doc = Jsoup.connect(ticker.GetURL()).get();
                    Elements tableRows = doc.select("#tableContent tr[class^=\"r_item\"]");
                    for (Element tableRow : tableRows)
                    {
                        Elements columns = tableRow.select("td");
                        headers += ", " + columns.get(0).text();
                    }
                    //write headers to file
                    writer.write(headers);
                    writer.write("\n");
                    log_msg.add("Writing firm data header: " + headers);                      
                    //flush and close writer
                    writer.flush();
                    writer.close();                    
                }                
            }             
        } catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");          
        }     
    }
    public int WriteFirmData(TickerURL ticker, boolean backward){  
        int emptyColCounter = 4;
        try  
        {
            //create file to write data
            File firmFile = new File(this.firmDataFile);         
            List<List<String>> rowsToFile = new ArrayList<>();
            
            //get html page from url
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(firmFile,true), 
                    StandardCharsets.UTF_8)) 
            {
                log_msg.add("URL: " + ticker.GetURL());                
                //get html page from url
                Document doc = Jsoup.connect(ticker.GetURL()).get();            
                //select table headers
                Elements tableHeaders = doc.select("#tblGridData td");
                //write table headers to file
                for (Element tableHeader: tableHeaders){
                    //ignore empty header or last header (dont need data from that column)
                    if (!tableHeader.equals(tableHeaders.last()))
                    {
                        if (tableHeader.hasText())
                        {
                            //dont need first header
                            if (!tableHeader.equals(tableHeaders.first()))
                            {
                                List<String> rowToFile = new ArrayList<>();
                                String[] date = tableHeader.text().split("-");
                                rowToFile.add(tableHeader.text());
                                rowToFile.add("1");
                                rowToFile.add(date[1] + ", " + date[0].replace("Quý ", ""));
                                rowsToFile.add(rowToFile);
                            }
                        }
                    }
                }   //select table content (list of rows)
                Elements tableRows = doc.select("#tableContent tr[class^=\"r_item\"]");
                //write content rows to file
                for (Element tableRow : tableRows)
                {
                    //select columns from each row
                    Elements columns = tableRow.select("td");
                    //loop through columns to write data
                    for (int i = 1; i <= rowsToFile.size(); i++){
                        String columnData = columns.get(i).text().replace(",", " ");
                        List<String> curRow = rowsToFile.get(i-1);
                        //check if it is an empty row or not
                        String isEmpty = "1";
                        if (!columnData.isEmpty() && !columnData.equals(" "))
                        {
                            isEmpty = "0";
                        }
                        if (curRow.get(1).equals("1"))
                            curRow.set(1, isEmpty);
                        curRow.set(2, curRow.get(2) + ", " + columnData);
                    }
                }   
                //start writing data to the stream
                if(tableRows.size() > 0){
                    emptyColCounter = 0;
                    if (backward){
                        Collections.reverse(rowsToFile);
                    }                   
                    for (List<String> rowToFile: rowsToFile){
                        if(!can_crawl_data){
                            return 0;          
                        }
                        String row = ticker.GetTicker() + ", " + rowToFile.get(2);
                        writer.write(row);
                        writer.write("\n");
                        log_msg.add("Writing firm data: " + row);                        
                        if (rowToFile.get(1).equals("1"))
                            ++emptyColCounter;
                    }                  
                }

                //flush and close writer
                writer.flush();
                writer.close();                
            }
        } catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");        
        }
        return (4 - emptyColCounter);
    }     
    
    public void WriteAllFirmDataByQuarter(){     
        try
        {
            File logFile = new File(firmDataLogFile);
            if (!logFile.exists())
                logFile.createNewFile();
            TickerURL ticker = new TickerURL();
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(logFile,true), 
                    StandardCharsets.UTF_8)) 
            {
                List<String> tickersToBeRemoved = new ArrayList<>();
//                String temp_t = "";
                for (String t: ticker_list){
                    if(!can_crawl_data)
                        break;                   
//                    if(!temp_t.equals("")){
//                        temp_tickers.add(temp_t);
//                    } 
//                    temp_t = t;
                    int successColCounter = 1;  
                    int tempYear = currentYear;
                    int totalSuccess = 0;
                    ticker.SetTicker(t);
                    while (successColCounter > 0 && can_crawl_data){
                        ticker.SetYear(tempYear);
                        ticker.SetQuarter(currentQuarter);
                        successColCounter = WriteFirmData(ticker, true);
                        if(successColCounter > 0){
                            --tempYear;
                            totalSuccess += successColCounter;
                        }else{
                            tickersToBeRemoved.add(t);
                        }
                    }
                    log_msg.add("Finished ticker " + t + " from Q" + currentQuarter + " Y" + tempYear +
                            " to Q" + currentQuarter + " Y" + currentYear + ": " + totalSuccess + " (successful quarters)");
                    log_msg.add(line_seperator);                       
                    writer.write("Finished ticker " + t + " from Q" + currentQuarter + " Y" + tempYear + 
                            " to Q" + currentQuarter + " Y" + currentYear + ": " + totalSuccess + " (successful quarters)" + "\n");
                    System.out.println("Finished ticker " + t + " from Q" + currentQuarter + " Y" + tempYear +
                            " to Q" + currentQuarter + " Y" + currentYear + ": " + totalSuccess + " (successful quarters)");                                    
                }
                writer.flush();
                writer.close();
                
                for(String t: tickersToBeRemoved){
                    ticker_list.remove(t);
                }                     
                System.out.println("remaining ticker list: " + ticker_list);
                if(ticker_list.size() == 0){
                    log_msg.add("Finish Getting All Firm Data");                    
                    btn_get_firm_data.setText("Get Firm Data");
                    btn_get_firm_data.setEnabled(false);                      
                    btn_load_ticker_list.setText("Load Ticker List");       
                    btn_load_ticker_list.setEnabled(true);
                    can_crawl_data = false;
//                    update_log_task.cancel();                    
//                    is_log_task_cancelled = true;                       
                     
                }              
            }
        }
        catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");          
        }
    }
    
    public List<String> GetTickerList(File tickerFile){
        List<String> tickers = null;
        try {        
            try (FileInputStream excelFile = new FileInputStream(tickerFile)) {
                Workbook workbook = new XSSFWorkbook(excelFile);
                Sheet datatypeSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = datatypeSheet.iterator();
                tickers = new ArrayList<>();
                while (iterator.hasNext()){
                    Row curRow = iterator.next();
                    Iterator<Cell> cellIterator = curRow.iterator();
                    int cellIndex = 0;  
                    String ticker = "";
                    while (cellIterator.hasNext()){
                        Cell curCell = cellIterator.next();
                        if (cellIndex == 1)
                            ticker = curCell.getStringCellValue();
                        else if (cellIndex == 4 && 
                                (curCell.getStringCellValue().equals("HOSE") || curCell.getStringCellValue().equals("HNX")))
                            tickers.add(ticker);
                        ++cellIndex;
                    }
                }
                excelFile.close();
            }
        }catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");
        }            
        return tickers;
    }
    
    public void WriteStockDataHeader (){
        try {
            File stockFile = new File(this.stockDataFile);
            if (!stockFile.exists())  {        
                stockFile.createNewFile();
                List<String> headers = new ArrayList<>();
                headers.add("Ticker");
                String url = "http://s.cafef.vn/Lich-su-giao-dich-DHG-1.chn/";                
                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(stockFile,false), 
                        StandardCharsets.UTF_8)) 
                {
                    Document doc = Jsoup.connect(url).get();
                    //get the table of data, the html source use different id of table element --> check which ID is being used
                    Elements tableRows = doc.select("#GirdTable2 tr").isEmpty() ? doc.select("#GirdTable tr") : doc.select("#GirdTable2 tr");
                    int j = 0;
                    //get first and second rows, which is header                  
                    Element firstRow = tableRows.get(0);
                    Element secondRow = tableRows.get(1);
                    //select columns from 1st and 2nd rows
                    Elements firstRowColumns = firstRow.select("td");
                    Elements secondRowColumns = secondRow.select("td");
                    //loop through columns to write header, skip last 3 columns
                    for (int i = 0; i < firstRowColumns.size() - 3; i++)
                    {                        
                        Element firstRowColumn = firstRowColumns.get(i);
                        //skip 4th column
                        if(i != 3)
                        {                                    
                            if(firstRowColumn.hasAttr("colspan"))
                            {
                                int colspanVal = Integer.parseInt(firstRowColumn.attr("colspan"));
                                for(int k = j; k < j + colspanVal; k++){
                                    headers.add(firstRowColumn.text() + " " + secondRowColumns.get(k).text());
                                }
                                j += colspanVal;                              
                            }else{
                                headers.add(firstRowColumn.text());    
                            }
                        }                           
                    }                                  
                  
                    String headers_to_write = "";
                    //write headers to file
                    for(String header: headers){
                        if(headers.indexOf(header) != 0)       
                            headers_to_write += ", ";
                       headers_to_write += header;
                    } 
                    writer.write(headers_to_write);                    
                    writer.write("\n");
                    log_msg.add("Writing stock data header: " + headers_to_write);                    
                    //flush and close writer
                    writer.flush();
                    writer.close();                    
                }                
            }             
        } catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");       
        }           
    }
    
    public void WriteAllStockData(){   
        try 
        {
            File logFile = new File(stockDataLogFile);
            if (!logFile.exists())
                logFile.createNewFile();    
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(logFile,true), 
                    StandardCharsets.UTF_8)) 
            {   
                List<String> tickerToBeRemoved = new ArrayList<>();                
//                String temp_t = "";                
                for (String t: ticker_list)
                {              
                    if(!can_crawl_data)
                        break;
//                    if(!temp_t.equals("")){
//                        temp_tickers.add(temp_t);
//                    } 
//                    temp_t = t;                      
                    try{ 
                        String url = "http://s.cafef.vn/Lich-su-giao-dich-" + t + "-1.chn/";
    //                    String url = "http://s.cafef.vn/Lich-su-giao-dich-AAV-1.chn/";
                        log_msg.add("URL: " + url);    
                        Connection.Response response = Jsoup.connect(url).method(Connection.Method.GET).execute();               
                        Document responseDoc = response.parse();
                        int success;
                        int page = 1;
                        do
                        {
                            String tickerKeywordSearchIDVal = responseDoc.select("input[name=" + tickerKeywordSearchID + "]").first().attr("value");
                            String pagingID = responseDoc.select("#" + pagingID1.replace("$", "_")).isEmpty() ? pagingID2 : pagingID1;                        
                            response = Jsoup.connect(url)
                                   .data(tickerKeywordSearchID, tickerKeywordSearchIDVal)
                                   .data(eventTargetID, pagingID)
                                   .data(eventArgumentID, Integer.toString(page))
                                   .method(Connection.Method.POST)
                                   .timeout(300 * 1000)
                                   .followRedirects(true)
                                   .execute();                
                            Document doc = response.parse();
                            success = WriteStockDataFrom1Page(doc);
                            if (success != 0)
                                ++page; 
                            else{
                                tickerToBeRemoved.add(t);                                
                                --page;
                            }
                        }
                        while (success != 0 && can_crawl_data);   
                        log_msg.add("Finished ticker " + t + ": " + page + " (successful pages)");
                        log_msg.add(line_seperator);                          
                        writer.write("Finished ticker " + t + ": " + page + " (successful pages)" + "\n");
                        System.out.println("Finished ticker " + t + ": " + page + " (successful pages)");                      
                    }
                    catch (IOException ex) {
                        Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
                    }                   
                }
                writer.flush();
                writer.close();
                for(String t: tickerToBeRemoved){
                    ticker_list.remove(t);
                }
//                System.out.println("remaining ticker list: " + ticker_list);
                if(ticker_list.size() == 0){
                    log_msg.add("Finish Getting All Firm Data");                    
                    btn_get_stock_data.setText("Get Stock Data"); 
                    btn_get_stock_data.setEnabled(false); 
                    btn_load_ticker_list.setText("Load Ticker List");                     
                    btn_load_ticker_list.setEnabled(true);                      
                    can_crawl_data = false;                  
//                    is_log_task_cancelled = true;
//                    update_log_task.cancel();                
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");              
        } 
    }

    //function to get table of from each available page
    public int WriteStockDataFrom1Page(Document doc){
        List<String> rowsToFile = new ArrayList<>(); 
        File stockFile = new File(this.stockDataFile); 
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(stockFile,true), 
                StandardCharsets.UTF_8)) 
        {                                
            String tickerCode = doc.select("#" + tickerKeywordSearchID.replace("$", "_")).attr("value");
            //get the table of data, the html source use different id of table element --> check which ID is being used
            Elements tableRows = doc.select("#GirdTable2 tr").isEmpty() ? doc.select("#GirdTable tr") : doc.select("#GirdTable2 tr");
            //loop through all table rows, skip first 2 rows as they are headers
            for (Element tableRow : tableRows)
            {                
                if(tableRows.indexOf(tableRow) > 1)
                {
                    String rowToFile = tickerCode + ", ";
                    //select columns from each row
                    Elements columns = tableRow.select("td");    
                    //loop through columns to write data, skip last 3 columns
                    for (int i = 0; i < columns.size() - 3; i++)
                    {
                        //skip 3rd and 4th columns as they are unnecessary fields
                        if (i != 3 && i != 4)
                        {
                            String columnData = columns.get(i).text().replace(",", " ");
                            rowToFile += columnData;
                            if (i != columns.size() - 4){
                                rowToFile += ", ";
                            }
                        }                         
                    }

                    //add row data to list
                    rowsToFile.add(rowToFile);                  
                }
            }
            //write data to file
            for (String row: rowsToFile)
            {   
                if(!can_crawl_data){
                    return 0;
                }
                writer.write(row);
                writer.write("\n");
                log_msg.add("Writing stock data: " + row);
            }            
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            Logger.getLogger(DataCrawlerView.class.getName()).log(Level.SEVERE, null, ex);
            log_msg.add("{Error: " + ex + "}");
        }
        return rowsToFile.size();
    }  
}
