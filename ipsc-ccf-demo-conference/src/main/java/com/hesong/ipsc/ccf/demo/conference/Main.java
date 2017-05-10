package com.hesong.ipsc.ccf.demo.conference;

import java.util.Scanner;

import com.hesong.ipsc.ccf.*;

/**
 * Created by tanbr on 2017/5/10.
 */
public class Main {

    private static String ipscIpAddr = "192.168.2.100"; /// IPSC 服务器的内网地址
    private static byte localId = 24;
    private static byte commanderId = 10;

    public static void main(String[] args) throws InterruptedException {
        /// Data Bus 客户端单元初始化
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                // 成功的连接到了IPSC服务程序的 Data Bus
                System.out.format("[%d] 连接成功", client.getId());
                if (client.getId() == commanderId) {


                }
            }

            public void connectFailed(Client client, int i) {
                System.out.format("[%d] 连接失败", client.getId());
            }

            public void connectLost(Client client) {
                System.out.format("[%d] 连接丢失", client.getId());
            }

            public void globalConnectStateChanged(byte b, byte b1, byte b2, byte b3, String s) {

            }
        });

        /// 新建一个命令发送者
        Unit.createCommander(commanderId, ipscIpAddr, new RpcEventListener() {
            public void onEvent(BusAddress busAddress, RpcRequest rpcRequest) {

            }
        });

        /// 开始执行
        String inputStr;
        Scanner scanner = new Scanner(System.in);
        System.out.printf("开始了! 输入 \"quit\" 或者 \"q\" 退出程序.\n");
        while (true) {
            inputStr = scanner.nextLine();
            if (inputStr.trim().toLowerCase().startsWith("q")) {
                break;
            }
        }

    }
}
