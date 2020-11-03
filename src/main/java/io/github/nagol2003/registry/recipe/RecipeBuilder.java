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

package io.github.nagol2003.registry.recipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import io.github.nagol2003.items.interfaces.IEnumItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class RecipeBuilder {

	private final String modId;

	private final String resourcePrefix;

	private int lastRecipeIndex = -1;

	private boolean jsonHellMode = false;

	private int oldRecipeRegisterCount;

	private final Map<ResourceLocation, IRSerializer> customSerializers = new HashMap<>();

	/**
	 * Instantiates a new recipe builder.
	 *
	 * @param modId the mod id
	 */
	public RecipeBuilder(String modId) {
		this.modId     = modId;
		resourcePrefix = modId + ":";
	}

	/**
	 * Gets the recipe key.
	 *
	 * @param key the key
	 * @return the recipe key
	 */
	private String getRecipeKey (String key) {
		if (key.isEmpty())
			key = "recipe" + (++lastRecipeIndex);
		return resourcePrefix + key.toLowerCase(Locale.ROOT);
	}

	/**
	 * Attempt json.
	 *
	 * @param name the name
	 * @param shaped the shaped
	 * @param result the result
	 * @param inputs the inputs
	 */
	private void attemptJson (String name, boolean shaped, ItemStack result, Object... inputs) {
		if (!jsonHellMode)
			return;
		name = name.replaceFirst(resourcePrefix, "");
		try {
			if (customSerializers.containsKey(result.getItem().getRegistryName()))
				JsonReceipe
						.createRecipe(name, customSerializers.get(result.getItem().getRegistryName()), result, inputs);
			else if (shaped)
				JsonReceipe.createShapedRecipe(name, result, inputs);
			else
				JsonReceipe.createShapelessRecipe(name, result, inputs);
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Failed to make JSON recipe \"" + name + "\" (" + ex.getMessage() + ")");
		}
	}

	/**
	 * Sets the recipe serializer.
	 *
	 * @param item the item
	 * @param serializer the serializer
	 */
	public void setRecipeSerializer (Item item, IRSerializer serializer) {
		customSerializers.put(item.getRegistryName(), serializer);
	}

	/*
	 * Recipes adders and makers. Adders will make and register a recipe. Makers
	 * just create a recipe (useful for guide book stuff, etc.) Group will
	 * default to the mod ID if not specified.
	 */

	/**
	 * Adds the shapeless.
	 *
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	// -------------------------------------------------- Shapeless --------------------------------------------------
	public IRecipe addShapeless (String key, ItemStack result, Object... inputs) {
		return addShapeless(modId, key, result, makeStackArray(inputs));
	}

	/**
	 * Adds the shapeless.
	 *
	 * @param group the group
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapeless (String group, String key, ItemStack result, Object... inputs) {
		return addShapeless(group, key, result, makeStackArray(inputs));
	}

	/**
	 * Adds the shapeless.
	 *
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapeless (String key, ItemStack result, ItemStack... inputs) {
		return addShapeless(modId, key, result, inputs);
	}

	/**
	 * Adds the shapeless.
	 *
	 * @param group the group
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapeless (String group, String key, ItemStack result, ItemStack... inputs) {
		key = getRecipeKey(key);
		IRecipe recipe = makeShapeless(result, inputs);
		registerRecipe(new ResourceLocation(key), recipe);
		attemptJson(key, false, result, (Object[]) inputs);
		return recipe;
	}

	/**
	 * Make shapeless.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (ItemStack result, Object... inputs) {
		return makeShapeless(result, makeStackArray(inputs));
	}

	/**
	 * Make shapeless.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (String group, ItemStack result, Object... inputs) {
		return makeShapeless(result, makeStackArray(inputs));
	}

	/**
	 * Make shapeless.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (ItemStack result, ItemStack... inputs) {
		return makeShapeless(modId, result, inputs);
	}

	/**
	 * Make shapeless.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (String group, ItemStack result, ItemStack... inputs) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (ItemStack stack : inputs)
			list.add(Ingredient.fromStacks(stack));
		return new ShapelessRecipes(group, result, list);
	}

	/**
	 * Make shapeless.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (ItemStack result, Ingredient... inputs) {
		return makeShapeless(modId, result, inputs);
	}

	/**
	 * Make shapeless.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapeless (String group, ItemStack result, Ingredient... inputs) {
		NonNullList<Ingredient> list = NonNullList.create();
		Collections.addAll(list, inputs);
		return new ShapelessRecipes(group, result, list);
	}

	// -------------------------------------------------- Shaped --------------------------------------------------

	/**
	 * Adds the shaped.
	 *
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShaped (String key, ItemStack result, Object... inputs) {
		return addShaped(modId, key, result, inputs);
	}

	/**
	 * Adds the shaped.
	 *
	 * @param group the group
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShaped (String group, String key, ItemStack result, Object... inputs) {
		key = getRecipeKey(key);
		IRecipe recipe = makeShaped(group, result, inputs);
		registerRecipe(new ResourceLocation(key), recipe);
		attemptJson(key, true, result, inputs);
		return recipe;
	}

	/**
	 * Make shaped.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShaped (ItemStack result, Object... inputs) {
		return makeShaped(modId, result, inputs);
	}

	/**
	 * Make shaped.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShaped (String group, ItemStack result, Object... inputs) {
		ShapedPrimer primer = CraftingHelper.parseShaped(inputs);
		return new ShapedRecipes(group, primer.width, primer.height, primer.input, result);
	}

	// -------------------------------------------------- Shapeless Ore --------------------------------------------------

	/**
	 * Adds the shapeless ore.
	 *
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapelessOre (String key, ItemStack result, Object... inputs) {
		return addShapelessOre(modId, key, result, inputs);
	}

	/**
	 * Adds the shapeless ore.
	 *
	 * @param group the group
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapelessOre (String group, String key, ItemStack result, Object... inputs) {
		key = getRecipeKey(key);
		IRecipe recipe = makeShapelessOre(group, result, inputs);
		registerRecipe(new ResourceLocation(key), recipe);
		attemptJson(key, false, result, inputs);
		return recipe;
	}

	/**
	 * Make shapeless ore.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapelessOre (ItemStack result, Object... inputs) {
		return makeShapelessOre(modId, result, inputs);
	}

	/**
	 * Make shapeless ore.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapelessOre (String group, ItemStack result, Object... inputs) {
		return new ShapelessOreRecipe(new ResourceLocation(group), result, inputs);
	}

	// -------------------------------------------------- Shaped Ore --------------------------------------------------

	/**
	 * Adds the shaped ore.
	 *
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapedOre (String key, ItemStack result, Object... inputs) {
		return addShapedOre(modId, key, result, inputs);
	}

	/**
	 * Adds the shaped ore.
	 *
	 * @param group the group
	 * @param key the key
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe addShapedOre (String group, String key, ItemStack result, Object... inputs) {
		key = getRecipeKey(key);
		IRecipe recipe = makeShapedOre(group, result, inputs);
		registerRecipe(new ResourceLocation(key), recipe);
		attemptJson(key, true, result, inputs);
		return recipe;
	}

	/**
	 * Make shaped ore.
	 *
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapedOre (ItemStack result, Object... inputs) {
		return makeShapedOre(modId, result, inputs);
	}

	/**
	 * Make shaped ore.
	 *
	 * @param group the group
	 * @param result the result
	 * @param inputs the inputs
	 * @return the i recipe
	 */
	public IRecipe makeShapedOre (String group, ItemStack result, Object... inputs) {
		return new ShapedOreRecipe(new ResourceLocation(group), result, inputs);
	}

	// -------------------------------------------------- Smelting --------------------------------------------------

	/**
	 * Adds the smelting.
	 *
	 * @param input the input
	 * @param output the output
	 * @param xp the xp
	 */
	public void addSmelting (Block input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	/**
	 * Adds the smelting.
	 *
	 * @param input the input
	 * @param output the output
	 * @param xp the xp
	 */
	public void addSmelting (Item input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	/**
	 * Adds the smelting.
	 *
	 * @param input the input
	 * @param output the output
	 * @param xp the xp
	 */
	public void addSmelting (ItemStack input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	// -------------------------------------------- Generic/Custom -------------------------------------------

	/**
	 * Adds some generic recipe. Not sure if this has any use. Typically you would use
	 * addCustomRecipe instead.
	 *
	 * @param key    Registry name.
	 * @param recipe Recipe object.
	 * @return The recipe.
	 */
	public IRecipe addGenericRecipe (String key, IRecipe recipe) {
		key = getRecipeKey(key);
		registerRecipe(new ResourceLocation(key), recipe);
		return recipe;
	}

	/**
	 * Adds custom recipe types.
	 *
	 * @param key    Registry name.
	 * @param recipe Recipe object.
	 * @return The recipe.
	 */
	public IRecipe addCustomRecipe (String key, IRecipe recipe) {
		key = getRecipeKey(key);
		registerRecipe(new ResourceLocation(key), recipe);
		return recipe;
	}

	/*
	 * Convenience recipe makers that simplify adding some common recipe types.
	 * These only have adders, not makers.
	 */

	/**
	 * Adds a compression recipe. For example, crafting ingots into blocks and vice versa. This will
	 * add both the compression (small to big) and decompression (big to small) recipes.
	 *
	 * @param key   Registry name for the recipe. Appends "_compress" or "_decompress" for the
	 *              appropriate recipes.
	 * @param small The small stack (such as ingots).
	 * @param big   The big stack (such as blocks).
	 * @param count The number of "small" needed to make a "big". Can be anything from 1 to 9.
	 * @return Both created recipes in an array. First is compression, second is decompression. They
	 * are both ShapelessRecipes.
	 */
	public IRecipe[] addCompression (String key, ItemStack small, ItemStack big, int count) {
		return addCompression(modId, key, small, big, count);
	}

	/**
	 * Adds a compression recipe. For example, crafting ingots into blocks and vice versa. This will
	 * add both the compression (small to big) and decompression (big to small) recipes.
	 *
	 * @param group The recipe group.
	 * @param key   Registry name for the recipe. Appends "_compress" or "_decompress" for the
	 *              appropriate recipes.
	 * @param small The small stack (such as ingots).
	 * @param big   The big stack (such as blocks).
	 * @param count The number of "small" needed to make a "big". Can be anything from 1 to 9.
	 * @return Both created recipes in an array. First is compression, second is decompression. They
	 * are both ShapelessRecipes.
	 */
	public IRecipe[] addCompression (String group, String key, ItemStack small, ItemStack big, int count) {
		// Clamp to sane values.
		count = MathHelper.clamp(count, 1, 9);

		// small -> big
		ItemStack[] smallArray = new ItemStack[count];
		for (int i = 0; i < count; ++i) {
			smallArray[i] = small;
		}
		IRecipe[] ret = new IRecipe[2];
		ret[0] = addShapeless(group, key + "_compress", big, smallArray);

		// big -> small
		ItemStack smallCopy = small.copy();
		smallCopy.setCount(count);
		ret[1] = addShapeless(group, key + "_decompress", smallCopy, big);

		return ret;
	}

	/**
	 * Adds the compression ore.
	 *
	 * @param key the key
	 * @param small the small
	 * @param big the big
	 * @param smallOreName the small ore name
	 * @param bigOreName the big ore name
	 * @param count the count
	 * @return the i recipe[]
	 */
	public IRecipe[] addCompressionOre (String key, ItemStack small, ItemStack big, @Nullable String smallOreName, @Nullable String bigOreName, int count) {
		// Clamp to sane values.
		count = MathHelper.clamp(count, 1, 9);

		// small -> big
		Object[] smallArray = new Object[count];
		for (int i = 0; i < count; ++i) {
			smallArray[i] = smallOreName != null && !smallOreName.isEmpty() ? smallOreName : small;
		}
		IRecipe[] ret = new IRecipe[2];
		ret[0] = addShapelessOre(modId, key + "_oredict_compress", big, smallArray);

		// big -> small
		ItemStack smallCopy = small.copy();
		smallCopy.setCount(count);
		Object bigObj = bigOreName != null && !bigOreName.isEmpty() ? bigOreName : big;
		ret[1] = addShapelessOre(modId, key + "_oredict_decompress", smallCopy, bigObj);

		return ret;
	}

	/**
	 * Adds one recipe consisting of a center item with 1-4 different items (2-8 of each)
	 * surrounding it.
	 *
	 * @param key         Registry name for the recipe.
	 * @param output      The item being crafted.
	 * @param middleStack The item in the middle of the crafting grid.
	 * @param surrounding The item(s) surrounding the middle item. Order affects the recipe.
	 * @return the i recipe
	 */
	public IRecipe addSurround (String key, ItemStack output, ItemStack middleStack, Object... surrounding) {
		return addSurround(modId, key, output, middleStack, surrounding);
	}

	/**
	 * Adds one recipe consisting of a center item with 1-4 different items (2-8 of each)
	 * surrounding it.
	 *
	 * @param group       The recipe group.
	 * @param key         Registry name for the recipe.
	 * @param output      The item being crafted.
	 * @param middleStack The item in the middle of the crafting grid.
	 * @param surrounding The item(s) surrounding the middle item. Order affects the recipe.
	 * @return the i recipe
	 */
	public IRecipe addSurround (String group, String key, ItemStack output, ItemStack middleStack, Object... surrounding) {
		ItemStack[] stacks = new ItemStack[surrounding.length];

		int i = -1;
		for (Object obj : surrounding) {
			++i;
			if (obj instanceof Block) {
				stacks[i] = new ItemStack((Block) obj);
			}
			else if (obj instanceof Item) {
				stacks[i] = new ItemStack((Item) obj);
			}
			else if (obj instanceof ItemStack) {
				stacks[i] = (ItemStack) obj;
			}
		}

		switch (surrounding.length) {
		case 0:
			// No surrounding stacks?
			throw new IllegalArgumentException("No surrounding items!");
		case 1:
			return addShaped(group, key, output, "xxx", "xcx", "xxx", 'c', middleStack, 'x', stacks[0]);
		case 2:
			return addShaped(group, key, output, "xyx", "ycy", "xyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1]);
		case 3:
			return addShaped(group, key, output, " xy", "zcz", "yx ", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2]);
		case 4:
			return addShaped(group, key, output, "xyz", "dcd", "zyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2], 'd', stacks[3]);
		default:
			// Too many things!
			throw new IllegalArgumentException("Too many items!");
		}
	}

	/**
	 * Adds one recipe consisting of a center item with 1-4 different items or oredict keys (2-8 of
	 * each) surrounding it.
	 *
	 * @param key         Registry name for the recipe.
	 * @param output      The item being crafted.
	 * @param middle      The item in the middle of the crafting grid.
	 * @param surrounding The item(s) or oredict key(s) surrounding the middle item. Order affects
	 *                    the recipe.
	 * @return the i recipe
	 */
	public IRecipe addSurroundOre (String key, ItemStack output, Object middle, Object... surrounding) {
		return addSurroundOre(modId, key, output, middle, surrounding);
	}

	/**
	 * Adds one recipe consisting of a center item with 1-4 different items or oredict keys (2-8 of
	 * each) surrounding it.
	 *
	 * @param group       The recipe group.
	 * @param key         Registry name for the recipe.
	 * @param output      The item being crafted.
	 * @param middle      The item in the middle of the crafting grid.
	 * @param surrounding The item(s) or oredict key(s) surrounding the middle item. Order affects
	 *                    the recipe.
	 * @return the i recipe
	 */
	public IRecipe addSurroundOre (String group, String key, ItemStack output, Object middle, Object... surrounding) {
		switch (surrounding.length) {
		case 0:
			// No surrounding stacks?
			throw new IllegalArgumentException("No surrounding items!");
		case 1:
			return addShapedOre(group, key, output, "xxx", "xcx", "xxx", 'c', middle, 'x', surrounding[0]);
		case 2:
			return addShapedOre(group, key, output, "xyx", "ycy", "xyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1]);
		case 3:
			return addShapedOre(group, key, output, " xy", "zcz", "yx ", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2]);
		case 4:
			return addShapedOre(group, key, output, "xyz", "dcd", "zyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2], 'd', surrounding[3]);
		default:
			// Too many things!
			throw new IllegalArgumentException("Too many items!");
		}
	}

	/**
	 * Registers a created recipe (called by adder methods, but not makers).
	 *
	 * @param name   Registry name for recipe.
	 * @param recipe The recipe to register.
	 * 
	 */
	private void registerRecipe (ResourceLocation name, IRecipe recipe) {
		if (recipe.getRegistryName() == null)
			recipe.setRegistryName(name);
		ForgeRegistries.RECIPES.register(recipe);
		setOldRecipeRegisterCount(getOldRecipeRegisterCount() + 1);
	}

	/**
	 * Make stack array.
	 *
	 * @param params the params
	 * @return the item stack[]
	 */
	private ItemStack[] makeStackArray (Object... params) {
		ItemStack[] result = new ItemStack[params.length];
		for (int i = 0; i < params.length; ++i) {
			Object obj = params[i];
			if (obj instanceof ItemStack)
				result[i] = (ItemStack) obj;
			else if (obj instanceof Item)
				result[i] = new ItemStack((Item) obj);
			else if (obj instanceof Block)
				result[i] = new ItemStack((Block) obj);
			else if (obj instanceof IEnumItems)
				result[i] = ((IEnumItems<?, ?>) obj).getStack();
			else
				throw new IllegalArgumentException("Can't make object of type " + obj.getClass()
						+ " into an ItemStack! Index " + i + ", obj=" + obj);
		}
		return result;
	}

	/**
	 * Gets the old recipe register count.
	 *
	 * @return the old recipe register count
	 */
	public int getOldRecipeRegisterCount () {
		return oldRecipeRegisterCount;
	}

	/**
	 * Sets the old recipe register count.
	 *
	 * @param oldRecipeRegisterCount the new old recipe register count
	 */
	public void setOldRecipeRegisterCount (int oldRecipeRegisterCount) {
		this.oldRecipeRegisterCount = oldRecipeRegisterCount;
	}
}
