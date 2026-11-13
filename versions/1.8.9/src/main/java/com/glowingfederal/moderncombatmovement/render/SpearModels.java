package com.glowingfederal.moderncombatmovement.render;

import java.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.glowingfederal.moderncombatmovement.combat.SpearSpec;
import com.glowingfederal.moderncombatmovement.compat.Content;
/** Forge perspective dispatch keeps the vanilla GUI sprite separate from its long held sprite. */
public final class SpearModels {
 public static void init(){net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new SpearModels());for(SpearSpec spec:SpearSpec.values()){Item item=(Item)Item.itemRegistry.getObject(new ResourceLocation(Content.uab?"etfuturum":"moderncombatmovement",spec.material+"_spear"));if(item==null)continue;net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear","inventory"));net.minecraft.client.resources.model.ModelBakery.registerItemVariants(item,new ResourceLocation("moderncombatmovement",spec.material+"_spear_in_hand"));}}
 @SubscribeEvent public void bake(ModelBakeEvent event){for(SpearSpec spec:SpearSpec.values()){ModelResourceLocation key=new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear","inventory"),heldKey=new ModelResourceLocation("moderncombatmovement:"+spec.material+"_spear_in_hand","inventory");IBakedModel gui=event.modelRegistry.getObject(key),held=event.modelRegistry.getObject(heldKey);if(gui!=null&&held!=null)event.modelRegistry.putObject(key,new ContextModel(gui,held));}}
 private static final class ContextModel implements net.minecraftforge.client.model.IPerspectiveAwareModel {
  final IBakedModel gui,held;ContextModel(IBakedModel gui,IBakedModel held){this.gui=gui;this.held=held;}
  public boolean isAmbientOcclusion(){return gui.isAmbientOcclusion();}public boolean isGui3d(){return gui.isGui3d();}public boolean isBuiltInRenderer(){return false;}
  public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleTexture(){return gui.getParticleTexture();}
  public ItemCameraTransforms getItemCameraTransforms(){return gui.getItemCameraTransforms();}
  public List<BakedQuad> getFaceQuads(EnumFacing face){return gui.getFaceQuads(face);}public List<BakedQuad> getGeneralQuads(){return gui.getGeneralQuads();}public net.minecraft.client.renderer.vertex.VertexFormat getFormat(){return net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM;}
  public org.apache.commons.lang3.tuple.Pair<? extends net.minecraftforge.client.model.IFlexibleBakedModel,javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type){
   IBakedModel selected=(type==ItemCameraTransforms.TransformType.FIRST_PERSON||type==ItemCameraTransforms.TransformType.THIRD_PERSON)?held:gui;
   return net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper.handlePerspective(new ContextModel(selected,selected),net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper.getTransforms(selected.getItemCameraTransforms()),type);
  }
 }
}
