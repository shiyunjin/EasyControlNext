package com.shiyunjin.easycontrolnext.app.adb;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.github.muntashirakon.adb.AbsAdbConnectionManager;
import io.github.muntashirakon.adb.AdbConnection;

public class ManagerChannel implements AdbChannel {
    private final AbsAdbConnectionManager manager;

    private final AdbConnection adbConnection;

    public ManagerChannel(AbsAdbConnectionManager _manager, Adb _adb) throws IOException, InterruptedException {
        manager = _manager;
        manager.setAdbCTop(_adb);
        adbConnection = manager.getAdbConnection();
    }

    @Override
    public void write(ByteBuffer data) throws IOException {
        adbConnection.sendPacket(data.array());
    }

    @Override
    public void flush() throws IOException {
        adbConnection.flushPacket();
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
        byte[] buffer = new byte[size];
        return ByteBuffer.wrap(buffer);
    }

    @Override
    public void close() {
        try {
            manager.disconnect();
        } catch (Exception ignored) {
        }
    }
}