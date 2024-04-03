package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static String statementsUrl = "https://www.tipranks.com/stocks/newticker/financials/income-statement";
    public static String peUrl = "https://www.ycharts.com/companies/newticker/pe_ratio";

    public static void main(String[] args){

        Scanner userTicker = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter a stock ticker");
        String inputTickers = userTicker.nextLine();
        String[] tickers = inputTickers.split(",");
        for(String ticker : tickers) {
            ticker=ticker.replaceAll("\\s+","");
            System.out.println(ticker.toUpperCase());
            getPE(peUrl.replaceFirst("newticker", ticker.toUpperCase()));
            getStatements(statementsUrl.replaceFirst("newticker", ticker));
        }
    }
    public static void getPE(String url){
        try {
            final Document document = Jsoup.connect(url).get();
            List<String> pe = document.select("table.table tr").select("td.text-right").eachText();
            if(document.select("table.table tr").select("td.text-right").eachText().size()>0){
                System.out.println("\tPE: " + pe.get(0));
            }
        }
        catch(Exception e){
            System.out.println("Unable to find PE ratio for this ticker");
        }
    }
    public static void getStatements(String url){
        try {
            final Document document = Jsoup.connect(url).get();
            String ebitda=null;
            float ebitdaChange=0;
            float epsChage=0;
            String eps = null;
            for(Element row : document.select("table.flexcss tr")){
                String statement = row.select("span.fontSize8").eachText().get(0);
                if(statement.equals("EBITDA")){
                    final Elements ebitdaElements = row.select("td.flexrec");
                    ebitda=ebitdaElements.eachText().get(0);
                    final List<String> ebitdaTitles = ebitdaElements.eachAttr("title");
                    if(ebitdaTitles.get(0)!="-" && ebitdaTitles.size()>2 && ebitdaTitles.get(2)!="-") {
                        float curr = Float.valueOf(ebitdaTitles.get(0).replaceAll(",",""));
                        float lastYear = Float.valueOf(ebitdaTitles.get(2).replaceAll(",",""));
                        ebitdaChange = curr - lastYear;
                    }
                }
                else if(statement.equals("Basic EPS")){
                    final Elements ebitdaElements = row.select("td.flexrec");
                    eps=ebitdaElements.eachText().get(0);
                    final List<String> epsTitles = ebitdaElements.eachAttr("title");
                    if(epsTitles.get(0)!="-" && epsTitles.size()>2 && epsTitles.get(2)!="-") {
                        float curr =0;
                        float lastYear = 0;
                        if((int)epsTitles.get(0).charAt(0)<48||(int)epsTitles.get(0).charAt(0)>57){
                            curr = Float.valueOf(epsTitles.get(0).substring(1));
                        }
                        else {
                            curr = Float.valueOf(epsTitles.get(0));
                        }
                        if((int)epsTitles.get(2).charAt(0)<48||(int)epsTitles.get(2).charAt(0)>57){
                              lastYear=Float.valueOf(epsTitles.get(2).substring(1));
                        }
                        else {
                            lastYear = Float.valueOf(epsTitles.get(2));
                        }
                        epsChage = curr - lastYear;
                    }
                }
            }
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            if(ebitda!=null){
                ebitda=ebitda.replaceAll(" ","");
            }
            if(eps!=null){
                eps=eps.replaceAll(" ","");
            }
            System.out.println("\tEPS: " + eps);
            System.out.println("\tEPS change: $" + df.format(epsChage));
            System.out.println("\tEBITDA: " + ebitda);
            System.out.println("\tEBITDA change: $" + df.format(ebitdaChange));
        }
        catch (Exception e){
            System.out.println("Unable to find statement information for this ticker");
        }
    }
}
