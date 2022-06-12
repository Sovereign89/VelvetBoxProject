package ru.geekbrains.velvetbox.network;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.geekbrains.velvetbox.global.GlobalMessagingService;

import java.io.IOException;
import java.net.Socket;

public class Network {

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    public ObjectDecoderInputStream getIs() {
        return is;
    }

    public ObjectEncoderOutputStream getOs() {
        return os;
    }

    public Network(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public String readString() throws IOException {
        return is.readUTF();
    }

    public int readInt() throws IOException {
        return is.readInt();
    }

    public void writeMessage(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public String readMessage() throws IOException {
        return readString();
    }

    public GlobalMessagingService read() throws IOException, ClassNotFoundException {
        return (GlobalMessagingService) is.readObject();
    }

    public void write(GlobalMessagingService msg) throws IOException {
        os.writeObject(msg);
        os.flush();
    }
}
