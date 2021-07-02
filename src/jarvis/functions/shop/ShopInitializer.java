package jarvis.functions.shop;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Sergey Chuvashov
 *
 * Класс предназначен для быстрой инициализации сервера при подключении по SSH.
 */
@Component
@Log
public class ShopInitializer {

    private final Map<String, ShopInitializerModel> shopInit = new HashMap<>();

    /**
     * Добавить сервер в мапу для быстрой инициализации при следующем использовании.
     * @param sap - sap магазина.
     * @param user - login для ssh авторизации.
     * @param password - password для ssh авторизации.
     */
    public void addShop(String sap, String user, String password){
        log.info("add shop: " + sap);
        shopInit.put(sap, new ShopInitializerModel(sap, user, password));
    }

    /**
     * Получить модель быстрой инициализации.
     * @param sap - sap магазина.
     * @return - модель быстрой инициализации, хранящий логин и пароль ssh.
     */
    public ShopInitializerModel getModel(String sap){
        if (!shopInit.containsKey(sap)){
            throw new NullPointerException("sap не найден в бысрой инициализации");
        }
        log.info("search: " + shopInit.containsKey(sap));
        return shopInit.get(sap);
    }
}
