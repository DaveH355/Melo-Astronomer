package com.dave.astronomer.client.multiplayer;

import java.net.InetAddress;
import java.util.Objects;

public class LanServer {
    public String serverName;
    public String serverInfo;
    public InetAddress address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanServer lanServer = (LanServer) o;
        return address.equals(lanServer.address);
    }
    @Override
    public int hashCode() {
        return Objects.hash(address);
    }




}
