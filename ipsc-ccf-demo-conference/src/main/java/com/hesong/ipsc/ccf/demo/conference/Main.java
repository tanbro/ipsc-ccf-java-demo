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

    private static Commander commander;
    private static String conferenceId;

    public static void main(String[] args) throws InterruptedException {
        logger.info("Data Bus 客户端单元初始化");
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                logger.info("成功的连接到了IPSC服务程序的 Data Bus");
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
        System.out.printf("会议DEMO开始了! 输入 \"c\" 建立会议.\n");
        while (true) {
            inputStr = scanner.nextLine();
            if (inputStr.trim().toLowerCase().startsWith("c")) {
                logger.info("建立会议");
                try {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("max_seconds", 300); /// 会议最长时间，这是必填参数
                    commander.createResource(
                            new BusAddress(commander.getConnectingUnitId(), (byte) 0),
                            "sys.conf",
                            params,
                            new RpcResultListener() {
                                @Override
                                protected void onResult(Object o) {
                                    Map<String, Object> result = (Map<String, Object>) o;
                                    conferenceId = (String) result.get("res_id");
                                    logger.info("会议建立成功，ID=%s", conferenceId);
                                }

                                @Override
                                protected void onError(RpcError rpcError) {
                                    logger.error("创建会议资源错误：%d %s", rpcError.getCode(), rpcError.getMessage());
                                }

                                @Override
                                protected void onTimeout() {
                                    logger.error("创建会议资源超时无响应");
                                }
                            }
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
