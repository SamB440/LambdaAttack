package com.github.games647.lambdaattack.bot;

import com.github.games647.lambdaattack.LambdaAttack;
import com.github.games647.lambdaattack.UniversalFactory;
import com.github.games647.lambdaattack.UniversalProtocol;
import com.github.games647.lambdaattack.bot.listener.SessionListener111;
import com.github.games647.lambdaattack.bot.listener.SessionListener112;
import com.github.games647.lambdaattack.bot.listener.SessionListener114;
import com.github.games647.lambdaattack.bot.listener.SessionListener115;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import java.net.Proxy;
import java.util.logging.Logger;

public class Bot {

    public static final char COMMAND_IDENTIFIER = '/';

    private final Proxy proxy;
    private final Logger logger;
    private final UniversalProtocol account;

    private Session session;
    private EntityLocation location;
    private float health = -1;
    private float food = -1;

    public Bot(UniversalProtocol account) {
        this(account, Proxy.NO_PROXY);
    }

    public Bot(UniversalProtocol account, Proxy proxy) {
        this.account = account;
        this.proxy = proxy;

        this.logger = Logger.getLogger(account.getProfile().getName());
        this.logger.setParent(LambdaAttack.getLogger());
    }

    public void connect(String host, int port) throws RequestException {
        Client client = new Client(host, port, account.getProtocol(), new TcpSessionFactory(proxy));
        this.session = client.getSession();

        switch (account.getGameVersion()) {
            case VERSION_1_11:
                client.getSession().addListener(new SessionListener111(this));
                break;
            case VERSION_1_12:
                client.getSession().addListener(new SessionListener112(this));
                break;
            case VERSION_1_14:
                client.getSession().addListener(new SessionListener114(this));
                break;
            case VERSION_1_15:
                client.getSession().addListener(new SessionListener115(this));
                break;
            default:
                throw new IllegalStateException("Unknown session listener");
        }

        client.getSession().connect();
    }

    public void sendMessage(String message) {
        if (session != null) {
            UniversalFactory.sendChatMessage(account.getGameVersion(), message, getSession());
        }
    }

    public boolean isOnline() {
        return session != null && session.isConnected();
    }

    public Session getSession() {
        return session;
    }

    public EntityLocation getLocation() {
        return location;
    }

    public void setLocation(EntityLocation location) {
        this.location = location;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public Logger getLogger() {
        return logger;
    }

    public GameProfile getGameProfile() {
        return account.getProfile();
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect("Disconnect");
        }
    }
}
