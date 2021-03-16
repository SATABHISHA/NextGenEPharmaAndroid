package org.arb.Nextgen.ePharma.config;

import org.arb.Nextgen.ePharma.Model.UserSingletonModel;

public class Config {
   static UserSingletonModel userSingletonModel = UserSingletonModel.getInstance();
//   public static String BaseUrl = "http://220.225.40.151:9012/TimesheetService.asmx/"; //--commented on 15th jan
   public static String BaseUrlEpharma = "http://14.99.211.60:9029/api/"; //test
//   public static String BaseUrlEpharma = "https://capletreporting.com/mobile/api/";  //live url
//   public static String BaseUrl = "http://erpgovmobile.com/TimesheetService.asmx/";
  /* public static String emailHostAddress = "smtp.gmail.com";
   public static String emailServerPort = "587";
   public static String emailUsername="gsttest123@gmail.com";
   public static String emailPassword="ARB@1234";*/

  /* public static String emailHostAddress = "smtp.gmail.com";
   public static String emailServerPort = userSingletonModel.getEmailServerPort();
   public static String emailUsername = userSingletonModel.getEmailSendingUsername();
   public static String emailPassword = userSingletonModel.getEmailPassword();*/
}
