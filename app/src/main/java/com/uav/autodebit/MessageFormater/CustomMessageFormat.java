package com.uav.autodebit.MessageFormater;

import java.text.MessageFormat;

public class CustomMessageFormat {
    public static String SUCCESS_fULL_BANK_MAPPED="Dear user, your previous e-mandate from your {0}, {1} number has been mapped successfully.";

    public  static String CreateManualMessage(String message , Object[] args  ){
        MessageFormat messageFormat = new MessageFormat(message);
        return messageFormat.format(args);
    }
}
