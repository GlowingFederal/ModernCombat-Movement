package com.glowingfederal.moderncombatmovement.render;

import java.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.glowingfederal.moderncombatmovement.combat.SpearSpec;
import com.glowingfederal.moderncombatmovement.compat.Content;
/** Forge perspective dispatch keeps the vanilla GUI sprite separate from its long held sprite. */
public final class SpearModels {
 public static void init(){net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new SpearModels());for(SpearSpec spec:SpearSpec.values()){Item item=Item.REGISTRY.getObject(new ResourceLocation(Content.uab?"etfuturum":"moderncombatmovement",spec.material+"_spear"));if(item==null)continue;net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear","inventory"));net.minecraftforge.client.model.ModelLoader.registerItemVariants(item,new ResourceLocation("moderncombatmovement",spec.material+"_spear_in_hand"));}}
 @SubscribeEvent public void bake(ModelBakeEvent event){for(SpearSpec spec:SpearSpec.values()){ModelResourceLocation key=new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear","inventory"),heldKey=new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear_in_hand","inventory");IBakedModel gui=event.getModelRegistry().getObject(key),held=event.getModelRegistry().getObject(heldKey);if(gui!=null&&held!=null)event.getModelRegistry().putObject(key,new ContextModel(gui,held));}}
 private static final class ContextModel implements IBakedModel {
  final IBakedModel gui,held;ContextModel(IBakedModel gui,IBakedModel held){this.gui=gui;this.held=held;}
  public boolean isAmbientOcclusion(){return gui.isAmbientOcclusion();}public boolean isGui3d(){return gui.isGui3d();}public boolean isBuiltInRenderer(){return false;}
  public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleTexture(){return gui.getParticleTexture();}
  public ItemCameraTransforms getItemCameraTransforms(){return gui.getItemCameraTransforms();}
  public List<BakedQuad> getQuads(net.minecraft.block.state.IBlockState state,EnumFacing face,long seed){return gui.getQuads(state,face,seed);}public ItemOverrideList getOverrides(){return gui.getOverrides();}
  public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel,javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type){
   IBakedModel selected=(type==ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND||type==ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND||type==ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND||type==ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)?held:gui;
   return selected.handlePerspective(type);
  }
 }
}
