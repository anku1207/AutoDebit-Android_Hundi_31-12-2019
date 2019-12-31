package com.uav.autodebit.util;

import java.io.File;

public interface FileDownloadInterface {

    void downloadComplete(File file);
    void error(String error);
}
