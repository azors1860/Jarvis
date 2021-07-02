package jarvis;

import jarvis.functions.InformationWindow;
import jarvis.functions.Cleanable;
import jarvis.functions.setting.Setting;
import jarvis.functions.setting.models.SettingButtonProp;
import jarvis.functions.shop.Shop;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Sergey Chuvashov
 */

@Log
@Lazy
public abstract class BasicButton {

    @Setter
    protected WindowApp windowApp;

    @Setter
    protected boolean windowItsAction;

    protected boolean confirmActionButton;
    protected boolean autoClose;
    protected static Set<BasicButton> windows = new HashSet<>();
    protected Shop shop;
    private Setting setting;

    /**
     * Инициализатор объектов.
     * Добавляет в коллекцию Cleanable, если объект им является;
     * Применяет настройки к объекту, если они были указаны, в ином случае применяются стандартные настройки.
     */
    @PostConstruct
    public void init() {
        if (this instanceof Cleanable) {
            shop.addCleanableServices((Cleanable) this);
        }
        BasicButton.windows.add(this);
        try {
            SettingButtonProp settingButton = setting.getSetting(getClass().getName());
            this.confirmActionButton = settingButton.isConfirmActionButton();
            this.autoClose = settingButton.isAutoClose();

        } catch (NullPointerException e) {
            log.warning(getClass().getName() + " no load setting");
        }
    }

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Autowired
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public abstract void action();

    /**
     * В случае, если настройка confirmActionButton активна - вызывает окно для подтверждения действия.
     *
     * В ином случае, сразу вызывает действие. Если действием является открытие окна
     * и при этом окно уже открыто - поднимает окно наверх.
     *
     */
    public void windowActionButton() {
        if (confirmActionButton) {
            InformationWindow.openWindowYesOrNo(this);
        } else {
            if (!windowItsAction) {
                windowItsAction = true;
                this.action();
                windowItsAction = false;
            } else if (windowApp!=null) {
                windowApp.windowUp();
            }
        }
    }

    /**
     * Перебирает список объектов и закрывает окна при соответствии условий.
     */
    public static void closeWindows() {
        for (BasicButton button : windows) {
            if ((button.windowApp != null) && (button.windowItsAction) && (button.autoClose)) {
                button.windowApp.closeWindow();
            }
        }
    }
}
