package io.github.nagol2003.init;

import io.github.nagol2003.blocks.FerminiumOre;
import io.github.nagol2003.blocks.StrangeMetallic;
import io.github.nagol2003.blocks.Denia.FROZENCO2;
import io.github.nagol2003.blocks.Ognious.MoistDirt;
import io.github.nagol2003.blocks.Ognious.OgniousGrass;
import io.github.nagol2003.blocks.Ognious.OgniousLeaves;
import io.github.nagol2003.blocks.Ognious.OgniousLog;
import io.github.nagol2003.blocks.Ognious.OgniousRock;
import io.github.nagol2003.blocks.Ognious.OgniousStone;
import io.github.nagol2003.blocks.Polulos.PolulosDirt;
import io.github.nagol2003.blocks.Polulos.PolulosRock;
import io.github.nagol2003.blocks.Polulos.PolulosSand;
import io.github.nagol2003.blocks.Polulos.ScorchedLeaves;
import io.github.nagol2003.blocks.Polulos.ScorchedLog;
import io.github.nagol2003.blocks.Tutorial.BlockCrust;
import io.github.nagol2003.blocks.Tutorial.BlockCrust.RockType;
import io.github.nagol2003.registry.APRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class InitBlocks {
	
	//tutorial blocks
	public static final Block ROCK_1 = new BlockCrust(RockType.IGNEOUS);
	public static final Block ROCK_2 = new BlockCrust(RockType.METAMORPHIC);
	public static final Block ROCK_3 = new BlockCrust(RockType.SEDIMENTARY);
	
	//Additional Planets blocks
	public static final Block FROZENCO2 = new FROZENCO2();
	public static final Block POLULOSROCK = new PolulosRock();
	public static final Block POLULOSDIRT = new PolulosDirt();
	public static final Block SCORCHEDLOG = new ScorchedLog();
	public static final Block POLULOSSAND = new PolulosSand();
	public static final Block SCORCHEDLEAVES = new ScorchedLeaves();
	public static final Block FERMINIUMORE = new FerminiumOre();
	public static final Block STRANGEMETALLIC = new StrangeMetallic();
	public static final Block OGNIOUSGRASS = new OgniousGrass();
	public static final Block MOISTDIRT= new MoistDirt();
	public static final Block OGNIOUSLOG= new OgniousLog();
	public static final Block OGNIOUSLEAVES= new OgniousLeaves();
	public static final Block OGNIOUSSTONE= new OgniousStone();
	public static final Block OGNIOUSROCK= new OgniousRock();
	
	public static void registerAll (APRegistry registry) {
		CreativeTabs toTab = AdditionalPlanetsTab.blockTab;
		CreativeTabs toTab2 = AdditionalPlanetsTab.AdditionalPlanets;
		
		registry.registerBlock(ROCK_1, "rock_1").setCreativeTab(toTab);
		registry.registerBlock(ROCK_2, "rock_2").setCreativeTab(toTab);
		registry.registerBlock(ROCK_3, "rock_3").setCreativeTab(toTab);
		registry.registerBlock(FROZENCO2, "frozenco2").setCreativeTab(toTab2);
		registry.registerBlock(POLULOSROCK, "polulosrock").setCreativeTab(toTab2);
		registry.registerBlock(POLULOSDIRT, "polulosdirt").setCreativeTab(toTab2);
		registry.registerBlock(SCORCHEDLOG, "schorchedlog").setCreativeTab(toTab2);
		registry.registerBlock(POLULOSSAND, "polulossand").setCreativeTab(toTab2);
		registry.registerBlock(SCORCHEDLEAVES, "schorchedleaves").setCreativeTab(toTab2);
		registry.registerBlock(FERMINIUMORE, "ferminiumore").setCreativeTab(toTab2);
		registry.registerBlock(STRANGEMETALLIC, "strangemetallic").setCreativeTab(toTab2);
		registry.registerBlock(OGNIOUSGRASS, "ogniousgrass").setCreativeTab(toTab2);
		registry.registerBlock(MOISTDIRT, "moistdirt").setCreativeTab(toTab2);
		registry.registerBlock(OGNIOUSLOG, "ogniouslog").setCreativeTab(toTab2);
		registry.registerBlock(OGNIOUSLEAVES, "ogniousleaves").setCreativeTab(toTab2);
		registry.registerBlock(OGNIOUSSTONE, "ogniousstone").setCreativeTab(toTab2);
		registry.registerBlock(OGNIOUSROCK, "ogniousrock").setCreativeTab(toTab2);
	}
	
}
