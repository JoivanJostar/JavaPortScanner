package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

//主窗体
public class GUIWindow extends JFrame {

    //组件定义

    private JPanel basePanel;//基层Panel

    public GUIWindow() {
        super();
        this.basePanel = new BasePanel();
        setup();
        build();
        this.setVisible(true);
    }

    //设置窗口参数
    private void setup() {
        this.setSize(900, 700);
        this.setTitle("端口扫描");
        this.setLocationRelativeTo(this.getOwner());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    //装载panel
    private void build() {
        add(this.basePanel);
    }

    //panel类
    private class BasePanel extends JPanel {
        private JLabel lbl_ipScope = new JLabel("IP地址");
        private JTextArea txt_ip = new JTextArea("localhost");
        private JScrollPane scroll = new JScrollPane(txt_ip);
        private JLabel lbl_portScope = new JLabel("端口扫描范围:");
        private JTextField txt_portBegin = new JTextField("0", 5);//起始port
        private JLabel lbl_toPort = new JLabel("-");
        private JTextField txt_portEnd = new JTextField("1024", 5); //终止端口
        //port为无符号16bit数据  故 0<= port <==65535
        private JLabel lbl_portType = new JLabel("端口类型:");
        private JRadioButton rdbtn_Tcp = new JRadioButton("TCP", true);//按钮：扫描TCP端口
        private JRadioButton rdbtn_Udp = new JRadioButton("UDP");//按钮：扫描UDP端口

        private ButtonGroup btn_group = new ButtonGroup();//按钮组：rdbtn_Tcp+rdbtn_Udp


        private JLabel lbl_threadNum = new JLabel("每个IP使用的线程数");
        private JTextField txt_threadNum = new JTextField("200", 5);//线程数量

        private JButton btn_start = new JButton("开始扫描");

        //扫描结果
        // private JLabel lbl_result=new JLabel("扫描结果:  ");
        private JTextArea txt_result = new JTextArea();//显示文本，显示扫描结果
        private JScrollPane scroll_result = new JScrollPane(this.txt_result);

        //问题的主要时间开销是等待某个端口反馈结果，此时等待的线程会挂起，可以认为这个线程就不干活了，cpu还是闲着的
        //所以问题是要充分榨干cpu算力。
        // 为了提高程序吞吐率，需要把线程数尽量开很多，建议开几百个
        // 需要满足这个关系才能榨干Cpu算力:理想线程数量K= 网络超时等待时间timeOut/(每个线程的任务计算时间t+线程切换开销c)
        // 但是注意一个机器能开辟的最大线程数量是有限的(几千?)

        //private int MAX_THREAD_NUMS=1000;

        public BasePanel() {
            setup();
            build();
        }

        //设置组件布局 和controller
        private void setup() {
            this.setLayout(null);
            //ip地址：
            lbl_ipScope.setBounds(30, 30, 100, 30);
            lbl_ipScope.setFont(new Font("宋体", Font.BOLD, 20));
            //文本输入框：
            txt_ip.setFont(new Font("宋体", Font.BOLD, 20));
            scroll.setViewportView(txt_ip);
            // scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scroll.setBounds(30, 70, 350, 100);


            //端口范围：
            lbl_portScope.setBounds(30, 170, 150, 30);
            lbl_portScope.setFont(new Font("宋体", Font.BOLD, 20));

            txt_portBegin.setBounds(50, 200, 80, 30);
            txt_portBegin.setFont(new Font("宋体", Font.BOLD, 20));
            lbl_toPort.setBounds(140, 200, 80, 30);
            lbl_toPort.setFont(new Font("宋体", Font.BOLD, 30));
            txt_portEnd.setBounds(170, 200, 80, 30);
            txt_portEnd.setFont(new Font("宋体", Font.BOLD, 20));

            //端口类型选择：
            lbl_portType.setBounds(30, 240, 120, 30);
            lbl_portType.setFont(new Font("宋体", Font.BOLD, 20));
            rdbtn_Tcp.setBounds(50, 270, 80, 50);
            rdbtn_Tcp.setFont(new Font("宋体", Font.BOLD, 20));
            rdbtn_Udp.setBounds(160, 270, 80, 50);
            rdbtn_Udp.setFont(new Font("宋体", Font.BOLD, 20));
            btn_group.add(rdbtn_Tcp);
            btn_group.add(rdbtn_Udp);

            //线程数量设置：
            lbl_threadNum.setBounds(30, 330, 200, 30);
            lbl_threadNum.setFont(new Font("宋体", Font.BOLD, 20));
            txt_threadNum.setBounds(250, 330, 100, 30);
            txt_threadNum.setFont(new Font("宋体", Font.BOLD, 20));
            //确认按钮
            btn_start.setBounds(100, 400, 200, 60);
            btn_start.setFont(new Font("宋体", Font.BOLD, 20));


            //为确认按钮添加Controller
            btn_start.addActionListener(new btn_StartScanListener());

            //结果显示
            this.txt_result.setFont(new Font("宋体", Font.BOLD, 20));
            this.txt_result.setEditable(false);
            this.scroll_result.setViewportView(txt_result);
            this.scroll_result.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            this.scroll_result.setBounds(400, 10, 450, 600);
        }

        //添加组件
        private void build() {
            this.add(lbl_ipScope);
            this.add(lbl_portScope);
            this.add(lbl_portType);
            this.add(lbl_toPort);
            this.add(lbl_threadNum);

            this.add(txt_portBegin);
            this.add(txt_portEnd);
            this.add(txt_threadNum);
            this.add(rdbtn_Tcp);
            this.add(rdbtn_Udp);
            this.add(btn_start);

            this.add(scroll_result);
            this.add(scroll);
        }

        //controller:
        private class btn_StartScanListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取ip列表
                String strIpList = txt_ip.getText().trim();
                List<String> ipList = new ArrayList<>();
                Collections.addAll(ipList, strIpList.split("\n"));
                ipList = MyUtil.rmvEmpty(ipList); //清理那些空白地址
                if (ipList.size() <= 0) {
                    MyUtil.alertMsg("ip地址列表为空");
                    return;
                }
                //获取端口范围
                String strPortBeg = txt_portBegin.getText().trim();
                String strPortEnd = txt_portEnd.getText().trim();
                int portBeg = 0;
                int portEnd = 65535;
                try {
                    portBeg = Integer.parseInt(strPortBeg);
                    portEnd = Integer.parseInt(strPortEnd);

                } catch (NumberFormatException numberFormatException) {
                    // numberFormatException.printStackTrace();
                    MyUtil.alertMsg("端口号不合法(0-65535)");
                    return;
                }
                if (portBeg < 0 || portEnd > 65535 || portBeg > portEnd) {
                    MyUtil.alertMsg("端口号不合法(0-65535)");
                    return;
                }
                //获取端口类型
                int portType = 0; // 0 : TCP 1: UDP
                if (rdbtn_Tcp.isSelected()) {
                    portType = 0;
                } else {
                    portType = 1;
                }
                //获取每个Ip使用的线程数量
                int threadNum = 200;
                try {
                    threadNum = Integer.parseInt(txt_threadNum.getText().trim());
                } catch (NumberFormatException numberFormatException) {
                    MyUtil.alertMsg("线程数量错误");
                    return;
                }
                if (threadNum <= 0) {
                    MyUtil.alertMsg("线程数量错误");
                    return;
                }
                txt_result.setText("");
                txt_result.paintImmediately(txt_result.getBounds());
                //先测试所有需要扫描的ip,去除那些不可达的地址
                Iterator<String> iterator = ipList.iterator();
                while (iterator.hasNext()) {
                    String nextIp = iterator.next();
                    if (false == MyUtil.ping(nextIp)) {
                        MyUtil.TextAreaAddMsg(txt_result, "目标主机" + nextIp + "不可达\n");
                        iterator.remove();
                    }else{
                        MyUtil.TextAreaAddMsg(txt_result, "目标主机" + nextIp + "可达\n");
                    }
                }

                //N个IP分别启动N个线程，每个线程再启动K个子线程完成扫描,共N*K个线程
                ArrayList<Thread> threadsLaunch=new ArrayList<Thread>();
                long start = System.currentTimeMillis();
                //每个ip开辟1个管理线程+K个工作线程
                for (String ip : ipList) {
                    int finalThreadNum = threadNum;
                    int finalPortBeg = portBeg;
                    int finalPortEnd = portEnd;
                    int finalThreadNum1 = threadNum;
                    int finalPortType = portType;
                    Thread t=new Thread(){
                        @Override
                        public void run() {
                            ArrayList<Thread> workers=new ArrayList<Thread>();
                            for(int i = 0; i< finalThreadNum; ++i){
                                //工作线程去执行扫描任务
                                Thread worker=new Thread(new PortScanner(ip, finalPortBeg, finalPortEnd, finalThreadNum1,txt_result, finalPortType,i));
                                workers.add(worker);
                                worker.start();
                            }
                            //回收K个工作线程
                            for (Thread worker : workers) {
                                try {
                                    worker.join();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    };
                    threadsLaunch.add(t);
                    t.start();
                }
                //回收N个管理线程
                for (Thread launch : threadsLaunch) {
                    try {
                        launch.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                //结束
                long end = System.currentTimeMillis();
                JOptionPane.showMessageDialog(null, "扫描完毕,用时"+(end-start)+"ms", "扫描结束", JOptionPane.OK_OPTION);
            }

        }


    }
}

