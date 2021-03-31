package ru.netology.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ServerRunnable implements Runnable {

    private final Socket socket;
    private final List<String> validPaths;
    private final Server server;

    public ServerRunnable(Socket socket, List<String> validPaths, Server server) {
        this.socket = socket;
        this.validPaths = validPaths;
        this.server = server;
    }

    @Override
    public void run() {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            final String requestLine = in.readLine();
            Request request = new Request();
            request.setRequestLine(requestLine);
            String line;
            while ((line = in.readLine()) != "\r\n") {
                request.addHeader(line);
            }
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            request.setBody(sb.toString());

            Handler handler = server.getHandler(request.getMethod(), request.getFirstPath());
            if (handler == null) {
                socket.close();
                return;
            }
            handler.handle(request, out);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
