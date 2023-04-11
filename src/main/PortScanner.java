package main;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class PortScanner implements Runnable{
    private String ip;
    private int portBeg;
    private int portEnd;
    private int threadsPerIp;
    private JTextArea txt_result;
    private int portType;
    private int id;
    public PortScanner(String ip, int portBeg, int portEnd, int threadsPerIp, JTextArea txt_result, int portType,int id) {
        this.ip = ip;
        this.portBeg = portBeg;
        this.portEnd = portEnd;
        this.threadsPerIp = threadsPerIp;
        this.txt_result = txt_result;
        this.portType = portType;
        this.id=id;
    }

    @Override
    public void run() {
        //线程id跨步循环
        for(int port = portBeg+id; port<=portEnd;port+=threadsPerIp){
            if(portType==0){//tcp端口扫描
                Socket socket=null;
                try {
                    InetAddress address = InetAddress.getByName(ip);
                    SocketAddress socketAddress = new InetSocketAddress(address, port);
                    socket=new Socket();
                    socket.connect(socketAddress, 800);
                    //对目标主机的指定端口进行连接，超时后连接失败
                    socket.close();
                    MyUtil.TextAreaAddMsg(txt_result,ip+":"+port+"/tcp opened\n");
                    System.out.println(ip+":"+port+"/tcp opened");
                } catch (UnknownHostException ex) {
                    MyUtil.TextAreaAddMsg(txt_result,ip+":"+"目标地址无效\n");
                    System.out.println("目标地址无效\n");
                } catch (IOException ioException) {
                    //ioException.printStackTrace();
                    MyUtil.TextAreaAddMsg(txt_result,ip+":"+port+"/tcp closed\n");
                    System.out.println(ip+":"+port+"/tcp closed\n");
                }
                if(!socket.isClosed()){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{//udp端口扫描
                DatagramSocket datagramSocket = null;
                InetAddress address=null;
                try {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(800);
                    address = InetAddress.getByName(ip);
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("无效的地址");
                    datagramSocket.close();
                    return;
                }

                SocketAddress socketAddress = new InetSocketAddress(address, port);
                byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
                DatagramPacket dp = new DatagramPacket(data,data.length,socketAddress);
                try {
                    datagramSocket.send(dp);
                }catch ( PortUnreachableException e){
                    MyUtil.TextAreaAddMsg(this.txt_result,ip+":"+port+"/udp closed\n");
                    System.out.println(ip+":"+port+"/udp closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] arr = new byte[1024];
                DatagramPacket recv_pack = new DatagramPacket(arr, arr.length);
                try {
                    datagramSocket.receive(recv_pack);
                    datagramSocket.close();
                    MyUtil.TextAreaAddMsg(this.txt_result,ip+":"+port+"/udp opened\n");
                    System.out.println(ip+":"+port+"/udp opened");
                }catch (PortUnreachableException e){
                    MyUtil.TextAreaAddMsg(this.txt_result,ip+":"+port+"/udp perhaps closed\n");
                    System.out.println(ip+":"+port+"/udp perhaps closed");
                }
                catch (IOException e) {
                    MyUtil.TextAreaAddMsg(this.txt_result,ip+":"+port+"/udp closed\n");
                    System.out.println(ip+":"+port+"/udp closed");
                }
                if(!datagramSocket.isClosed()){
                    datagramSocket.close();
                }

            }

        }
    }

}
