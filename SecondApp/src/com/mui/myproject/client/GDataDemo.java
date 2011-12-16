package com.mui.myproject.client;

import com.google.gwt.user.client.ui.Composite;

/**
 * All HelloMaps demos extend this class.
 */
public abstract class GDataDemo extends Composite {

  public final static String GDATA_API_KEY = "ABQIAAAAzoeFOtuTUN37p6oxfKqL9hSCOVA_ujwSKCzUYGl0XuDrgJTiPRQQryAU4Z-ajsptHxgLQf6OpIOx9g";
  
  /**
   * This inner static class creates a factory method to return an instance of
   * GDataDemo.
   */
  public abstract static class GDataDemoInfo {

    private GDataDemo instance;

    /**
     * @return a new instance of GDataDemo
     */
    public abstract GDataDemo createInstance();

    /**
     * @return a description of this demo.
     */
    public String getDescription() {
      return "<p><i>Description not provided.</i></p>\n"
          + "<p>(Add an implementation of <code>getDescriptionHTML()</code> "
          + "for this demo)</p>";
    }

    /**
     * Factory method for GDataDemo.
     * 
     * @return an instance of this GDataDemo class
     */
    public GDataDemo getInstance() {
      /* create a new instance every time. For the purposes of this demo
       * we want the GData output to be "live" always and never cached. */
      instance = createInstance();
      return instance;
    }

    public abstract String getName();
  }

  /**
   * Method that gets called by the main demo when this demo is now active on
   * the screen.
   */
  public void onShow() {
  }
}