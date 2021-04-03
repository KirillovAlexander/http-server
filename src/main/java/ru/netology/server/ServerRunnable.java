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

    public ServerRunnable(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            System.out.println("Жду сообщения.");
            final String requestLine = in.readLine();
            System.out.println("Получил сообщение: " + requestLine);
            final String[] parts = requestLine.split(" ");

            if (parts.length != 3) {
                System.out.println("Не корректный реквест, отправляю ошибку 400");
                badRequest(out);
                socket.close();
                return;
            }

            final String path = parts[1];
            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                socket.close();
                return;
            }

            final Path filePath = Path.of(".", "public", path);
            final String mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final String template = Files.readString(filePath);
                final byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                socket.close();
                return;
            }

            final long length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}
