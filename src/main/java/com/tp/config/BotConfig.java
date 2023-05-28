package com.tp.config;

import com.tp.handler.QHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.auth.QRCodeLoginListener;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.FileCacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

import static com.tp.common.constant.QConstant.LOGIN_PASSWORD;
import static com.tp.common.constant.QConstant.LOGIN_QR;

@Component
public class BotConfig {
    @Autowired
    QConfig qq;
    @Autowired
    QHandler qHandler;

    @PostConstruct
    public void init() {
        if (qq.getEnable()) {
            Mirai.getInstance().setFileCacheStrategy(new FileCacheStrategy.TempCache(new File("D:/")));
            //登录 登陆协议ANDROID_PHONE, ANDROID_PAD, ANDROID_WATCH, IPAD, MACOS
            Bot bot;
            if (LOGIN_QR.equals(qq.getLoginMethod())) {
                // 扫码登录
                bot = BotFactory.INSTANCE.newBot(qq.getAccount(), BotAuthorization.byQRCode(), configuration -> {
                    configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                });
            } else if (LOGIN_PASSWORD.equals(qq.getLoginMethod())){
                // 密码登录
                bot = BotFactory.INSTANCE.newBot(qq.getAccount(), qq.getPassword(), configuration -> {
                    configuration.setProtocol(BotConfiguration.MiraiProtocol.IPAD);
                });
            } else {
                throw new RuntimeException();
            }
            bot.login();
            bot.getEventChannel().registerListenerHost(qHandler);
        }
    }
}
