package main;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;


import javax.swing.*;

public class main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        //下面这个try catch块是在导入皮肤包，如果不用的话可以去掉
//        try
//        {
//            // BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike
//            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
//            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
//            UIManager.put("RootPane.setupButtonVisible", false);
//
//
//        }
//        catch(Exception e)
//        {
//            //TODO exception
//        }
        UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        GUIWindow guiWindow = new GUIWindow();//创建GUI窗体并保持运行
    }
}
