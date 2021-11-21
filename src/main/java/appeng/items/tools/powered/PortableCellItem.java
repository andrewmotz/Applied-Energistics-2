/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.items.tools.powered;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.config.FuzzyMode;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ItemStorageChannel;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.data.AEItemKey;
import appeng.core.AEConfig;
import appeng.hooks.ICustomReequipAnimation;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.menu.MenuLocator;
import appeng.menu.MenuOpener;
import appeng.menu.me.items.MEPortableCellMenu;
import appeng.parts.automation.UpgradeInventory;
import appeng.util.ConfigInventory;

public class PortableCellItem extends AEBasePoweredItem
        implements IBasicCellItem<AEItemKey>, IMenuItem, ICustomReequipAnimation {

    private final StorageTier tier;

    public PortableCellItem(StorageTier tier, Item.Properties props) {
        super(AEConfig.instance().getPortableCellBattery(), props);
        this.tier = tier;
    }

    @Override
    public double getChargeRate() {
        return 80d;
    }

    /**
     * Open a wireless terminal from a slot in the player inventory, i.e. activated via hotkey.
     *
     * @return True if the menu was opened.
     */
    public boolean openFromInventory(Player player, int inventorySlot) {
        var is = player.getInventory().getItem(inventorySlot);
        if (is.getItem() == this) {
            return MenuOpener.open(getMenuType(), player, MenuLocator.forInventorySlot(inventorySlot));
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(getMenuType(), player, MenuLocator.forHand(player, hand));
        }
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                player.getItemInHand(hand));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> lines,
            final TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, lines, advancedTooltips);
        addCellInformationToTooltip(stack, lines);
    }

    @Override
    public int getBytes(final ItemStack cellItem) {
        return this.tier.getBytes();
    }

    @Override
    public int getBytesPerType(final ItemStack cellItem) {
        return this.tier.getBytesPerType();
    }

    @Override
    public int getTotalTypes(final ItemStack cellItem) {
        return this.tier.getTypes();
    }

    @Override
    public double getIdleDrain() {
        return 0.5;
    }

    @Override
    public ItemStorageChannel getChannel() {
        return StorageChannels.items();
    }

    @Override
    public boolean isEditable(final ItemStack is) {
        return true;
    }

    @Override
    public UpgradeInventory getUpgradesInventory(final ItemStack is) {
        return new CellUpgrades(is, 2);
    }

    @Override
    public ConfigInventory<AEItemKey> getConfigInventory(final ItemStack is) {
        return CellConfig.create(getChannel(), is);
    }

    @Override
    public FuzzyMode getFuzzyMode(final ItemStack is) {
        final String fz = is.getOrCreateTag().getString("FuzzyMode");
        try {
            return FuzzyMode.valueOf(fz);
        } catch (final Throwable t) {
            return FuzzyMode.IGNORE_ALL;
        }
    }

    @Override
    public void setFuzzyMode(final ItemStack is, final FuzzyMode fzMode) {
        is.getOrCreateTag().putString("FuzzyMode", fzMode.name());
    }

    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, BlockPos pos) {
        return new PortableCellMenuHost(player, inventorySlot, stack, (p, sm) -> openFromInventory(p, inventorySlot));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public enum StorageTier {
        SIZE_1K(512, 54, 8),
        SIZE_4K(2048, 45, 32),
        SIZE_16K(8192, 36, 128),
        SIZE_64K(16834, 27, 512);

        private final int bytes;
        private final int types;
        private final int bytesPerType;

        StorageTier(int bytes, int types, int bytesPerType) {
            this.bytes = bytes;
            this.types = types;
            this.bytesPerType = bytesPerType;
        }

        public int getBytes() {
            return bytes;
        }

        public int getTypes() {
            return types;
        }

        public int getBytesPerType() {
            return bytesPerType;
        }

    }

    protected MenuType<MEPortableCellMenu> getMenuType() {
        return MEPortableCellMenu.TYPE;
    }
}
