package me.Cai1Hsu.Game.Server;

import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Optional;

import me.Cai1Hsu.Game.Server.Packets.ClientPacket;
import me.Cai1Hsu.Game.Server.Packets.ServerPacket;
import me.Cai1Hsu.Game.Shared.Gameplay.Direction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends ServerBase {
    private ServerSocket _server;
    private Socket _clientSocket;

    public LocalServer(int port) throws IOException {
        _server = new ServerSocket(port);
    }

    @Override
    public void onEnd() {
        try {
            _server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean swapMessage() {
        return true;
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

    private Optional<String> serverIp = Optional.empty();

    @Override
    public String getServerIp() {
        if (serverIp.isEmpty()) {
            var port = _server.getLocalPort();
            var ip = getLocalHostLANAddress().getHostAddress();
            serverIp = Optional.of(ip + ":" + port);
        }

        return serverIp.get();
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

        return _server.getInetAddress();
    }

    @Override
    public int getPing() {
        // Local server has no ping.
        return 0;
    }

    @Override
    public void connectServer() {
        try {
            _clientSocket = _server.accept();

            new Thread(new ClientHandler(_clientSocket)).start();
        } catch (IOException e) {
            _playfield.isGameOver = true;
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket _clientSocket;
        private ObjectInputStream _in;
        private ObjectOutputStream _out;

        public ClientHandler(Socket socket) throws IOException {
            this._clientSocket = socket;
            this._in = new ObjectInputStream(_clientSocket.getInputStream());
            this._out = new ObjectOutputStream(_clientSocket.getOutputStream());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    var packet = (ClientPacket) _in.readObject();

                    onPacketReceived(packet);
                } catch (IOException | ClassNotFoundException e) {
                }
            }
        }

        private int _count = 0;

        private boolean onPacketReceived(ClientPacket packet) {
            switch (packet.command) {
                case REQUEST_FULL_STATE: {
                    var toSend = new ServerPacket();
                    toSend.code = 0;
                    toSend.command = packet.command;
                    toSend.data = _host._canvas.Render();

                    return replyClient(toSend);
                }

                case JOIN: {
                    var toSend = new ServerPacket();
                    toSend.command = packet.command;
                    toSend.code = _playfield.joinGame();
                    toSend.data = _host._canvas.Render();

                    return replyClient(toSend);
                }

                case MOVE_DOWN:
                    _playfield.getPlayer(1).setDirection(Direction.DOWN);
                    return true;
                case MOVE_UP:
                    _playfield.getPlayer(1).setDirection(Direction.UP);
                    return true;
                case MOVE_LEFT:
                    _playfield.getPlayer(1).setDirection(Direction.LEFT);
                    return true;
                case MOVE_RIGHT:
                    _playfield.getPlayer(1).setDirection(Direction.RIGHT);
                    return true;

                case NONE:
                default:
                    return false;
            }
        }

        private boolean replyClient(ServerPacket packet) {
            try {
                _out.writeObject(packet);
                _out.flush();
                return true;
            } catch (IOException e) {
            }

            return false;
        }
    }
}
