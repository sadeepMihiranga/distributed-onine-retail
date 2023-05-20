package lk.sadeep.itt.retail.custom.nodemanager;

public class NodeInfo {

    private String protocol;
    private String port;
    private String ip;

    public NodeInfo(String protocol, String port, String ip) {
        this.protocol = protocol;
        this.port = port;
        this.ip = ip;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
