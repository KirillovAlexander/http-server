package ru.netology.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Runnable serverRunnable = new ServerRunnable(socket, validPaths);
                    threadPool.submit(serverRunnable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


