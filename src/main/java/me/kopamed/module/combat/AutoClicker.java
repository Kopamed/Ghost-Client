// add:
// onlyBlocks
// onlySword/Weapon
// alloweat
// allow bow

package me.kopamed.module.combat;

import io.netty.util.internal.ThreadLocalRandom;
import me.kopamed.Galacticc;
import me.kopamed.module.Category;
import me.kopamed.module.Module;
import me.kopamed.settings.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class AutoClicker extends Module {
    private long lastClick;
    private long hold;
    private double speedLeft, speedRight;
    private double leftHoldLength, rightHoldLength;
    private float leftMinCPS, leftMaxCPS, rightMinCPS, rightMaxCPS;
    private boolean leftActive, rightActive;

    public AutoClicker() {
        //Setting module attributes
        super("AutoClicker", "Clicks for you", false, false, Category.COMBAT);

        //Creating left click settings
        Setting leftActive = new Setting("Left Click", this, true);
        Setting leftMinCPS = new Setting("Left MinCPS", this, 8, 1, 60, false);
        Setting leftMaxCPS = new Setting("Left MaxCPS", this, 13, 1, 60, false);

        //Creating right click settings
        Setting rightActive = new Setting("Right Click", this, true);
        Setting rightMinCPS = new Setting("Right MinCPS", this, 12, 5, 60, false);
        Setting rightMaxCPS = new Setting("Right MaxCPS", this, 16, 5, 60, false);

        //Adding left click settings to gui
        Galacticc.instance.settingsManager.rSetting(leftActive);
        Galacticc.instance.settingsManager.rSetting(leftMinCPS);
        Galacticc.instance.settingsManager.rSetting(leftMaxCPS);

        //Adding right click settings to gui
        Galacticc.instance.settingsManager.rSetting(rightActive);
        Galacticc.instance.settingsManager.rSetting(rightMinCPS);
        Galacticc.instance.settingsManager.rSetting(rightMaxCPS);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if (Galacticc.instance.destructed) {return;}
        this.updateVals();
        //If none of the buttons are allowed to click, what is the point in generating clicktimes anyway?
        if (!leftActive && !rightActive) {
            return;
        }


        // Uhh left click only, mate
        if (Mouse.isButtonDown(0) && leftActive) {
            if (System.currentTimeMillis() - lastClick > speedLeft * 1000) {
                lastClick = System.currentTimeMillis();
                if (hold < lastClick){
                    hold = lastClick;
                }
                int key = mc.gameSettings.keyBindAttack.getKeyCode();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
            } else if (System.currentTimeMillis() - hold > leftHoldLength * 1000) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
        }
        //we cheat in a block game ft. right click
        if (Mouse.isButtonDown(1) && rightActive) {
            if (System.currentTimeMillis() - lastClick > speedRight * 1000) {
                lastClick = System.currentTimeMillis();
                if (hold < lastClick){
                    hold = lastClick;
                }
                int key = mc.gameSettings.keyBindUseItem.getKeyCode();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
            } else if (System.currentTimeMillis() - hold > rightHoldLength * 1000) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        this.updateVals();
    }

    private void updateVals(){
        //Getting setting values
        leftActive = Galacticc.instance.settingsManager.getSettingByName(this, "Left Click").getValBoolean();
        rightActive = Galacticc.instance.settingsManager.getSettingByName(this, "Right Click").getValBoolean();

        leftMinCPS = (float) Galacticc.instance.settingsManager.getSettingByName(this, "Left MinCPS").getValDouble();
        leftMaxCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Left MaxCPS").getValDouble();

        rightMinCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Right MinCPS").getValDouble();
        rightMaxCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Right MaxCPS").getValDouble();

        if (leftMinCPS >= leftMaxCPS) {
            Galacticc.instance.settingsManager.getSettingByName(this, "Left MaxCPS").setValDouble((float)leftMinCPS + 0.01F);
            Galacticc.instance.settingsManager.getSettingByName(this, "Left MinCPS").setValDouble((float)leftMaxCPS - 0.01F);

            leftMinCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Left MinCPS").getValDouble();
            leftMaxCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Left MaxCPS").getValDouble();
        }
        if (rightMinCPS >= rightMaxCPS) {
            Galacticc.instance.settingsManager.getSettingByName(this, "Right MaxCPS").setValDouble((float)rightMinCPS + 0.1F);
            Galacticc.instance.settingsManager.getSettingByName(this, "Right MinCPS").setValDouble((float)rightMaxCPS - 0.1F);

            rightMinCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Right MinCPS").getValDouble();
            rightMaxCPS = (float)Galacticc.instance.settingsManager.getSettingByName(this, "Right MaxCPS").getValDouble();
        }

        // add -0.2 to make it slightly more random
        speedLeft = 1.0 / ThreadLocalRandom.current().nextDouble(leftMinCPS - 0.2, leftMaxCPS);
        leftHoldLength = speedLeft / ThreadLocalRandom.current().nextDouble(leftMinCPS, leftMaxCPS);
        speedRight = 1.0 / ThreadLocalRandom.current().nextDouble( rightMinCPS - 0.2, rightMaxCPS);
        rightHoldLength = speedRight / ThreadLocalRandom.current().nextDouble(rightMinCPS, rightMaxCPS);
    }
}