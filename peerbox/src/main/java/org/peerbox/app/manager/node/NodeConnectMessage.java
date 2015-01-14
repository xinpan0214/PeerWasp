package org.peerbox.app.manager.node;

public final class NodeConnectMessage implements INodeMessage {

	private final String nodeAddress;

	NodeConnectMessage(final String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}

	public String getNodeAddress() {
		return nodeAddress;
	}

}
