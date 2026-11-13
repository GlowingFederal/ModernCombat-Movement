package com.glowingfederal.moderncombatmovement.combat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class CombatConsumables {
 private CombatConsumables(){}
 public static void init(){MaceMechanics.init();}
 public static void removeLegacyAppleRecipe(){
  java.util.Iterator<?> it=net.minecraft.item.crafting.CraftingManager.getInstance().getRecipeList().iterator();
  while(it.hasNext()){Object value=it.next();if(value.getClass()!=net.minecraft.item.crafting.ShapedRecipes.class)continue;net.minecraft.item.crafting.ShapedRecipes recipe=(net.minecraft.item.crafting.ShapedRecipes)value;ItemStack output=recipe.getRecipeOutput();if(output==null||output.getItem()!=net.minecraft.init.Items.golden_apple||output.getItemDamage()!=1||recipe.recipeWidth!=3||recipe.recipeHeight!=3)continue;boolean exact=true;for(int i=0;i<9;i++){ItemStack ingredient=recipe.recipeItems[i];if(ingredient==null||ingredient.getItem()!=(i==4?net.minecraft.init.Items.apple:net.minecraft.item.Item.getItemFromBlock(net.minecraft.init.Blocks.gold_block))){exact=false;break;}}if(exact)it.remove();}
 }

 public static boolean apple(ItemStack stack,EntityPlayer p){
  if(!Settings.kit(p.worldObj.isRemote))return false;
  if(!p.worldObj.isRemote){boolean enchanted=stack.getItemDamage()>0;
   p.addPotionEffect(new PotionEffect(Potion.regeneration.id,enchanted?400:100,1));
   p.addPotionEffect(new PotionEffect(Potion.potionTypes[22].id,2400,enchanted?3:0));
   if(enchanted){p.addPotionEffect(new PotionEffect(Potion.resistance.id,6000,0));p.addPotionEffect(new PotionEffect(Potion.fireResistance.id,6000,0));}
  }
  return true;
 }

}
