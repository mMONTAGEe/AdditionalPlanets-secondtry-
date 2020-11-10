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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.nagol2003.AddonMain;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public final class JsonReceipe {
	
	private JsonReceipe () {}

	/**
	 * An instance of Gson which is used by this class specifically.
	 */
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Creates a JSON recipe file with the provided serializer.
	 *
	 * @param name       Name for file
	 * @param serializer The {@link IRSerializer}
	 * @param result     The resulting {@link ItemStack} (note the built-in serializers do not
	 *                   support NBT)
	 * @param components Recipe components, which will depend on the serializer
	 * @since 3.0.0
	 */
	public static void createRecipe (String name, IRSerializer serializer, ItemStack result, Object... components) {
		createRecipeFile(serializer.serialize(result, components), name);
	}

	/**
	 * Creates a json file for a shaped recipe. Supports normal and ore recipes.
	 *
	 * @param result     The resulting item (NBT not supported by built-in serializers)
	 * @param components The components of the shaped recipe
	 */
	public static void createShapedRecipe (String name, ItemStack result, Object... components) {
		createRecipe(name, ShapedSerializer.INSTANCE, result, components);
	}

	/**
	 * Creates a json file for a shapeless recipe. Supports normal and ore recipes.
	 *
	 * @param result     The resulting item (NBT not supported by built-in serializers)
	 * @param components The components of the shapeless recipe
	 */
	public static void createShapelessRecipe (String name, ItemStack result, Object... components) {
		createRecipe(name, ShapelessSerializer.INSTANCE, result, components);
	}

	/**
	 * Serializes a component so it can be used in a json file.
	 *
	 * @param component The component to serialize.
	 * @return A map containing the data for the component.
	 */
	private static JsonObject serializeComponent (Object component) {
		// If the ingredient is an item, run it through again as an ItemStack.
		if (component instanceof Item) {
			return serializeComponent(new ItemStack((Item) component));
		}

		// If the ingredient is a block, run it through again as an ItemStack.
		if (component instanceof Block) {
			return serializeComponent(new ItemStack((Block) component));
		}

		// Handles the serialization of ItemStack. Doesn't support NBT yet.
		if (component instanceof ItemStack) {
			final ItemStack  stack = (ItemStack) component;
			final JsonObject ret   = new JsonObject();

			// Sets the item id.
			ret.addProperty("item", stack.getItem().getRegistryName().toString());

			// Sets the meta value of the item
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.addProperty("data", stack.getItemDamage());
			}

			// Sets the amount of items.
			if (stack.getCount() > 1) {
				ret.addProperty("count", stack.getCount());
			}

			// Warn about items with NBT, but go ahead and serialize what we can
			if (stack.hasTagCompound()) {
				AddonMain.LOGGER
						.warn("Recipe component contains NBT and cannot be serialized properly: {}", component);
			}

			return ret;
		}

		// Handles the serialization of an Ore Dict entry.
		if (component instanceof String) {
			final JsonObject ret = new JsonObject();
			ret.addProperty("type", "forge:ore_dict");
			ret.addProperty("ore", (String) component);

			return ret;
		}

		throw new IllegalArgumentException("Could not serialize the unsupported type "
				+ component.getClass().getName());
	}

	private static void createRecipeFile (JsonObject json, String fileName) {
		fileName = fileName.replaceAll(":", "_");
		// Sets the parent directory to that of the current mod being loaded.
		final File directory = new File("recipes/" + Loader.instance().activeModContainer().getModId());

		if (!directory.exists()) {
			directory.mkdirs();
		}

		// The actual file being written.
		final File output = new File(directory, fileName + ".json");
		//System.out.println(output.getAbsolutePath());

		// Writes the json file.
		try (FileWriter writer = new FileWriter(output)) {
			GSON.toJson(json, writer);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A basic serializer for shaped recipes, with ore dictionary support. Does not support NBT.
	 *
	 * @since 3.0.0
	 */
	public static final class ShapedSerializer implements IRSerializer {
		public static final ShapedSerializer INSTANCE = new ShapedSerializer();

		private ShapedSerializer() {}

		@Override
		public JsonObject serialize (ItemStack result, Object... components) {
			final JsonObject              json     = new JsonObject();
			final List<String>            pattern  = new ArrayList<>();
			final Map<String, JsonObject> inputMap = new HashMap<>();

			boolean   isOreDict = false;
			Character curKey    = null;

			// The current recipe argument index
			int i = 0;

			// Adds the initial pattern args to the json map.
			while (i < components.length && components[i] instanceof String) {
				pattern.add((String) components[i]);
				i++;
			}

			// Parses the rest of the recipe arguments
			for (; i < components.length; i++) {
				final Object component = components[i];

				// Handles character params. These should always be followed by an ingredient.
				if (component instanceof Character) {
					if (curKey != null) {
						throw new IllegalArgumentException("Provided two char keys in a row");
					}
					curKey = (Character) component;
				}

				// Handles ingredients.
				else {
					if (curKey == null) {
						throw new IllegalArgumentException("Providing object without a char key");
					}

					// If a string is used as an ingredient, it means this is an oredict recipe.
					if (component instanceof String) {
						isOreDict = true;
					}

					inputMap.put(Character.toString(curKey), serializeComponent(component));
					curKey = null;
				}
			}

			JsonArray patternArray = new JsonArray();
			pattern.forEach(patternArray::add);
			JsonObject inputKeys = new JsonObject();
			inputMap.forEach(inputKeys::add);

			json.addProperty("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
			json.add("pattern", patternArray);
			json.add("key", inputKeys);
			json.add("result", serializeComponent(result));

			return json;
		}
	}

	/**
	 * A basic serializer for shapeless recipes, with ore dictionary support. Does not support NBT.
	 *
	 * @since 3.0.0
	 */
	public static final class ShapelessSerializer implements IRSerializer {
		public static final ShapelessSerializer INSTANCE = new ShapelessSerializer();

		private ShapelessSerializer() {}

		@Override
		public JsonObject serialize (ItemStack result, Object... components) {
			final JsonObject       json        = new JsonObject();
			final List<JsonObject> ingredients = new ArrayList<>();
			boolean                isOreDict   = false;

			// Handles the processing/serialization for all the ingredients.
			for (final Object component : components) {
				// If a string is used as an ingredient, it means this is an oredict recipe.
				if (component instanceof String) {
					isOreDict = true;
				}

				ingredients.add(serializeComponent(component));
			}

			JsonArray ingredientArray = new JsonArray();
			ingredients.forEach(ingredientArray::add);
			json.add("ingredients", ingredientArray);
			json.addProperty("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
			json.add("result", serializeComponent(result));

			return json;
		}
	}
}
