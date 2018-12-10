package com.fow.main;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
 
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
 
public class WebContainer extends Application {
	
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static WebContainer Webcontainer = null;
	private WebView browser;
	private WebEngine webEngine;
	
	public static WebContainer waitForClient() {
	       try {
	           latch.await();
	       } catch (InterruptedException e) {
	           e.printStackTrace();
	       }
	       return Webcontainer;
	   }

	   public static void setWebContainer(WebContainer WebContainer0) {
		   Webcontainer = WebContainer0;
	     latch.countDown();
	   }

	   public WebContainer() {
		   setWebContainer(this);
	   }

    private Scene scene;
    
    @Override public void start(Stage stage) {
    	System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        // create the scene
    	BorderPane root = new BorderPane();
    	//Browser b = new Browser("https://localhost/FOWwebServer/home");
    	TrustManager[] trustAllCerts = new TrustManager[] { 
                new X509TrustManager() {     
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                        return null;
                    } 
                    public void checkClientTrusted( 
                        java.security.cert.X509Certificate[] certs, String authType) {
                        } 
                    public void checkServerTrusted( 
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                } 
            }; 

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (GeneralSecurityException e) {
            }
            
            browser = new WebView();
            webEngine = browser.getEngine();
            
            webEngine.setJavaScriptEnabled(true);
            webEngine.load("http://localhost/FOWwebServer/home");
            //webEngine.load("http://www.google.com");
    	root.setCenter(browser);
        stage.setTitle("Web View");
        scene = new Scene(root,750,500, Color.web("#666970"));
        stage.setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
        stage.show();
    }
 
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        System.exit(0);
    }
}
class Browser extends Region {
 
    final WebView browser;
    final WebEngine webEngine;
     
    public Browser(String url) {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
     // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { 
            new X509TrustManager() {     
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                    return null;
                } 
                public void checkClientTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                    } 
                public void checkServerTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            } 
        }; 

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL"); 
            sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
        
        browser = new WebView();
        webEngine = browser.getEngine();
        
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(url);
        //webEngine.load("http://www.google.com");
        //add the web view to the scene
        getChildren().add(browser);
 
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 750;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 500;
    }
}