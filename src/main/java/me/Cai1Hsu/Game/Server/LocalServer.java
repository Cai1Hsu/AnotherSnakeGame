package me.Cai1Hsu.Game.Server;

import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Optional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;

public class LocalServer extends ServerBase {
    private ServerSocket _socket;

    public LocalServer(int port) throws IOException {
        _socket = new ServerSocket(port);
        _socket.setReuseAddress(true);
    }

    @Override
    public void onEnd() {
        try {
            _socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean swapMessage() {
        return false;
    }

    @Override
    public void onUpdate(Duration delta) {
        // Update the game first so that the clients can get the latest game state.
        gameUpdate();

        var begin = Instant.now();

        while (Duration.between(begin, Instant.now()).compareTo(delta) < 0) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            swapMessage();
        }
    }

    private Optional<String> _serverIp = Optional.empty();

    @Override
    public String getServerIp() {
        if (_serverIp.isEmpty()) {
            var port = _socket.getLocalPort();
            var ip = getLocalHostLANAddress().getHostAddress();
            _serverIp = Optional.of(ip + ":" + port);
        }

        return _serverIp.get();
    }

    private InetAddress getLocalHostLANAddress() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();

            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual())
                    continue;

                Enumeration<InetAddress> addrs = nic.getInetAddresses();

                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr.isLoopbackAddress() || !addr.isSiteLocalAddress())
                        continue;

                    return addr;
                }
            }
        } catch (Exception e) {
        }

        return _socket.getInetAddress();
    }

    @Override
    public int getPing() {
        // Local server has no ping.
        return 0;
    }
}
