package com.librefra.daoliangshu.librefra.daoliangboom.service;

import android.view.MotionEvent;

import com.librefra.daoliangshu.librefra.daoliangboom.ChoiceBox;
import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Projectile;
import com.librefra.daoliangshu.librefra.daoliangboom.Player;
import com.librefra.daoliangshu.librefra.daoliangboom.TirAChoixView;
import com.librefra.daoliangshu.librefra.daoliangboom.data._CONST;

import java.util.ArrayList;

/**
 * Created by gigitintin on 06/08/16.
 * methods to process some events
 */
public class EventManager {

    /**
     * Trigger when receive touchEvent when the game is in active state
     *
     * @param event
     * @param v
     */
    public static void checkTouch_ActiveState(MotionEvent event, TirAChoixView v) {
        int action = event.getAction();

        //int X = (int) event.getX();
        //int Y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                switch (checkTouchUp(event, v)) {
                    case _CONST.JUMP_UP:
                        v.getMainCharacter().setSpeedY(-25);
                        break;
                    case _CONST.JUMP_DOWN:
                        v.getMainCharacter().setSpeedY(25);
                        break;
                    case _CONST.SHOOT:
                        if (v.getMainCharacter().getState()) break; // character currently busy
                        v.getMainCharacter().triggerActive(); /* trigger player animation */
                        ArrayList<DLB_Projectile> proj = v.getProjectiles();
                        Player mainChar = v.getMainCharacter();
                        proj.get(v.getNextProjectileIndex()).setPos(
                                mainChar.getPositionRectF().right + 5,
                                mainChar.getPositionRectF().top);
                        proj.get(v.getNextProjectileIndex()).setState(DLB_Projectile.STATE_ACTIVE);
                        proj.get(v.getNextProjectileIndex()).setSpeed(10);
                        v.updateNextProjectileIndex();
                        break;
                    case _CONST.BOX_TOUCHED:
                        break;
                    default:
                        ;
                }
                break;
        }
    }


    public static int checkTouchUp(MotionEvent e, TirAChoixView v) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        if (v.isCollideAt(v.getJumpBtnRect(), x, y)) return _CONST.JUMP_UP;
        else if (v.isCollideAt(v.getJumpInvBtnRect(), x, y)) return _CONST.JUMP_DOWN;
        else if (v.isCollideAt(v.getShootBtnRect(), x, y)) return _CONST.SHOOT;
        else {
            for (ChoiceBox tb : v.getTextBoxes()) {
                if (v.isCollideAt(tb.getRectF(), x, y)) {
                    v.setCurrentSelectedBox(tb.getIndex());
                    return _CONST.BOX_TOUCHED;
                }
            }
        }
        return -1;
    }

}
