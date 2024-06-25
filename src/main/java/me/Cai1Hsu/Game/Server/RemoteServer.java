package me.Cai1Hsu.Game.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

public class RemoteServer extends ServerBase {

    private InetSocketAddress _serverIp;
    private int _serverPort;

    private Socket _socket;

    public RemoteServer(String serverIp, int serverPort) throws UnknownHostException {
        _serverIp = new InetSocketAddress(serverIp, serverPort);
        _serverPort = serverPort;
    }

    public boolean connect(int timeout) throws UnknownHostException, IOException {
        _socket = new Socket(_serverIp.getHostName(), _serverPort);
        _socket.connect(_serverIp, timeout);

        return _socket.isConnected();
    }

    @Override
    public void onEnd() {
        try {
            _socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public boolean swapMessage() {
        return false;
    }

    @Override
    public void onUpdate(Duration delta) {
        var begin = Instant.now();
        boolean fetched = false;

        while (Duration.between(begin, Instant.now()).compareTo(delta) < 0) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!fetched && swapMessage()) {
                fetched = true;
            }
        }
    }

    @Override
    public String getServerIp() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getServerIp'");
    }

    @Override
    public int getPing() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPing'");
    }

}
