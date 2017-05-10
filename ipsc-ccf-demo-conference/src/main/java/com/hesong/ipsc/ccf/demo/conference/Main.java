package com.hesong.ipsc.ccf.demo.conference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hesong.ipsc.ccf.*;

/**
 * Created by tanbr on 2017/5/10.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String ipscIpAddr = "192.168.2.100"; /// IPSC 服务器的内网地址
    private static final byte localId = 24;
    private static final byte commanderId = 10;

    private static boolean isFirstConnected = false;
    private static Commander commander;

    public static void main(String[] args) throws InterruptedException {
        logger.info("Data Bus 客户端单元初始化");
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                logger.info("成功的连接到了IPSC服务程序的 Data Bus");
                if (client.getId() == commander.getId()) {
                    if (!isFirstConnected) {
                        isFirstConnected = true;
                        logger.info("第一次连接，新建一个会议资源");
                        try {
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("max_seconds", 300); /// 会议最长时间，这是必填参数
                            commander.createResource(
                                    new BusAddress(client.getConnectingUnitId(), (byte) 0),
                                    "sys.conf",
                                    params,
                                    new RpcResultListener() {
                                        @Override
                                        protected void onResult(Object o) {

                                        }

                                        @Override
                                        protected void onError(RpcError rpcError) {

                                        }

                                        @Override
                                        protected void onTimeout() {

                                        }
                                    }
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

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
        commander = Unit.createCommander(commanderId, ipscIpAddr, new RpcEventListener() {
            public void onEvent(BusAddress busAddress, RpcRequest rpcRequest) {

            }
        });

        /// 开始执行
        String inputStr;
        Scanner scanner = new Scanner(System.in);
        System.out.printf("会议DEMO开始了! 输入 \"quit\" 或者 \"q\" 退出程序.\n");
        while (true) {
            inputStr = scanner.nextLine();
            if (inputStr.trim().toLowerCase().startsWith("q")) {
                break;
            }
        }

    }
}
