package com.gempukku.ld43.provider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.gaming.input2d.InputScheme2dProvider;

@RegisterSystem(shared = InputScheme2dProvider.class)
public class InputScheme2dProviderImpl implements InputScheme2dProvider {
    private int[] jumpKeys = new int[]{Input.Keys.W, Input.Keys.UP};
    private int[] leftKeys = new int[]{Input.Keys.A, Input.Keys.LEFT};
    private int[] rightKeys = new int[]{Input.Keys.D, Input.Keys.RIGHT};

    @Override
    public boolean isJumpActivated() {
        return isAnyPressed(jumpKeys);
    }

    @Override
    public boolean isLeftActivated() {
        return isAnyPressed(leftKeys);
    }

    @Override
    public boolean isRightActivated() {
        return isAnyPressed(rightKeys);
    }

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key))
                return true;
        }
        return false;
    }
}
