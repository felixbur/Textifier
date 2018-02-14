package com.felix.textifier.servlets;

import java.io.File;

import javax.servlet.*;

import com.felix.textifier.Textifier;

public  class ServiceListener implements ServletContextListener {
  private ServletContext context = null;

  /*This method is invoked when the Web Application has been removed 
  and is no longer able to accept requests
  */

  public void contextDestroyed(ServletContextEvent event)
  {
    //Output a simple message to the server's console
    System.out.println("The Simple Web App. Has Been Removed");
    this.context = null;

  }


  //This method is invoked when the Web Application
  //is ready to service requests

  public void contextInitialized(ServletContextEvent event)
  {
    this.context = event.getServletContext();

    //Output a simple message to the server's console
    System.out.println("Initializing Textifier");
    String appPath = context.getRealPath(File.separator);
    String [] args = new String[]{"-appRoot", appPath};
    Textifier textifer = new Textifier(args);
    // textifier accessed via getInstance() method
//    context.setAttribute("textifier", textifer);
  }
}