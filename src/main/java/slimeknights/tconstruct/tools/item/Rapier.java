package slimeknights.tconstruct.tools.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class Rapier extends ToolCore {

  public Rapier() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.swordBlade),
          PartMaterialType.extra(TinkerTools.crossGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public boolean isEffective(IBlockState block) {
    return BroadSword.effective_materials.contains(block.getMaterial());
  }

  @Override
  public float damagePotential() {
    return 0.35f; // tad lower than broadsword if it had the same speed
  }

  @Override
  public float damageCutoff() {
    return 13f;
  }

  @Override
  public double attackSpeed() {
    return 4;
  }

  @Override
  public float knockback() {
    return 0.6f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    if(player instanceof EntityPlayer) {
      return dealHybridDamage(DamageSource.causePlayerDamage((EntityPlayer) player), entity, damage);
    }

    return dealHybridDamage(DamageSource.causeMobDamage(player), entity, damage);
  }

  // changes the passed in damagesource, but the default method calls we use always create a new object
  private boolean dealHybridDamage(DamageSource source, EntityLivingBase target, float damage) {
    // half damage normal, half damage armor bypassing
    boolean hit = target.attackEntityFrom(source, damage / 2f);
    if(hit) {
      // reset things to deal damage again
      target.hurtResistantTime = 0;
      target.lastDamage = 0;
      target.attackEntityFrom(source.setDamageBypassesArmor(), damage / 2f);
    }
    return hit;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player, EnumHand hand) {
    if(player.onGround) {
      player.addExhaustion(0.1f);
      player.motionY += 0.32;
      float f = 0.5F;
      player.motionX = (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
      player.motionZ = (double) (-MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI) * f);
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);

    data.durability *= 0.8f;

    return data.get();
  }
}