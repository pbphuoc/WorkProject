/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uq.researchassistance.M;


/**
 *
 * @author ffpbp
 */
public class TickerURL {
    private final String hostName = "http://s.cafef.vn";
    private final String pageName = "bao-cao-tai-chinh";
    private final String type = "IncSta";
    private String ticker;    
    private int year;
    private int quarter;
    private int showType;
    private int idx;
    private int option;
    private final String compName = "cong-ty-co-phan-sua-viet-nam";
    private final String reportType = "ket-qua-hoat-dong-kinh-doanh";
    private final String extType = ".chn";

    public TickerURL() {       
        this.option = 0 ;
        this.showType = 0;
        this.idx = 0;
    }

    public String GetTicker() {
        return ticker;
    }

    public int GetYear() {
        return year;
    }

    public int GetQuarter() {
        return quarter;
    }

    public int GetOption() {
        return option;
    }
    
    public void SetTicker(String ticker){
        this.ticker = ticker;
    }

    public void SetYear(int year) {
        this.year = year;
    }

    public void SetQuarter(int quarter) {
        this.quarter = quarter;
    }
    
    public void SetOption(int option) {
        this.option = option;
    }
    
    public String GetURL(){
        return hostName + "/" + pageName + "/" + ticker + "/" + type + "/" + year + "/" + quarter + "/" + idx 
                + "/" + showType + "/" + option  + "/" + reportType + "-" + compName + extType;
    }
}
