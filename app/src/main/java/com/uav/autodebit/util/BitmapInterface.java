package com.uav.autodebit.util;

import android.graphics.Bitmap;

import com.uav.autodebit.vo.BitmapVO;

import java.io.File;
import java.util.List;

public interface BitmapInterface {

    void downloadComplete(List<BitmapVO> bitmapVOs);
    void error(String error);
}
