package com.librefra.daoliangshu.librefra.daoliangboom;

/**
 * Created by daoliangshu on 12/7/16.
 * Configurations for the multiple choices game "DaoLiangBoom"
 */

public class DLB_Config {
    public final static int SCORE_UNIT = 10;
    public static float CUR_SCORE_WEIGHT = 1.0f;

    /*Sizes */
    public static int width = 100;
    public static int height = 100;
    public static int gameTop = 0;
    public static int gameBottom = 100;
    public static int scoreTop = 0;

    /*Speed Gestion*/
    public static final float SPEED_SLOW = -0.15f;
    public static final float SPEED_MEDIUM = -0.30f;
    public static final float SPEED_FAST = -0.45f;
    public static float currentSpeed = SPEED_SLOW;

    /*Component Ratio */
    public static final float SCORE_PANEL_RATIO = 0.05f;
    public static final float BUTTONS_PANEL_RATIO = 0.15f;
    public static final float TITLE_PANEL_RATION = 0.1f;
    public static final float GAME_PANEL_RATIO = 0.70f;


    public final static int BOX_PER_WAVE = 4;
    public final static int JUMP_UP = 1;
    public final static int JUMP_DOWN = 2;
    public final static int SHOOT = 3;
    public final static int BOX_TOUCHED = 100;

    /*Wave Type */
    public static final int WAVE_TYPE_MULTCHOICE = 0;
    public static final int WAVE_TYPE_ORDER = 1;

    public static void setSpeed(int speed_code) {
        switch (speed_code) {
            case 0:
                currentSpeed = SPEED_SLOW;
                break;
            case 1:
                currentSpeed = SPEED_MEDIUM;
                break;
            case 2:
                currentSpeed = SPEED_FAST;
                break;
            default:
                currentSpeed = SPEED_MEDIUM;
        }
    }

    public static int getSpeedCode() {
        if (currentSpeed == SPEED_SLOW) return 0;
        else if (currentSpeed == SPEED_FAST) return 2;
        else return 1;
    }
}
