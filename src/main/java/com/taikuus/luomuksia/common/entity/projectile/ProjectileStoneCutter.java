package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.setup.EntityRegistry;
import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.level.block.Blocks.STONECUTTER;

public class ProjectileStoneCutter extends AbstractModifiableProj {
    public float eyeRelatedXRot = 0.0f;
    public float eyeRelatedYRot = 0.0f;
    public boolean removeFlag = false;
    public ProjectileStoneCutter(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ProjectileStoneCutter(Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(EntityRegistry.PROJECTILE_STONE_CUTTER.get(), pOwner, pX, pY, pZ, pLevel);
        maxExistingTicks = 20 * 7;
        damage = 1.0f;
    }
    @Override
    public void tick(){
        super.tick();
        eyeRelatedXRot = (timer);
        eyeRelatedYRot = ((float) (Math.PI / 2f));
        if (!this.level().isClientSide) {
            if ((this.getDeltaMovement().length() < 0.2f && this.onGround()) || this.removeFlag) {
                this.attemptRemoval();
            }
            AABB aabb = this.getBoundingBox().inflate(0.1, 0.1, 0.1);
            BlockPos.betweenClosedStream(aabb).forEach(pos -> {
                if (!this.level().getBlockState(pos).isAir()) {
                    this.onHitBlock(new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                }
            });

        }
    }
    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        return super.canCollideWith(pEntity) && pEntity != getOwner();
    }
    @Override
    protected void beforeRemoval(){
        super.beforeRemoval();
        BlockPos blockPos = this.blockPosition();
        if(!this.isExpired()) {
            if (this.level().getBlockState(blockPos).is(BlockTags.REPLACEABLE)) {

                this.level().setBlock(blockPos, STONECUTTER.defaultBlockState().rotate(this.level(), blockPos, Rotation.getRandom(this.random)), 3);
            } else {
                ItemEntity sc = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), new ItemStack(STONECUTTER.asItem()));
                this.level().addFreshEntity(sc);
            }
        }
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_PLACE, this.getSoundSource(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.remove(RemovalReason.DISCARDED);

    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float velocity = (float) this.getDeltaMovement().length();
        float dmg = Mth.clamp(velocity * damage, 0.0f, (float) Integer.MAX_VALUE);

        if(entity.hurt(getDamageSource(), dmg)){
            Vec3 knockbackMotion = this.getDeltaMovement().multiply((double) this.knockback + 0.1D, 0.0D, (double) this.knockback + 0.1D);
            entity.push(knockbackMotion.x, 0.1D, knockbackMotion.z);
        }
        this.playSound(SoundEvents.UI_STONECUTTER_TAKE_RESULT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        Level level = this.level();
        RandomSource random = RandomSource.create(pResult.getBlockPos().asLong());
        boolean collisionFlag = false;
        if (!level.isClientSide) {
            BlockState blockState = level.getBlockState(pResult.getBlockPos());
            ItemStack itemStack = new ItemStack(blockState.getBlock().asItem());
            if (blockState.is(BlockTags.LOGS)) {  // "cut" the tree down
                level.destroyBlock(pResult.getBlockPos(), true);
                collisionFlag = true;
            }
            if (level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, new SingleRecipeInput(itemStack), level).size() > 0) { // "cut" the block
                // get a random stonecutter recipe
                List<RecipeHolder<StonecutterRecipe>> recipes = level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, new SingleRecipeInput(itemStack), level);
                int randomIndex = random.nextInt(recipes.size());
                RecipeHolder<StonecutterRecipe> randomRecipe = recipes.get(randomIndex);

                // assemble the recipe
                ItemStack result = randomRecipe.value().assemble(new SingleRecipeInput(itemStack), level.registryAccess());
                level.destroyBlock(pResult.getBlockPos(), false);
                // drop the result
                if (result.getItem() instanceof BlockItem blockItem) {
                    for (int i = 0; i < result.getCount(); i++) {
                        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pResult.getBlockPos(), blockItem.getBlock().defaultBlockState());
                        fallingBlock.setDeltaMovement(random.nextGaussian() * 0.08, 0.2, random.nextGaussian() * 0.08);
                    }
                } else {
                    level.addFreshEntity(new ItemEntity(level, pResult.getLocation().x, pResult.getLocation().y, pResult.getLocation().z, result));
                }
                collisionFlag = true;
            }
        }
        //this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
        if (collisionFlag){
            this.playSound(SoundEvents.UI_STONECUTTER_TAKE_RESULT, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        }
        //super.onHitBlock(pResult);
        if (!piercing) {
            this.removeFlag = true;
        }
    }
    @Override
    public DamageSource getDamageSource() {
        return MiscRegistry.DamageTypeRegistry.CUT_DAMAGE.getDamageSource(this.level(), this, this.getOwner());
    }
}
