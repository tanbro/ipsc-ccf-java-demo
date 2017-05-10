package com.hesong.ipsc.ccf.demo.conference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

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

    private static Commander commander = null;
    private static String conferenceId = "";
    private static BusAddress busAddress = null;

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info("Data Bus 客户端单元初始化");
        Unit.initiate(localId, new UnitCallbacks() {
            public void connectSucceed(Client client) {
                logger.info("成功的连接到了IPSC服务程序的 Data Bus");
                busAddress = new BusAddress(commander.getConnectingUnitId(), (byte) 0);
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
                String fullMethodName = rpcRequest.getMethod();
                if (fullMethodName.startsWith("sys.call")) {
                    String methodName = fullMethodName.substring(8);
                    final String callId = (String) rpcRequest.getParams().get("res_id");
                    if (methodName.equals("on_released")) {
                        logger.warn("呼叫 %s 已经释放", callId);
                    } else if (methodName.equals("on_ringing")) {
                        logger.info("呼叫 %s 振铃", callId);
                    } else if (methodName.equals("on_dial_completed")) {
                        String error = (String) rpcRequest.getParams().get("error");
                        if (error == null) {
                            logger.info("呼叫 %s 拨号成功，操作呼叫资源，让它加入会议 %s ...", callId, conferenceId);
                            try {
                                Map<String, Object> params = new HashMap<String, Object>();
                                params.put("res_id", callId);
                                params.put("conf_res_id", conferenceId);
                                params.put("max_seconds", 300);
                                commander.operateResource(
                                        busAddress,
                                        UUID.randomUUID().toString(),
                                        "sys.call.conf_enter",
                                        params,
                                        new RpcResultListener() {
                                            @Override
                                            protected void onResult(Object o) {
                                                logger.info("呼叫 %s 加入会议 %s 操作完毕", callId, conferenceId);
                                            }

                                            @Override
                                            protected void onError(RpcError rpcError) {
                                                logger.error("呼叫 %s 加入会议 %s 操作错误: %s", callId, conferenceId, rpcError.getMessage());
                                            }

                                            @Override
                                            protected void onTimeout() {
                                                logger.error("呼叫 %s 加入会议 %s 操作超时无响应", callId, conferenceId);
                                            }
                                        }
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            logger.error("呼叫 %s 拨号失败：%s", callId, error);
                        }
                    }
                }
            }
        });

        /// 开始执行
        String inputStr;
        Scanner scanner = new Scanner(System.in);
        System.out.printf("会议DEMO开始了! \n" +
                "\t输入 \"conf\" 建立会议.\n" +
                "\t输入 \"call + <空格> + <电话号码>\" 呼叫该号码并在呼通后加入会议");
        while (true) {
            inputStr = scanner.nextLine().trim().toLowerCase();
            if (inputStr.equals("conf")) {
                if (!conferenceId.isEmpty()) {
                    logger.warn("这个DEMO就写了一个会议，别新建多个！");
                    continue;
                }
                logger.info("建立会议");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("max_seconds", 300); /// 会议最长时间，这是必填参数
                commander.createResource(
                        busAddress,
                        "sys.conf",
                        params,
                        new RpcResultListener() {
                            @Override
                            protected void onResult(Object o) {
                                Map<String, Object> result = (Map<String, Object>) o;
                                conferenceId = (String) result.get("res_id");
                                logger.info("会议资源建立成功，ID=%s", conferenceId);
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
            } else if (inputStr.startsWith("call")) {
                String tel = inputStr.substring(4).trim();
                logger.info("呼叫 %s", tel);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("to_uri", tel); /// 被叫号码的 SIP URI
                params.put("max_answer_seconds", 300); /// 该呼叫最长通话允许时间
                commander.createResource(
                        busAddress,
                        "sys.call",
                        params,
                        new RpcResultListener() {
                            @Override
                            protected void onResult(Object o) {
                                Map<String, Object> result = (Map<String, Object>) o;
                                String callId = (String) result.get("res_id");
                                logger.info("呼叫资源建立成功，ID=%s。系统正在执行外呼……注意这不是呼叫成功！", callId);
                            }

                            @Override
                            protected void onError(RpcError rpcError) {
                                logger.error("创建呼叫资源错误：%d %s", rpcError.getCode(), rpcError.getMessage());
                            }

                            @Override
                            protected void onTimeout() {
                                logger.error("创建呼叫资源超时无响应");
                            }
                        }
                );
            }
        }
    }

}
