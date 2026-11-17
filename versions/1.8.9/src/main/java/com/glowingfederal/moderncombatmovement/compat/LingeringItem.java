/* Adapted from UAB ItemLingeringPotion; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import com.google.common.collect.HashMultimap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.BlockDispenser;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LingeringItem extends ItemPotion {

	public LingeringItem() {
		
		setUnlocalizedName("moderncombatmovement.lingering_potion");
		setCreativeTab(CreativeTabs.tabBrewing);

		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviourLingeringPotion());
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return PotionDelivery.use(stack,world,player);
	}

	@Override
	public List<PotionEffect> getEffects(int meta) {
        return super.getEffects(meta);
	}

	@Override
	public List<PotionEffect> getEffects(ItemStack stack) {
        return PotionDelivery.lingeringEffects(stack);
    }

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
        String modernName=PotionDelivery.displayName(stack);if(modernName!=null)return modernName;
		if (stack.getItemDamage() == 0 && PotionDelivery.effects(stack).isEmpty())
			return StatCollector.translateToLocal("item.emptyPotion.name").trim();
		String s = StatCollector.translateToLocal("potion.prefix.lingering").trim() + " ";

		List<PotionEffect> list = getEffects(stack);
		String s1;

		if (list != null && !list.isEmpty()) {
			s1 = list.get(0).getEffectName();
			s1 = s1 + ".postfix";
			return s + StatCollector.translateToLocal(s1).trim();
		}
		s1 = PotionHelper.getPotionPrefix(stack.getItemDamage());
		return StatCollector.translateToLocal(s1).trim() + " " + super.getItemStackDisplayName(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean isComplex) {
		if (stack.getItemDamage() == 0 && PotionDelivery.effects(stack).isEmpty())
			return;

		List<PotionEffect> effects = getEffects(stack);
		HashMultimap<String, AttributeModifier> attributes = HashMultimap.create();

		if (effects == null || effects.isEmpty()) {
			String s = StatCollector.translateToLocal("potion.empty").trim();
			list.add(EnumChatFormatting.GRAY + s);
		} else
			for (PotionEffect potioneffect : effects) {
				String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if (map != null && map.size() > 0)
					for (Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						attributes.put(entry.getKey().getAttributeUnlocalizedName(), attributemodifier1);
					}

				if (potioneffect.getAmplifier() > 0)
					s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				if (potioneffect.getDuration() > 20)
					s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";

				if (potion.isBadEffect())
					list.add(EnumChatFormatting.RED + s1);
				else
					list.add(EnumChatFormatting.GRAY + s1);
			}

		if (!attributes.isEmpty()) {
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

			for (Entry<String, AttributeModifier> entry1 : attributes.entries()) {
				AttributeModifier attributemodifier2 = entry1.getValue();
				double d0 = attributemodifier2.getAmount();
				double d1;

				if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
					d1 = attributemodifier2.getAmount();
				else
					d1 = attributemodifier2.getAmount() * 100.0D;

				if (d0 > 0.0D)
					list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				else if (d0 < 0.0D) {
					d1 *= -1.0D;
					list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				}
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        List<ItemStack> potions=new ArrayList<ItemStack>();net.minecraft.init.Items.potionitem.getSubItems(net.minecraft.init.Items.potionitem,tab,potions);
        for(ItemStack potion:potions)if(!isSplash(potion.getItemDamage())){ItemStack lingering=new ItemStack(this,1,potion.getItemDamage());if(potion.hasTagCompound())lingering.setTagCompound((NBTTagCompound)potion.getTagCompound().copy());list.add(lingering);}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return super.hasEffect(stack);
	}
}
