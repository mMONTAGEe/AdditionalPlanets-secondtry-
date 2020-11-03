/**
 * Copyright (C) 2020 Interstellar:  Exoplanets
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.nagol2003.blocks.interfaces;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockTileEntity {

    /**
     * Gets the class of the tile entity associated with this block
     *
     * @return The TileEntity class
     */
    Class<? extends TileEntity> getTileEntityClass();

    /**
     * Gets a TESR for the tile entity, or null if it does not have one
     *
     * @return The TESR to bind, or null if nothing should be done.
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    default TileEntitySpecialRenderer<?> getTileRenderer() {
        return null;
    }
}
