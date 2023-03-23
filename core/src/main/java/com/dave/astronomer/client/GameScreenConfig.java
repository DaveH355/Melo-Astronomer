package com.dave.astronomer.client;


import com.dave.astronomer.common.network.NetworkUtils;

public class GameScreenConfig {
    public boolean startServer = false;
    public String address = "localhost";
    public int tcpPort = NetworkUtils.TCP_PORT;
    public int udpPort = NetworkUtils.UDP_PORT;
    public int connectTimeout = 5000;

}
