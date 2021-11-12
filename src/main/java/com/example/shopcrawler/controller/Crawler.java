package com.example.shopcrawler.controller;

import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Crawler {
    
    public StringBuilder buildFileContent (String urlIn, String urlParam) throws Exception{

        int itemCount = 0, pagination = 1, threshold = 100;
        //SETUP CHROMEDRIVER
        System.setProperty("webdriver.chrome.driver", "shopcrawler/drivers/chromedriver.exe");
        //SETUP CHROME OPTIONS
        ChromeOptions chOpt = new ChromeOptions();
        chOpt.addArguments(
            "--user-agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36'",
            "--headless", 
            "--disable-gpu", 
            "--window-size=1920,1200",
            "--ignore-certificate-errors",
            "--disable-extensions",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-infobars",
            "--enable-javascript"
            );

        WebDriver driver = new ChromeDriver(chOpt);
        //PREPARE TO BUILD STRING BUILDER

        StringBuilder builder = new StringBuilder();

        try {
            //BUILD HEADER START
            builder.append("productname");
            builder.append(',');
            builder.append("description");
            builder.append(',');
            builder.append("imagelink");
            builder.append(',');
            builder.append("price");
            builder.append(',');
            builder.append("rating");
            builder.append(',');
            builder.append("merchant");
            builder.append('\n');
            //BUILD HEADER END

            while (itemCount < threshold) {
                driver.navigate().to(urlIn+pagination+urlParam);
                //WAIT TO PAGE LOADED
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
                //SCROLL DOWN TO END OF PAGE USING JAVASCRIPT EXECUTOR
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
                //ANOTHER WAIT PERHAPS ?
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

                Document doc = Jsoup.parse(driver.getPageSource());
            
                Element rootSection = doc.getElementsByAttributeValue("data-testid","lstCL2ProductList").first();
                //BUILD STRING BUILDER
                Elements parentSection = rootSection.getElementsByAttributeValue("class", "css-bk6tzz e1nlzfl3");
                
                /** BUILD STRING FOR FILE */
                builder.append(buildContents(parentSection, itemCount, threshold));
                //INDICATE PAGINATION
                itemCount+=parentSection.size();

                if(itemCount < threshold){
                    pagination+=1;
                }
            }

            driver.close();

        } catch (Exception e) {
            //TODO: handle exception
        }
        return builder;
    }

    private StringBuilder buildContents (Elements parentElement, int itemCount, int threshold) {
        StringBuilder sbItems = new StringBuilder();
        try {
            for (Element childElement : parentElement) {
                // FIRST ITEM
                itemCount+=1;

                //GET PRODUCT NAME
                Element prodNameElement = childElement.getElementsByAttributeValue("class", "css-1bjwylw").first();
                String prodName = getTextValue(prodNameElement, "PRODUCT"); 
                sbItems.append(prodName);
                sbItems.append(',');
                //GET DESCRIPTION
                Element descElement = childElement.getElementsByAttributeValue("class", "css-1kr22w3").first();
                sbItems.append(getTextValue(descElement, "DESCRIPTION"));
                sbItems.append(',');
                //GET IMAGE link
                Element imgElement = childElement.select("img").first();
                sbItems.append(getImgLink(imgElement, prodName, "IMAGE LINK"));
                sbItems.append(',');
                //GET PRICE
                Element priceElement = childElement.getElementsByAttributeValue("class", "css-o5uqvq").first();
                sbItems.append(getTextValue(priceElement, "PRICE"));
                sbItems.append(',');
                //GET RATING
                Elements ratingsElement = childElement.getElementsByAttributeValue("class", "css-177n1u3");
                sbItems.append(getRating(ratingsElement, "RATING"));
                sbItems.append(',');
                //GET MERCHANT NAME
                Element merchantElement = childElement.getElementsByAttributeValue("class", "css-1kr22w3").last();
                sbItems.append(getTextValue(merchantElement, "MERCHANT"));
                sbItems.append('\n');

                // IF THIS IS THE 100TH , THEN BREAK THE LOOP
                if (itemCount >= 100) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return sbItems;
    }

    private String getTextValue(Element elementIn, String segment){
        String itemOut = "";
        try {
            itemOut = elementIn.text();
        } catch (Exception e) {
            itemOut = "Fail to get "+segment;
        }
        return itemOut;
    }
    private String getImgLink(Element elementIn, String altTitle, String segment){
        String itemOut = "";
        try {
            itemOut = elementIn.attr("src");
            itemOut = itemOut.replaceAll(";", ((int)';')+"");
        } catch (Exception e) {
            itemOut = "Fail to get "+segment;
        }
        return itemOut;
    }

    private String getRating(Elements elementIn, String segment){
        String itemOut = "";
        try {
            int rateCountStar = 0;
            rateCountStar = elementIn.size();
            itemOut = rateCountStar+"";
        } catch (Exception e) {
            itemOut = "Fail to get "+segment;
        }
        return itemOut;
    }
    
}
