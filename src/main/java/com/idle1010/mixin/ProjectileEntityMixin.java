package com.idle1010.mixin;

import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {

	@Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;deflect(Lnet/minecraft/entity/ProjectileDeflection;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)Z"))
	private boolean redirectDeflect(ProjectileEntity instance, ProjectileDeflection deflection, @Nullable net.minecraft.entity.Entity deflector, @Nullable net.minecraft.entity.Entity owner, boolean fromAttack) {
		if (instance instanceof PersistentProjectileEntity) {
			deflection.deflect(instance, deflector, instance.random);
			if (!instance.getWorld().isClient) {
				instance.onDeflected(deflector, fromAttack);
			}

			return true;
		}
		return instance.deflect(deflection, deflector, owner, fromAttack);
	}
}