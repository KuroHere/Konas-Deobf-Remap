package me.darki.konas.module;

import me.darki.konas.module.Module;

public class Zoom
extends Module {
    public static float Field777;
    public static Class530<Float> Field778;

    @Override
    public void onEnable() {
        Field777 = Zoom.mc.gameSettings.fovSetting;
        Zoom.mc.gameSettings.fovSetting *= 1.6f - ((Float)Field778.getValue()).floatValue();
    }

    public Zoom() {
        super("Zoom", "Zoom in properly, not with perspective modification", Category.RENDER, new String[0]);
    }

    @Override
    public void onDisable() {
        Zoom.mc.gameSettings.fovSetting = Field777;
    }

    static {
        Field778 = new Class530("Zoom", Float.valueOf(1.1f), Float.valueOf(1.5f), Float.valueOf(0.5f), Float.valueOf(0.05f), Zoom::Method825);
    }

    public static void Method825(Float f) {
        if (Class167.Method1610(Zoom.class).Method1651()) {
            Zoom.mc.gameSettings.fovSetting = Field777 * (1.6f - f.floatValue());
        }
    }
}
