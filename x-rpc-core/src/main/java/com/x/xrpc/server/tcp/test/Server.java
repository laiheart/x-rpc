package com.x.xrpc.server.tcp.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    Launcher.executeCommand("run", Server.class.getName());
  }

  @Override
  public void start() throws Exception {

    vertx.createNetServer().connectHandler(sock -> {

      // Create a pipe
      sock.pipeTo(sock);

    }).listen(1234);

    System.out.println("Echo server is now listening");

  }
}