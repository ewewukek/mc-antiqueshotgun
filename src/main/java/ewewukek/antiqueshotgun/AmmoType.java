package ewewukek.antiqueshotgun;

import ewewukek.antiqueshotgun.item.AmmoItem;
import net.minecraft.item.Item;

public enum AmmoType {
    NONE,
    HANDMADE,
    BUCKSHOT,
    SLUG,
    RUBBER,
    THERMITE;

    public byte toByte() {
        switch(this) {
        case HANDMADE:
            return 1;
        case BUCKSHOT:
            return 2;
        case SLUG:
            return 3;
        case RUBBER:
            return 4;
        case THERMITE:
            return 5;
        default:
            return 0;
        }
    }

    public AmmoItem toItem() {
        switch(this) {
        case HANDMADE:
            return AntiqueShotgunMod.HANDMADE_SHELL;
        case BUCKSHOT:
            return AntiqueShotgunMod.BUCKSHOT_SHELL;
        case SLUG:
            return AntiqueShotgunMod.SLUG_SHELL;
        case RUBBER:
            return AntiqueShotgunMod.RUBBER_SHELL;
        case THERMITE:
            return AntiqueShotgunMod.THERMITE_SHELL;
        default:
            return null;
        }
    }

    public static AmmoType fromByte(byte value) {
        switch(value) {
        case 1:
            return HANDMADE;
        case 2:
            return BUCKSHOT;
        case 3:
            return SLUG;
        case 4:
            return RUBBER;
        case 5:
            return THERMITE;
        default:
            return NONE;
        }
    }

    public static AmmoType fromItem(Item item) {
        if (item == AntiqueShotgunMod.HANDMADE_SHELL) {
            return HANDMADE;
        } else if (item == AntiqueShotgunMod.BUCKSHOT_SHELL) {
            return BUCKSHOT;
        } else if (item == AntiqueShotgunMod.SLUG_SHELL) {
            return SLUG;
        } else if (item == AntiqueShotgunMod.RUBBER_SHELL) {
            return RUBBER;
        } else if (item == AntiqueShotgunMod.THERMITE_SHELL) {
            return THERMITE;
        }
        return NONE;
    }
}
