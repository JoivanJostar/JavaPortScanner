package main;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
//工具类
public class MyUtil {
    //错误弹窗
    public static void alertMsg(String msg){
        JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

    public static List<String> rmvEmpty(List<String> list){
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            if(next.isEmpty()){
                iterator.remove();
            }
        }
        return list;
    }
    //向文本框追加信息，多线程操作，需要加锁
    public static void TextAreaAddMsg(JTextArea textArea,String msg){
        synchronized (textArea){
            textArea.append(msg);
            textArea.paintImmediately(textArea.getBounds());
        }
    }
    //测试目标是否可达
    public static boolean ping(String ip){

        InetAddress addr = null;//读取实际IP地址
        try {
            addr = InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return false;
        }
        int times=1;
        for(times=1;times<=3;++times){
            // System.out.println("ip:"+ip+"第"+times+"次尝试...");
            try {
                if(addr.isReachable(3000)){ //3000ms
                    return true;
                }else{
                    continue;
                }
            } catch (IOException ex) {
                return false;
            }

        }
        return false;
    }
}