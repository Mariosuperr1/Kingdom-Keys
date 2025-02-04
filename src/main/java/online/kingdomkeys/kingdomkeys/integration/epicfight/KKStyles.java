package online.kingdomkeys.kingdomkeys.integration.epicfight;

import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum KKStyles implements Style {
    VALOR(true), WISDOM(false), MASTER(true), FINAL(true);
    private final boolean canUseOffhand;
    private final int id;

    KKStyles(boolean canUseOffhand) {
        this.id = Style.ENUM_MANAGER.assign(this);
        this.canUseOffhand = canUseOffhand;
    }

    @Override
    public boolean canUseOffhand() {
        return canUseOffhand;
    }

    @Override
    public int universalOrdinal() {
        return id;
    }
}
