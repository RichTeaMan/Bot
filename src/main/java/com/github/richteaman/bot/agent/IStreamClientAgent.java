package com.github.richteaman.bot.agent;

import java.net.SocketAddress;

public interface IStreamClientAgent {
	public void connect(SocketAddress streamServerAddress);
	public void stop();
}
