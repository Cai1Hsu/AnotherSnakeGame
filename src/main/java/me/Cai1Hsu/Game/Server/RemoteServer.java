package me.Cai1Hsu.Game.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import me.Cai1Hsu.Game.Server.Packets.ClientCommand;
import me.Cai1Hsu.Game.Server.Packets.ClientPacket;
import me.Cai1Hsu.Game.Server.Packets.ServerPacket;
import me.Cai1Hsu.Game.Shared.Gameplay.Direction;

public class RemoteServer extends ServerBase {

    private InetSocketAddress _serverIp;
    private int _serverPort;

    private Socket _socket;

    public RemoteServer(String serverIp, int serverPort) throws UnknownHostException {
        _serverIp = new InetSocketAddress(serverIp, serverPort);
        _serverPort = serverPort;
    }

    @Override
    public void onEnd() {
        try {
            _socket.close();
        } catch (IOException e) {
        }
    }

    private Instant lastSendTime = Instant.now();

    private void send(ClientPacket packet) {
        try {
            _out.writeObject(packet);
            _out.flush();
        } catch (IOException e) {
            // System.out.println(e.getMessage());
            _playfield.isGameOver = true;
        }

        lastSendTime = Instant.now();
    }

    @Override
    public boolean swapMessage() {
        return true;
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

            if (!fetched) {
                var packet = new ClientPacket();
                packet.command = ClientCommand.REQUEST_FULL_STATE;
                send(packet);
                fetched = swapMessage();
            }
        }
    }

    private void sendJoin() {
        var packet = new ClientPacket();
        packet.command = ClientCommand.JOIN;
        send(packet);
    }

    private void sendMove(Direction direction) {
        var packet = new ClientPacket();
        switch (direction) {
            case UP:
                packet.command = ClientCommand.MOVE_UP;
                break;

            case DOWN:
                packet.command = ClientCommand.MOVE_DOWN;
                break;

            case LEFT:
                packet.command = ClientCommand.MOVE_LEFT;
                break;

            case RIGHT:
                packet.command = ClientCommand.MOVE_RIGHT;
                break;

            default:
                break;
        }
        send(packet);
    }

    private Optional<String> serverIp = Optional.empty();

    @Override
    public void setSelfDirection(Direction direction) {
        super.setSelfDirection(direction);
        sendMove(direction);
    }

    @Override
    public String getServerIp() {
        if (serverIp.isEmpty()) {
            var port = _serverPort;
            var ip = _serverIp.getAddress().getHostAddress();
            serverIp = Optional.of(ip + ":" + port);
        }

        return serverIp.get();
    }

    private long ping = 0;

    @Override
    public int getPing() {
        return (int) ping;
    }

    private ObjectOutputStream _out;
    private ObjectInputStream _in;

    @Override
    public void connectServer() {
        try {
            _socket = new Socket(_serverIp.getHostName(), _serverPort);
            _out = new ObjectOutputStream(_socket.getOutputStream());
            _in = new ObjectInputStream(_socket.getInputStream());

            new Thread(new ServerHandler()).start();
        } catch (IOException e) {
            // System.out.println(e.getMessage());
            _playfield.isGameOver = true;
            return;
        }

        sendJoin();
    }

    class ServerHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    var packet = (ServerPacket) _in.readObject();
                    onPacketReceived(packet);
                } catch (IOException e) {
                    _playfield.isGameOver = true;
                    var msg = e.getMessage();
                    // System.out.println(msg);
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }

    private boolean onPacketReceived(ServerPacket packet) {
        ping = Instant.now().toEpochMilli() - lastSendTime.toEpochMilli();

        switch (packet.command) {
            case REQUEST_FULL_STATE:
                _host.drawCanvas((String) packet.data);
                break;

            case JOIN:
                _host.drawCanvas((String) packet.data);
                _selfId = packet.code;
                break;

            case NONE:
            default:
                return false;
        }

        return true;
    }
}
